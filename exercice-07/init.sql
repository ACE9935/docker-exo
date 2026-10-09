USE kennelDB;

CREATE TABLE clients (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(50) NOT NULL,
  prenom VARCHAR(50) NOT NULL,
  date_naissance DATE NOT NULL,
  pseudonyme VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE adresses (
  id INT AUTO_INCREMENT PRIMARY KEY,
  numero VARCHAR(10) NOT NULL,
  rue VARCHAR(100) NOT NULL,
  code_postal VARCHAR(10) NOT NULL,
  commune VARCHAR(100) NOT NULL
);

CREATE TABLE clients_adresses (
  client_id INT NOT NULL,
  adresse_id INT NOT NULL,
  PRIMARY KEY (client_id, adresse_id),
  FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
  FOREIGN KEY (adresse_id) REFERENCES adresses(id) ON DELETE CASCADE
);

CREATE TABLE chiens (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(50) NOT NULL,
  date_naissance DATE NOT NULL,
  race VARCHAR(50) NOT NULL,
  sterilise BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE chats (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(50) NOT NULL,
  date_naissance DATE NOT NULL,
  race VARCHAR(50) NOT NULL,
  sterilise BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO clients (nom, prenom, date_naissance, pseudonyme) VALUES
  ('Dupont', 'Alice', '1990-04-12', 'alice90'),
  ('Martin', 'Lucas', '1985-11-03', 'lucasm'),
  ('Bernard', 'Emma', '2001-07-25', 'emmab');

INSERT INTO adresses (numero, rue, code_postal, commune) VALUES
  ('12', 'rue Nationale', '59000', 'Lille'),
  ('4bis', 'avenue de la République', '59800', 'Lille'),
  ('27', 'boulevard Victor Hugo', '59100', 'Roubaix');

INSERT INTO clients_adresses (client_id, adresse_id) VALUES
  (1, 1),
  (2, 2),
  (3, 3),
  (3, 1);

INSERT INTO chiens (nom, date_naissance, race, sterilise) VALUES
  ('Rex', '2019-03-10', 'Berger allemand', TRUE),
  ('Pixel', '2022-08-01', 'Jack Russell', FALSE),
  ('Nala', '2020-12-15', 'Labrador', TRUE);

INSERT INTO chats (nom, date_naissance, race, sterilise) VALUES
  ('Mistigri', '2018-05-20', 'Européen', TRUE),
  ('Luna', '2021-09-09', 'Maine Coon', FALSE),
  ('Simba', '2023-02-14', 'Siamois', FALSE);
