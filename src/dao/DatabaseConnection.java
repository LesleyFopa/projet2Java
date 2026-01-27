package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/bibliotheque_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password"; // Changez ce mot de passe si nécessaire
    private static Connection connection = null;

    // Charger le driver au démarrage
    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver PostgreSQL chargé avec succès.");
        } catch (ClassNotFoundException e) {
            System.err.println("ERREUR: Driver PostgreSQL non trouvé!");
            System.err.println("Assurez-vous que le fichier postgresql-42.7.4.jar est dans le classpath.");
            e.printStackTrace();
            System.exit(1); // Arrêter le programme si le driver n'est pas trouvé
        }
    }

    private DatabaseConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                System.out.println("Tentative de connexion à: " + URL);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion à la base de données établie avec succès!");
            } catch (SQLException e) {
                System.err.println("❌ Erreur de connexion à la base de données: " + e.getMessage());
                System.err.println("\n=== DIAGNOSTIC ===");
                System.err.println("Vérifiez que:");
                System.err.println("1. PostgreSQL est démarré (sudo systemctl status postgresql)");
                System.err.println("2. La base 'bibliotheque_db' existe (CREATE DATABASE bibliotheque_db;)");
                System.err.println("3. Le mot de passe est correct (actuel: '" + PASSWORD + "')");
                System.err.println("4. Le port 5432 est accessible");
                e.printStackTrace();
            }
        }
        return connection;
    }

    // Méthode pour tester la connexion
    public static void testConnection() {
        System.out.println("=== TEST DE CONNEXION ===");
        try (Connection testConn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("✅ Connexion réussie!");
            System.out.println("Database: " + testConn.getMetaData().getDatabaseProductName());
            System.out.println("Version: " + testConn.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            System.err.println("❌ Échec de la connexion: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion fermée.");
                connection = null;
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture: " + e.getMessage());
            }
        }
    }
}