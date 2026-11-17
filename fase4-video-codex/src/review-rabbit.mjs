import 'dotenv/config';
import {chromium} from 'playwright';
import path from 'node:path';
import {ROOT} from './script.mjs';
import {links} from './inspect.mjs';
process.env.RAW_CAPTURE_NAME='captura-rabbitmq-revisao.mp4';
const {run,recorder,stopRecorder,focus,loginDashboard}=await import('./record.mjs');
const out=path.join(ROOT,'saida');
const browser=await chromium.launch({headless:false,args:['--start-maximized']});
const context=await browser.newContext({viewport:null});
let rec;
const sleep=ms=>new Promise(resolve=>setTimeout(resolve,ms));
async function maskLogin(page){
  await page.locator('body *').evaluateAll(els=>{for(const el of els){const text=el.textContent.trim();if(text.length<100&&/^User\s/.test(text))el.style.visibility='hidden';}});
}
try {
  const page=await context.newPage();await page.goto(links.RabbitMQ);await loginDashboard(page,'RabbitMQ');
  await page.getByRole('link',{name:'Exchanges',exact:true}).click();
  await page.getByRole('link',{name:'oficina.events',exact:true}).click();
  await page.getByText('Bindings',{exact:true}).click();
  await page.getByText('saga-orchestrator.events',{exact:true}).first().waitFor();
  await page.getByText('saga-orchestrator.events',{exact:true}).first().scrollIntoViewIfNeeded();
  await maskLogin(page);await focus(page,'RabbitMQ','Exchange e bindings reais');
  await page.screenshot({path:path.join(out,'frame-bindings-revisao.png')});
  rec=recorder();const start=Date.now();await sleep(15000);
  await page.getByRole('link',{name:/Queues and Streams/}).click();
  await page.getByRole('link',{name:'saga-orchestrator.events',exact:true}).click();
  await page.getByText('Consumers (1)',{exact:true}).first().waitFor({timeout:15000});
  await page.getByText('Consumers (1)',{exact:true}).first().scrollIntoViewIfNeeded();
  await maskLogin(page);await focus(page,'RabbitMQ','Fila e consumidor reais');
  const remain=30000-(Date.now()-start);if(remain>0)await sleep(remain);
  await stopRecorder(rec);rec=null;
  await run('ffmpeg',['-hide_banner','-loglevel','error','-y','-i',path.join(out,'video-fase4.mp4'),'-i',path.join(out,'captura-rabbitmq-revisao.mp4'),'-filter_complex','[0:v]trim=end=520,setpts=PTS-STARTPTS[p];[1:v]trim=duration=30,setpts=PTS-STARTPTS[r];[0:v]trim=start=550,setpts=PTS-STARTPTS[s];[p][r][s]concat=n=3:v=1:a=0[v]','-map','[v]','-map','0:a:0','-c:v','libx264','-preset','ultrafast','-crf','24','-c:a','copy','-movflags','+faststart',path.join(out,'video-fase4-revisado.mp4')]);
  console.log('Trecho RabbitMQ revisado com bindings visíveis e login oculto; sem mutações de negócio.');
} finally {if(rec)await stopRecorder(rec).catch(()=>{});await browser.close();}
