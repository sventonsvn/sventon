sventon is a Subversion web client for Java Servlet containers.

A J2SE 5.0 runtime and a servlet 2.4/JSP 2.0 compliant container is required for deployment, thus Tomcat 5.5 is a good container companion. You'll also need a Subversion server set up and configured with a repository. For info on Subversion, see http://subversion.tigris.org

A slightly modified verson of the JHighlight library from https://jhighlight.dev.java.net is used for
syntax highlighting.
Currently the library supports colorization of the following file formats:
* HTML/XHTML
* Java
* XML
* LZX
* RIFE (https://rife.dev.java.net)

Installation:
1. Download and unzip the file. 
2. Put the war-file in the appropriate application server directory, on standard Tomcat 5.5 installation this is in 'webapps'
3. Point your browser to <host:port>/svn
4. Enter basic configuration data
5. Restart the web applicaton context (or the entire container, if you like)
Note that the file names in the repository HEAD will be indexed at this point, if you have repository with many files (and/or tags or branches), this can take quite some time.

Support
See http://sventon.berlios.de

Source
If the sventon source was not included in the download, you can fetch it from http://sventon.berlios.de

License
License files are included in the download, please read them.

Many thanks to the developers of the libraries that sventon depends on, and to the developers of Subversion and TortoiseSVN!

Enjoy!

sventon project - http://sventon.berlios.de