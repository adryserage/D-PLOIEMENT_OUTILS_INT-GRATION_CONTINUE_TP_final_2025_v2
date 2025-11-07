package ca.college.bdeb.tp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository singleton pour gérer les étudiants avec H2 Database
 */
public class StudentRepository {
    private static StudentRepository instance;
    private final DatabaseManager dbManager;

    private StudentRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Retourne l'instance unique du repository
     */
    public static synchronized StudentRepository getInstance() {
        if (instance == null) {
            instance = new StudentRepository();
        }
        return instance;
    }

    /**
     * Ajoute un étudiant à la base de données
     * Retourne l'ID généré ou -1 en cas d'erreur
     */
    public int addStudent(Student student) {
        if (student == null) {
            return -1;
        }

        String sql = "INSERT INTO students (first_name, last_name, specialty) VALUES (?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setString(3, student.getSpecialty());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        student.setId(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[REPOSITORY] Erreur lors de l'ajout d'un étudiant");
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Retourne la liste de tous les étudiants
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, specialty, created_at FROM students ORDER BY id";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("id"),
                    rs.getString("last_name"),
                    rs.getString("first_name"),
                    rs.getString("specialty"),
                    rs.getTimestamp("created_at")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("[REPOSITORY] Erreur lors de la récupération des étudiants");
            e.printStackTrace();
        }
        
        return students;
    }

    /**
     * Retourne le nombre d'étudiants dans la BD
     */
    public int getStudentCount() {
        String sql = "SELECT COUNT(*) as total FROM students";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("[REPOSITORY] Erreur lors du comptage des étudiants");
            e.printStackTrace();
        }
        
        return 0;
    }

    /**
     * Trouve un étudiant par son ID
     */
    public Student getStudentById(int id) {
        String sql = "SELECT id, first_name, last_name, specialty, created_at FROM students WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getInt("id"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("specialty"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[REPOSITORY] Erreur lors de la recherche de l'étudiant #" + id);
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Supprime tous les étudiants (utile pour les tests)
     */
    public void clear() {
        String sql = "DELETE FROM students";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(sql);
            System.out.println("[REPOSITORY] Tous les étudiants ont été supprimés");
        } catch (SQLException e) {
            System.err.println("[REPOSITORY] Erreur lors du vidage de la table");
            e.printStackTrace();
        }
    }

    /**
     * Exporte les étudiants au format CSV
     */
    public String exportToCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Prénom,Nom,Spécialité,Date de Création\n");
        
        for (Student student : getAllStudents()) {
            csv.append(student.getId()).append(",")
               .append(escapeCsv(student.getFirstName())).append(",")
               .append(escapeCsv(student.getLastName())).append(",")
               .append(escapeCsv(student.getSpecialty())).append(",")
               .append(student.getCreatedAt()).append("\n");
        }
        
        return csv.toString();
    }

    /**
     * Exporte les étudiants au format SQL INSERT
     */
    public String exportToSQL() {
        StringBuilder sql = new StringBuilder();
        sql.append("-- Export des étudiants\n");
        sql.append("-- Généré le : ").append(new Timestamp(System.currentTimeMillis())).append("\n\n");
        
        for (Student student : getAllStudents()) {
            sql.append("INSERT INTO students (first_name, last_name, specialty) VALUES (")
               .append("'").append(escapeSql(student.getFirstName())).append("', ")
               .append("'").append(escapeSql(student.getLastName())).append("', ")
               .append("'").append(escapeSql(student.getSpecialty())).append("');\n");
        }
        
        return sql.toString();
    }

    /**
     * Échappe les valeurs pour CSV
     */
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Échappe les valeurs pour SQL
     */
    private String escapeSql(String value) {
        if (value == null) return "";
        return value.replace("'", "''");
    }
}
