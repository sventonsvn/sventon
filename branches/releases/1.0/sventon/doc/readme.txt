-sventon release 1.0 RC2-

sventon is a Subversion web client for Java Servlet containers.

A J2SE 5.0 runtime and a servlet 2.4/JSP 2.0 compliant container is
required for deployment, thus Tomcat 5.5 is a good container companion.
You'll also need a Subversion server set up and configured with a
repository. For info on Subversion, see http://subversion.tigris.org

The JHighlight library from https://jhighlight.dev.java.net is used for
syntax highlighting.
Currently the library supports colorization of the following file formats:
* HTML/XHTML
* Java
* Groovy
* C++
* XML
* LZX
* RIFE (http://rifers.org)

Installation:
1. Download and unzip the file. 
2. Put the war-file in the appropriate application server directory,
   on a standard Tomcat 5.5 installation this is in 'webapps'.
3. Point your browser to http://<host:port>/svn
4. Enter and submit basic configuration data.
If the indexing feature has been configured to be active, the background
indexing process of the file names in the repository HEAD will start at
this point. If you have a repository with many files (and/or tags or branches),
this can take quite some time. You will be able to use sventon during the
indexing process, but the search and flatten buttons will be disabled until
it's done.

Support
See http://sventon.berlios.de

Source
If the sventon source was not included in the download, you can fetch it
from http://sventon.berlios.de

License
License files are included in the download, please read them.

Many thanks to the developers of the libraries that sventon depends on,
and to the developers of Subversion and TortoiseSVN!

Enjoy!

sventon project - http://sventon.berlios.de
