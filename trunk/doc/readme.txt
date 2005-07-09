sventon is a Subversion web client for Java Servlet containers. It makes use 
of mainly two different sofware packages, TMate JavaSVN and Spring framework, 
all necessary libraries are included in the project.

Java 1.5 is required for build and deploy, thus Tomcat 5.5 may be a good 
container companion. Jakarta Ant build files are supplied for building.

The Ostermiller syntax highlighting library from http://ostermiller.org/syntax/ is used.
Currently the library supports colorization of the following file formats:
* HTML (html, htm)
* Java (java, jav)
* SQL (sql)
* C/C++ (c, h, cc, cpp, cxx, c++, hpp, hxx, hh)
* Java properties (properties, props)
* Latex (tex, sty, cls, dtx, ins, latex)

Build instructions:
1: Create file 'sventon.properties' in dir dev/java/conf and override properties
   from 'default-sventon.properties' and 'log4j.properties' in same path as needed.

2: Run ant/build.xml for info on building.

Contact sventon developers using the sventon dev mailing list: 
http://developer.berlios.de/projects/sventon

Many thanks to the developers of the libraries that sventon depends on!

Enjoy!