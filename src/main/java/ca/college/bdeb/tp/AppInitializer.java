package ca.college.bdeb.tp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Listener pour initialiser l'application au démarrage
 * Initialise H2 Database et crée 5 étudiants si la BD est vide
 */
public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("\n========================================");
        System.out.println("INITIALISATION DE L'APPLICATION");
        System.out.println("========================================\n");

        // Initialiser la connexion H2 et créer les tables
        DatabaseManager dbManager = DatabaseManager.getInstance();
        System.out.println("[INFO] " + DatabaseManager.getConsoleUrl() + "\n");

        StudentRepository repository = StudentRepository.getInstance();

        // Initialiser SEULEMENT si la base de données est vide (première initialisation)
        if (repository.getStudentCount() == 0) {
            System.out.println("Base de données vide. Initialisation des 5 étudiants de base...\n");

            // Création de 5 étudiants
            Student student1 = new Student("Tremblay", "Alice", "Informatique");
            Student student2 = new Student("Gagnon", "Marc", "Réseaux et Télécommunications");
            Student student3 = new Student("Roy", "Sophie", "Cybersécurité");
            Student student4 = new Student("Côté", "Jean", "Développement Web");
            Student student5 = new Student("Bouchard", "Marie", "Intelligence Artificielle");

            // Ajout dans la base de données
            int id1 = repository.addStudent(student1);
            int id2 = repository.addStudent(student2);
            int id3 = repository.addStudent(student3);
            int id4 = repository.addStudent(student4);
            int id5 = repository.addStudent(student5);

            if (id1 > 0 && id2 > 0 && id3 > 0 && id4 > 0 && id5 > 0) {
                System.out.println("✓ 5 étudiants initialisés avec succès dans H2 Database.\n");
            } else {
                System.err.println("⚠ Erreur lors de l'initialisation de certains étudiants.\n");
            }
        } else {
            System.out.println("Base de données contient déjà " + repository.getStudentCount() + 
                             " étudiant(s). Conservation des données persistées.\n");
        }

        // Affichage dans la console (toujours)
        System.out.println("Liste actuelle des étudiants (" + repository.getStudentCount() + " étudiants) :");
        System.out.println("----------------------------------------");
        for (Student student : repository.getAllStudents()) {
            System.out.println(String.format("[ID:%d] %s", student.getId(), student));
        }
        System.out.println("----------------------------------------\n");
        System.out.println("✅ Application prête !");
        System.out.println("   → Liste étudiants : /students");
        System.out.println("   → Export CSV : /export?format=csv");
        System.out.println("   → Export SQL : /export?format=sql\n");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("\n========================================");
        System.out.println("ARRÊT DE L'APPLICATION");
        System.out.println("========================================");
        
        // Fermer la connexion H2
        DatabaseManager.getInstance().closeConnection();
        System.out.println("✓ Connexion H2 fermée. Données persistées.\n");
    }
}
