CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    cpf_cnpj VARCHAR(14) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    endereco VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    data_cadastro TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP
);

CREATE TABLE veiculos (
    id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(7) NOT NULL UNIQUE,
    marca VARCHAR(255) NOT NULL,
    modelo VARCHAR(255) NOT NULL,
    ano INTEGER NOT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id)
);

CREATE TABLE servicos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    descricao VARCHAR(255) NOT NULL,
    valor NUMERIC(10, 2) NOT NULL,
    tempo_estimado_minutos INTEGER NOT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP
);

CREATE TABLE pecas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    descricao VARCHAR(255) NOT NULL,
    valor NUMERIC(10, 2) NOT NULL,
    quantidade_estoque INTEGER NOT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP
);

CREATE TABLE ordens_servico (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    veiculo_id BIGINT NOT NULL REFERENCES veiculos(id),
    status VARCHAR(30) NOT NULL,
    valor_total NUMERIC(10, 2),
    orcamento_aprovado BOOLEAN NOT NULL DEFAULT FALSE,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    data_entrega TIMESTAMP
);

CREATE TABLE itens_servico (
    id BIGSERIAL PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL REFERENCES ordens_servico(id),
    servico_id BIGINT NOT NULL REFERENCES servicos(id),
    quantidade INTEGER NOT NULL,
    valor_unitario NUMERIC(10, 2)
);

CREATE TABLE itens_peca (
    id BIGSERIAL PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL REFERENCES ordens_servico(id),
    peca_id BIGINT NOT NULL REFERENCES pecas(id),
    quantidade INTEGER NOT NULL,
    valor_unitario NUMERIC(10, 2)
);
