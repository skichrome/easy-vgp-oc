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
            steps {
                script {
                    build(job: 'create-android-emulator-in-docker-container',
                            parameters: [
                                    string(name: 'ANDROID_BUILD_VERSION', value: "${androidBuildVersion}"),
                                    string(name: 'ANDROID_BUILD_TOOLS_VERSION', value: "${androidBuildToolsVersion}"),
                                    string(name: 'EMULATOR_NAME', value: "${emulatorNamePipeline}"),
                                    string(name: 'APP_NAME', value: "${dockerAppName}"),
                                    string(name: 'DOCKER_NAME', value: "${dockerImageName}")
                            ])
                }
            }
        }
        stage("Compilation de l'application Android") {
            steps {
                script {
                    build(job: 'compil-android-gradle-project',
                            parameters: [
                                    string(name: 'PARENT_PIPELINE_WS', value: "${WORKSPACE}"),
                            ])
                }
            }
        }
        stage("Lancement de l'émulateur dans le conteneur Docker") {
            steps {
                script {
                    build(job: 'launch-android-emulator-in-docker-container',
                            parameters: [
                                    string(name: 'EMULATOR_NAME', value: "${emulatorNamePipeline}"),
                                    string(name: 'APP_NAME', value: "${dockerAppName}"),
                                    string(name: 'DOCKER_NAME', value: "${dockerImageName}")
                            ])
                }
            }
        }
        stage("Exécution des tests unitaires et des tests instrumentalisés") {
            steps {
                script {
                    build(job: 'execute-android-tests-with-gradle',
                            parameters: [
                                    string(name: 'PARENT_PIPELINE_WS', value: "${WORKSPACE}")
                            ])
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
        stage("Archivage des APK") {
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
                build(job: 'stop-android-emulator-docker-container',
                        parameters: [
                                string(name: 'APP_NAME', value: "${dockerAppName}")
                        ])
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
                    subject: " - Jenkins - ${env.JOB_NAME} - ${currentBuild.currentResult}",
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