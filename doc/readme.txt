sventon is a Subversion web client for Java Servlet containers. It makes use 
of mainly two different sofware packages, TMate JavaSVN and Spring framework, 
all necessary libraries are included in the project.

Java 1.5 is required for build and deploy, thus Tomcat 5.5 may be a good 
container companion. Jakarta Ant build files are supplied for building.

Build instructions:
1: Edit dev/java/web/WEB-INF/sventon-servlet.xml, enter repository access 
  details.
2: Edit dev/java/web/WEB-INF/classs/log4j.properties, enter logging settings.

Run ant/build.xml for info on building.

Contact sventon developers using the sventon dev mailing list: 
http://developer.berlios.de/projects/sventon

Many thanks to the developers of sventon dependent libraries!

Enjoy!