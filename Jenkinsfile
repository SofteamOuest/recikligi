#!groovy
import java.text.SimpleDateFormat

podTemplate(label: 'recikligi-build-pod', nodeSelector: 'medium', containers: [
        containerTemplate(name: 'jnlp', image: 'jenkinsci/jnlp-slave:alpine'),
        containerTemplate(name: 'maven', image: 'maven:3.5.0', ttyEnabled: true, command: 'cat'),
        containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl', command: 'cat', ttyEnabled: true)],

        volumes: [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')]
) {

    node('recikligi-build-pod') {
        def dockerTagname = "registry.wildwidewest.xyz/repository/docker-repository/pocs/recikligi-${env.BUILD_ID}"

        stage('checkout sources'){
            git url: 'https://github.com/yvzn/recikligi.git', branch: 'softeam'
        }

        container('maven') {
            stage('build sources'){
                sh 'mvn clean install'
            }
        }

        container('docker') {
            stage('build docker image'){
                sh "docker build -t ${dockerTagname} ."

                sh 'mkdir /etc/docker'
                sh 'echo {"insecure-registries" : ["registry.wildwidewest.xyz"]} > /etc/docker/daemon.json'

                withCredentials([string(credentialsId: 'nexus_password', variable: 'NEXUS_PWD')]) {
                     sh "docker login -u admin -p ${NEXUS_PWD} registry.wildwidewest.xyz"
                }
                
                sh "docker push ${dockerTagname}"
            }
        }

        container('kubectl') {
            stage('deploy'){
                sh "sed -i 's,@dockerTagname@,${dockerTagname},' src/main/kubernetes/recikligi.yml"
                sh 'kubectl delete ing recikligi || :'
                sh 'kubectl delete svc recikligi || :'
                sh 'kubectl delete deployment recikligi || :'
                sh 'kubectl apply -f src/main/kubernetes/recikligi.yml'
            }
        }
    }
}
