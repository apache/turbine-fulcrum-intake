
Build instructions
------------------

1. Add the avalon-meta plugin to $MAVEN_HOME/plugins
   http://www.dpml.net/avalon-meta/plugins/avalon-meta-plugin-1.2-SNAPSHOT.jar

2. Build the crypto api jar file.

   $ cd api
   $ maven jar:install

3. Build the crypro implementation jar file. This build has a dependency
   on a abstract test case that includes the merlin container. It will 
   establish a test container from which the testcase can resolve the 
   service (functionally equivalent to the ECM abstract unit-test).

   $ cd ../impl
   $ maven jar:install


Deployment
----------

To use the component in a real deployment scenario you will need to 
download Merlin.  The latest snapshot build is available at the following
url:

  http://dpml.net/merlin/distributions/latest/

After downloading, make sure you add the plugins to $MAVEN_HOME/plugins
directory and declare you MERLIN_HOME environment variable.  After that
you should be ready to run.

$ cd crypto/impl
$ merlinx -execute target/fulcrum-crypto-impl-1.0-alpha-3.jar

The above command is telling Merlin to use the Maven repository to 
locate resources, to execute (meaning run up the component then 
shutdown), where the component is defined by a block descriptor
bundled in the jar file under /BLOCK-INF/block.xml.  The jar file
is itself implicitly added to the containers classloader.  You 
will not see much because the default logging level is set to INFO
and you component is logging everything at DEBUG.  To get an idea
of what is happening you can take a peak behind the scenes with
the following command:

$ merlinx -execute target/fulcrum-crypto-impl-1.0-alpha-3.jar -config conf/config.xml

or for even more noise:

$ merlinx -execute target/fulcrum-crypto-impl-1.0-alpha-3.jar -debug -info

