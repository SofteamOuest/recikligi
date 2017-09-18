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

        git url: 'https://github.com/yvzn/recikligi.git', branch: 'softeam'

        container('maven') {
            sh 'mvn clean install'
        }

        container('docker') {
            sh 'mkdir /etc/docker'
            sh 'echo {"insecure-registries" : ["registry.wildwidewest.xyz"]} > /etc/docker/daemon.json'
            sh 'docker login -u admin -p softeam44 registry.wildwidewest.xyz'
            sh "docker build -t ${dockerTagname} ."
            sh "docker push ${dockerTagname}"
        }

        container('kubectl') {
            sh "sed -i 's,@dockerTagname@,${dockerTagname},' src/main/kubernetes/recikligi.yml"
            sh 'kubectl --namespace=development --server=http://92.222.81.117:8080 apply -f src/main/kubernetes/recikligi.yml'
        }
    }
}
