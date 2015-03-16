_These instructions are for sventon 2.5.x_

The sventon team provides binaries for current sventon releases and betas. Occasionally developer previews are also available for download. Downloads can be found at http://www.sventon.org.

Starting with release 1.4 sventon is using Maven for building.

To build sventon from trunk, follow these steps (if you are running OS X 10.5, Maven and Subversion is already installed, you can skip step 1 and 2):

1. Download and install [Maven 2](http://maven.apache.org/)

2. Download and install [Subversion](http://subversion.tigris.org)

3. Export the code from the sventon Subversion repository:
```
svn export svn://svn.berlios.de/sventon/trunk sventon-build
```
This will create a `sventon-build` subdirectory containing all the source and libraries.

4. Build dependencies in `contrib`:
```
cd sventon-build
cd contrib

mvn install
```

5. Build sventon web application:
```
cd ..
mvn package assembly:assembly
```

When the build completes there will be two binary archives and one source archive created:

  1. `target/sventon-<version>-bin-javahl.zip` (JavaHL version)
  1. `target/sventon-<version>-bin-jsvnkit.zip` (SVNKit version)
  1. `target/sventon-<verson>-src.zip` (Source)