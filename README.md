#New Installer Concept

* `MARYBASE/lib` is on classpath at runtime; all jars in it are loaded (languages, voices, etc.).

* `MARYBASE/installed` is a metadata cache for components (languages, voices) that have been installed into `MARYBASE/lib`.

* `MARYBASE/download` is a cache for components that have been downloaded, whether or not they are installed.

* the *new installer* works just like the `marytts-component-installer`, but is powered by Apache Ivy.

* Workflow for component installation:

	* resolve desired artifact (language or voice) with dependencies (in the case of voices, the corresponding language)
	
	* check if installed
	
	* check in cache
	
	* resolve from one of several remote repositories (these include [DFKI](http://mary.dfki.de/download/5.0-SNAPSHOT), Dropbox, and Google Drive)
	
	* retrieve (download) if necessary
	
	* deliver (unpack) to `MARYBASE/lib`
