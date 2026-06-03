CREATE DATABASE IF NOT EXISTS gynlog_frotas
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE gynlog_frotas;

CREATE TABLE IF NOT EXISTS veiculos (
    id_veiculo BIGINT PRIMARY KEY,
    placa VARCHAR(20) NOT NULL UNIQUE,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NUgynlog_frotasLL,
    ano_fabricacao VARCHAR(10) NOT NULL,gynlog_frotas
    ativo BOOLEAN NOT NULL,
    tipo VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tipos_despesas (
    id_tipo_despesa BIGINT PRIMARY KEY,
    descricao VARCHAR(120) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS movimentacoes (
    id_movimentacao BIGINT PRIMARY KEY,
    id_veiculo BIGINT NOT NULL,
    id_tipo_despesa BIGINT NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    data_movimentacao DATE NULL,
    valor DECIMAL(12,2) NOT NULL,
    tipo VARCHAR(120) NOT NULL,
    distancia_percorrida_km DECIMAL(12,2) DEFAULT 0,
    litros_combustivel DECIMAL(12,2) DEFAULT 0,
    CONSTRAINT fk_mov_veiculo
        FOREIGN KEY (id_veiculo) REFERENCES veiculos(id_veiculo)
        ON UPDATE CASCADE,
    CONSTRAINT fk_mov_tipo
        FOREIGN KEY (id_tipo_despesa) REFERENCES tipos_despesas(id_tipo_despesa)
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE OR REPLACE VIEW vw_gastos_por_veiculo AS
SELECT
    v.id_veiculo,
    v.placa,
    v.marca,
    v.modelo,
    v.tipo AS tipo_veiculo,
    COUNT(m.id_movimentacao) AS qtd_movimentacoes,
    COALESCE(SUM(m.valor), 0) AS total_gasto
FROM veiculos v
LEFT JOIN movimentacoes m ON m.id_veiculo = v.id_veiculo
GROUP BY v.id_veiculo, v.placa, v.marca, v.modelo, v.tipo;

CREATE OR REPLACE VIEW vw_consumo_medio_veiculo AS
SELECT
    v.id_veiculo,
    v.placa,
    v.marca,
    v.modelo,
    SUM(m.distancia_percorrida_km) AS distancia_total_km,
    SUM(m.litros_combustivel) AS litros_total,
    CASE
        WHEN SUM(m.litros_combustivel) > 0
            THEN SUM(m.distancia_percorrida_km) / SUM(m.litros_combustivel)
        ELSE 0
    END AS consumo_medio_km_l
FROM veiculos v
LEFT JOIN movimentacoes m ON m.id_veiculo = v.id_veiculo
GROUP BY v.id_veiculo, v.placa, v.marca, v.modelo;

CREATE OR REPLACE VIEW vw_veiculos_inativos AS
SELECT *
FROM veiculos
WHERE ativo = false;
