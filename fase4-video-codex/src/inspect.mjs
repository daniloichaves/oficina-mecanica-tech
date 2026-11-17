import 'dotenv/config';
import {chromium} from 'playwright';
import {getScenes,getTotalBudget,ROOT} from './script.mjs';
import path from 'node:path';
import {existsSync} from 'node:fs';

const links = {
 OS:process.env.OS_SWAGGER||'http://localhost:8081/swagger-ui/index.html',
 Billing:process.env.BILLING_SWAGGER||'http://localhost:8082/swagger-ui/index.html',
 Execution:process.env.EXECUTION_SWAGGER||'http://localhost:8083/swagger-ui/index.html',
 Saga:process.env.SAGA_SWAGGER||'http://localhost:8084/swagger-ui/index.html',
 RabbitMQ:process.env.RABBIT_URL||'http://localhost:15672',
 Grafana:process.env.GRAFANA_URL||'http://localhost:3000/d/oficina-fase4',
 Prometheus:process.env.PROMETHEUS_URL||'http://localhost:9090/targets',
};
const EXPECT={
 OS:[['POST','/api/os'],['GET','/api/os/{id}']],
 Billing:[['GET','/api/orcamentos/{id}'],['PATCH','/api/orcamentos/{id}/aprovar'],['PATCH','/api/orcamentos/{id}/pagar']],
 Execution:[['GET','/api/execucoes/{id}'],['PATCH','/api/execucoes/{id}/iniciar'],['PATCH','/api/execucoes/{id}/finalizar']],
 Saga:[['GET','/api/sagas/ordens/{ordemId}'],['POST','/api/sagas/ordens/{ordemId}/eventos']],
};
export {links,EXPECT};
export async function preflight({showBrowser=false}={}){
  const browser=await chromium.launch({headless:!showBrowser});
  const context=await browser.newContext({viewport:{width:1500,height:900},acceptDownloads:true});
  const pages={};const errors=[];
  try {
    for(const [name,url] of Object.entries(links)){
      const page=await context.newPage();pages[name]=page;
      try{
        const r=await page.goto(url,{waitUntil:'domcontentloaded',timeout:15000});
        if(r && r.status()>=400) throw Error(`HTTP ${r.status()}`);
        if(EXPECT[name]){
          await page.locator('.swagger-ui .opblock').first().waitFor({timeout:12000});
          const actual=await page.locator('.swagger-ui .opblock').evaluateAll(blocks=>blocks.map(b=>({method:(b.querySelector('.opblock-summary-method')?.textContent||'').trim().toUpperCase(), route:(b.querySelector('.opblock-summary-path')?.getAttribute('data-path')||b.querySelector('.opblock-summary-path')?.textContent||'').trim().split(/\s/)[0]})));
          for(const [method,route] of EXPECT[name]) if(!actual.some(v=>v.method===method&&v.route===route)) errors.push(`${name}: operação ausente: ${method} ${route}`);
        }
        console.log(`OK: ${name} (${new URL(url).origin})`);
      }catch(e){errors.push(`${name}: ${e.message}`);console.log(`PENDENTE: ${name}`);}
    }
    for(const p of Object.values(pages)) await p.close().catch(()=>{});
  }finally {await browser.close();}
  const files=['docs/architecture/fase4/README.md','fase4-priority4/saga-orchestrator/src/test/resources/features/saga_ordem_servico.feature'];
  for(const rel of files){const isThere=existsSync(path.join(path.resolve(ROOT,process.env.REPO_ROOT||'..'),rel));console.log(`${isThere?'OK':'PENDENTE'} arquivo ${rel}`); if(!isThere)errors.push(`arquivo não encontrado: ${rel} (configure REPO_ROOT para a raiz do projeto original)`);}
  console.log(`Narração extraída: ${getScenes(process.env.VIDEO_MODE||'ensaio').length} cenas, ${getTotalBudget(getScenes(process.env.VIDEO_MODE||'ensaio'))} segundos planejados`);
  return errors;
}
if(process.argv[1]&&process.argv[1].replaceAll('\\','/').endsWith('inspect.mjs')) {
 preflight().then(errors=>{if(errors.length){console.error('\nBLOQUEIOS REAIS (não simular):\n'+errors.map(x=>' - '+x).join('\n'));process.exitCode=1;}else console.log('\nPré-inspeção aprovada. NENHUMA operação POST/PATCH foi executada.');}).catch(e=>{console.error(e.message);process.exitCode=1;});
}
