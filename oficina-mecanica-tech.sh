#!/bin/bash

# Script de gerenciamento do projeto Oficina Mecânica Tech
# Funções: start, stop, logs, clean

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Diretório do script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Função para imprimir mensagens coloridas
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

docker_compose() {
    if command -v docker-compose &> /dev/null; then
        docker-compose "$@"
    else
        docker compose "$@"
    fi
}

# Função start: Inicia os containers Docker
start() {
    print_info "Iniciando containers Docker..."
    docker_compose up -d
    
    print_info "Aguardando serviços ficarem prontos..."
    sleep 5
    
    print_info "Verificando status dos containers..."
    docker_compose ps
    
    print_success "Containers iniciados com sucesso!"
    print_info "API disponível em: http://localhost:8080"
    print_info "Swagger UI disponível em: http://localhost:8080/swagger-ui.html"
    print_info "PostgreSQL disponível em: localhost:5432"
}

# Função stop: Para os containers Docker
stop() {
    print_info "Parando containers Docker..."
    docker_compose stop
    
    print_success "Containers parados com sucesso!"
}

# Função logs: Mostra os logs dos containers
logs() {
    local service=$1
    
    if [ -z "$service" ]; then
        print_info "Mostrando logs de todos os containers (Ctrl+C para sair)..."
        docker_compose logs -f
    else
        print_info "Mostrando logs do serviço: $service (Ctrl+C para sair)..."
        docker_compose logs -f "$service"
    fi
}

# Função clean: Para e remove containers, volumes e limpa o projeto
clean() {
    print_warning "Esta ação irá parar e remover todos os containers e volumes."
    read -p "Deseja continuar? (y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_info "Parando e removendo containers..."
        docker_compose down -v
        
        print_info "Limpando diretório target do Maven..."
        if [ -d "target" ]; then
            rm -rf target
            print_success "Diretório target removido."
        fi
        
        print_info "Limpando logs do Docker..."
        docker system prune -f
        
        print_success "Limpeza concluída com sucesso!"
    else
        print_info "Operação cancelada."
    fi
}

# Função restart: Reinicia os containers
restart() {
    print_info "Reiniciando containers Docker..."
    docker_compose restart
    
    print_success "Containers reiniciados com sucesso!"
}

# Função status: Mostra o status dos containers
status() {
    print_info "Status dos containers:"
    docker_compose ps
}

# Função test: Executa os testes do projeto
test() {
    print_info "Executando testes do projeto..."
    
    # Verifica se o Maven está instalado
    if ! command -v mvn &> /dev/null; then
        print_error "Maven não está instalado."
        exit 1
    fi
    
    mvn clean test
    
    if [ $? -eq 0 ]; then
        print_success "Testes executados com sucesso!"
    else
        print_error "Alguns testes falharam."
        exit 1
    fi
}

# Função coverage: Verifica a cobertura de testes
coverage() {
    print_info "Executando testes e gerando relatório de cobertura..."
    
    # Verifica se o Maven está instalado
    if ! command -v mvn &> /dev/null; then
        print_error "Maven não está instalado."
        exit 1
    fi
    
    mvn clean verify
    
    if [ $? -eq 0 ]; then
        print_success "Testes executados e relatório de cobertura gerado!"
        print_info "Relatório Jacoco: target/site/jacoco/index.html"
        
        # Verifica se o relatório foi gerado
        if [ -f "target/site/jacoco/index.html" ]; then
            print_info "Abrindo relatório no navegador..."
            open target/site/jacoco/index.html
        else
            print_warning "Relatório não encontrado."
        fi
    else
        print_error "Alguns testes falharam."
        exit 1
    fi
}

# Função sonar: Inicia SonarQube e executa análise
sonar() {
    print_info "Iniciando SonarQube..."
    docker_compose -f docker-compose.sonar.yml up -d
    
    print_info "Aguardando SonarQube ficar pronto (isso pode levar alguns minutos)..."
    
    # Aguarda SonarQube estar pronto (status UP, não apenas respondendo)
    local max_attempts=60
    local attempt=0
    local sonar_status=""

    while [ $attempt -lt $max_attempts ]; do
        sonar_status=$(curl -s http://localhost:9000/api/system/status 2>/dev/null | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
        if [ "$sonar_status" = "UP" ]; then
            print_success "SonarQube está pronto (status: UP)!"
            break
        fi
        attempt=$((attempt + 1))
        echo -n "."
        sleep 5
    done
    echo
    
    if [ $attempt -eq $max_attempts ]; then
        print_error "SonarQube não ficou pronto a tempo. Verifique os logs com: docker compose -f docker-compose.sonar.yml logs"
        exit 1
    fi
    
    print_info "Criando usuário admin2..."
    curl -X POST -u admin:admin \
        -F "login=admin2" \
        -F "name=Admin2" \
        -F "password=admin2" \
        http://localhost:9000/api/users/create > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        print_success "Usuário admin2 criado com sucesso!"
    else
        print_warning "Não foi possível criar o usuário admin2 (pode já existir)."
    fi
    
    print_info "Executando análise SonarQube..."
    
    # Verifica se o arquivo sonar-project.properties existe, senão cria configuração via linha de comando
    if [ ! -f "sonar-project.properties" ]; then
        print_info "Configurando análise via parâmetros..."
        mvn sonar:sonar \
            -Dsonar.host.url=http://localhost:9000 \
            -Dsonar.login=admin \
            -Dsonar.password=admin \
            -Dsonar.projectKey=oficina-mecanica \
            -Dsonar.projectName=Oficina-Mecanica \
            -Dsonar.projectVersion=1.0.0
    else
        mvn sonar:sonar
    fi
    
    print_success "Análise concluída!"
    print_info "Acesse o dashboard em: http://localhost:9000"
    print_info "Credenciais padrão: admin / admin"
}

# Função sonar-stop: Para o SonarQube
sonar_stop() {
    print_info "Parando SonarQube..."
    docker_compose -f docker-compose.sonar.yml down
    
    print_success "SonarQube parado com sucesso!"
}

# Função sonar-logs: Mostra logs do SonarQube
sonar_logs() {
    print_info "Mostrando logs do SonarQube (Ctrl+C para sair)..."
    docker_compose -f docker-compose.sonar.yml logs -f
}

# Função trivy_scan: Executa análise de segurança com Trivy
trivy_scan() {
    # Verifica se o Trivy está instalado
    # Usa 'command -v' com '\trivy' para ignorar funções shell e encontrar apenas o binário externo
    if ! command -v \trivy &> /dev/null; then
        print_error "Trivy não está instalado."
        print_info "Para instalar o Trivy, execute:"
        print_info "  brew install trivy  (macOS)"
        print_info "  ou visite: https://aquasecurity.github.io/trivy/latest/getting-started/installation/"
        exit 1
    fi

        local trivy_bin
        trivy_bin="$(command -v \trivy)"
        local report_dir="target/security"
        mkdir -p "$report_dir"

        print_info "Executando análise Trivy no Dockerfile..."
        "$trivy_bin" config --severity HIGH,CRITICAL --exit-code 1 Dockerfile | tee "$report_dir/trivy-dockerfile.txt"

        print_info "Executando análise Trivy no docker-compose.yml..."
        "$trivy_bin" config --severity HIGH,CRITICAL --exit-code 1 docker-compose.yml | tee "$report_dir/trivy-docker-compose.txt"

        print_info "Executando análise Trivy no código fonte..."
        "$trivy_bin" fs --severity HIGH,CRITICAL --exit-code 1 --skip-dirs target . | tee "$report_dir/trivy-fs.txt"

        print_success "Análise Trivy concluída sem findings HIGH/CRITICAL!"
        print_info "Relatórios Trivy em: $report_dir"
}

# Função security: Pipeline completo de segurança
security() {
        local report_dir="target/security"
        mkdir -p "$report_dir"

        print_info "Etapa 1/4 - Executando Trivy..."
        trivy_scan

        print_info "Etapa 2/4 - Executando cobertura JaCoCo..."
        coverage

        print_info "Etapa 3/4 - Executando análise SonarQube..."
        sonar

        print_info "Etapa 4/4 - Consolidando evidências..."
        # Recria o diretório pois 'mvn clean' na coverage pode ter apagado
        mkdir -p "$report_dir"
        local generated_at
        generated_at="$(date '+%Y-%m-%d %H:%M:%S')"
        cat > "$report_dir/security-summary.md" << EOF
# Security Execution Summary

- Generated at: $generated_at
- Trivy:
    - target/security/trivy-dockerfile.txt
    - target/security/trivy-docker-compose.txt
    - target/security/trivy-fs.txt
- JaCoCo:
    - target/site/jacoco/index.html
- SonarQube:
    - http://localhost:9000

EOF

        print_success "Pipeline de segurança concluído!"
        print_info "Resumo gerado em: $report_dir/security-summary.md"
}

# Função help: Mostra ajuda
help() {
    echo "Uso: $0 <comando>"
    echo ""
    echo "Comandos disponíveis:"
    echo "  start       - Inicia os containers Docker"
    echo "  stop        - Para os containers Docker"
    echo "  restart     - Reinicia os containers Docker"
    echo "  logs        - Mostra logs de todos os containers (ou: logs <serviço>)"
    echo "  status      - Mostra o status dos containers"
    echo "  clean       - Para e remove containers, volumes e limpa o projeto"
    echo "  test        - Executa os testes do projeto"
    echo "  coverage    - Executa testes e gera relatório de cobertura Jacoco"
    echo "  sonar       - Inicia SonarQube e executa análise do código"
    echo "  sonar-stop  - Para o SonarQube"
    echo "  sonar-logs  - Mostra logs do SonarQube"
    echo "  trivy       - Executa análise de segurança com Trivy"
    echo "  security    - Executa Trivy + JaCoCo + Sonar e consolida evidências"
    echo "  help        - Mostra esta mensagem de ajuda"
    echo ""
    echo "Exemplos:"
    echo "  $0 start"
    echo "  $0 logs app"
    echo "  $0 logs postgres"
    echo "  $0 test"
    echo "  $0 coverage"
    echo "  $0 sonar"
    echo "  $0 trivy"
    echo "  $0 security"
}

# Verifica se o Docker e Docker Compose estão instalados
check_dependencies() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker não está instalado. Por favor, instale o Docker primeiro."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_error "Docker Compose não está instalado. Por favor, instale o Docker Compose primeiro."
        exit 1
    fi
}

# Main
check_dependencies

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    logs)
        logs "$2"
        ;;
    status)
        status
        ;;
    clean)
        clean
        ;;
    test)
        test
        ;;
    coverage)
        coverage
        ;;
    sonar)
        sonar
        ;;
    sonar-stop)
        sonar_stop
        ;;
    sonar-logs)
        sonar_logs
        ;;
    trivy)
        trivy_scan
        ;;
    security)
        security
        ;;
    help|--help|-h)
        help
        ;;
    *)
        print_error "Comando não reconhecido: $1"
        echo ""
        help
        exit 1
        ;;
esac
