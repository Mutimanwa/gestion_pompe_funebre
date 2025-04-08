DROP TABLE IF EXISTS Adresse ;
CREATE TABLE Adresse (idAdress INT AUTO_INCREMENT NOT NULL,
pays VARCHAR(20),
commune VARCHAR(20),
zone VARCHAR(20),
avenue VARCHAR(20),
quartier VARCHAR(20),
num INT,
PRIMARY KEY (idAdress)) ENGINE=InnoDB;

DROP TABLE IF EXISTS personnel ;
CREATE TABLE personnel (idpersonnel INT AUTO_INCREMENT NOT NULL,
Nom VARCHAR,
prenom VARCHAR(20),
tel INT,
PRIMARY KEY (idpersonnel)) ENGINE=InnoDB;

DROP TABLE IF EXISTS client ;
CREATE TABLE client (idclient INT AUTO_INCREMENT NOT NULL,
nomclient VARCHAR(20),
DN VARCHAR(20),
PRIMARY KEY (idclient)) ENGINE=InnoDB;

DROP TABLE IF EXISTS produit ;
CREATE TABLE produit (idproduit INT AUTO_INCREMENT NOT NULL,
qualite VARCHAR,
couleur VARCHAR(20),
PVU VARCHAR(20),
DU VARCHAR(20),
DS VARCHAR(15),
PRIMARY KEY (idproduit)) ENGINE=InnoDB;

DROP TABLE IF EXISTS Fournisseur ;
CREATE TABLE Fournisseur (idfournisseur INT AUTO_INCREMENT NOT NULL,
nom VARCHAR,
adresse VARCHAR(20),
email VARCHAR(50),
PRIMARY KEY (idfournisseur)) ENGINE=InnoDB;

DROP TABLE IF EXISTS habiter ;
CREATE TABLE habiter (idAdress INT AUTO_INCREMENT NOT NULL,
idfournisseur INT NOT NULL,
idclient INT NOT NULL,
idpersonnel INT NOT NULL,
PRIMARY KEY (idAdress,
 idfournisseur,
 idclient,
 idpersonnel)) ENGINE=InnoDB;

DROP TABLE IF EXISTS commander ;
CREATE TABLE commander (idproduit INT AUTO_INCREMENT NOT NULL,
idclient INT NOT NULL,
PRIMARY KEY (idproduit,
 idclient)) ENGINE=InnoDB;

DROP TABLE IF EXISTS gerer ;
CREATE TABLE gerer (idpersonnel INT AUTO_INCREMENT NOT NULL,
idclient INT NOT NULL,
PRIMARY KEY (idpersonnel,
 idclient)) ENGINE=InnoDB;

ALTER TABLE habiter ADD CONSTRAINT FK_habiter_idAdress FOREIGN KEY (idAdress) REFERENCES Adresse (idAdress);

ALTER TABLE habiter ADD CONSTRAINT FK_habiter_idfournisseur FOREIGN KEY (idfournisseur) REFERENCES Fournisseur (idfournisseur);
ALTER TABLE habiter ADD CONSTRAINT FK_habiter_idclient FOREIGN KEY (idclient) REFERENCES client (idclient);
ALTER TABLE habiter ADD CONSTRAINT FK_habiter_idpersonnel FOREIGN KEY (idpersonnel) REFERENCES personnel (idpersonnel);
ALTER TABLE commander ADD CONSTRAINT FK_commander_idproduit FOREIGN KEY (idproduit) REFERENCES produit (idproduit);
ALTER TABLE commander ADD CONSTRAINT FK_commander_idclient FOREIGN KEY (idclient) REFERENCES client (idclient);
ALTER TABLE gerer ADD CONSTRAINT FK_gerer_idpersonnel FOREIGN KEY (idpersonnel) REFERENCES personnel (idpersonnel);
ALTER TABLE gerer ADD CONSTRAINT FK_gerer_idclient FOREIGN KEY (idclient) REFERENCES client (idclient);
