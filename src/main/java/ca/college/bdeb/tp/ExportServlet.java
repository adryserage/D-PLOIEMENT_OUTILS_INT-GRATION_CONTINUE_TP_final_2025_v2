package ca.college.bdeb.tp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Servlet pour exporter les étudiants en CSV ou SQL
 */
public class ExportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String format = request.getParameter("format");
        
        if (format == null || format.trim().isEmpty()) {
            showExportPage(request, response);
            return;
        }

        StudentRepository repository = StudentRepository.getInstance();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        switch (format.toLowerCase()) {
            case "csv":
                exportCSV(response, repository, timestamp);
                break;
            case "sql":
                exportSQL(response, repository, timestamp);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "Format invalide. Utilisez 'csv' ou 'sql'.");
        }
    }

    /**
     * Affiche la page d'export avec les options
     */
    private void showExportPage(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        StudentRepository repository = StudentRepository.getInstance();
        int count = repository.getStudentCount();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Exportation des Étudiants</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
        out.println("h1 { color: #333; }");
        out.println(".container { max-width: 800px; background-color: white; padding: 30px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        out.println(".info { margin: 20px 0; padding: 15px; background-color: #e7f3fe; border-left: 4px solid #2196F3; }");
        out.println(".export-option { margin: 20px 0; padding: 20px; background-color: #f9f9f9; border: 1px solid #ddd; border-radius: 5px; }");
        out.println(".export-option h3 { margin-top: 0; color: #4CAF50; }");
        out.println("a.button { display: inline-block; padding: 12px 24px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin: 10px 5px; }");
        out.println("a.button:hover { background-color: #45a049; }");
        out.println("a.button.secondary { background-color: #2196F3; }");
        out.println("a.button.secondary:hover { background-color: #0b7dda; }");
        out.println(".back-link { margin-top: 30px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<h1>📥 Exportation des Étudiants</h1>");
        
        out.println("<div class='info'>");
        out.println("<strong>Nombre d'étudiants à exporter :</strong> " + count);
        out.println("</div>");

        // Option CSV
        out.println("<div class='export-option'>");
        out.println("<h3>📊 Format CSV</h3>");
        out.println("<p>Exporte les données au format CSV (Comma-Separated Values) compatible avec Excel, Google Sheets, etc.</p>");
        out.println("<p><strong>Colonnes :</strong> ID, Prénom, Nom, Spécialité, Date de Création</p>");
        out.println("<a href='" + request.getContextPath() + "/export?format=csv' class='button'>Télécharger CSV</a>");
        out.println("</div>");

        // Option SQL
        out.println("<div class='export-option'>");
        out.println("<h3>💾 Format SQL</h3>");
        out.println("<p>Exporte les données sous forme d'instructions SQL INSERT prêtes à être exécutées.</p>");
        out.println("<p><strong>Utilisation :</strong> Importation dans une autre base de données ou sauvegarde</p>");
        out.println("<a href='" + request.getContextPath() + "/export?format=sql' class='button secondary'>Télécharger SQL</a>");
        out.println("</div>");

        out.println("<div class='back-link'>");
        out.println("<a href='" + request.getContextPath() + "/students'>← Retour à la liste des étudiants</a>");
        out.println("</div>");

        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Exporte au format CSV
     */
    private void exportCSV(HttpServletResponse response, StudentRepository repository, String timestamp)
            throws IOException {
        
        String filename = "etudiants_" + timestamp + ".csv";
        String csvContent = repository.exportToCSV();

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        
        PrintWriter out = response.getWriter();
        out.print(csvContent);
        out.flush();

        System.out.println("[EXPORT] CSV généré : " + filename + " (" + repository.getStudentCount() + " étudiants)");
    }

    /**
     * Exporte au format SQL
     */
    private void exportSQL(HttpServletResponse response, StudentRepository repository, String timestamp)
            throws IOException {
        
        String filename = "etudiants_" + timestamp + ".sql";
        String sqlContent = repository.exportToSQL();

        response.setContentType("application/sql; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        
        PrintWriter out = response.getWriter();
        out.print(sqlContent);
        out.flush();

        System.out.println("[EXPORT] SQL généré : " + filename + " (" + repository.getStudentCount() + " étudiants)");
    }
}
