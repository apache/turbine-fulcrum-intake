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

Building Fulcrum from CVS is fairly straightforward.  Changes
have been made to the Fulcrum build process to simplify the
acquisition of jar dependencies.  The entire build process is now
a four-step process.

The first step of the process is to obtain the source.  Checkout
the 'jakarta-turbine-fulcrum' repository.  You'll also need to
check out 'jakarta-turbine-torque' because the current build
process references templates in that source tree.  If you are
unfamiliar with the Jakarta CVS repositories, please refer to the
CVS Repositories document
(http://jakarta.apache.org/site/cvsindex.html) document for
assistance.

Next, you must define the 'lib.repo' property in your
'${user.home}/build.properties' file.  If you do not have a
'${user.home}/build.properties' file, create one in your home
directory and add the following line:

  lib.repo = /path/to/some/directory  

The value of this property determines the location that the
Fulcrum dependencies will be stored after they have been
downloaded.  Note: this directory must exist in the
filesystem.

Next, in the top-level directory of the Fulcrum distribution,
type the following command to download all of the
dependencies required to build Fulcrum:

  ant update-jars  

Lastly, after all of the jars have been downloaded to your
'lib.repo' directory, building the Fulcrum distribution is only a
matter of verifying that the 'torque.dir' property (defined in
'jakarta-turbine-fulcrum/default.properties') is set
correctly, and typing the following command:

  ant

The resulting jar file will be located in the 'bin' directory.  
