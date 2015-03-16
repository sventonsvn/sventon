# Installing sventon #

_These instructions are for sventon 2.5.x_

**Prerequisites SVNKit**
  1. [Java SE 6](http://www.oracle.com/technetwork/java/javase/downloads/) or later.
  1. A servlet container supporting Servlet 2.4 and JSP 2.0, such as [Tomcat 6.0](http://tomcat.apache.org/) or later.
  1. A [Subversion](http://subversion.apache.org/) repository.
  1. sventon [installation file](http://www.sventon.org/).

**Prequisites JavaHL**
In addition to the items above:
  1. Subversion client with JavaHL bindings must be installed on your system.

**sventon installation**
  1. Drop the `svn.war` file in the webapps install folder on the server (in Tomcat this directory is called `webapps`).
  1. Point your browser to `http://<host>/svn` and follow on-screen instructions to configure sventon.
  1. Browse repository.

## Configuration files ##
The sventon configuration files and logs are by default stored in the servlet container's temp directory, in Tomcat this is `<tomcat home>/temp`.

The following FAQ entries describe how to change the location of these files:
  * [Change log file location](FAQ.md)
  * [Change configuration file and cache database location](FAQ.md)

## Upgrading ##
Some upgrades are drop-ins, others require that cache indices are rebuilt. Refer to the file `upgrade.txt` in the distribution package for further info.

## Installation issues ##
If you run Jetty 6.x and get an error like this:

```
org.apache.jasper.JasperException: /WEB-INF/tags/
clickableUrl.tag(23,2) PWC6340: According to the TLD, rtexprvalue is
true, and deferred-value is specified for the attribute items of the
tag handler org.apache.taglibs.standard.tag.rt.core.ForTokensTag, but
the argument for the setter method is not a java.lang.Object 
[...]
```
There is a version conflict with the `standard.jar` shipping with Jetty. The easiest way seem to be to simply delete the file `standard.jar` from sventon's `/WEB-INF/lib` directory.

If you run JBoss 5.1.0 you will get an error:

There is a version conflict with the `standard.jar` and `jstl.jar` shipping with JBoss. The easiest way seem to be to simply delete the file  `standard-1.1.2.jar` and `jstl-1.1.2.jar` from sventon's `/WEB-INF/lib` directory.

## Uninstalling ##
See FAQ entry [How do I uninstall sventon?](FAQ.md)

## Security ##
For production use, you may want to read up on sventon [Security](Security.md).