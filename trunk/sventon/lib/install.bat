call mvn install:install-file -Dfile=svnkit.jar -DpomFile=svnkit-pom.xml
call mvn install:install-file -Dfile=trilead.jar -DpomFile=trilead-pom.xml
call mvn install:install-file -Dfile=compass-2.2.0-M2.jar -DgroupId=org.compass-project -DartifactId=compass -Dversion=2.2.0-M2 -Dpackaging=jar
