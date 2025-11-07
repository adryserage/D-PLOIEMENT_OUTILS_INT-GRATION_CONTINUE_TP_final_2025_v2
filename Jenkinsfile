pipeline {
    agent any
    
    tools {
        maven 'Maven 3.9.6' // Nom configuré dans Jenkins Global Tool Configuration
        jdk 'JDK 17'        // Nom configuré dans Jenkins Global Tool Configuration
    }
    
    environment {
        // Variables d'environnement
        PROJECT_NAME = 'tp-etudiants'
        MAVEN_OPTS = '-Dmaven.test.failure.ignore=true'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Récupération du code source depuis GitHub...'
                checkout scm
                echo "✓ Branch: ${env.GIT_BRANCH}"
                echo "✓ Commit: ${env.GIT_COMMIT}"
            }
        }
        
        stage('Vérification Environnement') {
            steps {
                echo '🔍 Vérification des outils...'
                script {
                    if (isUnix()) {
                        sh 'java -version'
                        sh 'mvn -version'
                    } else {
                        bat 'java -version'
                        bat 'mvn -version'
                    }
                }
                echo "✓ Workspace: ${env.WORKSPACE}"
            }
        }
        
        stage('Clean') {
            steps {
                echo '🧹 Nettoyage des builds précédents...'
                script {
                    if (isUnix()) {
                        sh 'mvn clean'
                    } else {
                        bat 'mvn clean'
                    }
                }
            }
        }
        
        stage('Compile') {
            steps {
                echo '🔨 Compilation du projet...'
                script {
                    if (isUnix()) {
                        sh 'mvn compile'
                    } else {
                        bat 'mvn compile'
                    }
                }
            }
        }
        
        stage('Test') {
            steps {
                echo '🧪 Exécution des tests...'
                script {
                    try {
                        if (isUnix()) {
                            sh 'mvn test'
                        } else {
                            bat 'mvn test'
                        }
                    } catch (Exception e) {
                        echo "⚠️ Tests échoués ou absents (normal pour ce projet)"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    // Publier les résultats de tests (si présents)
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo '📦 Création du WAR...'
                script {
                    if (isUnix()) {
                        sh 'mvn package -DskipTests'
                    } else {
                        bat 'mvn package -DskipTests'
                    }
                }
            }
        }
        
        stage('Archive Artifact') {
            steps {
                echo '💾 Archivage de l\'artifact...'
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
                echo "✓ WAR archivé: target/${PROJECT_NAME}.war"
            }
        }
        
        stage('Quality Analysis') {
            steps {
                echo '📊 Analyse de qualité du code...'
                script {
                    try {
                        if (isUnix()) {
                            // Linux/Mac
                            sh 'mvn dependency:tree'
                            sh 'mvn versions:display-dependency-updates || true'
                        } else {
                            // Windows
                            bat 'mvn dependency:tree'
                            bat 'mvn versions:display-dependency-updates || exit 0'
                        }
                    } catch (Exception e) {
                        echo "⚠️ Analyse de qualité partielle"
                    }
                }
            }
        }
        
        stage('Deploy to Tomcat') {
            when {
                expression { env.GIT_BRANCH == 'origin/master' || env.GIT_BRANCH == 'master' }
            }
            steps {
                echo '🚀 Déploiement automatique vers Tomcat local...'
                script {
                    try {
                        if (isUnix()) {
                            // Linux/Mac
                            sh './deploy.sh local'
                        } else {
                            // Windows - Utiliser le script batch
                            bat 'deploy-tomcat.bat'
                        }
                        echo '✅ Application déployée avec succès sur Tomcat !'
                        echo '🌐 URL: http://localhost:8080/tp-etudiants/students'
                    } catch (Exception e) {
                        echo "❌ Échec du déploiement Tomcat"
                        echo "   Vérifiez que Tomcat est installé et accessible"
                        echo "   Chemin attendu: C:\\apache-tomcat-9.0.104"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
        
        stage('Build & Push Docker Image') {
            when {
                expression { env.GIT_BRANCH == 'origin/master' || env.GIT_BRANCH == 'master' }
            }
            steps {
                echo '🐳 Build et push de l\'image Docker vers Docker Hub...'
                script {
                    try {
                        // Credentials Docker Hub (à configurer dans Jenkins)
                        // Dashboard > Manage Jenkins > Manage Credentials > Add Credentials
                        // ID: 'ef706bd3-a2ae-4156-8eda-67e338eaa0af'
                        withCredentials([usernamePassword(
                            credentialsId: 'ef706bd3-a2ae-4156-8eda-67e338eaa0af',
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                        )]) {
                            // Nom de l'image Docker
                            def dockerImage = "${DOCKER_USER}/tp-etudiants:${env.BUILD_NUMBER}"
                            def dockerImageLatest = "${DOCKER_USER}/tp-etudiants:latest"
                            
                            echo "📦 Building Docker image: ${dockerImage}"
                            
                            if (isUnix()) {
                                // Linux/Mac
                                sh "docker build -t ${dockerImage} -t ${dockerImageLatest} ."
                                sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
                                sh "docker push ${dockerImage}"
                                sh "docker push ${dockerImageLatest}"
                                sh "docker logout"
                            } else {
                                // Windows
                                bat "docker build -t ${dockerImage} -t ${dockerImageLatest} ."
                                bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"
                                bat "docker push ${dockerImage}"
                                bat "docker push ${dockerImageLatest}"
                                bat "docker logout"
                            }
                            
                            echo "✅ Image Docker publiée avec succès !"
                            echo "🐳 Docker Hub: https://hub.docker.com/r/${DOCKER_USER}/tp-etudiants"
                            echo "   Tags: ${env.BUILD_NUMBER}, latest"
                        }
                    } catch (Exception e) {
                        echo "❌ Échec du déploiement Docker Hub"
                        echo "   Vérifiez que Docker est installé et les credentials configurés"
                        echo "   Error: ${e.message}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ ========================================='
            echo '✅ BUILD RÉUSSI !'
            echo '✅ ========================================='
            echo "Artifact: target/${PROJECT_NAME}.war"
            echo "Branch: ${env.GIT_BRANCH}"
            echo "Commit: ${env.GIT_COMMIT}"
            
            // Notifications (optionnel)
            // emailext subject: "✅ Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            //          body: "Le build a réussi!\n\nConsultez: ${env.BUILD_URL}",
            //          to: "votre.email@example.com"
        }
        
        failure {
            echo '❌ ========================================='
            echo '❌ BUILD ÉCHOUÉ !'
            echo '❌ ========================================='
            echo "Consultez les logs: ${env.BUILD_URL}console"
            
            // Notifications (optionnel)
            // emailext subject: "❌ Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            //          body: "Le build a échoué!\n\nConsultez: ${env.BUILD_URL}",
            //          to: "votre.email@example.com"
        }
        
        unstable {
            echo '⚠️ ========================================='
            echo '⚠️ BUILD INSTABLE (tests échoués)'
            echo '⚠️ ========================================='
        }
        
        always {
            echo '🧹 Nettoyage du workspace...'
            cleanWs deleteDirs: true, patterns: [
                [pattern: 'target/**', type: 'INCLUDE']
            ]
        }
    }
}
