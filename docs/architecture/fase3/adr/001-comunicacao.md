# ADR 001 — Comunicação síncrona REST

**Status:** Aceita

Cliente, Traefik, Lambda de autenticação e aplicação usam HTTPS/REST. É compatível com
as APIs existentes, Swagger e o ForwardAuth. Notificações e integrações que precisarem
de retentativa deverão evoluir para eventos assíncronos, evitando bloquear transações
de ordens de serviço.
