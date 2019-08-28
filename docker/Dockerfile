FROM amazonlinux

RUN yum update -y && yum install -y java-1.8.0-openjdk-devel.x86_64 && yum clean all
EXPOSE 8080 8081
COPY target/docker-files/standalone.jar standalone.jar
COPY start.sh start.sh

ENTRYPOINT ["bash", "start.sh"]
