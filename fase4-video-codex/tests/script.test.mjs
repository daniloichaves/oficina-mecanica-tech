import {test} from 'node:test';
import assert from 'node:assert/strict';
import {getScenes,getTotalBudget,findField,findRootId,requireNumber,requireUUID} from '../src/script.mjs';
test('13 cenas e 13:40 de orçamento',()=>{const s=getScenes('ensaio');assert.equal(s.length,13); assert.equal(getTotalBudget(s),820);assert.ok(s.every(x=>x.voice.length>40));});
test('não fala sobre evidências não comprovadas no ensaio',()=>{const s=getScenes('ensaio');assert.match(s[11].voice,/Neste ensaio local/);assert.doesNotMatch(s[11].voice,/rollout Kubernetes e a imagem/);});
test('detecta campos reais sem ler histórico antigo',()=>{const obj={historicoEtapas:[{status:'PENDENTE'}],estadoAtual:'FINALIZADA',dados:{orcamentoId:23,execucaoId:'123e4567-e89b-12d3-a456-426614174000'}};assert.equal(findField(obj,['orcamentoId']),23);assert.equal(findField(obj,['estadoAtual']), 'FINALIZADA');});
test('valida os IDs sem presumir sequência',()=>{assert.equal(requireNumber(findRootId({id:42}),'OS_A'),'42');assert.equal(requireUUID('123e4567-e89b-12d3-a456-426614174000','EXEC_A'),'123e4567-e89b-12d3-a456-426614174000');assert.throws(()=>requireUUID('1','EXEC_A'));});
