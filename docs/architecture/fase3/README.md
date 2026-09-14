# Arquitetura — Fase 3

## Demonstração da entrega

**Vídeo demonstrativo da Fase 3 (AWS, Lambda, EKS, RDS, Datadog):**
https://www.awesomescreenshot.com/video/56501252?key=47f588f269fe75963aa5f637b94e1072

**Vimeo:** https://vimeo.com/1226680429?share=copy&fl=sv&fe=ci

## Visão geral

Esta fase amplia o MVP da oficina mecânica para um cenário corporativo com autenticação
serverless, gateway de entrada com Traefik, orquestração em Kubernetes na AWS (EKS), banco
managed em RDS PostgreSQL e observabilidade com Datadog. O objetivo é consolidar a solução
em um ambiente de produção orientado a operação, observabilidade e segurança.

- [Componentes](componentes.md)
- [Sequências](sequencias.md)
- [Autenticação por CPF e validação de JWT no gateway](autenticacao.md)
- [Modelo relacional](modelo-relacional.md)
- [RFC 001 — AWS](rfc/001-aws.md)
- [RFC 002 — PostgreSQL gerenciado](rfc/002-postgresql-rds.md)
- [RFC 003 — Autenticação por CPF](rfc/003-autenticacao-cpf.md)
- [ADR 001 — Comunicação](adr/001-comunicacao.md)
- [ADR 002 — HPA](adr/002-hpa.md)
- [ADR 003 — Traefik](adr/003-traefik.md)

As decisões aprovadas são refletidas no código da aplicação e nos projetos exportáveis
em `fase3-repositories/`. URLs, contas, domínios e evidências de deploy serão incluídos
após a publicação nos ambientes AWS.
