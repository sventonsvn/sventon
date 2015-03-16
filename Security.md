There are a few things to consider when setting up sventon for production use.

## Configuration files and logs ##
By default log files, cache databases, and configuration files, including any user ids and passwords supplied during sventon configuration, will be stored in the servlet container's temporary files directory. This ensures that the servlet container has read and write access to the files.

_These files may contain sensitive information, such as repository username and password and repository contents as part of the cache database, and are stored unencrypted in plain text._

Depending on the server set-up and usage it may be advisable to move these files to another location. Configuration files and caches are always stored together, while the log file may be stored in a different location.

The following FAQ entries describe how to change the location of these files:
  * [Change log file location](FAQ.md)
  * [Change configuration file and cache database location](FAQ.md)

## Repository access restriction ##
sventon does not provide any way to restrict repository access on its own, it relies on the mechanisms provided by Subversion. Access to the repository is protected by the mechanisms provided by the configured Subversion access protocol.

sventon only performs read operations and should never alter contents of the repository.

If the repository provides access restrictions you may configure sventon in one of two ways:

**1 Set a global user id and password in sventon**

The user id and password will be stored in the sventon configuration files and used when accessing the Subversion repository. Any user with access to sventon will have transparent acccess to the repository.

**2 User-based user id and password** (available starting with sventon 1.4)

By selecting user-based access when adding the repository to sventon, sventon will ask for username and password from the sventon user when she tries to access a restricted (part of a) repository. This information will be stored in the user session in the servlet container and used for accessing the repository. See note regarding user based authentication below.

In addition to this you may also configure your servlet container to restrict access to the sventon application, if supported.

## User-based authentication ##
When employing user-based authentication, user credentials will be temporarily stored in the servlet container session after the user has logged in. This information may be written to disk or sent over the network to other servlet containers at the container's discretion, depending on set-up. The information may also be extracted from the servlet container using various tools under certain circumstances.

## Contact regarding security issues ##
If you think you have discovered a security issue with sventon, please contact **sventonproject@gmail.com** and we will get back to you as soon as possible.