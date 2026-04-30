# Casos de Uso × Services × Endpoints

Rastreabilidade entre os Casos de Uso modelados no Event Storming, os Services da camada de Application e os endpoints REST expostos.

## 1. Bounded Context: Atendimento

| Caso de Uso | Service | Endpoint |
|-------------|---------|----------|
| Cadastrar Cliente | `ClienteService.criar` | `POST /clientes` |
| Listar Clientes | `ClienteService.listar` | `GET /clientes` |
| Consultar Cliente por id | `ClienteService.buscarPorId` | `GET /clientes/{id}` |
| Consultar Cliente por documento | `ClienteService.buscarPorCpfCnpj` | `GET /clientes/cpf/{cpfCnpj}` |
| Atualizar Cliente | `ClienteService.atualizar` | `PUT /clientes/{id}` |
| Remover Cliente | `ClienteService.remover` | `DELETE /clientes/{id}` |
| Cadastrar Veículo | `VeiculoService.criar` | `POST /veiculos` |
| Listar Veículos | `VeiculoService.listar` | `GET /veiculos` |
| Consultar Veículo por id | `VeiculoService.buscarPorId` | `GET /veiculos/{id}` |
| Consultar Veículo por placa | `VeiculoService.buscarPorPlaca` | `GET /veiculos/placa/{placa}` |
| Listar Veículos do Cliente | `VeiculoService.listarPorCliente` | `GET /veiculos/cliente/{clienteId}` |
| Atualizar Veículo | `VeiculoService.atualizar` | `PUT /veiculos/{id}` |
| Remover Veículo | `VeiculoService.remover` | `DELETE /veiculos/{id}` |

## 2. Bounded Context: Catálogo de Serviços

| Caso de Uso | Service | Endpoint |
|-------------|---------|----------|
| Cadastrar Serviço | `ServicoService.criar` | `POST /servicos` |
| Listar Serviços | `ServicoService.listar` | `GET /servicos` |
| Consultar Serviço | `ServicoService.buscarPorId` | `GET /servicos/{id}` |
| Atualizar Serviço | `ServicoService.atualizar` | `PUT /servicos/{id}` |
| Remover Serviço | `ServicoService.remover` | `DELETE /servicos/{id}` |

## 3. Bounded Context: Estoque / Peças

| Caso de Uso | Service | Endpoint |
|-------------|---------|----------|
| Cadastrar Peça | `PecaService.criar` | `POST /pecas` |
| Listar Peças | `PecaService.listar` | `GET /pecas` |
| Consultar Peça | `PecaService.buscarPorId` | `GET /pecas/{id}` |
| Listar Peças com estoque baixo | `PecaService.listarEstoqueBaixo` | `GET /pecas/estoque-baixo` |
| Atualizar Peça | `PecaService.atualizar` | `PUT /pecas/{id}` |
| Atualizar Estoque | `PecaService.atualizarEstoque` | `PATCH /pecas/{id}/estoque` |
| Remover Peça | `PecaService.remover` | `DELETE /pecas/{id}` |

## 4. Bounded Context: Ordem de Serviço

| Caso de Uso | Service | Endpoint |
|-------------|---------|----------|
| Criar OS | `OrdemServicoService.criar` | `POST /ordens-servico` |
| Listar OS | `OrdemServicoService.listar` | `GET /ordens-servico` |
| Consultar OS | `OrdemServicoService.buscarPorId` | `GET /ordens-servico/{id}` |
| Listar OS por Cliente | `OrdemServicoService.listarPorCliente` | `GET /ordens-servico/cliente/{clienteId}` |
| Listar OS por Veículo | `OrdemServicoService.listarPorVeiculo` | `GET /ordens-servico/veiculo/{veiculoId}` |
| Listar OS por Status | `OrdemServicoService.listarPorStatus` | `GET /ordens-servico/status/{status}` |
| Iniciar Diagnóstico | `OrdemServicoService.iniciarDiagnostico` → `OrdemServico.iniciarDiagnostico()` | `PATCH /ordens-servico/{id}/iniciar-diagnostico` |
| Concluir Diagnóstico | `OrdemServicoService.concluirDiagnostico` → `OrdemServico.concluirDiagnostico()` | `PATCH /ordens-servico/{id}/concluir-diagnostico` |
| Aprovar Orçamento | `OrdemServicoService.aprovarOrcamento` → `OrdemServico.aprovarOrcamento()` | `PATCH /ordens-servico/{id}/aprovar-orcamento` |
| Finalizar OS | `OrdemServicoService.finalizar` → `OrdemServico.finalizar()` | `PATCH /ordens-servico/{id}/finalizar` |
| Entregar Veículo | `OrdemServicoService.entregar` → `OrdemServico.entregar()` | `PATCH /ordens-servico/{id}/entregar` |

## 5. Bounded Context: Monitoramento

| Caso de Uso | Endpoint |
|-------------|----------|
| Consultar Tempo Médio de Execução | `GET /metricas/tempo-medio-execucao` |

## 6. Bounded Context: Identidade & Acesso

| Caso de Uso | Endpoint |
|-------------|----------|
| Login (gerar JWT) | `POST /auth/login` |
| Proteção de endpoints administrativos | Filtro JWT em `infrastructure.config` |

## 7. Cobertura do Event Storming

Todos os eventos de domínio mapeados em `02-event-storming-os.md` e `03-event-storming-pecas.md` são alcançáveis a partir de pelo menos um endpoint listado acima — exceto os marcados como **🟥 Hotspot**, que dependem de decisões de produto antes de virarem casos de uso.
