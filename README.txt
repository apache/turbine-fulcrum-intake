--------------------------------------------------------------------------
F U L C R U M
--------------------------------------------------------------------------

Fulcrum is services framework.

target/     Temporary directory for building the project.
build/      Location of Ant build.xml and build.properties files.
src/        Location of Java sources and Torque templates.
xdocs/      Fulcrum documention in Anakia formatted tags.

--------------------------------------------------------------------------
B U I L D I N G
--------------------------------------------------------------------------
You must have ant version 1.4 or newer installed.

Building the Fulcrum from CVS is now very easy.  Fulcrum has been
Maven-enabled.  Please refer to the Maven Getting Started document for
instructions on building.  This document is available here:

http://jakarta.apache.org/turbine/maven/getting-started.html

Note: you'll also need Torque's Maven plugin. It can be installed by
checking out the jakarta-turbine-torque repository and running the
command 'maven plugin:install' in the 'src/maven-plugin' directory.
