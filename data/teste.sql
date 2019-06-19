CREATE TABLE emp(
	codEmp INT NOT NULL,
	codProj VARCHAR(255),
	tempAl INT,
	cat VARCHAR(255),
	dataIni DATE,
	nome VARCHAR(255),
	sal DOUBLE,
	PRIMARY KEY(codEmp)
);

CREATE TABLE proj(
	codProj VARCHAR(255) NOT NULL,
	tipo VARCHAR(255),
	descr VARCHAR(255),
	PRIMARY KEY(codProj)
);

ALTER TABLE emp ADD FOREIGN KEY(codProj) REFERENCES proj(codProj);

INSERT INTO emp(codEmp, codProj, tempAl, cat, dataIni, nome, sal) VALUES
(2146, 'LSC001', 24, 'A1', '1991-10-31', 'Joao', 4000.0);

INSERT INTO proj(codProj, tipo, descr) VALUES
('LSC001', 'Novo Desenv.', 'Sistema de estoque'),
('PAG02', 'Manutenção.', 'Sistema de RH');

