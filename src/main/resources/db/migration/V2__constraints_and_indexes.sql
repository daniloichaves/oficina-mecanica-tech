ALTER TABLE clientes ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ATIVO';

ALTER TABLE clientes DROP CONSTRAINT IF EXISTS ck_clientes_status;
ALTER TABLE clientes ADD CONSTRAINT ck_clientes_status
    CHECK (status IN ('ATIVO', 'INATIVO', 'BLOQUEADO'));

ALTER TABLE ordens_servico DROP CONSTRAINT IF EXISTS ck_ordens_servico_status;
ALTER TABLE ordens_servico ADD CONSTRAINT ck_ordens_servico_status
    CHECK (status IN ('RECEBIDA', 'EM_DIAGNOSTICO', 'AGUARDANDO_APROVACAO',
                      'EM_EXECUCAO', 'FINALIZADA', 'ENTREGUE', 'CANCELADA'));

ALTER TABLE pecas DROP CONSTRAINT IF EXISTS ck_pecas_estoque;
ALTER TABLE pecas ADD CONSTRAINT ck_pecas_estoque CHECK (quantidade_estoque >= 0);

ALTER TABLE itens_servico DROP CONSTRAINT IF EXISTS ck_itens_servico_quantidade;
ALTER TABLE itens_servico ADD CONSTRAINT ck_itens_servico_quantidade CHECK (quantidade > 0);

ALTER TABLE itens_peca DROP CONSTRAINT IF EXISTS ck_itens_peca_quantidade;
ALTER TABLE itens_peca ADD CONSTRAINT ck_itens_peca_quantidade CHECK (quantidade > 0);

CREATE INDEX IF NOT EXISTS idx_clientes_status ON clientes(status);
CREATE INDEX IF NOT EXISTS idx_veiculos_cliente ON veiculos(cliente_id);
CREATE INDEX IF NOT EXISTS idx_ordens_cliente ON ordens_servico(cliente_id);
CREATE INDEX IF NOT EXISTS idx_ordens_veiculo ON ordens_servico(veiculo_id);
CREATE INDEX IF NOT EXISTS idx_ordens_status_criacao ON ordens_servico(status, data_criacao);
CREATE INDEX IF NOT EXISTS idx_itens_servico_ordem ON itens_servico(ordem_servico_id);
CREATE INDEX IF NOT EXISTS idx_itens_peca_ordem ON itens_peca(ordem_servico_id);
