JAVA_HOME="C:/Program Files/Java/jdk1.8.0_91/";export JAVA_HOME;echo $JAVA_HOME
mvn package && "$JAVA_HOME/bin/java" -jar "target/Magneato-3.0-SNAPSHOT.jar" server config.yml
