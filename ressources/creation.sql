-- Création de la base de données (si elle n'existe pas)
CREATE DATABASE bibliotheque_db;

-- Connexion à la base de données
\c bibliotheque_db;

-- ============================================
-- SUPPRESSION DES TABLES EXISTANTES (pour un clean start)
-- ============================================
DROP VIEW IF EXISTS vue_emprunts_en_cours CASCADE;
DROP TRIGGER IF EXISTS trigger_update_exemplaires ON Emprunt CASCADE;
DROP FUNCTION IF EXISTS update_exemplaires_disponibles() CASCADE;
DROP TABLE IF EXISTS Emprunt CASCADE;
DROP TABLE IF EXISTS Membre CASCADE;
DROP TABLE IF EXISTS Livre CASCADE;

-- ============================================
-- CRÉATION DES TABLES
-- ============================================

-- Table des livres
CREATE TABLE Livre (
                       id SERIAL PRIMARY KEY,
                       titre VARCHAR(255) NOT NULL,
                       auteur VARCHAR(255) NOT NULL,
                       categorie VARCHAR(100),
                       nombreExemplaires INTEGER NOT NULL DEFAULT 1,
                       exemplaires_disponibles INTEGER NOT NULL DEFAULT 1,
                       date_ajout TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des membres
CREATE TABLE Membre (
                        id SERIAL PRIMARY KEY,
                        nom VARCHAR(100) NOT NULL,
                        prenom VARCHAR(100) NOT NULL,
                        email VARCHAR(255) UNIQUE NOT NULL,
                        adhesionDate DATE NOT NULL DEFAULT CURRENT_DATE,
                        date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des emprunts
CREATE TABLE Emprunt (
                         id SERIAL PRIMARY KEY,
                         membre_id INTEGER REFERENCES Membre(id) ON DELETE CASCADE,
                         livre_id INTEGER REFERENCES Livre(id) ON DELETE CASCADE,
                         dateEmprunt DATE NOT NULL DEFAULT CURRENT_DATE,
                         dateRetourPrevue DATE NOT NULL,
                         dateRetourEffective DATE,
                         penalite DECIMAL(10, 2) DEFAULT 0.00,
                         CONSTRAINT check_dates CHECK (dateRetourPrevue > dateEmprunt)
);

-- ============================================
-- INDEX POUR AMÉLIORER LES PERFORMANCES
-- ============================================
CREATE INDEX idx_livres_titre ON Livre(titre);
CREATE INDEX idx_livres_auteur ON Livre(auteur);
CREATE INDEX idx_livres_categorie ON Livre(categorie);
CREATE INDEX idx_membres_nom ON Membre(nom);
CREATE INDEX idx_membres_email ON Membre(email);
CREATE INDEX idx_emprunts_membre ON Emprunt(membre_id);
CREATE INDEX idx_emprunts_livre ON Emprunt(livre_id);
CREATE INDEX idx_emprunts_dates ON Emprunt(dateRetourPrevue, dateRetourEffective);
CREATE INDEX idx_emprunts_en_cours ON Emprunt(dateRetourEffective) WHERE dateRetourEffective IS NULL;

-- ============================================
-- DONNÉES D'EXEMPLE
-- ============================================
INSERT INTO Livre (titre, auteur, categorie, nombreExemplaires, exemplaires_disponibles) VALUES
                                                                                             ('Le Petit Prince', 'Antoine de Saint-Exupéry', 'Littérature jeunesse', 5, 5),
                                                                                             ('1984', 'George Orwell', 'Science-fiction', 3, 3),
                                                                                             ('Les Misérables', 'Victor Hugo', 'Classique', 4, 4),
                                                                                             ('Harry Potter à l''école des sorciers', 'J.K. Rowling', 'Fantasy', 6, 6),
                                                                                             ('Le Seigneur des Anneaux', 'J.R.R. Tolkien', 'Fantasy', 3, 3);

INSERT INTO Membre (nom, prenom, email, adhesionDate) VALUES
                                                          ('Lesley', 'Fopa', 'lesley.fopa@gmail.com', '2026-01-15'),
                                                          ('Dupont', 'Jean', 'jean.dupont@email.com', '2024-01-15'),
                                                          ('Martin', 'Sophie', 'sophie.martin@email.com', '2024-02-01'),
                                                          ('Bernard', 'Pierre', 'pierre.bernard@email.com', '2024-02-15');

-- ============================================
-- TRIGGER POUR METTRE À JOUR LES EXEMPLAIRES DISPONIBLES
-- ============================================
CREATE OR REPLACE FUNCTION update_exemplaires_disponibles()
RETURNS TRIGGER AS $$
BEGIN
    -- INSERT : Nouvel emprunt
    IF TG_OP = 'INSERT' THEN
UPDATE Livre
SET exemplaires_disponibles = exemplaires_disponibles - 1
WHERE id = NEW.livre_id;

-- UPDATE : Retour de livre
ELSIF TG_OP = 'UPDATE' AND NEW.dateRetourEffective IS NOT NULL
          AND OLD.dateRetourEffective IS NULL THEN
UPDATE Livre
SET exemplaires_disponibles = exemplaires_disponibles + 1
WHERE id = NEW.livre_id;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_exemplaires
    AFTER INSERT OR UPDATE ON Emprunt
                        FOR EACH ROW
                        EXECUTE FUNCTION update_exemplaires_disponibles();

-- ============================================
-- VUE POUR LES EMPRUNTS EN COURS
-- ============================================
CREATE OR REPLACE VIEW vue_emprunts_en_cours AS
SELECT
    e.id as emprunt_id,
    m.nom || ' ' || m.prenom as membre,
    l.titre as livre,
    e.dateEmprunt,
    e.dateRetourPrevue,
    CASE
        WHEN CURRENT_DATE > e.dateRetourPrevue
            THEN CURRENT_DATE - e.dateRetourPrevue
        ELSE 0
        END as jours_retard
FROM Emprunt e
         JOIN Membre m ON e.membre_id = m.id
         JOIN Livre l ON e.livre_id = l.id
WHERE e.dateRetourEffective IS NULL;

-- ============================================
-- FONCTION POUR AJOUTER UN EMPRUNT AVEC VÉRIFICATION
-- ============================================
CREATE OR REPLACE FUNCTION ajouter_emprunt_securise(
    p_membre_id INTEGER,
    p_livre_id INTEGER,
    p_date_retour_prevue DATE
) RETURNS INTEGER AS $$
DECLARE
v_exemplaires_disponibles INTEGER;
    v_emprunt_id INTEGER;
BEGIN
    -- Vérifier s'il y a des exemplaires disponibles
SELECT exemplaires_disponibles
INTO v_exemplaires_disponibles
FROM Livre
WHERE id = p_livre_id;

-- Si aucun exemplaire disponible, lever une exception
IF v_exemplaires_disponibles IS NULL OR v_exemplaires_disponibles <= 0 THEN
        RAISE EXCEPTION 'Aucun exemplaire disponible pour ce livre';
END IF;

    -- Créer l'emprunt
INSERT INTO Emprunt (membre_id, livre_id, dateRetourPrevue)
VALUES (p_membre_id, p_livre_id, p_date_retour_prevue)
    RETURNING id INTO v_emprunt_id;

RETURN v_emprunt_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- AFFICHAGE DES DONNÉES POUR VÉRIFICATION
-- ============================================
SELECT '=== VÉRIFICATION DES TABLES ===' AS info;

SELECT 'LIVRES:' AS table_name, COUNT(*) AS nombre_de_lignes FROM Livre
UNION ALL
SELECT 'MEMBRES:', COUNT(*) FROM Membre
UNION ALL
SELECT 'EMPRUNTS:', COUNT(*) FROM Emprunt;

SELECT '=== LISTE DES LIVRES ===' AS info;
SELECT id, titre, auteur, exemplaires_disponibles, nombreExemplaires FROM Livre;

SELECT '=== LISTE DES MEMBRES ===' AS info;
SELECT id, nom, prenom, email FROM Membre;

-- Tester le système avec un emprunt d'exemple
DO $$
DECLARE
v_emprunt_id INTEGER;
BEGIN
    -- Ajouter un emprunt pour tester
INSERT INTO Emprunt (membre_id, livre_id, dateRetourPrevue)
VALUES (1, 1, CURRENT_DATE + 14);

RAISE NOTICE '=== TEST D''EMPRUNT ===';
    RAISE NOTICE 'Emprunt créé avec succès';

    -- Afficher l'état après emprunt
    RAISE NOTICE 'Exemplaires disponibles après emprunt: %',
        (SELECT exemplaires_disponibles FROM Livre WHERE id = 1);
END $$;

-- Afficher la vue des emprunts en cours
SELECT '=== EMPRUNTS EN COURS ===' AS info;
SELECT * FROM vue_emprunts_en_cours;