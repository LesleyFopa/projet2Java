package dao;

import dao.DatabaseConnection;
import model.Membre;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MembreDAO {
    private Connection connection;

    public MembreDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Inscrire un nouveau membre
    public boolean inscrireMembre(Membre membre) {
        String sql = "INSERT INTO Membre (nom, prenom, email, adhesionDate) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getPrenom());
            stmt.setString(3, membre.getEmail());
            stmt.setDate(4, Date.valueOf(membre.getAdhesionDate()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'inscription: " + e.getMessage());
            return false;
        }
    }

    // Rechercher par nom
    public List<Membre> rechercherParNom(String nom) {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM Membre WHERE nom ILIKE ? OR prenom ILIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nom + "%");
            stmt.setString(2, "%" + nom + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Membre membre = new Membre(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getDate("adhesiondate").toLocalDate()
                );
                membres.add(membre);
            }
        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
        }
        return membres;
    }

    // Supprimer un membre
    public boolean supprimerMembre(int id) {
        String sql = "DELETE FROM Membre WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
            return false;
        }
    }

    // Obtenir tous les membres
    public List<Membre> obtenirTousLesMembres() {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM Membre ORDER BY nom";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Membre membre = new Membre(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getDate("adhesiondate").toLocalDate()
                );
                membres.add(membre);
            }
        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
        }
        return membres;
    }
}