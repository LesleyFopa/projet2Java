package service;

import dao.DatabaseConnection;
import dao.EmpruntDAO;
import dao.LivreDAO;
import dao.MembreDAO;
import model.Emprunt;
import model.Livre;
import model.Membre;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class BibliothequeService {
    private LivreDAO livreDAO;
    private MembreDAO membreDAO;
    private EmpruntDAO empruntDAO;
    private Scanner scanner;

    public BibliothequeService() {
        this.livreDAO = new LivreDAO();
        this.membreDAO = new MembreDAO();
        this.empruntDAO = new EmpruntDAO();
        this.scanner = new Scanner(System.in);
    }



    // Menu principal
    public void afficherMenu() {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n=== GESTION DE BIBLIOTHÈQUE ===");
            System.out.println("1. Gestion des Livres");
            System.out.println("2. Gestion des Membres");
            System.out.println("3. Gestion des Emprunts");
            System.out.println("4. Recherche de Livres");
            System.out.println("5. Calculer les Pénalités");
            System.out.println("0. Quitter");
            System.out.print("Choix: ");

            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer la nouvelle ligne

            switch (choix) {
                case 1:
                    menuGestionLivres();
                    break;
                case 2:
                    menuGestionMembres();
                    break;
                case 3:
                    menuGestionEmprunts();
                    break;
                case 4:
                    menuRechercheLivres();
                    break;
                case 5:
                    calculerPenalites();
                    break;
                case 0:
                    continuer = false;
                    System.out.println("Au revoir!");
                    DatabaseConnection.closeConnection();
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void menuGestionLivres() {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n=== GESTION DES LIVRES ===");
            System.out.println("1. Ajouter un livre");
            System.out.println("2. Afficher tous les livres");
            System.out.println("3. Supprimer un livre");
            System.out.println("0. Retour");
            System.out.print("Choix: ");

            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    ajouterLivre();
                    break;
                case 2:
                    afficherTousLesLivres();
                    break;
                case 3:
                    supprimerLivre();
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void ajouterLivre() {
        System.out.println("\n=== AJOUT D'UN NOUVEAU LIVRE ===");

        System.out.print("Titre: ");
        String titre = scanner.nextLine();

        System.out.print("Auteur: ");
        String auteur = scanner.nextLine();

        System.out.print("Catégorie: ");
        String categorie = scanner.nextLine();

        System.out.print("Nombre d'exemplaires: ");
        int nombreExemplaires = scanner.nextInt();
        scanner.nextLine();

        Livre livre = new Livre(0, titre, auteur, categorie, nombreExemplaires);

        if (livreDAO.ajouterLivre(livre)) {
            System.out.println("Livre ajouté avec succès!");
        } else {
            System.out.println("Erreur lors de l'ajout du livre.");
        }
    }

    private void afficherTousLesLivres() {
        System.out.println("\n=== LISTE DE TOUS LES LIVRES ===");
        List<Livre> livres = livreDAO.obtenirTousLesLivres();

        if (livres.isEmpty()) {
            System.out.println("Aucun livre dans la bibliothèque.");
        } else {
            for (Livre livre : livres) {
                System.out.println(livre.afficherDetails());
            }
            System.out.println("Total: " + livres.size() + " livre(s)");
        }
    }

    private void supprimerLivre() {
        System.out.print("\nID du livre à supprimer: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Êtes-vous sûr? (oui/non): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("oui")) {
            if (livreDAO.supprimerLivre(id)) {
                System.out.println("Livre supprimé avec succès!");
            } else {
                System.out.println("Erreur lors de la suppression.");
            }
        }
    }

    private void menuGestionMembres() {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n=== GESTION DES MEMBRES ===");
            System.out.println("1. Inscrire un nouveau membre");
            System.out.println("2. Rechercher un membre");
            System.out.println("3. Afficher tous les membres");
            System.out.println("4. Supprimer un membre");
            System.out.println("0. Retour");
            System.out.print("Choix: ");

            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    inscrireMembre();
                    break;
                case 2:
                    rechercherMembre();
                    break;
                case 3:
                    afficherTousLesMembres();
                    break;
                case 4:
                    supprimerMembre();
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void inscrireMembre() {
        System.out.println("\n=== INSCRIPTION D'UN NOUVEAU MEMBRE ===");

        System.out.print("Nom: ");
        String nom = scanner.nextLine();

        System.out.print("Prénom: ");
        String prenom = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        Membre membre = new Membre(0, nom, prenom, email, LocalDate.now());

        if (membreDAO.inscrireMembre(membre)) {
            System.out.println("Membre inscrit avec succès!");
        } else {
            System.out.println("Erreur lors de l'inscription.");
        }
    }

    private void rechercherMembre() {
        System.out.print("\nNom ou prénom à rechercher: ");
        String nom = scanner.nextLine();

        List<Membre> membres = membreDAO.rechercherParNom(nom);

        if (membres.isEmpty()) {
            System.out.println("Aucun membre trouvé.");
        } else {
            System.out.println("=== RÉSULTATS DE LA RECHERCHE ===");
            for (Membre membre : membres) {
                System.out.println( membre.afficherDetails());

            }
        }
    }

    private void afficherTousLesMembres() {
        System.out.println("\n=== LISTE DE TOUS LES MEMBRES ===");
        List<Membre> membres = membreDAO.obtenirTousLesMembres();

        if (membres.isEmpty()) {
            System.out.println("Aucun membre inscrit.");
        } else {
            for (Membre membre : membres) {
                System.out.println(membre.afficherDetails());
            }
            System.out.println("Total: " + membres.size() + " membre(s)");
        }
    }

    private void supprimerMembre() {
        System.out.print("\nID du membre à supprimer: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Êtes-vous sûr? (oui/non): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("oui")) {
            if (membreDAO.supprimerMembre(id)) {
                System.out.println("Membre supprimé avec succès!");
            } else {
                System.out.println("Erreur lors de la suppression.");
            }
        }
    }

    private void menuGestionEmprunts() {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n=== GESTION DES EMPRUNTS ===");
            System.out.println("1. Enregistrer un emprunt");
            System.out.println("2. Retourner un livre");
            System.out.println("3. Afficher les emprunts en cours");
            System.out.println("4. Afficher les emprunts en retard");
            System.out.println("0. Retour");
            System.out.print("Choix: ");

            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    enregistrerEmprunt();
                    break;
                case 2:
                    retournerLivre();
                    break;
                case 3:
                    afficherEmpruntsEnCours();
                    break;
                case 4:
                    afficherEmpruntsEnRetard();
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void enregistrerEmprunt() {
        System.out.println("\n=== ENREGISTRER UN EMPRUNT ===");

        System.out.print("ID du membre: ");
        int membreId = scanner.nextInt();

        System.out.print("ID du livre: ");
        int livreId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Durée de l'emprunt (en jours): ");
        int duree = scanner.nextInt();
        scanner.nextLine();

        LocalDate dateEmprunt = LocalDate.now();
        LocalDate dateRetourPrevue = dateEmprunt.plusDays(duree);

        // Vérifier la disponibilité du livre
        Livre livre = null;
        List<Livre> livres = livreDAO.rechercherParTitre(""); // Récupérer tous pour trouver par ID
        for (Livre l : livres) {
            if (l.getId() == livreId && l.getNombreExemplaires() > 0) {
                livre = l;
                break;
            }
        }

        if (livre == null) {
            System.out.println("Livre non disponible ou inexistant.");
            return;
        }

        Emprunt emprunt = new Emprunt(0, membreId, livreId, dateEmprunt, dateRetourPrevue);

        if (empruntDAO.enregistrerEmprunt(emprunt)) {
            // Mettre à jour le nombre d'exemplaires disponibles
            livreDAO.mettreAJourExemplaires(livreId, livre.getNombreExemplaires() - 1);
            System.out.println("Emprunt enregistré avec succès!");
            System.out.println("Date de retour prévue: " + dateRetourPrevue);
        } else {
            System.out.println("Erreur lors de l'enregistrement de l'emprunt.");
        }
    }

    private void retournerLivre() {
        System.out.print("\nID de l'emprunt à retourner: ");
        int empruntId = scanner.nextInt();
        scanner.nextLine();

        if (empruntDAO.retournerLivre(empruntId)) {
            System.out.println("Livre retourné avec succès!");

            // Calculer les pénalités éventuelles
            List<Emprunt> emprunts = empruntDAO.obtenirEmpruntsEnCours();
            for (Emprunt emprunt : emprunts) {
                if (emprunt.getIdEmprunt() == empruntId) {
                    emprunt.setDateRetourEffective(LocalDate.now());
                    double penalite = emprunt.calculPenalites(emprunt.getDateRetourPrevue(),LocalDate.now());
                    if (penalite > 0) {
                        System.out.printf("Pénalité à payer: %.2f F CFA%n", penalite);
                    }

                    // Remettre le livre en stock
                    LivreDAO livreDAO = new LivreDAO();
                    List<Livre> livres = livreDAO.obtenirTousLesLivres();
                    for (Livre livre : livres) {
                        if (livre.getId() == emprunt.getLivreId()) {
                            livreDAO.mettreAJourExemplaires(livre.getId(),
                                    livre.getNombreExemplaires() + 1);
                            break;
                        }
                    }
                    break;
                }
            }
        } else {
            System.out.println("Erreur lors du retour du livre.");
        }
    }

    private void afficherEmpruntsEnCours() {
        System.out.println("\n=== EMPRUNTS EN COURS ===");
        List<Emprunt> emprunts = empruntDAO.obtenirEmpruntsEnCours();

        if (emprunts.isEmpty()) {
            System.out.println("Aucun emprunt en cours.");
        } else {
            for (Emprunt emprunt : emprunts) {
                System.out.println("ID Emprunt: " + emprunt.getIdEmprunt());
                System.out.println("Membre ID: " + emprunt.getMembreId());
                System.out.println("Livre ID: " + emprunt.getLivreId());
                System.out.println("Date emprunt: " + emprunt.getDateEmprunt());
                System.out.println("Date retour prévue: " + emprunt.getDateRetourPrevue());
                if (emprunt.getDateRetourEffective().isAfter(emprunt.getDateRetourPrevue())) {
                    System.out.println("STATUS: EN RETARD!");
                }
                System.out.println("-------------------");

                // Vérifier si l'emprunt est en retard
                if (LocalDate.now().isAfter(emprunt.getDateRetourPrevue())) {
                    System.out.println("STATUS: EN RETARD!");
                    System.out.println("Jours de retard: " +
                            java.time.temporal.ChronoUnit.DAYS.between(
                                    emprunt.getDateRetourPrevue(), LocalDate.now()));
                } else {
                    System.out.println("STATUS: EN COURS");
                }
                System.out.println("-------------------");
            }
        }
    }

    private void afficherEmpruntsEnRetard() {
        System.out.println("\n=== EMPRUNTS EN RETARD ===");
        List<Emprunt> emprunts = empruntDAO.obtenirEmpruntsEnRetard();

        if (emprunts.isEmpty()) {
            System.out.println("Aucun emprunt en retard.");
        } else {
            for (Emprunt emprunt : emprunts) {
                System.out.println("ID Emprunt: " + emprunt.getIdEmprunt());
                System.out.println("Membre ID: " + emprunt.getMembreId());
                System.out.println("Livre ID: " + emprunt.getLivreId());
                System.out.println("Date emprunt: " + emprunt.getDateEmprunt());
                System.out.println("Date retour prévue: " + emprunt.getDateRetourPrevue());
                System.out.println("Jours de retard: " +
                        java.time.temporal.ChronoUnit.DAYS.between(
                                emprunt.getDateRetourPrevue(), LocalDate.now()));
                System.out.println("-------------------");
            }
        }
    }

    private void menuRechercheLivres() {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n=== RECHERCHE DE LIVRES ===");
            System.out.println("1. Par titre");
            System.out.println("2. Par auteur");
            System.out.println("3. Par catégorie");
            System.out.println("0. Retour");
            System.out.print("Choix: ");

            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    rechercherLivresParTitre();
                    break;
                case 2:
                    rechercherLivresParAuteur();
                    break;
                case 3:
                    rechercherLivresParCategorie();
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }

    private void rechercherLivresParTitre() {
        System.out.print("\nTitre à rechercher: ");
        String titre = scanner.nextLine();

        List<Livre> livres = livreDAO.rechercherParTitre(titre);
        afficherResultatsRecherche(livres);
    }

    private void rechercherLivresParAuteur() {
        System.out.print("\nAuteur à rechercher: ");
        String auteur = scanner.nextLine();

        List<Livre> livres = livreDAO.rechercherParAuteur(auteur);
        afficherResultatsRecherche(livres);
    }

    private void rechercherLivresParCategorie() {
        System.out.print("\nCatégorie à rechercher: ");
        String categorie = scanner.nextLine();

        List<Livre> livres = livreDAO.rechercherParCategorie(categorie);
        afficherResultatsRecherche(livres);
    }

    private void afficherResultatsRecherche(List<Livre> livres) {
        if (livres.isEmpty()) {
            System.out.println("Aucun livre trouvé.");
        } else {
            System.out.println("\n=== RÉSULTATS DE LA RECHERCHE ===");
            for (Livre livre : livres) {
                System.out.println(livre.afficherDetails());
            }
            System.out.println("Total: " + livres.size() + " livre(s) trouvé(s)");
        }
    }

    private void calculerPenalites() {
        System.out.println("\n=== CALCUL DES PÉNALITÉS ===");

        List<Emprunt> empruntsEnRetard = empruntDAO.obtenirEmpruntsEnRetard();
        double totalPenalites = 0;

        if (empruntsEnRetard.isEmpty()) {
            System.out.println("Aucune pénalité à calculer.");
            return;
        }

        System.out.println("Pénalités dues:");
        for (Emprunt emprunt : empruntsEnRetard) {
            emprunt.setDateRetourEffective(LocalDate.now());
            double penalite = emprunt.calculPenalites(emprunt.getDateRetourPrevue(),LocalDate.now());
            totalPenalites += penalite;

            System.out.printf("Emprunt ID %d: %.2f F CFA%n",
                    emprunt.getIdEmprunt(), penalite);
        }

        System.out.printf("\nTotal des pénalités: %.2f F CFA%n", totalPenalites);
    }
}