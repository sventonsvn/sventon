

### What is sventon ###
sventon is a pure java web application for browsing [Subversion](http://subversion.apache.org) version control system repositories.

### What features does sventon have? ###
Read the [feature list](http://www.sventon.org/index.php?page=features)

### What do I need to run sventon? ###
[Java SE 6](http://www.oracle.com/technetwork/java/javase/downloads/) or higher and a servlet container like [Tomcat 6.0](http://tomcat.apache.org/). The Java SE 6 is a requirement since sventon 2.5.0. sventon 2.1.x requires Java 1.5 or higher.

### What platforms are supported? ###
Any platform capable of running a servlet container with [Java SE 6](http://www.oracle.com/technetwork/java/javase/downloads/) should do.

### What configurations have been tested? ###
We test all releases on Tomcat 6.0, Java 1.6 running on WinXP and OS X. Users have reported successful deployment on servers such as Resin, Glassfish, JBoss and OC4J.

### How do I install and configure sventon? ###
See [Installing](Installing.md)

### I get an error trying to install sventon on Jetty, what could be wrong? ###
See [Installation Issues](Installing#InstallationIssues.md)

### I am running sventon on a private server behind a public web dispatcher or proxy but the internal URLs are pointing to the private server's IP. How do I fix that? ###
Check out http://tomcat.apache.org/tomcat-6.0-doc/config/ajp.html

### How do I uninstall sventon? ###
Remove the file `svn.war` from the webapps directory and the subdirectory `svn`. The configuration and the cache files will be kept in a subdirectory called `sventon_config` in the container's temp directory. Find it and delete it.

### How does the cache/indexing work? ###
The cache/index function, if configured to be active, enables features such as searching, directory flattening and log message search. The first time sventon is started all revisions will be cached aswell as the repository HEAD. This can take a couple of minutes depending on network speed and the number of entries.

Each time a cache dependant action is triggered, the cache will be updated if necessary. There's also a scheduled job that triggers regularly to ensure that the index is up-to-date. The polling interval is default set to 10 minutes, but can be customized in `WEB-INF/applicationContext.xml`, look for the bean id `repositoryChangeMonitorUpdateTrigger` and the property `repeatInterval`.

A good reason for disabling the index is if the repository is really large and contains a lot of branches and tags, or if the network connection to it is just too slow.

### How do I upgrade sventon to a newer version? ###
Simply replace the old `svn.war` file. The configuration files are stored in the container's temp directory, so they will be reused automatically. Note that some upgrades require reindexing. Refer to the file `upgrade.txt` in the distribution for further information.

### Will sventon in any way jeopardize my Subversion repository? ###
No. Unless something really scary is going on, sventon will only perform read operations.

### Will sventon write any information to my Windows registry? ###
No.

### Why is not file type XYZ colorized when displayed in sventon? ###
sventon uses the [JHighlight](https://jhighlight.dev.java.net/) library to colorize files, which supports a fairly big, but limited, number of file formats.
The file extension mapping is done in `WEB-INF/applicationContext.xml` to enable easy modification or additions.

### Why is the binary file type XYZ treated as a text file when I view it in sventon? ###
Subversion's binary detection algorithm sometimes fails for binary files.
Since sventon 1.3 it is possible to override the detection by adding file extensions to the `textFileExtensionPattern` and the `binaryFileExtensionPattern` in `WEB-INF/sventon-servlet.xml`

### Is there a log file for sventon? ###
Yes, it's called `sventon.log` and is by default written to the container's temp directory. Logging can be customized by editing the file `WEB-INF/classes/log4j.properties`.

### Where are configuration and cache files stored? ###
The sventon configuration files and cache databases are by default stored in the servlet container's temp directory, in Tomcat this is `<tomcat home>/temp`. The default location can be changed by editing `WEB-INF/applicationContext.xml`. Find the bean with id `sventonTempRootDir` and replace the value `${java.io.tmpdir`} with the preferred location. Make sure the servlet container has read and write access to this location.

### Does sventon support non-US-ASCII charsets? ###
Yes. It is hard to test all possible combinations, so please report issues if you find any.
For Tomcat to work correctly with non-US-ASCII charsets, the Tomcat connector attribute `URIEncoding` should be set to UTF-8, or alternatively the attribute `useBodyEncodingForURI` should be set to true.

### Can I send suggestions for new functions? ###
Yes, please do! But we cannot guarantee when or if your suggestion will be implemented.

### Will sventon be avaliable for CVS (or any other version control system) in the future? ###
No, there are currently no such plans.

### What license do you use? ###
GPLv3. See http://svn.sventon.org/repos/berlios/show/trunk/LICENSE.txt

### What's the deal with JavaHL vs. SVNKit? ###
Starting with version 2.5.x sventon is supplied in two versions, one using SVNKit and one using JavaHL. The SVNKit version is pure Java and easier to install but comes with a a license that prohibits use in closed source projects. JavaHL requires a bit more work to install, but is licensed under Apache License, Version 2.0. Pick your poison.

### Why did you start this project? ###
Well, we kind of needed the functionality but we couldn't find what we were looking for, so we went ahead and started this project.

### Is there a publicly running version of Sventon that I can try? ###
Yes! You can browse the sventon Subversion repository using [sventon](http://svn.sventon.org/).
Please note that the repository and the sventon instance is hosted in two geographically different locations. This could make the sventon instance response times sluggish from time to time.

Also, have a look at the [screenshots](http://www.sventon.org/index.php?page=screenshots) to get an idea of what a running sventon installation could look like.

### Can I change the layout of the RSS feed? ###
Yes. It's template based, and the default template file is called `rsstemplate.html` and is located in `WEB-INF/classes`. Edit it the way you want it. See [RSSTemplate](RSSTemplate.md) for substitution properties.

### Can I use sventon together with my favorite tool XYX? ###
Probably. See [Integrating](Integrating.md) for a list of known tools with sventon support.

### Where can I find the change history? ###
[Here](http://svn.sventon.org/repos/berlios/show/trunk/changes.txt?revision=HEAD&format=raw)!

### How do I enable the administrator functionality? ###
Edit the file `WEB-INF/applicationContext.xml` and change the property `editableConfig` to `true` and set a login password by changing the property `configPassword` to something other than the default `password`.

### How do I add or remove a repository in an already configured sventon instance? ###
First you will have to make sure the admin functionality is enabled. Simply browse to `/repos/listconfigs`, eg. http://localhost/svn/repos/listconfigs.

### How do I change the default diff view? ###
Edit the file `WEB-INF/applicationContext.xml` and change the constant `defaultDiffView` to `inline`, `sidebyside` or `unified`.

### How do I change the default location of the configuration and cache files? ###
Edit the file `WEB-INF/applicationContext.xml`, locate the text `sventonTempRootDir` and change the default value from `${java.io.tmpdir}` to a directory of your choice. An easier way is to set the system property `sventon.dir`, which will also simplify a future upgrade of your sventon installation, as you won't have to re-edit the XML file. Note that the location of the log file is controlled by the settings in the file `WEB-INF/classes/log4j.properties`.

### Can I run more than one sventon instance in the same servlet container? ###
Yes, it works out of the box if you use a newer servlet container that supports `getContextPath()`, eg. [Tomcat 6.0](http://tomcat.apache.org/) or later. Otherwise you can always change the location of the config files  manually.

### How can I use Glorbosoft XYZ to maximize team productivity? ###
Many of our customers want to know how they can maximize productivity through our patented office groupware innovations. The answer is simple: first, click on the "File" menu, scroll down to "Increase Productivity", then...

Further reading: [Version Control with Subversion](http://svnbook.red-bean.com/en/1.1/svn-book.html#svn-foreword).