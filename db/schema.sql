DROP TABLE IF EXISTS itensservicoexterno CASCADE;
DROP TABLE IF EXISTS itensservicooficina CASCADE;
DROP TABLE IF EXISTS itempecas CASCADE;
DROP TABLE IF EXISTS servicoexterno CASCADE;
DROP TABLE IF EXISTS historicocliente CASCADE;
DROP TABLE IF EXISTS ordemservico CASCADE;
DROP TABLE IF EXISTS fornecedor_pecas CASCADE;
DROP TABLE IF EXISTS pecas CASCADE;
DROP TABLE IF EXISTS servico CASCADE;
DROP TABLE IF EXISTS colaborador_funcao CASCADE;
DROP TABLE IF EXISTS funcao CASCADE;
DROP TABLE IF EXISTS colaborador CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;
DROP TABLE IF EXISTS pessoajuridica CASCADE;
DROP TABLE IF EXISTS pessoafisica CASCADE;
DROP TABLE IF EXISTS pessoa CASCADE;
DROP TABLE IF EXISTS veiculo CASCADE;
DROP TABLE IF EXISTS modelo CASCADE;
DROP TABLE IF EXISTS marca CASCADE;
DROP TABLE IF EXISTS parceiro_externo CASCADE;
DROP TABLE IF EXISTS fornecedor CASCADE;

CREATE TABLE pessoa (
    idpessoa SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    ddi1 VARCHAR(10) DEFAULT '55',
    ddd1 VARCHAR(10) DEFAULT '',
    numerotelefone1 VARCHAR(20) DEFAULT '',
    ddi2 VARCHAR(10),
    ddd2 VARCHAR(10),
    numerotelefone2 VARCHAR(20),
    email VARCHAR(255),
    endereco VARCHAR(255),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(50),
    cep INTEGER,
    datacadastro DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE pessoafisica (
    idpessoafisica SERIAL PRIMARY KEY,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    rg VARCHAR(20),
    datanascimento DATE,
    idpessoa INTEGER NOT NULL REFERENCES pessoa(idpessoa)
);

CREATE TABLE pessoajuridica (
    idpessoajuridica SERIAL PRIMARY KEY,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    inscricaoestadual VARCHAR(20),
    razaosocial VARCHAR(255) NOT NULL,
    idpessoa INTEGER NOT NULL REFERENCES pessoa(idpessoa)
);

CREATE TABLE cliente (
    idcliente SERIAL PRIMARY KEY,
    statuscliente VARCHAR(10) NOT NULL DEFAULT 'Ativo',
    observacoes TEXT,
    idpessoafisica INTEGER REFERENCES pessoafisica(idpessoafisica),
    idpessoajuridica INTEGER REFERENCES pessoajuridica(idpessoajuridica)
);

CREATE TABLE colaborador (
    idcolaborador SERIAL PRIMARY KEY,
    matricula VARCHAR(20) NOT NULL UNIQUE,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    dataadmissao DATE,
    datademissao DATE,
    salario DECIMAL(10,2),
    observacoes TEXT,
    idpessoa INTEGER NOT NULL REFERENCES pessoa(idpessoa),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE funcao (
    idfuncao SERIAL PRIMARY KEY,
    especialidade VARCHAR(100),
    comissao DECIMAL(5,2),
    funcaocolaborador VARCHAR(100)
);

CREATE TABLE colaborador_funcao (
    idcolaborador INTEGER NOT NULL REFERENCES colaborador(idcolaborador),
    idfuncao INTEGER NOT NULL REFERENCES funcao(idfuncao),
    PRIMARY KEY (idcolaborador, idfuncao)
);

CREATE TABLE marca (
    idmarca SERIAL PRIMARY KEY,
    nomemarca VARCHAR(100) NOT NULL,
    logo_url VARCHAR(500)
);

CREATE TABLE modelo (
    idmodelo SERIAL PRIMARY KEY,
    nomemodelo VARCHAR(100) NOT NULL,
    idmarca INTEGER NOT NULL REFERENCES marca(idmarca)
);

CREATE TABLE veiculo (
    idveiculo SERIAL PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    chassi VARCHAR(20) NOT NULL,
    anofabricacao INTEGER NOT NULL,
    anomodelo INTEGER NOT NULL,
    cor VARCHAR(30) NOT NULL,
    quilometragem INTEGER NOT NULL DEFAULT 0,
    acessorios TEXT,
    idmodelo INTEGER NOT NULL REFERENCES modelo(idmodelo),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE ordemservico (
    idordemservico SERIAL PRIMARY KEY,
    numeroos INTEGER UNIQUE,
    entradaveiculo DATE NOT NULL,
    dataabertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    datafechamento TIMESTAMP,
    defeitorelatado TEXT NOT NULL,
    quantidadepecas INTEGER DEFAULT 0,
    valortotalpecas DECIMAL(10,2) DEFAULT 0,
    valormaodeobra DECIMAL(10,2) DEFAULT 0,
    valorservicoexterno DECIMAL(10,2) DEFAULT 0,
    formadepagamento VARCHAR(50),
    valordesconto DECIMAL(10,2) DEFAULT 0,
    valortotal DECIMAL(10,2) DEFAULT 0,
    garantia INTEGER DEFAULT 0,
    status VARCHAR(30) DEFAULT 'Aberta',
    idveiculo INTEGER NOT NULL REFERENCES veiculo(idveiculo),
    idcolaborador INTEGER REFERENCES colaborador(idcolaborador)
);

CREATE TABLE pecas (
    idpecas SERIAL PRIMARY KEY,
    codigonacional BIGINT UNIQUE,
    codigointernopeca VARCHAR(50) UNIQUE,
    nomepeca VARCHAR(200) NOT NULL,
    descricaopeca TEXT,
    fabricantepeca VARCHAR(200),
    categoriapeca VARCHAR(100),
    valorcustopeca DECIMAL(10,2) NOT NULL,
    valorvendapeca DECIMAL(10,2) NOT NULL,
    quantidadeestoque INTEGER NOT NULL DEFAULT 0,
    datacomprapeca DATE,
    garantiapeca INTEGER NOT NULL DEFAULT 180,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE fornecedor (
    idfornecedor SERIAL PRIMARY KEY,
    razaosocial VARCHAR(200) NOT NULL,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    ddi VARCHAR(10) DEFAULT '55',
    ddd VARCHAR(10) DEFAULT '',
    numerofornecedor VARCHAR(20),
    email VARCHAR(255),
    enderecofornecedor VARCHAR(255),
    bairrofornecedor VARCHAR(100),
    cidadefornecedor VARCHAR(100),
    estadofornecedor VARCHAR(50),
    cepfornecedor INTEGER,
    ativo BOOLEAN DEFAULT true
);

CREATE TABLE fornecedor_pecas (
    idfornecedor INTEGER NOT NULL REFERENCES fornecedor(idfornecedor),
    idpecas INTEGER NOT NULL REFERENCES pecas(idpecas),
    PRIMARY KEY (idfornecedor, idpecas)
);

CREATE TABLE servico (
    idservico SERIAL PRIMARY KEY,
    nomeservico VARCHAR(200) NOT NULL,
    descricaoservico TEXT,
    valorservico DECIMAL(10,2) NOT NULL,
    garantiadias INTEGER NOT NULL DEFAULT 90,
    tempoestimado VARCHAR(50),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE itensservicooficina (
    iditensservicooficina SERIAL PRIMARY KEY,
    quantidadeitenservico INTEGER NOT NULL DEFAULT 1,
    valorunitarioitenservico DECIMAL(10,2) NOT NULL,
    valortotalitenservico DECIMAL(10,2) NOT NULL,
    garantiadias INTEGER DEFAULT 0,
    idcolaborador INTEGER NOT NULL REFERENCES colaborador(idcolaborador),
    idservico INTEGER NOT NULL REFERENCES servico(idservico),
    idordemservico INTEGER NOT NULL REFERENCES ordemservico(idordemservico),
    horainicio TIMESTAMP,
    horafim TIMESTAMP,
    statusitenservico VARCHAR(30)
);

CREATE TABLE itempecas (
    iditempecas SERIAL PRIMARY KEY,
    quantidade INTEGER NOT NULL DEFAULT 1,
    valorunitario DECIMAL(10,2) NOT NULL,
    valortotal DECIMAL(10,2) NOT NULL,
    garantia INTEGER DEFAULT 0,
    idpecas INTEGER NOT NULL REFERENCES pecas(idpecas),
    idordemservico INTEGER NOT NULL REFERENCES ordemservico(idordemservico)
);

CREATE TABLE parceiro_externo (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    tipo_servico VARCHAR(100),
    telefone VARCHAR(20),
    email VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE servicoexterno (
    idservicoexterno SERIAL PRIMARY KEY,
    descricao TEXT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    prazo DATE DEFAULT CURRENT_DATE,
    garantiadias INTEGER NOT NULL DEFAULT 90,
    idparceiro INTEGER NOT NULL REFERENCES parceiro_externo(id)
);

CREATE TABLE itensservicoexterno (
    iditensservicoexterno SERIAL PRIMARY KEY,
    quantidade INTEGER NOT NULL DEFAULT 1,
    valorunitario DECIMAL(10,2) NOT NULL,
    valortotal DECIMAL(10,2) NOT NULL,
    garantiadias INTEGER DEFAULT 0,
    idservicoexterno INTEGER NOT NULL REFERENCES servicoexterno(idservicoexterno),
    idordemservico INTEGER NOT NULL REFERENCES ordemservico(idordemservico)
);

CREATE TABLE historicocliente (
    idveiculo INTEGER NOT NULL REFERENCES veiculo(idveiculo),
    idcliente INTEGER NOT NULL REFERENCES cliente(idcliente),
    datainicio DATE NOT NULL DEFAULT CURRENT_DATE,
    datafim DATE,
    PRIMARY KEY (idveiculo, idcliente, datainicio)
);
