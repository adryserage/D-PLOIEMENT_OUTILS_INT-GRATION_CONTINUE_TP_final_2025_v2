@echo off
REM =========================================================
REM Script de déploiement automatique vers Tomcat local
REM Usage: deploy-tomcat.bat
REM =========================================================

SET PROJECT_NAME=tp-etudiants
SET WAR_FILE=target\%PROJECT_NAME%.war
SET TOMCAT_HOME=C:\apache-tomcat-9.0.104
SET TOMCAT_WEBAPPS=%TOMCAT_HOME%\webapps

echo =========================================
echo DEPLOIEMENT AUTOMATIQUE VERS TOMCAT
echo =========================================
echo.

REM Vérifier que le WAR existe
IF NOT EXIST "%WAR_FILE%" (
    echo [ERREUR] WAR non trouve: %WAR_FILE%
    echo          Verifiez que Maven a bien package le projet
    exit /b 1
)

echo [OK] WAR trouve: %WAR_FILE%
for %%A in ("%WAR_FILE%") do echo      Taille: %%~zA octets

REM Vérifier que Tomcat existe
IF NOT EXIST "%TOMCAT_WEBAPPS%" (
    echo [ERREUR] Repertoire Tomcat non trouve: %TOMCAT_WEBAPPS%
    echo          Verifiez CATALINA_HOME ou modifiez ce script
    exit /b 1
)

echo [OK] Tomcat webapps: %TOMCAT_WEBAPPS%
echo.

REM Supprimer l'ancienne application
echo [STEP 1] Suppression de l'ancienne application...
IF EXIST "%TOMCAT_WEBAPPS%\%PROJECT_NAME%" (
    rmdir /S /Q "%TOMCAT_WEBAPPS%\%PROJECT_NAME%"
    echo          Dossier supprime: %PROJECT_NAME%
)

IF EXIST "%TOMCAT_WEBAPPS%\%PROJECT_NAME%.war" (
    del /F /Q "%TOMCAT_WEBAPPS%\%PROJECT_NAME%.war"
    echo          WAR supprime: %PROJECT_NAME%.war
)

REM Attendre un peu pour que Tomcat libère les fichiers
timeout /t 2 /nobreak >nul

echo.
echo [STEP 2] Copie du nouveau WAR...
copy /Y "%WAR_FILE%" "%TOMCAT_WEBAPPS%\%PROJECT_NAME%.war"

IF %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Echec de la copie du WAR
    exit /b 1
)

echo          [OK] WAR copie vers: %TOMCAT_WEBAPPS%\%PROJECT_NAME%.war
echo.

REM Optionnel: Redémarrer Tomcat (si installé comme service Windows)
REM echo [STEP 3] Redemarrage de Tomcat...
REM net stop Tomcat9
REM timeout /t 3 /nobreak >nul
REM net start Tomcat9

echo =========================================
echo DEPLOIEMENT REUSSI !
echo =========================================
echo Application: %PROJECT_NAME%
echo WAR: %TOMCAT_WEBAPPS%\%PROJECT_NAME%.war
echo URL: http://localhost:8080/%PROJECT_NAME%/students
echo.
echo Tomcat deploiera automatiquement le WAR dans 10-15 secondes.
echo Consultez les logs Tomcat pour confirmer:
echo   %TOMCAT_HOME%\logs\catalina.out
echo.

exit /b 0
