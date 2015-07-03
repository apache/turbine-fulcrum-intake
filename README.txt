--------------------------------------------------------------------------
F U L C R U M
--------------------------------------------------------------------------

Fulcrum is a collection of components originally part of the Turbine core
project that are suitable for use in any environment.  They are designed to
be used within any Avalon-compatible container.


--------------------------------------------------------------------------
B U I L D I N G
--------------------------------------------------------------------------
You must have Maven 2.x. or 3.x

Building the Fulcrum from SVN is very easy.  Fulcrum has been
Maven-enabled.  Please refer to the Maven Getting Started document for
instructions on building.  This document is available here:

https://maven.apache.org/guides/getting-started/


--------------------------------------------------------------------------
COMPONENT DEVELOPMENT  
--------------------------------------------------------------------------
Publishing Workflow

Prerequisites
 // jars
 mvn deploy -Papache-release

More Information
  https://www.apache.org/dev/publishing-maven-artifacts.html#prepare-poms
  http://maven.apache.org/developers/website/deploy-component-reference-documentation.html
  
Steps
1) Local Testing
  // Verify gpg.homedir, gpg.useagent, gpg.passphrase. Check, if -Dgpg.useagent=false is needed
  mvn clean site install -Papache-release -Dgpg.passphrase=<xx> 
  // multi module
  mvn release:prepare -DdryRun=true -DautoVersionSubmodules=true -Papache-release 
  // single
  mvn release:prepare -DdryRun=true -Papache-release 
  mvn release:clean

2) Remote Testing
  // multi module
  mvn release:prepare -DautoVersionSubmodules=true -P apache-release -Dusername=<username> -Dpassword=<pw>
  // single
  mvn release:prepare -P apache-release -Dusername=<username> -Dpassword=<pw>
  // Helpful hint from Apache Website: If you're located in Europe then release:prepare may fail with 'Unable to tag SCM' and ' svn: No such revision X '. Wait 10 seconds and run mvn release:prepare again.
  
4) Release Preparing
  // performs an upload to repository.apache.org/service/local/staging/deploy/maven2/
  // Hint: Add -Dgpg.useagent=false helps, if running from a windows machine to avoid hanging while gpg plugin signing process ..
  mvn release:perform 
  
  You could find more Information here: http://www.sonatype.com/books/nexus-book/reference/staging.html
  
5) Close the staging
  Login and close in Nexus Repo
  https://repository.apache.org/index.html#stagingRepositories
  More Information available: https://www.apache.org/dev/publishing-maven-artifacts.html#close-stage
  
6) Prepare Voting Information and Voting
  ....
  
7) Either Promote / Publish or Drop and Restage
  http://www.apache.org/dev/publishing-maven-artifacts.html#promote
  http://www.apache.org/dev/publishing-maven-artifacts.html#drop
  After Drop "reverse merge the release prepare, manually delete tag in svn repo and drop staged repository in nexus and start again with step 1.
  
8)  Stage the latest documentation 
  // http://maven.apache.org/developers/website/deploy-component-reference-documentation.html
  // SVN Checkout <tagged release version> source
  // Generate and Publish Site
  mvn site site:stage scm-publish:publish-scm -Dscmpublish.dryRun=true
  mvn clean site site:stage scm-publish:publish-scm -Dusername=<username> -Dpassword=<pw>
 
9) Distribution 
  // Cft. http://www.apache.org/dev/release.html#host-GA and http://www.apache.org/dev/release-publishing.html#distribution
  // SVN checkout target distribution from https://dist.apache.org/repos/dist/release/turbine/<...>/<...>
  // SVN checkout released source from https://svn.apache.org/repos/asf/turbine/<..>/<..>
  // Generate artifacts (check local repo or target for artifacts)
  mvn clean install package -Papache-release -DcreateChecksum=true
  // SVN Add <binaries>, <sources> artifacts (jar/zip/tar.gz,asc,md5,sha1 files) to target repo
  // SVN Remove old releases binaries and sources 
   