# language: pt
Funcionalidade: Orquestração da Saga de Ordem de Serviço
  Para garantir consistência distribuída entre OS Service, Billing Service e Execution Service
  Como orquestrador da Saga
  Devo coordenar as transições de estado e executar compensações em caso de rejeição ou falha

  Cenário: Fluxo completo de OS até finalização com sucesso
    Dado que uma nova ordem de serviço 501 foi aberta para o cliente "cli-501"
    Quando o orçamento da ordem 501 for aprovado
    E a execução da ordem 501 for finalizada
    Então o status final da saga da ordem 501 deve ser "FINALIZADA"
    E o histórico da saga da ordem 501 deve conter "ORCAMENTO_GERADO" e "EXECUCAO_INICIADA" e "SAGA_FINALIZADA"

  Cenário: Compensação da Saga quando o orçamento é rejeitado
    Dado que uma nova ordem de serviço 502 foi aberta para o cliente "cli-502"
    Quando o orçamento da ordem 502 for rejeitado
    Então o status final da saga da ordem 502 deve ser "CANCELADA"
    E o histórico da saga da ordem 502 deve conter "COMPENSACAO_OS_CANCELADA"

  Cenário: Compensação automática quando ocorre falha na execução
    Dado que uma nova ordem de serviço 503 foi aberta para o cliente "cli-503"
    Quando ocorrer uma falha na etapa de execução da ordem 503
    Então o status final da saga da ordem 503 deve ser "COMPENSADA"
    E o histórico da saga da ordem 503 deve conter "COMPENSACAO_ORCAMENTO_ESTORNADO" e "COMPENSACAO_OS_CANCELADA"
