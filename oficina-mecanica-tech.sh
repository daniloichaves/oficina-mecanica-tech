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

# Função start: Inicia os containers Docker
start() {
    print_info "Iniciando containers Docker..."
    docker-compose up -d
    
    print_info "Aguardando serviços ficarem prontos..."
    sleep 5
    
    print_info "Verificando status dos containers..."
    docker-compose ps
    
    print_success "Containers iniciados com sucesso!"
    print_info "API disponível em: http://localhost:8080"
    print_info "Swagger UI disponível em: http://localhost:8080/swagger-ui.html"
    print_info "PostgreSQL disponível em: localhost:5432"
}

# Função stop: Para os containers Docker
stop() {
    print_info "Parando containers Docker..."
    docker-compose stop
    
    print_success "Containers parados com sucesso!"
}

# Função logs: Mostra os logs dos containers
logs() {
    local service=$1
    
    if [ -z "$service" ]; then
        print_info "Mostrando logs de todos os containers (Ctrl+C para sair)..."
        docker-compose logs -f
    else
        print_info "Mostrando logs do serviço: $service (Ctrl+C para sair)..."
        docker-compose logs -f "$service"
    fi
}

# Função clean: Para e remove containers, volumes e limpa o projeto
clean() {
    print_warning "Esta ação irá parar e remover todos os containers e volumes."
    read -p "Deseja continuar? (y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_info "Parando e removendo containers..."
        docker-compose down -v
        
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
    docker-compose restart
    
    print_success "Containers reiniciados com sucesso!"
}

# Função status: Mostra o status dos containers
status() {
    print_info "Status dos containers:"
    docker-compose ps
}

# Função help: Mostra ajuda
help() {
    echo "Uso: $0 <comando>"
    echo ""
    echo "Comandos disponíveis:"
    echo "  start     - Inicia os containers Docker"
    echo "  stop      - Para os containers Docker"
    echo "  restart   - Reinicia os containers Docker"
    echo "  logs      - Mostra logs de todos os containers (ou: logs <serviço>)"
    echo "  status    - Mostra o status dos containers"
    echo "  clean     - Para e remove containers, volumes e limpa o projeto"
    echo "  help      - Mostra esta mensagem de ajuda"
    echo ""
    echo "Exemplos:"
    echo "  $0 start"
    echo "  $0 logs app"
    echo "  $0 logs postgres"
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
