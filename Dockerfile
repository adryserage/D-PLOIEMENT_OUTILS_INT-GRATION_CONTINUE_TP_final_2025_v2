# =========================================================
# Dockerfile pour l'application TP Étudiants
# Base: Tomcat 9 avec JRE 17
# =========================================================

FROM tomcat:9.0.104-jre17

# Métadonnées de l'image
LABEL maintainer="votre.email@example.com"
LABEL description="Application TP Étudiants - Servlet + H2 Database"
LABEL version="2.0"

# Variables d'environnement
ENV CATALINA_HOME=/usr/local/tomcat
ENV APP_NAME=tp-etudiants

# Supprimer les applications par défaut de Tomcat
RUN rm -rf $CATALINA_HOME/webapps/*

# Copier le WAR dans le répertoire webapps de Tomcat
# Le fichier WAR doit être dans target/ après le build Maven
COPY target/tp-etudiants.war $CATALINA_HOME/webapps/tp-etudiants.war

# Créer le répertoire pour la base de données H2
RUN mkdir -p /app/data

# Exposer le port 8080
EXPOSE 8080

# Healthcheck pour vérifier que Tomcat répond
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/tp-etudiants/students || exit 1

# Démarrer Tomcat
CMD ["catalina.sh", "run"]
