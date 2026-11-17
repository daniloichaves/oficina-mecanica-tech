import 'dotenv/config';
import {spawnSync} from 'node:child_process';
import {existsSync} from 'node:fs';
import {createRequire} from 'node:module';
const require=createRequire(import.meta.url);
const checks=[['Node.js 20+',Number(process.versions.node.split('.')[0])>=20],['Windows 10/11',process.platform==='win32']];
let pw;try{pw=require('playwright');}catch{}
checks.push(['Playwright instalado',Boolean(pw)]);
checks.push(['Chromium Playwright',Boolean(pw&&existsSync(pw.chromium.executablePath()))]);
for(const bin of ['ffmpeg','ffprobe'])checks.push([`${bin} no PATH`,spawnSync(bin,['-version'],{windowsHide:true}).status===0]);
const voice=process.env.VOICE_PROVIDER||'windows';checks.push(['voz: windows ou openai',['windows','openai'].includes(voice)]);
if(voice==='openai')checks.push(['OPENAI_API_KEY definida localmente',Boolean(process.env.OPENAI_API_KEY)]);
checks.push(['VIDEO_MODE válido',['ensaio','final'].includes(process.env.VIDEO_MODE||'ensaio')]);
for(const [title,pass] of checks)console.log(`${pass?'OK':'PENDENTE'} ${title}`);
if(process.env.ALLOW_DEMO_MUTATIONS!=='true')console.log('BLOQUEIO DE SEGURANÇA ATIVO: video não executará POST/PATCH até ALLOW_DEMO_MUTATIONS=true.');
console.log('Credenciais de ferramentas, se necessárias, ficam SOMENTE no .env local; nunca as inclua no chat.');
if(checks.some(x=>!x[1]))process.exitCode=1;
