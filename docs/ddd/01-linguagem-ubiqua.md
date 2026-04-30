# Linguagem Ubíqua

Glossário oficial dos termos do domínio de Oficina Mecânica. Esses são os **únicos** nomes permitidos em código, conversas, documentação e API. Sinônimos comuns que **não devem ser usados** estão marcados como ⛔.

## Atores

| Termo | Definição | Sinônimos proibidos |
|-------|-----------|---------------------|
| **Cliente** | Pessoa física ou jurídica proprietária de um veículo, identificada por CPF ou CNPJ. | ⛔ Consumidor, freguês, dono |
| **Atendente** | Funcionário responsável por receber o veículo, cadastrar Cliente/Veículo e abrir a OS. | ⛔ Recepcionista, vendedor |
| **Mecânico** | Funcionário que realiza diagnóstico e executa serviços. | ⛔ Técnico, operador |
| **Administrador** | Usuário com acesso a APIs administrativas (catálogo de serviços, peças, métricas). Autenticado via JWT. | ⛔ Gerente, root |

## Conceitos do Domínio

| Termo | Definição | Onde vive no código |
|-------|-----------|---------------------|
| **Veículo** | Bem do Cliente que será atendido. Identificado de forma única por **Placa**. | `domain.entities.Veiculo` |
| **Ordem de Serviço (OS)** | Documento que registra atendimento: cliente, veículo, itens de serviço, itens de peça, status, orçamento e datas. É o **Aggregate Root** do contexto de execução. | `domain.entities.OrdemServico` |
| **Serviço** | Item do catálogo executável pela oficina (descrição, valor, tempo estimado). | `domain.entities.Servico` |
| **Peça / Insumo** | Item físico do estoque consumido durante a execução de uma OS. | `domain.entities.Peca` |
| **Item de Serviço** | Serviço aplicado a uma OS específica, com quantidade. | `domain.entities.ItemServico` |
| **Item de Peça** | Peça aplicada a uma OS específica, com quantidade. | `domain.entities.ItemPeca` |
| **Orçamento** | Valor total calculado a partir dos itens (serviços + peças) da OS, sujeito à aprovação do Cliente. | `OrdemServico.calcularOrcamento()` |
| **Diagnóstico** | Etapa em que o Mecânico avalia o veículo e define os itens (serviços/peças) que comporão o orçamento. | Estados `EM_DIAGNOSTICO` |
| **Estoque** | Quantidade disponível de uma Peça. Não pode ficar negativa. | `Peca.quantidadeEstoque` |
| **Estoque Baixo** | Condição em que a quantidade de uma Peça atinge ou cruza o limite mínimo configurado. | `GET /pecas/estoque-baixo` |

## Status da Ordem de Serviço

Os estados refletem `domain.entities.StatusOrdemServico`:

| Estado | Significado |
|--------|-------------|
| **RECEBIDA** | OS foi aberta; veículo entregue à oficina aguardando diagnóstico. |
| **EM_DIAGNOSTICO** | Mecânico avaliando o veículo. |
| **AGUARDANDO_APROVACAO** | Diagnóstico concluído; orçamento gerado e enviado ao Cliente. |
| **EM_EXECUCAO** | Cliente aprovou o orçamento; serviços sendo executados. |
| **FINALIZADA** | Execução concluída; veículo pronto para entrega. |
| **ENTREGUE** | Veículo devolvido ao Cliente; OS encerrada. |

> Não existe estado `REJEITADA` no MVP atual. O fluxo de rejeição é um **hotspot** documentado em `02-event-storming-os.md`.

## Value Objects

| Termo | Regra | Onde vive |
|-------|-------|-----------|
| **CPF/CNPJ** | Documento brasileiro do Cliente, validado por dígito verificador. | `domain.valueobjects.CpfCnpj` |
| **Placa** | Identificador único de veículo, formato Mercosul ou antigo. | `domain.valueobjects.Placa` |
| **Valor Monetário** | `BigDecimal` com 2 casas decimais; nunca negativo. | Atributos `valor`, `valorTotal` |

## Verbos do domínio (linguagem dos comandos)

Sempre usar estes verbos no código e na conversa:

- **Cadastrar** Cliente / Veículo / Serviço / Peça
- **Abrir / Criar** uma OS
- **Iniciar Diagnóstico** / **Concluir Diagnóstico**
- **Aprovar Orçamento**
- **Finalizar** OS
- **Entregar** veículo
- **Atualizar Estoque** (entrada ou consumo)

⛔ Evitar: "fechar OS", "salvar OS", "processar OS" — são genéricos demais e escondem a regra.
