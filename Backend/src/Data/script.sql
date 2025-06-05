-- Création de la table des continents
CREATE TABLE continents (
  id_continents INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255)
);

-- Création de la table des pays avec référence aux continents
CREATE TABLE countries (
  id_countries INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255),
  id_continents INT,
  FOREIGN KEY (id_continents) REFERENCES continents(id_continents)
);

-- Création de la table des régions avec référence aux continents
CREATE TABLE regions (
  id_regions INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255),
  id_continents INT,
  FOREIGN KEY (id_continents) REFERENCES continents(id_continents)
);

-- Création de la table des pandémies
CREATE TABLE pandemics (
  id_pandemics INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255)
);

-- Création de la table de données journalières
CREATE TABLE total_by_day (
  id_pandemics INT,
  id_regions INT,
  case_count INT,
  death INT,
  recovered INT,
  date_by_day DATE,
  PRIMARY KEY (id_pandemics, id_regions, date_by_day),
  FOREIGN KEY (id_pandemics) REFERENCES pandemics(id_pandemics),
  FOREIGN KEY (id_regions) REFERENCES regions(id_regions)
);