// ════════════════════════════════════════════════════════════════════════════
//  HealthConnect CI/CD Pipeline
//  - Parameterized: BUILD_SERVICE selects which microservice to build/deploy
//  - Stages: Checkout → Build → Test → Docker Build/Push → Deploy to DEV
//  - Mirrors the Optum pattern: per-service pipelines triggered independently
// ════════════════════════════════════════════════════════════════════════════

pipeline {

    agent any

    parameters {
        choice(
            name: 'BUILD_SERVICE',
            choices: [
                'all',
                'healthconnect-common',
                'discovery-server',
                'api-gateway',
                'auth-service',
                'eligibility-service',
                'payer-service',
                'claims-service',
                'authorization-service',
                'era-service',
                'document-service',
                'notification-service'
            ],
            description: 'Which service to build and deploy'
        )
        choice(
            name: 'DEPLOY_ENV',
            choices: ['dev', 'qa', 'uat', 'prod'],
            description: 'Target deployment environment'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip unit tests (use only for hotfixes)'
        )
    }

    environment {
        DOCKER_REGISTRY  = credentials('docker-registry-url')    // e.g. 123456789.dkr.ecr.ca-central-1.amazonaws.com
        DOCKER_CREDS     = credentials('docker-registry-creds')
        SONAR_TOKEN      = credentials('sonar-token')
        KUBECONFIG_FILE  = credentials('kubeconfig-dev')
        IMAGE_TAG        = "${env.BUILD_NUMBER}-${env.GIT_COMMIT?.take(7) ?: 'local'}"
        MAVEN_OPTS       = '-Xmx1024m'
    }

    tools {
        maven 'Maven-3.9'
        jdk   'JDK-8'
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
        disableConcurrentBuilds()
    }

    stages {

        // ── 1. Checkout ──────────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_MSG = sh(
                        script: 'git log -1 --pretty=%B',
                        returnStdout: true
                    ).trim()
                }
                echo "Branch: ${env.BRANCH_NAME} | Commit: ${env.GIT_COMMIT?.take(7)} | Service: ${params.BUILD_SERVICE}"
            }
        }

        // ── 2. Build (compile + package) ─────────────────────────────────────
        stage('Build') {
            steps {
                dir('healthconnect') {
                    script {
                        def mvnCmd = params.BUILD_SERVICE == 'all'
                            ? 'mvn clean package -DskipTests -q'
                            : "mvn -pl healthconnect-common,${params.BUILD_SERVICE} -am clean package -DskipTests -q"
                        sh mvnCmd
                    }
                }
            }
            post {
                failure { echo "Build failed — check compilation errors above." }
            }
        }

        // ── 3. Test ──────────────────────────────────────────────────────────
        stage('Test') {
            when {
                expression { return !params.SKIP_TESTS }
            }
            steps {
                dir('healthconnect') {
                    script {
                        def mvnCmd = params.BUILD_SERVICE == 'all'
                            ? 'mvn test'
                            : "mvn -pl healthconnect-common,${params.BUILD_SERVICE} -am test"
                        sh mvnCmd
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: '**/target/surefire-reports/*.xml'
                }
                failure { echo "Tests failed — deployment blocked." }
            }
        }

        // ── 4. Code Quality (SonarQube) ───────────────────────────────────────
        stage('Code Quality') {
            when {
                allOf {
                    expression { return !params.SKIP_TESTS }
                    branch 'main'
                }
            }
            steps {
                dir('healthconnect') {
                    withSonarQubeEnv('SonarQube') {
                        sh "mvn sonar:sonar -Dsonar.token=${env.SONAR_TOKEN} -DskipTests"
                    }
                }
            }
        }

        // ── 5. Docker Build & Push ────────────────────────────────────────────
        stage('Docker Build & Push') {
            when {
                expression {
                    return params.BUILD_SERVICE != 'healthconnect-common'
                }
            }
            steps {
                dir('healthconnect') {
                    script {
                        def services = params.BUILD_SERVICE == 'all'
                            ? ['discovery-server','api-gateway','auth-service','eligibility-service',
                               'payer-service','claims-service','authorization-service',
                               'era-service','document-service','notification-service']
                            : [params.BUILD_SERVICE]

                        docker.withRegistry("https://${env.DOCKER_REGISTRY}", 'docker-registry-creds') {
                            for (svc in services) {
                                def img = docker.build(
                                    "${env.DOCKER_REGISTRY}/healthconnect/${svc}:${env.IMAGE_TAG}",
                                    "-f ${svc}/Dockerfile ."
                                )
                                img.push()
                                img.push('latest')
                                echo "Pushed: healthconnect/${svc}:${env.IMAGE_TAG}"
                            }
                        }
                    }
                }
            }
        }

        // ── 6. Deploy to DEV ─────────────────────────────────────────────────
        stage('Deploy') {
            when {
                expression { return params.DEPLOY_ENV == 'dev' || env.BRANCH_NAME == 'main' }
            }
            steps {
                script {
                    def services = params.BUILD_SERVICE == 'all'
                        ? ['discovery-server','api-gateway','auth-service','eligibility-service',
                           'payer-service','claims-service','authorization-service',
                           'era-service','document-service','notification-service']
                        : [params.BUILD_SERVICE]

                    withCredentials([file(credentialsId: 'kubeconfig-dev', variable: 'KUBECONFIG')]) {
                        for (svc in services) {
                            sh """
                                kubectl set image deployment/${svc} \
                                    ${svc}=${env.DOCKER_REGISTRY}/healthconnect/${svc}:${env.IMAGE_TAG} \
                                    --namespace=healthconnect-${params.DEPLOY_ENV} \
                                    --kubeconfig=\$KUBECONFIG
                                kubectl rollout status deployment/${svc} \
                                    --namespace=healthconnect-${params.DEPLOY_ENV} \
                                    --timeout=120s \
                                    --kubeconfig=\$KUBECONFIG
                            """
                            echo "Deployed ${svc} → ${params.DEPLOY_ENV}"
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline SUCCESS — ${params.BUILD_SERVICE} @ ${env.IMAGE_TAG} → ${params.DEPLOY_ENV}"
        }
        failure {
            echo "Pipeline FAILED — ${params.BUILD_SERVICE} build #${env.BUILD_NUMBER}"
        }
        always {
            cleanWs()
        }
    }
}
