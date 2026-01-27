package dao;

import model.Livre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO {

    private Connection connection;

    public LivreDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Ajouter un livre
    public boolean ajouterLivre(Livre livre) {
        String sql = "INSERT INTO Livre (titre, auteur, categorie, nombreExemplaires, exemplaires_disponibles) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setString(3, livre.getCategorie());
            stmt.setInt(4, livre.getNombreExemplaires());
            stmt.setInt(5, livre.getNombreExemplaires());

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du livre: " + e.getMessage());
            return false;
        }
    }


    // Rechercher par titre
    public List<Livre> rechercherParTitre(String titre) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM Livre WHERE titre ILIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + titre + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Livre livre = new Livre(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getInt("nombreExemplaires")
                );
                livre.setNombreExemplaires(rs.getInt("exemplaires_disponibles"));
                livres.add(livre);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
        }
        return livres;
    }

    // Rechercher par auteur
    public List<Livre> rechercherParAuteur(String auteur) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM Livre WHERE auteur ILIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + auteur + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Livre livre = new Livre(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getInt("nombreExemplaires")
                );
                livre.setNombreExemplaires(rs.getInt("exemplaires_disponibles"));
                livres.add(livre);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
        }
        return livres;
    }

    // Rechercher par catégorie
    public List<Livre> rechercherParCategorie(String categorie) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM Livre WHERE categorie ILIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + categorie + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Livre livre = new Livre(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getInt("nombreExemplaires")
                );
                livre.setNombreExemplaires(rs.getInt("exemplaires_disponibles"));
                livres.add(livre);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
        }
        return livres;
    }

    // Obtenir tous les livres
    public List<Livre> obtenirTousLesLivres() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM Livre ORDER BY titre";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Livre livre = new Livre(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getInt("nombreExemplaires")
                );
                livre.setNombreExemplaires(rs.getInt("exemplaires_disponibles"));
                livres.add(livre);
            }
        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
        }
        return livres;
    }


    // Mettre à jour les exemplaires disponibles
    public boolean mettreAJourExemplaires(int livreId, int nouveauNombre) {
        String sql = "UPDATE livres SET exemplaires_disponibles = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, nouveauNombre);
            stmt.setInt(2, livreId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
            return false;
        }
    }

    // Supprimer un livre
    public boolean supprimerLivre(int id) {
        String sql = "DELETE FROM livres WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
            return false;
        }
    }


}
