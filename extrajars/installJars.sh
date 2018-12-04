mvn install:install-file  -Dfile=xom-1.2.11.jar  -DgroupId=nu.xom  -DartifactId=xom  -Dversion=1.2.11  -Dpackaging=jar  -DgeneratePom=true
mvn install:install-file -DgroupId=org.eclipse.mylyn.wikitext -DartifactId=confluence -Dversion=1.8.0 -Dpackaging=jar -Dfile=org.eclipse.mylyn.wikitext.confluence.core_1.8.0.I20121130-0624.jar
mvn install:install-file -DgroupId=org.eclipse.mylyn -DartifactId=wikitext -Dversion=1.8.0 -Dpackaging=jar -Dfile=org.eclipse.mylyn.wikitext.core_1.8.0.I20121130-0624.jar
