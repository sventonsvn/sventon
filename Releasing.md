**TODO: Update!**

  1. Make sure your WC is updated, pointing at trunk and hasn't got any local modifications.
  1. Get the latest revision number (R) from the repository to be able to calculate the future release revision, R+3.
  1. Make sure everything bilds correctly by executing "mvn clean package assembly:assembly".
  1. Update the file "changes.txt" and add a row for the release containing the future revision (R+3) and the version/date.
  1. Update the web page web/download.php and include a link to the new, to be released, version.
  1. Commit the updated files and log "Prepared for release x.y.z.".
  1. Run "mvn clean release:prepare".
  1. Copy the artifacts from the target directory to the ftp servers at berlios.de and sventon.org.
  1. Create a new release at berlios.de.
  1. Upload the updated web pages to sventon.org.