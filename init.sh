#!/bin/bash

# Script de Inicialização - Oficina Mecânica Tech
# Verifica pré-requisitos, configura ambiente e inicia o projeto

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Ícones
CHECK="✓"
ERROR="✗"

# Diretório do script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# ================== FUNÇÕES DE PRINT ==================

print_header() {
    echo -e "\n${CYAN}╔════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║ $1${NC}"
    echo -e "${CYAN}╚════════════════════════════════════════════╝${NC}\n"
}

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[${CHECK}]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

print_error() {
    echo -e "${RED}[${ERROR}]${NC} $1"
}

print_step() {
    echo -e "\n${CYAN}▶${NC} $1"
}

# ================== VALIDAÇÕES ==================

check_command() {
    local cmd=$1
    local name=$2
    
    if command -v "$cmd" &> /dev/null; then
        local version=$($cmd --version 2>&1 | head -n1)
        print_success "$name instalado: $version"
        return 0
    else
        print_error "$name não encontrado"
        return 1
    fi
}

detect_os() {
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        OS=$ID
        # Detectar distros baseadas em Ubuntu/Debian
        if [[ "$OS" == "linuxmint" ]] || [[ "$OS" == "ubuntu" ]] || [[ "$OS" == "debian" ]]; then
            OS="debian"
        elif [[ "$ID_LIKE" == *"ubuntu"* ]] || [[ "$ID_LIKE" == *"debian"* ]]; then
            OS="debian"
        fi
    elif type lsb_release >/dev/null 2>&1; then
        OS=$(lsb_release -si | tr '[:upper:]' '[:lower:]')
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        OS="macos"
    else
        OS="unknown"
    fi
    echo "$OS"
}

sudo_available() {
    if ! command -v sudo &> /dev/null; then
        return 1
    fi

    if sudo -n true &> /dev/null; then
        return 0
    fi

    return 1
}

docker_compose_available() {
    if command -v docker-compose &> /dev/null; then
        echo "docker-compose"
        return 0
    fi

    if command -v docker &> /dev/null && docker compose version &> /dev/null; then
        echo "docker compose"
        return 0
    fi

    if sudo_available && command -v docker &> /dev/null && sudo docker compose version &> /dev/null; then
        echo "sudo docker compose"
        return 0
    fi

    return 1
}

docker_accessible() {
    if command -v docker &> /dev/null && docker info &> /dev/null; then
        echo "docker"
        return 0
    fi

    if sudo_available && command -v docker &> /dev/null && sudo docker info &> /dev/null; then
        echo "sudo docker"
        return 0
    fi

    return 1
}

install_java() {
    local os=$1
    print_info "Instalando Java 21..."
    
    if [[ "$os" != "macos" ]] && ! sudo_available; then
        print_warning "sudo não está disponível ou está bloqueado. Instalação automática não é possível."
        echo -e "\n${CYAN}sudo apt-get update${NC}"
        echo -e "${CYAN}sudo apt-get install -y openjdk-21-jdk${NC}\n"
        return 1
    fi
    
    case $os in
        debian)
            print_info "Executando: sudo apt-get update && sudo apt-get install -y openjdk-21-jdk"
            if sudo apt-get update && sudo apt-get install -y openjdk-21-jdk 2>/dev/null; then
                print_success "Java instalado com sucesso!"
                return 0
            else
                print_warning "Falha na instalação automática. Execute manualmente:"
                echo -e "\n${CYAN}sudo apt-get update${NC}"
                echo -e "${CYAN}sudo apt-get install -y openjdk-21-jdk${NC}\n"
                return 1
            fi
            ;;
        rhel|fedora|centos)
            print_info "Executando: sudo dnf install -y java-21-openjdk java-21-openjdk-devel"
            if sudo dnf install -y java-21-openjdk java-21-openjdk-devel 2>/dev/null; then
                print_success "Java instalado com sucesso!"
                return 0
            else
                print_warning "Falha na instalação automática. Execute manualmente:"
                echo -e "\n${CYAN}sudo dnf install -y java-21-openjdk java-21-openjdk-devel${NC}\n"
                return 1
            fi
            ;;
        macos)
            print_info "Executando: brew install openjdk@21"
            if brew install openjdk@21 2>/dev/null; then
                print_success "Java instalado com sucesso!"
                return 0
            else
                print_warning "Falha na instalação automática. Execute manualmente:"
                echo -e "\n${CYAN}brew install openjdk@21${NC}\n"
                return 1
            fi
            ;;
        *)
            print_error "SO não suportado para instalação automática"
            return 1
            ;;
    esac
}

install_maven() {
    local os=$1
    print_info "Instalando Maven..."
    
    if [[ "$os" != "macos" ]] && ! sudo_available; then
        print_warning "sudo não está disponível ou está bloqueado. Instalação automática não é possível."
        echo -e "\n${CYAN}sudo apt-get install -y maven${NC}\n"
        return 1
    fi
    
    case $os in
        debian)
            print_info "Executando: sudo apt-get install -y maven"
            if sudo apt-get install -y maven 2>/dev/null; then
                print_success "Maven instalado com sucesso!"
                return 0
            else
                print_warning "Falha na instalação automática. Execute manualmente:"
                echo -e "\n${CYAN}sudo apt-get install -y maven${NC}\n"
                return 1
            fi
            ;;
        rhel|fedora|centos)
            print_info "Executando: sudo dnf install -y maven"
            if sudo dnf install -y maven 2>/dev/null; then
                print_success "Maven instalado com sucesso!"
                return 0
            else
                print_warning "Falha na instalação automática. Execute manualmente:"
                echo -e "\n${CYAN}sudo dnf install -y maven${NC}\n"
                return 1
            fi
            ;;
        macos)
            print_info "Executando: brew install maven"
            if brew install maven 2>/dev/null; then
                print_success "Maven instalado com sucesso!"
                return 0
            else
                print_warning "Falha na instalação automática. Execute manualmente:"
                echo -e "\n${CYAN}brew install maven${NC}\n"
                return 1
            fi
            ;;
        *)
            print_error "SO não suportado para instalação automática"
            return 1
            ;;
    esac
}

install_docker() {
    local os=$1
    print_info "Instalando Docker..."
    
    if [[ "$os" != "macos" ]] && ! sudo_available; then
        print_warning "sudo não está disponível ou está bloqueado. Instalação automática não é possível."
        echo -e "\n${CYAN}curl -fsSL https://get.docker.com | sudo bash${NC}"
        echo -e "${CYAN}sudo usermod -aG docker \$USER${NC}\n"
        return 1
    fi
    
    case $os in
        debian)
            print_info "Baixando script de instalação do Docker..."
            if curl -fsSL https://get.docker.com -o /tmp/get-docker.sh 2>/dev/null; then
                print_info "Executando: sudo sh /tmp/get-docker.sh"
                if sudo sh /tmp/get-docker.sh 2>/dev/null; then
                    sudo usermod -aG docker $USER 2>/dev/null
                    print_success "Docker instalado com sucesso!"
                    print_info "Você precisa fazer login novamente ou executar: newgrp docker"
                    rm -f /tmp/get-docker.sh
                    return 0
                else
                    print_warning "Falha na instalação automática. Execute manualmente:"
                    echo -e "\n${CYAN}curl -fsSL https://get.docker.com | sudo bash${NC}\n"
                    echo -e "${CYAN}sudo usermod -aG docker \$USER${NC}\n"
                    rm -f /tmp/get-docker.sh
                    return 1
                fi
            fi
            ;;
        macos)
            print_info "Executando: brew install docker"
            if brew install docker 2>/dev/null; then
                print_success "Docker instalado com sucesso!"
                return 0
            else
                print_warning "Falha na instalação automática. Execute manualmente:"
                echo -e "\n${CYAN}brew install docker${NC}\n"
                return 1
            fi
            ;;
        *)
            print_warning "Instalação automática de Docker não suportada para $os"
            print_info "Visite: https://www.docker.com/products/docker-desktop"
            return 1
            ;;
    esac
}

check_prerequisites() {
    print_header "Validando Pré-requisitos"
    
    local os=$(detect_os)
    local missing_java=false
    local missing_maven=false
    local missing_docker=false
    
    # Verificar Java
    if ! check_command "java" "Java"; then
        missing_java=true
    else
        local java_version=$(java -version 2>&1 | grep -oP '"\K[^"]*' | head -1 | cut -d. -f1)
        if [[ $java_version -lt 21 ]]; then
            print_error "Java 21+ necessário (encontrado: $java_version)"
            missing_java=true
        fi
    fi
    
    # Verificar Maven
    if ! check_command "mvn" "Maven"; then
        missing_maven=true
    fi
    
    # Verificar Docker (opcional)
    if ! check_command "docker" "Docker"; then
        print_warning "Docker não encontrado (opcional para execução com containers)"
        missing_docker=true
    else
        if ! docker_compose_available &> /dev/null; then
            print_warning "Docker Compose não encontrado. O modo Docker Compose pode não funcionar."
            print_info "Para instalar: sudo apt-get install -y docker-compose-plugin"
        fi
        if ! docker_accessible &> /dev/null; then
            print_warning "O Docker existe, mas não há permissão para acessar o socket."
            print_info "Tente adicionar seu usuário ao grupo docker: sudo usermod -aG docker \$USER"
            print_info "Ou rode o script como root/with sudo quando usar Docker."
        fi
    fi
    
    # Se algum requisito obrigatório está faltando, oferecer instalação
    if [[ "$missing_java" == "true" ]] || [[ "$missing_maven" == "true" ]]; then
        print_error "\nAlguns requisitos obrigatórios não foram encontrados!"
        echo ""
        read -p "Deseja instalar as dependências faltantes? (s/N): " -n 1 -r
        echo
        
        if [[ $REPLY =~ ^[Ss]$ ]]; then
            if [[ "$missing_java" == "true" ]]; then
                install_java "$os" || return 1
            fi
            
            if [[ "$missing_maven" == "true" ]]; then
                install_maven "$os" || return 1
            fi
            
            if [[ "$missing_docker" == "true" ]]; then
                echo ""
                read -p "Deseja instalar Docker também? (s/N): " -n 1 -r
                echo
                if [[ $REPLY =~ ^[Ss]$ ]]; then
                    install_docker "$os"
                fi
            fi
            
            print_success "\nTodos os pré-requisitos foram instalados!"
            return 0
        else
            return 1
        fi
    fi
    
    print_success "\nTodos os pré-requisitos foram validados!"
    return 0
}

# ================== MENU INTERATIVO ==================

show_menu() {
    print_header "Escolha o Modo de Execução"
    
    echo "1) ${GREEN}Docker Compose${NC} (Recomendado - Automático)"
    echo "2) ${BLUE}Maven Local${NC} (Requer PostgreSQL local)"
    echo "3) ${YELLOW}Docker + Maven${NC} (BD em Docker, App local)"
    echo "4) ${CYAN}Configurar Apenas${NC} (Sem iniciar)"
    echo "5) Sair"
    echo ""
    read -t 10 -p "Selecione uma opção [1-5] (padrão 1 em 10s): " choice || true
    if [ -z "$choice" ]; then
        print_info "Nenhuma opção selecionada em 10 segundos. Aplicando opção padrão 1."
        choice=1
    fi
}

# ================== EXECUÇÃO ==================

init_docker_compose() {
    print_step "Iniciando com Docker Compose..."
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker não está instalado. Instale em: https://www.docker.com/products/docker-desktop"
        return 1
    fi

    compose_cmd=$(docker_compose_available 2>/dev/null || true)
    if [ -z "$compose_cmd" ]; then
        print_error "Docker Compose não está disponível."
        print_info "Instale o plugin do Compose ou o binário docker-compose."
        echo "  sudo apt-get update"
        echo "  sudo apt-get install -y docker-compose-plugin"
        echo "  sudo apt-get install -y docker-compose"
        return 1
    fi

    if [[ "$compose_cmd" == "docker compose" ]]; then
        compose_cmd_array=(docker compose)
    elif [[ "$compose_cmd" == "sudo docker compose" ]]; then
        compose_cmd_array=(sudo docker compose)
    else
        compose_cmd_array=(docker-compose)
    fi
    
    print_info "Usando: ${compose_cmd}" 
    print_info "Construindo imagens e iniciando containers..."
    "${compose_cmd_array[@]}" up -d --build
    
    print_info "Aguardando serviços ficarem prontos (30s)..."
    sleep 30
    
    if "${compose_cmd_array[@]}" ps | grep -q "Up"; then
        print_success "Docker Compose iniciado com sucesso!"
        print_info "API: http://localhost:8080"
        print_info "Swagger: http://localhost:8080/swagger-ui.html"
        print_info "PostgreSQL: localhost:5432"
        print_info "\nPara ver logs: ${compose_cmd} logs -f"
        print_info "Para parar: ${compose_cmd} down"
    else
        print_error "Falha ao iniciar Docker Compose"
        "${compose_cmd_array[@]}" logs
        return 1
    fi
}

init_maven_local() {
    print_step "Preparando para execução com Maven local..."
    
    # Verificar se PostgreSQL está rodando
    if ! nc -z localhost 5432 &> /dev/null; then
        print_error "PostgreSQL não está acessível em localhost:5432"
        print_warning "Opções:"
        echo "  1. Instale PostgreSQL 16+ localmente"
        echo "  2. Use Docker: docker run -d --name oficina-postgres -e POSTGRES_DB=oficina_mecanica -e POSTGRES_USER=oficina -e POSTGRES_PASSWORD=oficina123 -p 5432:5432 postgres:16-alpine"
        echo "  3. Escolha 'Docker + Maven' no menu anterior"
        return 1
    fi
    
    print_success "PostgreSQL detectado!"
    
    print_info "Executando aplicação..."
    print_warning "Pressione Ctrl+C para parar a aplicação"
    print_info ""
    
    mvn clean spring-boot:run
}

init_docker_maven() {
    print_step "Iniciando PostgreSQL em Docker + App com Maven..."
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker não está instalado"
        return 1
    fi

    docker_exec() {
        if docker "$@"; then
            return 0
        elif sudo_available && sudo docker "$@"; then
            return 0
        fi
        return 1
    }

    if docker_exec ps -a --format '{{.Names}}' | grep -q "^oficina-postgres$"; then
        print_info "Container oficina-postgres já existe. Iniciando..."
        if ! docker_exec start oficina-postgres; then
            print_error "Falha ao iniciar o container oficina-postgres"
            return 1
        fi
    else
        print_info "Criando container PostgreSQL..."
        if ! docker_exec run -d \
            --name oficina-postgres \
            -e POSTGRES_DB=oficina_mecanica \
            -e POSTGRES_USER=oficina \
            -e POSTGRES_PASSWORD=oficina123 \
            -p 5432:5432 \
            postgres:16-alpine; then
            print_error "Falha ao criar o container PostgreSQL"
            return 1
        fi
    fi
    
    print_info "Aguardando PostgreSQL ficar pronto (10s)..."
    sleep 10
    
    if ! nc -z localhost 5432 &> /dev/null; then
        print_error "PostgreSQL não ficou acessível"
        return 1
    fi
    
    print_success "PostgreSQL pronto!"
    print_info "Executando aplicação com Maven..."
    print_warning "Pressione Ctrl+C para parar a aplicação"
    print_info ""
    
    mvn clean spring-boot:run
}

setup_only() {
    print_step "Configurando apenas (sem iniciar)..."
    
    print_info "Verificando estrutura do projeto..."
    
    if [ ! -f "pom.xml" ]; then
        print_error "pom.xml não encontrado"
        return 1
    fi
    
    if [ ! -f "docker-compose.yml" ]; then
        print_error "docker-compose.yml não encontrado"
        return 1
    fi
    
    if [ ! -f ".env.example" ]; then
        print_warning ".env.example não encontrado (opcional)"
    fi
    
    print_success "Estrutura do projeto validada!"
    print_info "\nPróximos passos:"
    echo "  Docker Compose:  ${GREEN}docker compose up -d${NC}"
    echo "  Maven local:     ${BLUE}mvn spring-boot:run${NC}"
    echo "  Docker + Maven:  ${YELLOW}docker run -d --name oficina-postgres -e POSTGRES_DB=oficina_mecanica -e POSTGRES_USER=oficina -e POSTGRES_PASSWORD=oficina123 -p 5432:5432 postgres:16-alpine${NC}"
}

# ================== MAIN ==================

main() {
    clear
    print_header "Inicialização - Oficina Mecânica Tech"
    
    # Validar pré-requisitos
    if ! check_prerequisites; then
        print_error "Instale os requisitos faltantes e tente novamente"
        exit 1
    fi
    
    # Menu interativo
    while true; do
        show_menu
        
        case $choice in
            1)
                init_docker_compose
                break
                ;;
            2)
                init_maven_local
                break
                ;;
            3)
                init_docker_maven
                break
                ;;
            4)
                setup_only
                break
                ;;
            5)
                print_info "Saindo..."
                exit 0
                ;;
            *)
                print_error "Opção inválida. Tente novamente."
                ;;
        esac
    done
}

# Executar
main
