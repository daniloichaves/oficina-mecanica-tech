/**
 * Automação da interface Swagger UI: somente operação especificada pelo roteiro.
 * Captura a resposta real da rede e mantém a resposta renderizada na página.
 * Não constrói chamadas de negócio fora dos botões "Execute" do próprio Swagger.
 */
export class SwaggerOperator {
  constructor(page, label) {this.page=page;this.label=label;}
  async operation(method,route){
    const page=this.page;
    const blocks=page.locator('.swagger-ui .opblock');
    await blocks.first().waitFor({state:'visible',timeout:18000});
    const count=await blocks.count();
    for(let i=0;i<count;i++){
      const block=blocks.nth(i);
      const foundMethod=(await block.locator('.opblock-summary-method').first().innerText().catch(()=>'' )).trim().toUpperCase();
      const text=(await block.locator('.opblock-summary-path').first().getAttribute('data-path').catch(()=>null)) || (await block.locator('.opblock-summary-path').first().innerText().catch(()=>''));
      const clean=text.trim().split(/\s/)[0].replace(/\s+/g,'');
      if(foundMethod===method.toUpperCase() && clean===route) return block;
    }
    throw new Error(`${this.label}: operação ${method} ${route} não encontrada no Swagger. Adapte os seletores/rota ao OpenAPI REAL.`);
  }
  async run(method,route,{params={},body,expect}={}){
    const page=this.page;
    const block=await this.operation(method,route);
    await block.scrollIntoViewIfNeeded();
    const toggle=block.locator('.opblock-summary').first();
    const isClosed=!(await block.locator('.opblock-body').isVisible().catch(()=>false));
    if(isClosed) await toggle.click();
    await block.locator('.opblock-body').waitFor({state:'visible',timeout:10000});
    const tryButton=block.locator('button.try-out__btn').first();
    if(await tryButton.count() && /Try it out/i.test(await tryButton.innerText())) await tryButton.click();

    for(const [name,value] of Object.entries(params)){
      await block.locator('tr[data-param-name]').first().waitFor({state:'visible',timeout:10000});
      let row=block.locator(`tr[data-param-name="${name}"]`).first();
      if(!(await row.count())) {
        const rows=block.locator('tr[data-param-name]');
        for(let i=0;i<await rows.count();i++){
          const text=(await rows.nth(i).locator('.parameter__name').first().textContent().catch(()=>'' )).trim();
          if(text===name || text.startsWith(name+' ') || text.startsWith(name+'*')){row=rows.nth(i);break;}
        }
      }
      if(!(await row.count())) throw new Error(`${this.label}: parâmetro ${name} ausente em ${method} ${route}`);
      const input=row.locator('input:not([type=hidden]),textarea,select').first();
      if(!(await input.count())) throw new Error(`${this.label}: controle do parâmetro ${name} ausente`);
      const tag=(await input.evaluate(el=>el.tagName)).toUpperCase();
      if(tag==='SELECT') await input.selectOption({label:String(value)}).catch(()=>input.selectOption(String(value)));
      else await input.fill(String(value));
    }
    if(body!==undefined){
      const input=block.locator('textarea.body-param__text, .body-param textarea, textarea').first();
      if(!(await input.count())) throw new Error(`${this.label}: requestBody editável não encontrado em ${method} ${route}`);
      await input.fill(typeof body==='string'?body:JSON.stringify(body,null,2));
    }
    const execute=block.locator('button.execute, .execute-wrapper button').first();
    await execute.waitFor({state:'visible',timeout:10000});
    // A resposta precisa corresponder exatamente à chamada disparada neste clique.
    const templateRegex = new RegExp('(?:^|/)' + route.replace(/^\//,'').split('/').map(segment=> /^\{.*\}$/.test(segment)?'[^/]+':segment.replace(/[.*+?^${}()|[\]\\]/g,'\\$&')).join('/')+'/?$');
    const waiter=page.waitForResponse(response => {
      try {return response.request().method()===method.toUpperCase() && templateRegex.test(new URL(response.url()).pathname);}catch{return false;}
    },{timeout:45000});
    await execute.click();
    const response=await waiter;
    const status=response.status();
    let data;const text=await response.text().catch(()=>'');
    try{data=JSON.parse(text);}catch{data=text;}
    // Mostrar o resultado no Swagger, não apenas o objeto obtido via Playwright.
    const responseTable=block.locator('.responses-wrapper .live-responses-table').first();
    await responseTable.waitFor({state:'visible',timeout:10000});
    await responseTable.scrollIntoViewIfNeeded();
    let statusText='';
    for(let i=0;i<50;i++){
      statusText=(await responseTable.locator('td.response-col_status:not(.col_header)').first().textContent().catch(()=>''))||'';
      if(statusText.includes(String(status)))break;
      await page.waitForTimeout(200);
    }
    if(!statusText.includes(String(status)))throw new Error(`${this.label} ${method} ${route}: HTTP real não renderizado`);
    if(expect!==undefined && status!==expect) throw new Error(`${this.label} ${method} ${route}: HTTP ${status} (esperado ${expect}). Verifique a resposta real no Swagger.`);
    if(status>=400) throw new Error(`${this.label} ${method} ${route}: falha HTTP ${status}; gravação interrompida, sem simulação`);
    await page.waitForTimeout(850);
    return {status,data};
  }
}
