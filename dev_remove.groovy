pipeline {
    agent { label 'pet' }

    stages {
        stage('Helm Uninstall dev-review') {
            steps {
                dir('Petclinic_HelmChart') {
                    script {
                        def services = [
                            [name: "config-server"],
                            [name: "discovery-server"],
                            [name: "admin-server"],
                            [name: "api-gateway"],
                            [name: "customers-service"],
                            [name: "genai-service"],
                            [name: "vets-service"],
                            [name: "visits-service"],
                            [name: "tracing-server"]

                        ]

                        services.each { service ->
                            echo "Uninstall ${service.name} "
                            sh """
                                helm uninstall ${service.name} --namespace petclinic-review || true
                            """

                        }

                        
                    }
                }
            }
        }


    }
}