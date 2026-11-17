import 'dotenv/config';
import {chromium} from 'playwright';
import path from 'node:path';
import {promises as fs} from 'node:fs';
import {ROOT,getScenes} from './script.mjs';
import {links} from './inspect.mjs';
import {SwaggerOperator} from './swagger.mjs';

// Retoma apenas leituras após as nove cenas reais já capturadas.
process.env.RAW_CAPTURE_NAME='captura-parte2.mp4';
const {run,duration,recorder,stopRecorder,focus,showArchitecture,showEvidence,loginDashboard,tuneGrafana}=await import('./record.mjs');
const out=path.join(ROOT,'saida');
const scenes=getScenes('ensaio');
const sleep=ms=>new Promise(resolve=>setTimeout(resolve,ms));
const ids=JSON.parse((await fs.readFile(path.join(out,'ids-demo.json'),'utf8')).replace(/^\uFEFF/,''));
const prefixSeconds=scenes.slice(0,9).reduce((n,s)=>n+s.budgetSeconds,0);
const prefix=path.join(out,'captura-sem-audio.mp4');
if(await duration(prefix)<prefixSeconds*1000)throw Error('Captura anterior não contém nove cenas completas');
const context=await chromium.launchPersistentContext(path.join(ROOT,'.browser-profile'),{headless:false,viewport:null,args:['--start-maximized']});
let recording;
try {
  const pages={};
  for(const name of ['RabbitMQ','Prometheus','Grafana','Saga']) {
    const page=await context.newPage();pages[name]=page;
    await page.goto(links[name],{waitUntil:'domcontentloaded'});
    if(['RabbitMQ','Grafana'].includes(name))await loginDashboard(page,name);
  }
  await tuneGrafana(pages.Grafana);
  // Resolver e conferir os destinos por links reais antes da captura.
  const rabbit=pages.RabbitMQ;
  await rabbit.getByRole('link',{name:'Exchanges',exact:true}).click();
  const exchangeLink=rabbit.getByRole('link',{name:'oficina.events',exact:true}).first();
  await exchangeLink.waitFor({timeout:20000});
  const exchangeUrl=await exchangeLink.evaluate(el=>el.href);
  await exchangeLink.click();
  await rabbit.getByRole('link',{name:/Queues and Streams/}).first().click();
  const queueLink=rabbit.getByRole('link',{name:'saga-orchestrator.events',exact:true}).first();
  await queueLink.waitFor({timeout:20000});
  const queueUrl=await queueLink.evaluate(el=>el.href);
  await queueLink.click();
  const targets=await (await pages.Prometheus.request.get('http://localhost:9090/api/v1/targets')).json();
  if(targets.data.activeTargets.length!==4||targets.data.activeTargets.some(t=>t.health!=='up'))throw Error('Coleta Prometheus incompleta');
  const sagaClient=new SwaggerOperator(pages.Saga,'Saga');
  const before=await sagaClient.run('GET','/api/sagas/ordens/{ordemId}',{params:{ordemId:ids.OS_B}});
  if(before.data.status!=='COMPENSADA')throw Error('Saga compensada não confirmada');
  const diagram=await context.newPage();await showArchitecture(diagram);
  await rabbit.goto(exchangeUrl);await focus(rabbit,'RabbitMQ','Exchange real oficina.events');
  recording=recorder();
  const start=Date.now();const timeline=[];
  async function scene(number,action){
    const tick=Date.now();console.log(`Cena ${number}/13 (retomada)`);
    await action();
    await sleep(Math.max(0,scenes[number-1].budgetSeconds*1000-(Date.now()-tick)));
    timeline.push({cena:number,inicioMs:prefixSeconds*1000+tick-start,duracaoRealMs:Date.now()-tick});
  }
  await scene(10,async()=>{
    await rabbit.goto(exchangeUrl);await focus(rabbit,'RabbitMQ','Exchange e bindings reais');await sleep(15000);
    await rabbit.goto(queueUrl);await focus(rabbit,'RabbitMQ','Fila saga-orchestrator.events e consumidor');await sleep(15000);
    await focus(pages.Prometheus,'Prometheus','Quatro targets UP');await sleep(18000);
    await focus(pages.Grafana,'Grafana','Disponibilidade e contadores após os fluxos');await sleep(18000);
    await pages.Grafana.mouse.wheel(0,600);await sleep(18000);
    await pages.Grafana.mouse.wheel(0,550);await sleep(18000);
  });
  await scene(11,async()=>{await focus(pages.Saga,'Histórico da Saga','IDs reais da OS compensada');await sagaClient.run('GET','/api/sagas/ordens/{ordemId}',{params:{ordemId:ids.OS_B}});});
  await scene(12,async()=>{await showEvidence(diagram);});
  await scene(13,async()=>{await showArchitecture(diagram);await focus(diagram,'Encerramento','Ensaio com voz sintética em português');});
  await stopRecorder(recording);recording=null;
  await fs.writeFile(path.join(out,'timeline-retomada.json'),JSON.stringify(timeline,null,2));
  console.log('Montando captura anterior e retomada real...');
  const joined=path.join(out,'captura-completa.mp4');
  await run('ffmpeg',['-hide_banner','-loglevel','error','-y','-i',prefix,'-i',path.join(out,'captura-parte2.mp4'),'-filter_complex',`[0:v]trim=duration=${prefixSeconds},setpts=PTS-STARTPTS[v0];[1:v]setpts=PTS-STARTPTS[v1];[v0][v1]concat=n=2:v=1:a=0[v]`,'-map','[v]','-c:v','libx264','-preset','ultrafast','-crf','24','-pix_fmt','yuv420p',joined]);
  let offset=0;const clips=[];
  for(const s of scenes){clips.push({file:path.join(out,'audio',`${String(s.number).padStart(2,'0')}.wav`),start:offset});offset+=s.budgetSeconds*1000;}
  const delays=clips.map((c,i)=>`[${i+1}:a]adelay=${c.start}:all=1[a${i}]`);
  const mix=delays.join(';')+';'+clips.map((_,i)=>`[a${i}]`).join('')+`amix=inputs=13:duration=longest:normalize=0,apad[a]`;
  const final=path.join(out,'video-fase4.mp4');
  await run('ffmpeg',['-hide_banner','-loglevel','error','-y','-i',joined,...clips.flatMap(c=>['-i',c.file]),'-filter_complex',mix,'-map','0:v:0','-map','[a]','-c:v','copy','-c:a','aac','-b:a','160k','-shortest','-movflags','+faststart',final]);
  const finalDuration=await duration(final);if(finalDuration>900000)throw Error('Vídeo excede 15 minutos');
  await fs.writeFile(path.join(out,'narracao.txt'),scenes.map(s=>`${s.number}. ${s.title}\n${s.voice}`).join('\n\n'));
  const fullTimeline=[];let startMs=0;
  for(const s of scenes){fullTimeline.push({cena:s.number,titulo:s.title,inicioMs:startMs,duracaoPlanejadaMs:s.budgetSeconds*1000,origem:s.number<10?'captura original':'retomada real'});startMs+=s.budgetSeconds*1000;}
  await fs.writeFile(path.join(out,'timeline.json'),JSON.stringify(fullTimeline,null,2));
  console.log(`ENSAIO GERADO: ${final}; duração=${finalDuration/1000}s; sem novas mutações na retomada`);
} finally {if(recording)await stopRecorder(recording).catch(()=>{});await context.close();}
