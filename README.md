# MaryTTS Installer

A standalone cloud-based installer and wrapper for
[MaryTTS](http://mary.dfki.de/)

## Download

Just download the latest source code package from the [Releases
page](https://github.com/marytts/marytts-installer/releases) (the source code
*is* the installer!) and unpack it anywhere on your system.

## Usage

The MaryTTS Installer can be used on the command line, by changing to the
directory where it was unpacked and running tasks, for example to run
`install voice-cmu-slt`, do:

### OSX and Linux

    $ ./marytts install voice-cmu-slt
    
### for Centos 7
   $ .marytts install cmu-slt

### Windows

    > marytts install voice-cmu-slt

## First-time setup

The MaryTTS Installer uses [Gradle](https://gradle.org/), and will first
automatically download it if it isn't already installed.

Before MaryTTS can be used, one or more voices must be installed from the cloud.
This is done by running the `install` task with one or more selected voices,
for example, to install `voice-cmu-slt`, change into the directory where you
unpacked the installer and run

    marytts install voice-cmu-slt

If no voices are installed, `voice-cmu-slt-hsmm` will be installed by default.

## Common tasks

### Install a voice

    marytts install voice-cmu-slt

### List available voices

    marytts list

### Show details for an available voice

    marytts info voice-cmu-slt

### Uninstall an installed voice

    marytts uninstall voice-cmu-slt

Note that this is effectively the same as simply removing the corresponding
voice files from the `installed` directory.

## Run MaryTTS

    marytts server

or simply

    marytts

will start a local MaryTTS server with all installed voices.

### Verbose output

    marytts --info

or simply

    marytts -i

will print `INFO` level log messages from MaryTTS (and Gradle) to the console.

To get `DEBUG` level log output, run

    marytts --debug

or simply

    marytts -d

Of course it is also possible to customize the MaryTTS server's environment by
specifying arbitrary properties, just like when invoking `java` directly.

## Download cache

All downloaded artifacts for MaryTTS and its voices are cached in the local
Gradle home (`$HOME/.gradle` by default) so that they don't have to be
downloaded again if a voice is uninstalled and then installed again, or if
MaryTTS is also installed into additional directories. Note that this cache also
stores the packaged data files for unit-selection voices, which can be several
hundred MB in size.

### Cache cleaning

In order to free up storage space, it is possible to delete the cached files for
an individual voice, like so:

    marytts purge voice-cmu-slt

Of course this means that any subsequent installation of the corresponding voice
will require downloading the artifacts again. Note that purging a voice's cached
files will *not* uninstall it.

### Custom cache location

It is possible to customize the location of the download cache by running the
`install` tasks with the `--gradle-user-home` (or simply `-g`) parameter; i.e.,
to use some (possibly new) directory named `download` as the download cache, run

    marytts --gradle-user-home download install voice-cmu-slt

or simply

    marytts -g download install voice-cmu-slt

Note that this will also first download Gradle itself into the custom download
cache.
