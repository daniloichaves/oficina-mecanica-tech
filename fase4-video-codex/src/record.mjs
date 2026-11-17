/**
 * Filmagem real do roteiro do usuário, no Windows. NÃO É um simulador de respostas.
 * POST/PATCH bloqueados por padrão. Modo 'ensaio' não comprova CI/CD/qualidade/deploy.
 */
import 'dotenv/config';
import {chromium} from 'playwright';
import OpenAI from 'openai';
import {spawn,spawnSync} from 'node:child_process';
import {promises as fs} from 'node:fs';
import fsSync from 'node:fs';
import path from 'node:path';
import {fileURLToPath,pathToFileURL} from 'node:url';
import {getScenes,ROOT,requireNumber,requireUUID,findField,findRootId,currentState} from './script.mjs';
import {SwaggerOperator} from './swagger.mjs';
import {links,preflight} from './inspect.mjs';

const OUT=path.join(ROOT,'saida'), AUDIO=path.join(OUT,'audio');
const RAW=path.join(OUT,process.env.RAW_CAPTURE_NAME||'captura-sem-audio.mp4'), FINAL=path.join(OUT,'video-fase4.mp4');
const MODE=process.env.VIDEO_MODE||'ensaio';
const VOICE=process.env.VOICE_PROVIDER||'windows';
const REPO=path.resolve(ROOT,process.env.REPO_ROOT||'..');
const SCENES=getScenes(MODE);
const sleep=ms=>new Promise(ok=>setTimeout(ok,ms));
const min=ms=>new Promise(ok=>setTimeout(ok,ms));
const stateText =obj=>String(currentState(obj)||'').toUpperCase();
const safeText=s=>String(s??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));

function run(command,args){return new Promise((resolve,reject)=>{
 const p=spawn(command,args,{windowsHide:true});let stdout='',stderr='';
 p.stdout.on('data',d=>stdout+=d.toString());p.stderr.on('data',d=>stderr=(stderr+d.toString()).slice(-5000));
 p.on('error',reject);p.on('close',code=>code===0?resolve(stdout):reject(Error(`${command}: código ${code}; ${stderr}`)));
});}
async function duration(file){const val=await run('ffprobe',['-v','error','-show_entries','format=duration','-of','default=noprint_wrappers=1:nokey=1',file]);const n=Number(val.trim());if(!(n>0))throw Error('Áudio sem duração: '+path.basename(file));return Math.ceil(n*1000);}
async function voiceClip(scene,openai){
 const ext=VOICE==='openai'?'mp3':'wav';const filename=path.join(AUDIO,`${String(scene.number).padStart(2,'0')}.${ext}`);
 if(VOICE==='openai'){
   const result=await openai.audio.speech.create({model:process.env.TTS_MODEL||'gpt-4o-mini-tts',voice:process.env.TTS_VOICE||'cedar',input:scene.voice,instructions:'Narre em português brasileiro com ritmo moderado, didático e profissional. Leia siglas e termos técnicos naturalmente.',response_format:'mp3'});
   await fs.writeFile(filename,Buffer.from(await result.arrayBuffer()));
 }else await run('powershell.exe',['-NoProfile','-ExecutionPolicy','Bypass','-File',path.join(ROOT,'src','voice-win.ps1'),'-Text',scene.voice,'-OutFile',filename]);
 return {file:filename,ms:await duration(filename)};
}
function recorder(){
 const args=['-hide_banner','-loglevel','error','-y','-f','gdigrab','-framerate','25','-draw_mouse','1','-i','desktop','-vf','scale=trunc(iw/2)*2:trunc(ih/2)*2','-c:v','libx264','-preset','ultrafast','-crf','24','-pix_fmt','yuv420p','-an',RAW];
 const p=spawn('ffmpeg',args,{windowsHide:true,stdio:['pipe','ignore','pipe']});let stderr='';p.stderr.on('data',d=>stderr=(stderr+d.toString()).slice(-5000));
 const done=new Promise((resolve,reject)=>{p.on('error',reject);p.on('close',code=>code===0?resolve():reject(Error('Falha FFmpeg: '+stderr)));});
 return {p,done};
}
async function stopRecorder(r){if(!r)return;if(r.p.exitCode===null&&!r.p.killed){r.p.stdin.write('q');r.p.stdin.end();}await r.done;}
async function combine(clips){
 const args=clips.flatMap(c=>['-i',c.file]);
 const delays=clips.map((c,i)=>`[${i+1}:a]adelay=${c.startMs}:all=1[a${i}]`);
 const join=clips.map((_,i)=>`[a${i}]`).join('');
 const filter=delays.join(';')+';'+join+`amix=inputs=${clips.length}:duration=longest:dropout_transition=0:normalize=0,apad[aout]`;
 await run('ffmpeg',['-hide_banner','-loglevel','error','-y','-i',RAW,...args,'-filter_complex',filter,'-map','0:v:0','-map','[aout]','-c:v','copy','-c:a','aac','-b:a','160k','-shortest','-movflags','+faststart',FINAL]);
}
async function banner(page,title,subtitle=''){
 try{await page.evaluate(({title,subtitle})=>{
   const id='__fase4_label';let el=document.getElementById(id);
   if(!el){el=document.createElement('div');el.id=id;el.style.cssText='position:fixed;right:26px;top:22px;z-index:2147483647;background:rgba(10,30,55,.92);border-left:5px solid #3bd3b2;color:white;border-radius:12px;padding:14px 18px;max-width:480px;font:600 18px Segoe UI,Arial,sans-serif;pointer-events:none;box-shadow:0 10px 25px #0006';document.body.append(el);}
   el.textContent=title;const s=document.createElement('div');s.style.cssText='font-size:13px;margin-top:7px;font-weight:400';s.textContent=subtitle;el.append(s);
 },{title,subtitle});}catch{}
}
async function focus(page,title,subtitle=''){await page.bringToFront();await banner(page,title,subtitle);await sleep(250);}
async function getUntil(op,route,params,predicate,label,{attempts=12,intervalMs=2500}={}){
 let last;
 for(let i=0;i<attempts;i++){
   const r=await op.run('GET',route,{params});last=r.data;
   if(predicate(last))return last;
   if(i<attempts-1)await sleep(intervalMs);
 }
 throw Error(`${label}: estado ou campo não apareceu após ${attempts} consultas; último estado=${stateText(last)||'não identificado'}`);
}
async function showArchitecture(page){
 const doc=path.join(REPO,'docs/architecture/fase4/README.md');
 if(!fsSync.existsSync(doc))throw Error('Diagrama/README de arquitetura ausente: '+doc);
 const html=`<!doctype html><html lang="pt-BR"><meta charset="utf-8"><style>body{background:#101828;color:#f1f5f9;font:18px Segoe UI,Arial;padding:35px 55px}h1{font-size:40px}h2{font-size:25px;color:#a3e6d2}.grid{display:flex;gap:14px;justify-content:space-between;margin-top:50px}.service{width:22%;background:#233656;border:1px solid #5fa7dd;border-radius:14px;padding:22px 13px;text-align:center;font-size:24px;font-weight:700}.sub{font-size:16px;font-weight:400;color:#d9e4f0}.bus{font-size:22px;text-align:center;margin-top:35px;padding:21px;background:#153f43;border:1px solid #61c8ac;border-radius:12px}footer{position:absolute;bottom:26px;color:#bbc9dc;font-size:15px}</style><h1>Fase 4 — Oficina Mecânica</h1><h2>Visão didática baseada no roteiro de arquitetura</h2><div class="grid"><div class="service">OS<div class="sub">PostgreSQL</div></div><div class="service">Billing<div class="sub">PostgreSQL</div></div><div class="service">Execution<div class="sub">MongoDB</div></div><div class="service">Saga Orchestrator<div class="sub">PostgreSQL</div></div></div><div class="bus">REST + eventos RabbitMQ · Orquestração Saga</div><footer>Diagrama oficial / documentação: docs/architecture/fase4/README.md</footer></html>`;
 await page.setContent(html);await focus(page,'Arquitetura da aplicação','Microsserviços, bancos e RabbitMQ');
}
async function showEvidence(page){
 const feature=path.join(REPO,'fase4-priority4/saga-orchestrator/src/test/resources/features/saga_ordem_servico.feature');
 if(!fsSync.existsSync(feature))throw Error('Feature Gherkin ausente; não afirmar evidência: '+feature);
 const raw=await fs.readFile(feature,'utf8');
 const workflows=path.join(REPO,'.github/workflows');
 const names=fsSync.existsSync(workflows)?(await fs.readdir(workflows)).filter(x=>/\.ya?ml$/.test(x)):[];
 if(!names.length)throw Error('Nenhum pipeline .github/workflows/*.yml encontrado; não afirmar sua existência');
 const shown=raw.split(/\r?\n/).filter(line=>!/(password|senha|secret|token|authorization|api[_-]?key)/i.test(line)).slice(0,42).join('\n');
 await page.setContent(`<!doctype html><html lang="pt-BR"><meta charset="utf-8"><style>body{padding:30px 45px;background:#0f172a;color:#f8fafc;font:17px Segoe UI,Arial}h1{font-size:32px}h2{color:#93c5fd}pre{background:#1e293b;border-radius:12px;padding:25px;white-space:pre-wrap;font:15px Consolas;line-height:1.45}p{color:#e9b47f}</style><h1>Evidências disponíveis no repositório — ensaio</h1><h2>Gherkin: saga_ordem_servico.feature</h2><pre>${safeText(shown)}</pre><h2>Arquivos de workflow: ${safeText(names.join(' · '))}</h2><p>ATENÇÃO: arquivos de pipeline e testes NÃO comprovam execução de CI/CD, Sonar, cobertura ou Kubernetes.</p></html>`);
 await focus(page,'Testes e pipelines — ensaio','Somente arquivos presentes; sem afirmar execução ou deploy');
}
async function loginDashboard(page,type){
 const loginSelector=type==='RabbitMQ'?'input[name=username],input[name=login]':'input[name=user],input[name=username]';
 await page.locator(loginSelector).first().waitFor({state:'visible',timeout:5000}).catch(()=>{});
 const needsLogin=type==='RabbitMQ' ? await page.locator('input[name=username],input[name=login]').first().isVisible().catch(()=>false) : await page.locator('input[name=user],input[name=username]').first().isVisible().catch(()=>false);
 if(!needsLogin)return;
 const user=process.env[type==='RabbitMQ'?'RABBIT_USER':'GRAFANA_USER'];
 const password=process.env[type==='RabbitMQ'?'RABBIT_PASSWORD':'GRAFANA_PASSWORD'];
 if(!user||!password)throw Error(`${type} solicita login; configure credenciais SOMENTE no .env local ou use sessão já autenticada. Não publique segredos.`);
 if(type==='RabbitMQ'){
   await page.locator('input[name=username],input[name=login]').first().fill(user);
   await page.locator('input[type=password]').first().fill(password);
   await page.locator('input[type=submit],button[type=submit]').first().click();
 }else{
   await page.locator('input[name=user],input[name=username]').first().fill(user);
   await page.locator('input[type=password]').first().fill(password);
   await page.locator('button[type=submit]').first().click();
 }
 await sleep(1500);
}
async function tuneGrafana(page){
 const url=new URL(links.Grafana);
 url.searchParams.set('from','now-15m');
 url.searchParams.set('to','now');
 url.searchParams.set('refresh','5s');
 await page.goto(url.href,{waitUntil:'domcontentloaded',timeout:30000});
 await page.getByText('Serviços disponíveis / 4',{exact:true}).first().waitFor({timeout:30000});
 await page.getByRole('button',{name:/Last 15 minutes/}).first().waitFor({timeout:15000});
 if(new URL(page.url()).searchParams.get('refresh')!=='5s')throw Error('Grafana: atualização de 5s não confirmada');
}

async function main(){
 if(process.platform!=='win32')throw Error('A gravação do desktop exige Windows com sessão desbloqueada.');
 if(process.env.ALLOW_DEMO_MUTATIONS!=='true')throw Error('Por segurança, POST/PATCH desativados: configure ALLOW_DEMO_MUTATIONS=true apenas na base de DEMONSTRAÇÃO, após revisar o roteiro.');
 if(MODE==='final')throw Error('Modo final não liberado: evidências reais de CI/CD, JaCoCo, Sonar e rollout Kubernetes precisam ser verificadas e automatizadas pelo Codex ANTES do vídeo final. Rode VIDEO_MODE=ensaio para revisão.');
 for(const command of ['ffmpeg','ffprobe'])if(spawnSync(command,['-version'],{windowsHide:true}).status!==0)throw Error(`${command} não encontrado no PATH`);
 if(!['windows','openai'].includes(VOICE))throw Error('VOICE_PROVIDER deve ser windows ou openai');
 if(VOICE==='openai'&&!process.env.OPENAI_API_KEY)throw Error('Chave de voz OpenAI ausente no .env local');
 if(process.env.CAPTURE_DESKTOP==='false')throw Error('CAPTURE_DESKTOP=false: esta versão requer captura real da área de trabalho');
 await fs.mkdir(AUDIO,{recursive:true});
 console.log('Inspecionando todas as telas antes de executar operações de negócio...');
 const blockers=await preflight();if(blockers.length)throw Error('Bloqueios na inspeção: '+blockers.join(' | '));
 const openai=VOICE==='openai'?new OpenAI({apiKey:process.env.OPENAI_API_KEY}):null;
 console.log('Gerando narração a partir do texto do ROTEIRO-VIDEO.md...');
 const clips=[];for(const scene of SCENES){const clip=await voiceClip(scene,openai);clips.push({...clip,id:scene.number,voice:scene.voice});}
 for(const [i,c] of clips.entries())if(c.ms>SCENES[i].budgetSeconds*1000)throw Error(`Cena ${i+1}: narração (${Math.ceil(c.ms/1000)} s) extrapola orçamento (${SCENES[i].budgetSeconds} s). Ajuste ritmo/roteiro sem alterar os fatos.`);
 const context=await chromium.launchPersistentContext(path.join(ROOT,'.browser-profile'),{headless:false,viewport:null,acceptDownloads:true,args:['--start-maximized']});
 const pages={};const clients={};let rec=null;
 const ids={};const timeline=[];
 let diagram=null;
 try{
   for(const [name,url] of Object.entries(links)){
     const p=await context.newPage();await p.goto(url,{waitUntil:'domcontentloaded',timeout:15000});pages[name]=p;
     if(['RabbitMQ','Grafana'].includes(name))await loginDashboard(p,name);
     if(['OS','Billing','Execution','Saga'].includes(name))clients[name]=new SwaggerOperator(p,name);
   }
   diagram=await context.newPage();await showArchitecture(diagram);
   await tuneGrafana(pages.Grafana);
   await diagram.bringToFront();
   await sleep(1300);
   console.log('Atenção: notificações e janelas do desktop podem aparecer no vídeo; mantenha a sessão de gravação limpa.');
   rec=recorder();const start=Date.now();
   async function scene(number,action){
     const sc=SCENES[number-1],clip=clips[number-1];clip.startMs=Date.now()-start;
     console.log(`Cena ${number}/13: ${sc.title}`);
     const tick=Date.now();
     await action();
     const waited=Date.now()-tick;
     const remain=sc.budgetSeconds*1000-waited;
     if(remain>0)await sleep(remain);
     timeline.push({cena:number,titulo:sc.title,inicioMs:clip.startMs,duracaoRealMs:Date.now()-tick,vozMs:clip.ms});
     if(Date.now()-start>900000)throw Error('Tempo ultrapassou 15 minutos; vídeo interrompido para não gerar entrega fora do limite.');
   }
   await scene(1,async()=>{await focus(diagram,'Apresentação','Grupo 267 — Fase 4');});
   await scene(2,async()=>{await showArchitecture(diagram);});
   await scene(3,async()=>{await focus(pages.Grafana,'Dashboard antes dos fluxos','Disponibilidade e contadores iniciais');await pages.Grafana.waitForTimeout(1200);});
   await scene(4,async()=>{
     await focus(pages.OS,'Criar primeira ordem','POST /api/os — fluxo de sucesso');
     const first={placaVeiculo:'ABC1234',clienteId:'cli-video-sucesso',nomeCliente:'Cliente Demonstracao',descricaoDefeito:'Revisao do sistema de freios'};
     const created=await clients.OS.run('POST','/api/os',{body:first,expect:201});ids.OS_A=requireNumber(findRootId(created.data),'OS_A');
   });
   await scene(5,async()=>{
     await focus(pages.Saga,'Orçamento automático','GET /api/sagas/ordens/{ordemId}');
     const saga=await getUntil(clients.Saga,'/api/sagas/ordens/{ordemId}',{ordemId:ids.OS_A},x=>Boolean(findField(x,['orcamentoId'])),'ORC_A');
     ids.ORC_A=requireNumber(findField(saga,['orcamentoId']),'ORC_A');
     await focus(pages.Billing,'Orçamento inicial','GET /api/orcamentos/{id}');
     await clients.Billing.run('GET','/api/orcamentos/{id}',{params:{id:ids.ORC_A}});
   });
   await scene(6,async()=>{
     await focus(pages.Billing,'Aprovação e pagamento simulado','ORC_A — operações PATCH no Swagger');
     await clients.Billing.run('PATCH','/api/orcamentos/{id}/aprovar',{params:{id:ids.ORC_A}});
     await clients.Billing.run('PATCH','/api/orcamentos/{id}/pagar',{params:{id:ids.ORC_A}});
   });
   await scene(7,async()=>{
     await focus(pages.Saga,'Aguardar execução','Ler execucaoId UUID inteiro da Saga');
     const saga=await getUntil(clients.Saga,'/api/sagas/ordens/{ordemId}',{ordemId:ids.OS_A},x=>stateText(x).includes('EXECUCAO_EM_ANDAMENTO')&&Boolean(findField(x,['execucaoId'])),'EXEC_A');
     ids.EXEC_A=requireUUID(findField(saga,['execucaoId']),'EXEC_A');
     await focus(pages.Execution,'Executar tarefa','GET → iniciar → finalizar');
     await clients.Execution.run('GET','/api/execucoes/{id}',{params:{id:ids.EXEC_A}});
     await clients.Execution.run('PATCH','/api/execucoes/{id}/iniciar',{params:{id:ids.EXEC_A}});
     await clients.Execution.run('PATCH','/api/execucoes/{id}/finalizar',{params:{id:ids.EXEC_A}});
     await focus(pages.OS,'OS finalizada','Verificar estado real');
     await getUntil(clients.OS,'/api/os/{id}',{id:ids.OS_A},x=>stateText(x)==='FINALIZADA','OS_A finalizada',{attempts:10});
     await focus(pages.Saga,'Saga finalizada','Verificar FINALIZADA e SAGA_FINALIZADA');
     await getUntil(clients.Saga,'/api/sagas/ordens/{ordemId}',{ordemId:ids.OS_A},x=>stateText(x)==='FINALIZADA'&&JSON.stringify(x).includes('SAGA_FINALIZADA'),'Saga A finalizada',{attempts:10});
   });
   await scene(8,async()=>{
     await focus(pages.OS,'Segunda ordem','Cenário controlado de compensação');
     const second={placaVeiculo:'DEF5678',clienteId:'cli-video-falha',nomeCliente:'Cliente Compensacao',descricaoDefeito:'Reparo com falha simulada na execucao'};
     const created=await clients.OS.run('POST','/api/os',{body:second,expect:201});ids.OS_B=requireNumber(findRootId(created.data),'OS_B');
     await focus(pages.Saga,'Obter segundo orçamento','Ler ORC_B da Saga');
     const saga=await getUntil(clients.Saga,'/api/sagas/ordens/{ordemId}',{ordemId:ids.OS_B},x=>Boolean(findField(x,['orcamentoId'])),'ORC_B');
     ids.ORC_B=requireNumber(findField(saga,['orcamentoId']),'ORC_B');
     await focus(pages.Billing,'Aprovar e pagar segunda OS','Operações simuladas previstas no roteiro');
     await clients.Billing.run('PATCH','/api/orcamentos/{id}/aprovar',{params:{id:ids.ORC_B}});
     await clients.Billing.run('PATCH','/api/orcamentos/{id}/pagar',{params:{id:ids.ORC_B}});
     await focus(pages.Saga,'Aguardar etapa de execução','Não injetar falha antes do estado esperado');
     await getUntil(clients.Saga,'/api/sagas/ordens/{ordemId}',{ordemId:ids.OS_B},x=>stateText(x).includes('EXECUCAO_EM_ANDAMENTO'),'Saga B execução');
   });
   await scene(9,async()=>{
     await focus(pages.Saga,'Falha controlada','EXECUCAO_FALHOU para OS_B (sem simular resposta)');
     await clients.Saga.run('POST','/api/sagas/ordens/{ordemId}/eventos',{params:{ordemId:ids.OS_B,evento:'EXECUCAO_FALHOU'}});
     const sb=await getUntil(clients.Saga,'/api/sagas/ordens/{ordemId}',{ordemId:ids.OS_B},x=>stateText(x)==='COMPENSADA'&&['FALHA_EXECUCAO','COMPENSACAO_ORCAMENTO_ESTORNADO','COMPENSACAO_OS_CANCELADA'].every(s=>JSON.stringify(x).includes(s)),'Saga B compensada');
     await focus(pages.Billing,'Confirmar estorno','Status real ESTORNADO/refunded');
     await getUntil(clients.Billing,'/api/orcamentos/{id}',{id:ids.ORC_B},x=>stateText(x)==='ESTORNADO'&&JSON.stringify(x).toLowerCase().includes('refunded'),'Orçamento B estornado');
     await focus(pages.OS,'Confirmar cancelamento','Status real CANCELADA');
     await getUntil(clients.OS,'/api/os/{id}',{id:ids.OS_B},x=>stateText(x)==='CANCELADA','OS_B cancelada');
   });
   await scene(10,async()=>{
     await focus(pages.RabbitMQ,'RabbitMQ','Exchanges → oficina.events');
     const ex=pages.RabbitMQ.getByRole('link',{name:'Exchanges',exact:true}).first();await ex.click({timeout:15000});
     const exchange=pages.RabbitMQ.getByRole('link',{name:'oficina.events',exact:true}).first();await exchange.click({timeout:15000});
     await sleep(3000);
     const queues=pages.RabbitMQ.getByRole('link',{name:/Queues and Streams/}).first();await queues.click({timeout:15000});
     const queue=pages.RabbitMQ.getByRole('link',{name:'saga-orchestrator.events',exact:true}).first();await queue.click({timeout:15000});
     await sleep(3000);
     await focus(pages.Prometheus,'Prometheus','Targets UP — conferir os quatro serviços');
     await sleep(6000);
     await focus(pages.Grafana,'Grafana','Disponibilidade, contadores e métricas reais');
     await pages.Grafana.mouse.wheel(0,840);await sleep(4500);
     await pages.Grafana.mouse.wheel(0,780);await sleep(4500);
   });
   await scene(11,async()=>{
     await focus(pages.Saga,'Histórico da Saga','Relacionar IDs e etapas da OS compensada');
     await clients.Saga.run('GET','/api/sagas/ordens/{ordemId}',{params:{ordemId:ids.OS_B}});
   });
   await scene(12,async()=>{
     await showEvidence(diagram);
   });
   await scene(13,async()=>{await showArchitecture(diagram);await focus(diagram,'Encerramento','Fluxos Saga, compensação e observabilidade');});
   await sleep(800);await stopRecorder(rec);rec=null;
   await fs.writeFile(path.join(OUT,'timeline.json'),JSON.stringify(timeline,null,2));
   await fs.writeFile(path.join(OUT,'ids-demo.json'),JSON.stringify(ids,null,2));
   await fs.writeFile(path.join(OUT,'narracao.txt'),SCENES.map(x=>`${x.number}. ${x.title}\n${x.voice}`).join('\n\n'));
   console.log('Mesclando voz e captura real...');await combine(clips);
   const actual=Number((await run('ffprobe',['-v','error','-show_entries','format=duration','-of','default=noprint_wrappers=1:nokey=1',FINAL])).trim());
   if(actual>900)throw Error(`O vídeo gerado tem ${Math.round(actual)} s, acima do limite de 15 min; revise antes de entregar`);
   console.log('ENSAIO GERADO:',FINAL,'(',Math.round(actual),'segundos)');
   console.log('IMPORTANTE: A cena 12 não comprova execução real de qualidade/CI/CD/Kubernetes.');
 }finally{
   if(rec)await stopRecorder(rec).catch(e=>console.error('Parada de captura:',e.message));
   await context.close().catch(()=>{});
 }
}
export {run,duration,recorder,stopRecorder,focus,showArchitecture,showEvidence,loginDashboard,tuneGrafana};
if(process.argv[1]&&path.resolve(process.argv[1])===fileURLToPath(import.meta.url))main().catch(e=>{console.error('AUTOMAÇÃO INTERROMPIDA:',e.message);process.exitCode=1;});
