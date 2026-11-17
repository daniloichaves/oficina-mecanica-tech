# Dashboard local — Fase 4

Grafana: http://localhost:3000/d/oficina-fase4 — usuário `admin`, senha local `oficina-local`.
Prometheus: http://localhost:9090/targets.

Na raiz do projeto, com Docker Desktop em execução:

```powershell
$env:PATH = 'C:\Program Files\Docker\Docker\resources\bin;' + $env:PATH
docker compose -f docker-compose.fase4.yml -f docker-compose.fase4-observability.yml up -d
```

O Prometheus coleta `/actuator/prometheus` dos quatro serviços a cada 5 segundos. O Grafana recebe automaticamente o datasource e o dashboard pelos arquivos versionados, conforme a [documentação oficial de provisioning](https://grafana.com/docs/grafana/latest/administration/provisioning/).

O dashboard mostra disponibilidade, contadores de Sagas, requisições de negócio, latência média, erros HTTP, memória e CPU. Aguarde pelo menos um minuto de coleta para os gráficos de taxa. Sem requisições novas, a taxa fica em zero; métricas ainda não criadas podem aparecer como `No data`. Os contadores de negócio representam o processo atual e reiniciam junto com a aplicação. Métricas não são registros persistentes do estado das ordens.

Para gravar, abra o dashboard, execute novas requisições nos Swagger e acompanhe a atualização. Use o histórico da Saga e os logs para relacionar as etapas de uma OS. Esta configuração coleta métricas: não instala Datadog nem implementa traces distribuídos ou agregação de logs.

As portas do Grafana e do Prometheus ficam vinculadas apenas ao computador local. Seus dados ficam em volumes Docker; não é necessário recriar os quatro serviços para adicionar o dashboard.
