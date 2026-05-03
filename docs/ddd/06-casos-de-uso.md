# Casos de Uso × Services × Endpoints

Rastreabilidade entre os Casos de Uso modelados no Event Storming, os Services da camada de Application e os endpoints REST expostos.

## 1. Bounded Context: Atendimento

| Caso de Uso | Service | Endpoint |
|-------------|---------|----------|
| Cadastrar Cliente | `ClienteService.criar` | `POST /api/clientes` |
| Listar Clientes | `ClienteService.listar` | `GET /api/clientes` |
| Consultar Cliente por id | `ClienteService.buscarPorId` | `GET /api/clientes/{id}` |
| Consultar Cliente por documento | `ClienteService.buscarPorCpfCnpj` | `GET /api/clientes/cpf/{cpfCnpj}` |
| Atualizar Cliente | `ClienteService.atualizar` | `PUT /api/clientes/{id}` |
| Remover Cliente | `ClienteService.remover` | `DELETE /api/clientes/{id}` |
| Cadastrar Veículo | `VeiculoService.criar` | `POST /api/veiculos` |
| Listar Veículos | `VeiculoService.listar` | `GET /api/veiculos` |
| Consultar Veículo por id | `VeiculoService.buscarPorId` | `GET /api/veiculos/{id}` |
| Consultar Veículo por placa | `VeiculoService.buscarPorPlaca` | `GET /api/veiculos/placa/{placa}` |
| Listar Veículos do Cliente | `VeiculoService.listarPorCliente` | `GET /api/veiculos/cliente/{clienteId}` |
| Atualizar Veículo | `VeiculoService.atualizar` | `PUT /api/veiculos/{id}` |
| Remover Veículo | `VeiculoService.remover` | `DELETE /api/veiculos/{id}` |

## 2. Bounded Context: Catálogo de Serviços

| Caso de Uso | Service | Endpoint |
|-------------|---------|----------|
| Cadastrar Serviço | `ServicoService.criar` | `POST /api/servicos` |
| Listar Serviços | `ServicoService.listar` | `GET /api/servicos` |
| Consultar Serviço | `ServicoService.buscarPorId` | `GET /api/servicos/{id}` |
| Atualizar Serviço | `ServicoService.atualizar` | `PUT /api/servicos/{id}` |
| Remover Serviço | `ServicoService.remover` | `DELETE /api/servicos/{id}` |

## 3. Bounded Context: Estoque / Peças

| Caso de Uso | Service | Endpoint |
|-------------|---------|----------|
| Cadastrar Peça | `PecaService.criar` | `POST /api/pecas` |
| Listar Peças | `PecaService.listar` | `GET /api/pecas` |
| Consultar Peça | `PecaService.buscarPorId` | `GET /api/pecas/{id}` |
| Listar Peças com estoque baixo | `PecaService.listarEstoqueBaixo` | `GET /api/pecas/estoque-baixo` |
| Atualizar Peça | `PecaService.atualizar` | `PUT /api/pecas/{id}` |
| Atualizar Estoque | `PecaService.atualizarEstoque` | `PATCH /api/pecas/{id}/estoque` |
| Remover Peça | `PecaService.remover` | `DELETE /api/pecas/{id}` |

## 4. Bounded Context: Ordem de Serviço

| Caso de Uso | Service | Endpoint |
|-------------|---------|----------|
| Criar OS | `OrdemServicoService.criar` | `POST /api/ordens-servico` |
| Listar OS | `OrdemServicoService.listar` | `GET /api/ordens-servico` |
| Consultar OS | `OrdemServicoService.buscarPorId` | `GET /api/ordens-servico/{id}` |
| Listar OS por Cliente | `OrdemServicoService.listarPorCliente` | `GET /api/ordens-servico/cliente/{clienteId}` |
| Listar OS por Veículo | `OrdemServicoService.listarPorVeiculo` | `GET /api/ordens-servico/veiculo/{veiculoId}` |
| Listar OS por Status | `OrdemServicoService.listarPorStatus` | `GET /api/ordens-servico/status/{status}` |
| Iniciar Diagnóstico | `OrdemServicoService.iniciarDiagnostico` → `OrdemServico.iniciarDiagnostico()` | `PATCH /api/ordens-servico/{id}/iniciar-diagnostico` |
| Concluir Diagnóstico | `OrdemServicoService.concluirDiagnostico` → `OrdemServico.concluirDiagnostico()` | `PATCH /api/ordens-servico/{id}/concluir-diagnostico` |
| Aprovar Orçamento | `OrdemServicoService.aprovarOrcamento` → `OrdemServico.aprovarOrcamento()` | `PATCH /api/ordens-servico/{id}/aprovar-orcamento` |
| Finalizar OS | `OrdemServicoService.finalizar` → `OrdemServico.finalizar()` | `PATCH /api/ordens-servico/{id}/finalizar` |
| Entregar Veículo | `OrdemServicoService.entregar` → `OrdemServico.entregar()` | `PATCH /api/ordens-servico/{id}/entregar` |

## 5. Bounded Context: Monitoramento

| Caso de Uso | Endpoint |
|-------------|----------|
| Consultar Tempo Médio de Execução | `GET /api/metricas/tempo-medio-execucao` |

## 6. Bounded Context: Identidade & Acesso

| Caso de Uso | Endpoint |
|-------------|----------|
| Login (gerar JWT) | `POST /api/auth/login` |
| Proteção de endpoints administrativos | Filtro JWT em `infrastructure.security` |

## 7. Cobertura do Event Storming

Todos os eventos de domínio mapeados em `02-event-storming-os.md` e `03-event-storming-pecas.md` são alcançáveis a partir de pelo menos um endpoint listado acima — exceto os marcados como **🟥 Hotspot**, que dependem de decisões de produto antes de virarem casos de uso.
