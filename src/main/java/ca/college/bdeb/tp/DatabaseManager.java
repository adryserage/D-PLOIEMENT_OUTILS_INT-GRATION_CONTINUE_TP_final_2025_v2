package ca.college.bdeb.tp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestionnaire de la base de données H2 embarquée
 * Singleton pour gérer la connexion unique à la BD
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    // Configuration H2 - fichier dans le répertoire utilisateur
    private static final String DB_URL = "jdbc:h2:~/tp_etudiants;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private DatabaseManager() {
        try {
            // Charger le driver H2
            Class.forName("org.h2.Driver");
            // Établir la connexion
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("[DATABASE] Connexion H2 établie : " + DB_URL);
            
            // Créer les tables si elles n'existent pas
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("[DATABASE] Erreur : Driver H2 non trouvé");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[DATABASE] Erreur de connexion à H2");
            e.printStackTrace();
        }
    }

    /**
     * Retourne l'instance unique du gestionnaire
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Retourne la connexion active
     */
    public Connection getConnection() {
        try {
            // Vérifier si la connexion est toujours valide
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("[DATABASE] Erreur lors de la reconnexion");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Crée les tables nécessaires si elles n'existent pas
     */
    private void createTables() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS students (
                id INT AUTO_INCREMENT PRIMARY KEY,
                first_name VARCHAR(100) NOT NULL,
                last_name VARCHAR(100) NOT NULL,
                specialty VARCHAR(200) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("[DATABASE] Table 'students' vérifiée/créée avec succès");
        } catch (SQLException e) {
            System.err.println("[DATABASE] Erreur lors de la création de la table");
            e.printStackTrace();
        }
    }

    /**
     * Ferme la connexion à la base de données
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DATABASE] Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("[DATABASE] Erreur lors de la fermeture de la connexion");
            e.printStackTrace();
        }
    }

    /**
     * Retourne l'URL de la console H2 Web
     */
    public static String getConsoleUrl() {
        return "Console H2 disponible à : http://localhost:8082 (JDBC URL: " + DB_URL + ")";
    }
}
