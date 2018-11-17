FROM java
ENV version=0.0.3
MAINTAINER Tobias Eljasik-Swoboda ${version}
EXPOSE 8080/tcp
EXPOSE 8081/tcp
ADD ./target/c3-committee-${version}-SNAPSHOT.jar /opt/c3-committee/target/c3-committee-${version}-SNAPSHOT.jar
ADD ./c3-committee.yml /opt/c3-committee/target/c3-committee.yml
RUN java -jar /opt/c3-committee/target/c3-committee-${version}-SNAPSHOT.jar server /opt/c3-committee/target/c3-committee.yml