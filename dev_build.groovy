pipeline {
    agent { label 'pet' }

    parameters {
        string(name: 'admin_server_branch', defaultValue: 'main', description: 'Branch cho admin-server')
        string(name: 'api_gateway_branch', defaultValue: 'main', description: 'Branch cho api-gateway')
        string(name: 'config_server_branch', defaultValue: 'main', description: 'Branch cho config-server')
        string(name: 'customer_service_branch', defaultValue: 'main', description: 'Branch cho customer-service')
        string(name: 'discovery_server_branch', defaultValue: 'main', description: 'Branch cho discovery-server')
        string(name: 'genai_service_branch', defaultValue: 'main', description: 'Branch cho genai-service')
        string(name: 'vets_service_branch', defaultValue: 'main', description: 'Branch cho vets-service')
        string(name: 'visits_service_branch', defaultValue: 'main', description: 'Branch cho visits-service')
    }

    stages {
        stage('Checkout HelmChart Repo') {
            steps {
                        sh "rm -rf spring-clinic-helm"
                        sh "git clone https://github.com/KhacThien88/spring-clinic-helm"
            }
        }

        stage('Helm Install dev-review') {
            steps {
                dir('spring-clinic-helm') {
                    script {
                        def services = [
                            [name: "config-server", branch: params.config_server_branch],
                            [name: "discovery-server", branch: params.discovery_server_branch],
                            [name: "admin-server", branch: params.admin_server_branch],
                            [name: "api-gateway", branch: params.api_gateway_branch],
                            [name: "customers-service", branch: params.customer_service_branch],
                            [name: "genai-service", branch: params.genai_service_branch],
                            [name: "vets-service", branch: params.vets_service_branch],
                            [name: "visits-service", branch: params.visits_service_branch]
                        ]

                        services.each { service ->
                            echo "Updating ${service.name} from branch: ${service.branch}"

                            def imageTag = ""
                            if (service.branch == 'main') {
                                imageTag = 'latest'
                            } else {
                                imageTag = sh(script: "git ls-remote ${SPRING_REPO} ${service.branch} | awk '{print \$1}' | cut -c1-7", returnStdout: true).trim()
                                commitMessages.add("${service.name}:${imageTag}")

                            }

                            echo "Using image tag: ${imageTag}"

                            sh """
                                helm dependency build charts/${service.name}/
                                helm upgrade --install ${service.name} charts/${service.name} \
                                    --namespace petclinic-review --create-namespace \
                                    -f charts/${service.name}/values.yaml \
                                    --set image.tag=${imageTag} \
                                    --set ingress.domainName=${service.name}-review.khacthienit.click
                            """
                        }

                        sh """
                                helm dependency build charts/tracing-server/
                                helm upgrade --install tracing-server charts/tracing-server \
                                    --namespace petclinic-review --create-namespace \
                                    -f charts/tracing-server/values.yaml \
                                    --set ingress.domainName=tracing-server-review.khacthienit.click
                            """
                    }
                }
            }
        }


    }
}