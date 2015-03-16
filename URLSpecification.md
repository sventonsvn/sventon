
---


## Show revision details ##
### /repos/your-repo-name/info ###
Parameters:
  * revision, defaults to "HEAD" if missing


---


## Resolve a path to list directory or show file contents ##
### /repos/your-repo-name/goto/some/path/or/file ###
Parameters:
  * revision, defaults to "HEAD" if missing


---


## List directory ##
### /repos/your-repo-name/list/some/path/ ###
Parameters:
  * revision, defaults to "HEAD" if missing
  * bypassEmpty, bypass empty directories, true/false
  * filterExtension, e.g. "jpg"


---


## Show file contents ##
### /repos/your-repo-name/show/some/file ###
Parameters:
  * revision, defaults to "HEAD" if missing
  * format, "raw" or empty
  * archivedEntry, path to entry inside archive
  * forceDisplay, force display of file contents of archived file, true/false


---


## Blame/annotate file ##
### /repos/your-repo-name/blame/some/file ###
Parameters:
  * revision, defaults to "HEAD" if missing


---


## Show log history for a file or directory ##
### /repos/your-repo-name/log/some/path/or/file ###
Parameters:
  * revision, defaults to "HEAD" if missing
  * stopOnCopy, true/false


---


## Get/download file ##
### /repos/your-repo-name/get/some/file ###
Parameters:
  * revision, defaults to "HEAD" if missing
  * disp, "attachment" (default), "inline" or "thumbnail"


---


## Show difference between current and previous version of a file ##
### /repos/your-repo-name/diff/some/file ###
Parameters:
  * revision, defaults to "HEAD" if missing


---


## Show difference between files or directories ##
### /repos/your-repo-name/diff/ ###
Parameters:
  * 2 entries containing path@revision, eg. entries=/some/path/or/file@123&entries=/some/path/or/file@456


---


## Get RSS feed for file or directory ##
### /xml/your-repo-name/rss/some/path/or/file ###
Parameters:
  * revision, defaults to "HEAD" if missing



---
