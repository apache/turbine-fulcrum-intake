--------------------------------------------------------------------------
F U L C R U M
--------------------------------------------------------------------------

Fulcrum is a collection of components originally part of the Jakarta Turbine
project that are suitable for use in any environment.  They are designed to
be used within any Avalon-compatible container.


--------------------------------------------------------------------------
B U I L D I N G
--------------------------------------------------------------------------
You must have Maven RC1 or better.

Building the Fulcrum from CVS is now very easy.  Fulcrum has been
Maven-enabled.  Please refer to the Maven Getting Started document for
instructions on building.  This document is available here:

http://maven.apache.org/start/index.html

Note: you'll also need Avalon's Maven plugin as well as the Merlin
plugin. It can be installed by running the plugin:download goal
like this:

$ maven plugin:download -DartifactId=avalon-meta-plugin -DgroupId=avalon-meta -Dversion=1.2
$ maven plugin:download -DartifactId=merlin-plugin -DgroupId=merlin -Dversion=1.1-SNAPSHOT
