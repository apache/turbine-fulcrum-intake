--------------------------------------------------------------------------
F U L C R U M
--------------------------------------------------------------------------

Fulcrum is services framework.

bin/        Temporary directory for building the project.
build/      Location of Ant build.xml and build.properties files.
src/        Location of Java sources and Torque templates.
xdocs/      Fulcrum documention in Anakia formatted tags.

--------------------------------------------------------------------------
B U I L D I N G
--------------------------------------------------------------------------

In order to build Fulcrum you must must set the following properties in
either your ${user.home}/build.properties file, or the build.properties
file provided in the Fulcrum build/ directory:

log4j.jar
bsf.jar (http://oss.software.ibm.com/developerworks/projects/bsf)
velocity.jar
village.jar (http://share.whichever.com/)
jdbc.jar
torque.jar
regexp.jar
xmlrpc.jar
xalan.jar
xerces.jar
servlet.jar
javamail.jar (http://java.sun.com/products/javamail/index.html)
jaf.jar (http://java.sun.com/products/javabeans/glasgow/jaf.html)

Alternately, you can set the property lib.repo to the directory acting
as a repository holding the JARs named by default.properties.

These are paths to all the JARs that are required for building
all the services. Soon there will be a mini build for each of
the individual services: each service can state it's 
dependencies and the overall build will simply not build
services whose requirements aren't satisfied.
