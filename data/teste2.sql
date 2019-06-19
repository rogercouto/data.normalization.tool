DROP DATABASE IF EXISTS teste2;

CREATE DATABASE teste2;

USE teste2;

CREATE TABLE Funcionario(
	Funcionario_id INTEGER AUTO_INCREMENT NOT NULL,
	Codigo VARCHAR(255),
	Nome VARCHAR(255),
	Cat_id INTEGER NOT NULL,
	PRIMARY KEY(Funcionario_id)
);

CREATE TABLE Endereco(
	Endereco_id INTEGER AUTO_INCREMENT NOT NULL,
	Funcionario_id INTEGER NOT NULL,
	Endereco_value VARCHAR(255),
	PRIMARY KEY(Endereco_id)
);

CREATE TABLE Cat(
	Cat_id INTEGER AUTO_INCREMENT NOT NULL,
	Cat VARCHAR(255),
	DescCat VARCHAR(255),
	Salario VARCHAR(255),
	PRIMARY KEY(Cat_id)
);

INSERT INTO Funcionario(Codigo, Nome, Cat_id) VALUES
('2019001', 'Estevao', 1),
('2019002', 'Carol', 2),
('2019003', 'Antonio', 1),
('2019004', 'Roger', 3),
('2019005', 'Eduardo', 3),
('2019006', 'EduardoX', 2),
('2019007', 'Eduardo1', 3);

INSERT INTO Endereco(Funcionario_id, Endereco_value) VALUES
(1, 'Rua das flores, Nº 123, Santa Maria - RS'),
(2, 'Av. Julio Cezar, 3640, Porto Alegre - RS'),
(2, 'Rua Teodoro N 36, Santa Maria - RS'),
(3, 'Av. Malibu No 9065, Curitiba - PA'),
(3, 'Rua das aranhas Numero 295, Restinga Sêca - RS'),
(4, 'Rua Ernesto Friedrich, 185 apto. 204, Restinga Seca - RS'),
(5, 'Rua Ernesto Friedrich, 185 apto. 204, Restinga Seca - RS'),
(6, 'Rua Ernesto Friedrich, 185 apto. 204, Restinga Seca - RS'),
(7, 'Rua Ernesto Friedrich, 185 apto. 204, Restinga Seca - RS');

INSERT INTO Cat(Cat, DescCat, Salario) VALUES
('PJ', 'Programador Junior', '3000.00'),
('PS', 'Programador Senior', '5000.00'),
('TI', 'Tecnico em Informatica', '2500.00');

ALTER TABLE Funcionario ADD FOREIGN KEY(Cat_id) REFERENCES Cat(Cat_id);

ALTER TABLE Endereco ADD FOREIGN KEY(Funcionario_id) REFERENCES Funcionario(Funcionario_id);

