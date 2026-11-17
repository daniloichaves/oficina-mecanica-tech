import fs from 'node:fs';
import path from 'node:path';
import {fileURLToPath} from 'node:url';

export const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
export const SCRIPT_PATH = path.join(ROOT, 'ROTEIRO-VIDEO.md');
export const BUDGETS = [40,70,30,40,50,50,90,60,90,120,40,110,30];
export const DESCRIPTIONS = [
  'Apresentação','Arquitetura','Dashboard inicial','Criar OS_A',
  'Orçamento automático','Aprovar e pagar','Executar e finalizar','Criar OS_B',
  'Falha e compensação','Mensageria e métricas','Histórico e logs',
  'Testes, qualidade e deploy','Encerramento'
];

export function parseScript(source) {
  const result=[];
  const headers=[...source.matchAll(/^## (\d{1,2})\. ([^\n]+)$/gm)];
  for (let i=0; i<headers.length; i++) {
    const match=headers[i];
    const order=Number(match[1]);
    const body=source.slice(match.index+match[0].length, headers[i+1]?.index??source.length);
    const voice=body.split(/\r?\n/).filter(line=>/^>\s?/.test(line)).map(line=>line.replace(/^>\s?/,''));
    result.push({number:order,title:match[2],budgetSeconds:BUDGETS[order-1],voice:voice.join(' ').replace(/\s+/g,' ').trim(),body});
  }
  return result;
}
export function getScenes(mode='ensaio') {
  if(!['ensaio','final'].includes(mode)) throw Error('VIDEO_MODE deve ser ensaio ou final');
  const raw=fs.readFileSync(SCRIPT_PATH,'utf8');
  const scenes=parseScript(raw);
  if(scenes.length!==13 || scenes.some((s,i)=>s.number!==i+1)) throw Error('Não foi possível extrair as 13 cenas do roteiro original');
  // O trecho 12 é explicitamente condicional no arquivo original.
  if(mode==='ensaio') scenes[11].voice='O repositório contém cenários BDD e configuração dos pipelines. Neste ensaio local, ainda não demonstramos a execução das verificações de qualidade nem o deploy automatizado em Kubernetes.';
  if(scenes.some(s=>!s.voice)) throw Error('Uma cena do roteiro está sem narração');
  return scenes;
}
export function getTotalBudget(scenes) {return scenes.reduce((sum,s)=>sum+s.budgetSeconds,0);}

export function findField(data,names,maxDepth=5) {
  if(maxDepth<0||data===null||typeof data!=='object') return undefined;
  for(const name of names) if(Object.hasOwn(data,name) && data[name]!==null) return data[name];
  if(Array.isArray(data)) {for(const item of data){const v=findField(item,names,maxDepth-1); if(v!==undefined) return v;}}
  else for(const [key,v] of Object.entries(data)) {
    if(key==='historicoEtapas') continue; // evita pegar ID ou estado antigo
    const found=findField(v,names,maxDepth-1); if(found!==undefined) return found;
  }
  return undefined;
}
export function currentState(data) {return findField(data,['estadoAtual','statusAtual','estado','status','situacao'],3);}
export function findRootId(data) {
  if(data&&typeof data==='object') return data.id??data.ordemId??data.data?.id??data.result?.id;
}
export function requireNumber(value,label) {
  if(value===undefined || !/^\d+$/.test(String(value))) throw Error(`${label}: resposta não trouxe ID numérico`);
  return String(value);
}
export function requireUUID(value,label) {
  if(typeof value!=='string'|| !/^[\da-f]{8}-[\da-f]{4}-[\da-f]{4}-[\da-f]{4}-[\da-f]{12}$/i.test(value)) throw Error(`${label}: UUID não encontrado na Saga`);
  return value;
}
