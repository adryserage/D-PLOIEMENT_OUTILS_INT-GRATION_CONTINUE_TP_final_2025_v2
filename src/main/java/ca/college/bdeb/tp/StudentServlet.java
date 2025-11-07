package ca.college.bdeb.tp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet pour gérer les opérations sur les étudiants
 * - GET sans paramètres : liste tous les étudiants
 * - GET avec action=create : crée un nouvel étudiant
 */
public class StudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("create".equals(action)) {
            handleCreate(request, response);
        } else {
            handleList(request, response);
        }
    }

    /**
     * Liste tous les étudiants
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        StudentRepository repository = StudentRepository.getInstance();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Liste des Étudiants</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
        out.println("h1 { color: #333; }");
        out.println("table { border-collapse: collapse; width: 100%; max-width: 800px; background-color: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        out.println("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
        out.println("th { background-color: #4CAF50; color: white; }");
        out.println("tr:hover { background-color: #f5f5f5; }");
        out.println(".info { margin: 20px 0; padding: 10px; background-color: #e7f3fe; border-left: 4px solid #2196F3; }");
        out.println(".export-link { margin: 20px 0; }");
        out.println(".export-link a { display: inline-block; padding: 10px 20px; background-color: #FF9800; color: white; text-decoration: none; border-radius: 5px; margin-right: 10px; }");
        out.println(".export-link a:hover { background-color: #e68900; }");
        out.println(".add-form { margin: 20px 0; padding: 20px; background-color: white; max-width: 800px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        out.println("input[type=text] { padding: 8px; margin: 5px 0; width: 200px; }");
        out.println("input[type=submit] { padding: 10px 20px; background-color: #4CAF50; color: white; border: none; cursor: pointer; }");
        out.println("input[type=submit]:hover { background-color: #45a049; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>📚 Liste des Étudiants</h1>");
        
        out.println("<div class='info'>");
        out.println("<strong>Nombre total d'étudiants :</strong> " + repository.getStudentCount());
        out.println(" | <strong>Base de données :</strong> H2 (persistance fichier)");
        out.println("</div>");

        out.println("<div class='export-link'>");
        out.println("📥 <a href='" + request.getContextPath() + "/export'>Exporter les données</a>");
        out.println("</div>");

        out.println("<table>");
        out.println("<tr>");
        out.println("<th>ID</th>");
        out.println("<th>Prénom</th>");
        out.println("<th>Nom</th>");
        out.println("<th>Spécialité</th>");
        out.println("<th>Date Création</th>");
        out.println("</tr>");

        for (Student student : repository.getAllStudents()) {
            out.println("<tr>");
            out.println("<td>" + student.getId() + "</td>");
            out.println("<td>" + escapeHtml(student.getFirstName()) + "</td>");
            out.println("<td>" + escapeHtml(student.getLastName()) + "</td>");
            out.println("<td>" + escapeHtml(student.getSpecialty()) + "</td>");
            out.println("<td>" + (student.getCreatedAt() != null ? student.getCreatedAt().toString().substring(0, 19) : "N/A") + "</td>");
            out.println("</tr>");
        }

        out.println("</table>");

        // Formulaire pour ajouter un étudiant
        out.println("<div class='add-form'>");
        out.println("<h2>➕ Ajouter un étudiant</h2>");
        out.println("<form method='GET' action='" + request.getContextPath() + "/students'>");
        out.println("<input type='hidden' name='action' value='create'/>");
        out.println("<label>Prénom: <input type='text' name='firstName' required/></label><br/>");
        out.println("<label>Nom: <input type='text' name='lastName' required/></label><br/>");
        out.println("<label>Spécialité: <input type='text' name='specialty' required/></label><br/>");
        out.println("<input type='submit' value='Ajouter'/>");
        out.println("</form>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Crée un nouvel étudiant
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String specialty = request.getParameter("specialty");

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Création Étudiant</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
        out.println(".message { padding: 20px; max-width: 600px; margin: 20px auto; background-color: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        out.println(".success { border-left: 4px solid #4CAF50; }");
        out.println(".error { border-left: 4px solid #f44336; }");
        out.println("a { color: #2196F3; text-decoration: none; }");
        out.println("a:hover { text-decoration: underline; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        if (firstName != null && !firstName.trim().isEmpty() &&
            lastName != null && !lastName.trim().isEmpty() &&
            specialty != null && !specialty.trim().isEmpty()) {

            Student newStudent = new Student(lastName.trim(), firstName.trim(), specialty.trim());
            StudentRepository.getInstance().addStudent(newStudent);

            // Affichage dans la console
            System.out.println("[CREATION] Nouvel étudiant ajouté : " + newStudent);

            out.println("<div class='message success'>");
            out.println("<h2>✅ Étudiant créé avec succès !</h2>");
            out.println("<p><strong>Prénom :</strong> " + escapeHtml(newStudent.getFirstName()) + "</p>");
            out.println("<p><strong>Nom :</strong> " + escapeHtml(newStudent.getLastName()) + "</p>");
            out.println("<p><strong>Spécialité :</strong> " + escapeHtml(newStudent.getSpecialty()) + "</p>");
            out.println("<p><a href='" + request.getContextPath() + "/students'>← Retour à la liste</a></p>");
            out.println("</div>");

        } else {
            out.println("<div class='message error'>");
            out.println("<h2>❌ Erreur</h2>");
            out.println("<p>Tous les champs sont obligatoires (prénom, nom, spécialité).</p>");
            out.println("<p><a href='" + request.getContextPath() + "/students'>← Retour à la liste</a></p>");
            out.println("</div>");
        }

        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Échappe les caractères HTML pour éviter les injections XSS
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }
}
