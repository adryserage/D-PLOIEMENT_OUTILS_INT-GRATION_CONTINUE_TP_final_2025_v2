#!/bin/bash

# Script de déploiement Tomcat (stub pour Railway Jenkins)
# Ce script simule un déploiement Tomcat pour la démo CI/CD
# En production locale, ce script copierait le WAR vers Tomcat

echo "========================================="
echo "DÉPLOIEMENT TOMCAT (MODE SIMULATION)"
echo "========================================="
echo ""
echo "Environnement: $1"
echo "Artifact: target/tp-etudiants.war"
echo ""

if [ ! -f "target/tp-etudiants.war" ]; then
    echo "❌ Erreur: WAR file not found!"
    exit 1
fi

echo "✅ WAR file trouvé: $(ls -lh target/tp-etudiants.war | awk '{print $5}')"
echo ""
echo "Note: Ce script est un stub pour Jenkins Railway."
echo "      Pour déploiement local réel, utilisez deploy-tomcat.bat (Windows)"
echo "      ou configurez CATALINA_HOME pour déploiement Linux/Mac."
echo ""
echo "✅ Simulation de déploiement réussie!"
echo "========================================="

exit 0
