-- Créer la base de données
CREATE DATABASE IF NOT EXISTS pompefunebre;
USE pompefunebre;

-- Table pour les familles (clients)
CREATE TABLE famille (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    adresse VARCHAR(255),
    telephone VARCHAR(20)
);

-- Table pour les défunts
CREATE TABLE defunt (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    date_naissance DATE,
    date_deces DATE,
    lieu_deces VARCHAR(255),
    id_famille INT,
    FOREIGN KEY (id_famille) REFERENCES famille(id) ON DELETE CASCADE
);

-- Table pour les cérémonies
CREATE TABLE ceremonie (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_defunt INT,
    date_ceremonie DATE,
    heure_ceremonie TIME,
    lieu_ceremonie VARCHAR(255),
    type_ceremonie VARCHAR(100),
    FOREIGN KEY (id_defunt) REFERENCES defunt(id) ON DELETE CASCADE
);

-- Table pour les employés
CREATE TABLE employe (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    poste VARCHAR(100),
    telephone VARCHAR(20),
    email VARCHAR(100),
    statut VARCHAR(20) DEFAULT 'actif'
);

-- Table pour le matériel funéraire
CREATE TABLE materiel (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    type_materiel VARCHAR(100),
    prix DECIMAL(10, 2),
    disponibilite BOOLEAN DEFAULT TRUE
);


-- Table pour les factures
CREATE TABLE facture (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_famille INT,
    date_facture DATE,
    montant_total DECIMAL(10, 2),
    details TEXT,
    FOREIGN KEY (id_famille) REFERENCES famille(id) ON DELETE CASCADE
);

-- Mise à jour des employés existants
ALTER TABLE employe ADD COLUMN prenom VARCHAR(100) DEFAULT '';
ALTER TABLE employe ADD COLUMN email VARCHAR(100) DEFAULT '';
ALTER TABLE employe ADD COLUMN statut VARCHAR(20) DEFAULT 'actif';
UPDATE employe SET statut = 'actif' WHERE statut IS NULL;
