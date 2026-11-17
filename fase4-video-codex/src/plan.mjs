import 'dotenv/config';
import {getScenes,getTotalBudget} from './script.mjs';
const scenes=getScenes(process.env.VIDEO_MODE||'ensaio');
let elapsed=0;
console.log(`\nPLANO DE GRAVAÇÃO — FASE 4 (${process.env.VIDEO_MODE||'ensaio'})\n`);
for (const s of scenes){
  const end=elapsed+s.budgetSeconds;
  const fmt=x=>`${String(Math.floor(x/60)).padStart(2,'0')}:${String(x%60).padStart(2,'0')}`;
  console.log(`${String(s.number).padStart(2,'0')} | ${fmt(elapsed)}–${fmt(end)} | ${s.title}\n   Voz: ${s.voice}\n`);
  elapsed=end;
}
console.log(`Tempo programado: ${getTotalBudget(scenes)} s (${Math.floor(elapsed/60)}:${String(elapsed%60).padStart(2,'0')}). Limite: 15:00.`);
console.log('As demonstrações dependem de respostas reais; esperas assíncronas podem prolongar a gravação.');
if((process.env.VIDEO_MODE||'ensaio')==='ensaio') console.log('CENA 12 EM MODO ENSAIO: evidências finais NÃO comprovadas; este vídeo não substitui o requisito de entrega final.');
