import 'dotenv/config';
import {chromium} from 'playwright';
import {SwaggerOperator} from './swagger.mjs';
import {links} from './inspect.mjs';

const browser=await chromium.launch({headless:true});
try {
  const page=await browser.newPage({viewport:{width:1500,height:900}});
  await page.goto(links.Saga);
  const spec=await (await page.request.get('http://localhost:8084/api-docs')).json();
  const event=spec.paths['/api/sagas/ordens/{ordemId}/eventos'].post;
  if(!event.parameters.some(p=>p.name==='evento'&&p.in==='query'))throw Error('Contrato do evento difere do formulário esperado');
  const saga=new SwaggerOperator(page,'Saga');
  const block=await saga.operation('POST','/api/sagas/ordens/{ordemId}/eventos');
  await block.locator('.opblock-summary').click();
  await block.locator('button.try-out__btn').click();
  const parameters=await block.locator('tr[data-param-name]').allTextContents();
  if(!parameters.some(x=>x.includes('evento'))||!parameters.some(x=>x.includes('ordemId')))throw Error('Formulário de falha sem parâmetros');
  console.log('OK contrato e formulário: ordemId path; evento query; sem executar POST');
  await page.goto(links.OS);
  const os=new SwaggerOperator(page,'OS');
  const listed=await os.run('GET','/api/os');
  if(!Array.isArray(listed.data))throw Error('Listagem de OS inválida');
  if(listed.data.length) {
    const id=listed.data[0].id;
    await os.run('GET','/api/os/{id}',{params:{id}});
    console.log('OK seletor do Swagger, preenchimento e resposta real GET');
  }
  await page.goto(links.RabbitMQ);
  await page.locator('input[name=username]').fill(process.env.RABBIT_USER);
  await page.locator('input[type=password]').fill(process.env.RABBIT_PASSWORD);
  await page.locator('input[type=submit]').click();
  const exchanges=page.getByRole('link',{name:'Exchanges',exact:true});
  await exchanges.waitFor(); await exchanges.click();
  await page.getByRole('link',{name:'oficina.events',exact:true}).click();
  await page.getByRole('link',{name:/Queues and Streams/}).click();
  await page.getByRole('link',{name:'saga-orchestrator.events',exact:true}).click();
  console.log('OK RabbitMQ: login, exchange e fila');
  await page.goto(links.Grafana);
  const user=page.locator('input[name=user]');
  await user.waitFor();await user.fill(process.env.GRAFANA_USER);
  await page.locator('input[type=password]').fill(process.env.GRAFANA_PASSWORD);
  await page.locator('button[type=submit]').click();
  await page.getByText('Serviços disponíveis / 4',{exact:true}).first().waitFor({timeout:30000});
  const url=new URL(links.Grafana);url.searchParams.set('from','now-15m');url.searchParams.set('to','now');url.searchParams.set('refresh','5s');
  await page.goto(url.href);
  await page.getByText('Serviços disponíveis / 4',{exact:true}).first().waitFor({timeout:30000});
  console.log('Grafana controles:',JSON.stringify(await page.getByRole('button').allTextContents()));
  await page.getByRole('button',{name:/Last 15 minutes/}).first().waitFor();
  const targets=await (await page.request.get('http://localhost:9090/api/v1/targets')).json();
  const active=targets.data.activeTargets;
  if(active.length!==4||active.some(t=>t.health!=='up'))throw Error('Prometheus não tem quatro targets UP');
  await page.goto(links.Prometheus);
  console.log('OK Grafana: dashboard real e período de 15m, refresh 5s; Prometheus: quatro UP');
} finally {await browser.close();}
