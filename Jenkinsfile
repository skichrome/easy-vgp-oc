#!/usr/bin/env groovy

def emulatorNamePipeline = "emulator-pipeline"
def dockerAppName = "android-emulator-debian"
def dockerImageName = "skichrome/${dockerAppName}:1.1.0"

def androidBuildVersion = '28'
def androidBuildToolsVersion = '29.0.2'

def getRepoURL() {
    sh "git config --get remote.origin.url > .git/remote-url"
    return readFile(".git/remote-url").trim()
}

def getCommitSha() {
    sh "git rev-parse HEAD > .git/current-commit"
    return readFile(".git/current-commit").trim()
}

def updateGithubCommitStatus(build) {
    repoUrl = getRepoURL()
    commitSha = getCommitSha()

    step([
            $class            : 'GitHubCommitStatusSetter',
            reposSource       : [$class: "ManuallyEnteredRepositorySource", url: repoUrl],
            commitShaSource   : [$class: "ManuallyEnteredShaSource", sha: commitSha],
            errorHandlers     : [[$class: 'ShallowAnyErrorHandler']],
            statusResultSource: [
                    $class : 'ConditionalStatusResultSource',
                    results: [
                            [$class: 'BetterThanOrEqualBuildResult', result: 'SUCCESS', state: 'SUCCESS', message: build.description],
                            [$class: 'BetterThanOrEqualBuildResult', result: 'FAILURE', state: 'FAILURE', message: build.description],
                            [$class: 'AnyBuildResult', state: 'PENDING', message: 'Building and testing recently pushed commit']
                    ]
            ]
    ])
}

pipeline {
    agent any
    options {
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }
    stages {
        stage("Mise à jour du statut du repository Github") {
            steps {
                script {
                    updateGithubCommitStatus(currentBuild)
                }
            }
        }
        stage("Création du conteneur Docker") {
            when {
                branch 'dev'
            }
            steps {
                script {
                    build(job: 'create-android-emulator-in-docker-container',
                            parameters: [
                                    string(name: 'ANDROID_BUILD_VERSION', value: "${androidBuildVersion}"),
                                    string(name: 'ANDROID_BUILD_TOOLS_VERSION', value: "${androidBuildToolsVersion}"),
                                    string(name: 'EMULATOR_NAME', value: "${emulatorNamePipeline}"),
                                    string(name: 'APP_NAME', value: "${dockerAppName}"),
                                    string(name: 'DOCKER_NAME', value: "${dockerImageName}")
                            ]
                    )
                }
            }
        }
        stage("Compilation de l'application Android") {
            steps {
                withCredentials(
                        [
                                file(credentialsId: 'keystore-android', variable: 'STORE_FILE'),
                                string(credentialsId: 'keystore-password', variable: 'STORE_PASS'),
                                string(credentialsId: 'keystore-android-alias', variable: 'KEY_ALIAS'),
                                string(credentialsId: 'keystore-key-password', variable: 'KEY_PASS'),
                                file(credentialsId: 'acces-admin-firebase-app-distribution-file', variable: 'FIREBASE_APP_DISTRIBUTION_FILE')
                        ]
                ) {
                    sh '''
                    set +x
                    ./gradlew clean build 
                    '''
                }
            }
        }
        stage("Préparation de l'application pour les tests") {
            when {
                branch 'dev'
            }
            steps {
                withCredentials(
                        [
                                file(credentialsId: 'keystore-android', variable: 'STORE_FILE'),
                                string(credentialsId: 'keystore-password', variable: 'STORE_PASS'),
                                string(credentialsId: 'keystore-android-alias', variable: 'KEY_ALIAS'),
                                string(credentialsId: 'keystore-key-password', variable: 'KEY_PASS'),
                                file(credentialsId: 'acces-admin-firebase-app-distribution-file', variable: 'FIREBASE_APP_DISTRIBUTION_FILE')
                        ]
                ) {
                    sh '''
                    set +x
                    ./gradlew assembleAndroidTest
                    '''
                }
            }
        }
        stage("Lancement de l'émulateur dans le conteneur Docker") {
            when {
                branch 'dev'
            }
            steps {
                script {
                    build(job: 'launch-android-emulator-in-docker-container',
                            parameters: [
                                    string(name: 'EMULATOR_NAME', value: "${emulatorNamePipeline}"),
                                    string(name: 'APP_NAME', value: "${dockerAppName}"),
                                    string(name: 'DOCKER_NAME', value: "${dockerImageName}")
                            ]
                    )
                }
            }
        }
        stage("Exécution des tests unitaires et des tests instrumentalisés") {
            when {
                branch 'dev'
            }
            steps {
                script {
                    build(job: 'execute-android-tests-with-gradle',
                            parameters: [
                                    string(name: 'PARENT_PIPELINE_WS', value: "${WORKSPACE}")
                            ]
                    )
                }
            }
        }
        stage("Publication des rapports HTML") {
            steps {
                script {
                    publishHTML([
                            allowMissing         : false,
                            alwaysLinkToLastBuild: true,
                            keepAll              : false,
                            reportDir            : 'app/build/reports',
                            reportFiles          : 'lint-results.html',
                            reportName           : 'Lint report',
                            reportTitles         : 'Lint report'
                    ])
                }
            }
        }
        stage("Publication des rapports XML") {
            when {
                branch 'dev'
            }
            steps {
                script {
                    step([
                            $class           : 'JUnitResultArchiver',
                            allowEmptyResults: true,
                            testResults      : 'app/build/test-results/**/*.xml'
                    ])
                    step([
                            $class           : 'JUnitResultArchiver',
                            allowEmptyResults: true,
                            testResults      : 'app/build/outputs/androidTest-results/connected/*.xml'
                    ])
                }
            }
        }
        stage("Déploiement de la version de test") {
            when {
                branch 'test'
            }
            steps {
                withCredentials(
                        [
                                file(credentialsId: 'keystore-android', variable: 'STORE_FILE'),
                                string(credentialsId: 'keystore-password', variable: 'STORE_PASS'),
                                string(credentialsId: 'keystore-android-alias', variable: 'KEY_ALIAS'),
                                string(credentialsId: 'keystore-key-password', variable: 'KEY_PASS'),
                                file(credentialsId: 'acces-admin-firebase-app-distribution-file', variable: 'FIREBASE_APP_DISTRIBUTION_FILE')
                        ]
                ) {
                    // Avoid keystore.jks not found, because temp directory has changed at this stage
                    sh '''
                    set +x
                    ./gradlew assembleDemoRelease appDistributionUploadDemoRelease
                    '''
                }
            }
        }
        stage("Archivage des APK") {
            when {
                branch 'test'
            }
            steps {
                script {
                    archiveArtifacts artifacts: 'app/build/outputs/apk/release/*.apk'
                }
            }
        }
    }
    post {
        always {
            updateGithubCommitStatus(currentBuild)
        }
        cleanup {
            script {
                if (env.BRANCH_NAME == 'dev') {
                    build(job: 'stop-android-emulator-docker-container',
                            parameters: [
                                    string(name: 'APP_NAME', value: "${dockerAppName}")
                            ]
                    )
                }
            }
        }
        failure {
            emailext body: "<b>Projet : ${env.JOB_NAME}</b><br>" +
                    "Numéro de build : ${env.BUILD_NUMBER} <br>" +
                    " URL : ${env.BUILD_URL}<br><br>" +
                    "Quelque chose c'est mal passé avec le build de cette application," +
                    "et a besoin d'être fixé, connectez vous à Jenkins pour en savoir plus.",
                    from: '',
                    mimeType: 'text/html',
                    replyTo: '',
                    subject: " - Jenkins - ${env.JOB_NAME} - branch ${env.BRANCH_NAME} - ${currentBuild.currentResult}",
                    to: 'campeoltoni@gmail.com',
                    attachLog: true
            script {
                step([
                        $class           : 'JUnitResultArchiver',
                        allowEmptyResults: true,
                        testResults      : 'app/build/test-results/**/*.xml'
                ])
                step([
                        $class           : 'JUnitResultArchiver',
                        allowEmptyResults: true,
                        testResults      : 'app/build/outputs/androidTest-results/connected/*.xml'
                ])
            }
        }
    }
}