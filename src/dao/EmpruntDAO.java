package dao;

import model.Emprunt;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpruntDAO {
    private Connection connection;

    public EmpruntDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Enregistrer un emprunt
    public boolean enregistrerEmprunt(Emprunt emprunt) {
        String sql = "INSERT INTO Emprunt (membre_id, livre_id, dateEmprunt, dateRetourPrevue) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, emprunt.getMembreId());
            stmt.setInt(2, emprunt.getLivreId());
            stmt.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            stmt.setDate(4, Date.valueOf(emprunt.getDateRetourPrevue()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
            return false;
        }
    }

    // Gérer le retour d'un livre
    public boolean retournerLivre(int empruntId) {
        String sql = "UPDATE Emprunt SET dateRetourEffective = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, empruntId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
            return false;
        }
    }

    // Obtenir les emprunts en cours
    public List<Emprunt> obtenirEmpruntsEnCours() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM Emprunt WHERE dateRetourEffective IS NULL";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Emprunt emprunt = new Emprunt(
                        rs.getInt("id"),
                        rs.getInt("membre_id"),
                        rs.getInt("livre_id"),
                        rs.getDate("dateEmprunt").toLocalDate(),
                        rs.getDate("dateRetourPrevue").toLocalDate()
                );
                Date retourEffectif = rs.getDate("dateRetourEffective");
                if (retourEffectif != null) {
                    emprunt.setDateRetourEffective(retourEffectif.toLocalDate());
                }
                emprunts.add(emprunt);
            }
        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
        }
        return emprunts;
    }

    // Obtenir les emprunts en retard
    public List<Emprunt> obtenirEmpruntsEnRetard() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM Emprunt WHERE dateRetourEffective IS NULL " +
                "AND date_retour_prevue < ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Emprunt emprunt = new Emprunt(
                        rs.getInt("id"),
                        rs.getInt("membre_id"),
                        rs.getInt("livre_id"),
                        rs.getDate("dateEmprunt").toLocalDate(),
                        rs.getDate("dateRetourPrevue").toLocalDate()
                );
                emprunts.add(emprunt);
            }
        } catch (SQLException e) {
            System.err.println("Erreur: " + e.getMessage());
        }
        return emprunts;
    }
}