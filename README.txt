--------------------------------------------------------------------------
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
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

-------------------------------------------
GIT READONLY
-------------------------------------------

You could use git to checkout current trunk:

git clone https://github.com/apache/turbine-fulcrum.git
git checkout -b remote-trunk remotes/origin/trunk

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
  // Verify gpg.homedir, gpg.useagent, gpg.passphrase. Check, if -Dgpg.useagent=false is needed,  see below comment to pinentry.
  mvn clean site install -Papache-release -Dgpg.passphrase=<xx> 
  // multi module
  mvn release:prepare -DdryRun=true -DautoVersionSubmodules=true -Papache-release 
  // single
  mvn release:prepare -DdryRun=true -Papache-release 
  // 
  mvn release:clean

2) Remote Testing
  // explicit authentication with -Dusername=<username> -Dpassword=<pw>
  // multi module
  mvn release:prepare -DautoVersionSubmodules=true -P apache-release
  // success will be on the master build, the others are skipped
  // single
  mvn release:prepare -P apache-release
  // Helpful hint from Apache Website: If you're located in Europe then release:prepare may fail with 'Unable to tag SCM' and ' svn: No such revision X '. Wait 10 seconds and run mvn release:prepare again.
  
4) Release Preparing
  //  
  // if you get a 401 error on the upload to repository.apache.org, make sure
  // that your mvn security settings are in place ~/.m2/settings.xml and ~/.m2/settings-security.xml 
  // For more information on setting up security see the encryption guide:
  //    http://maven.apache.org/guides/mini/guide-encryption.html
  //
  // performs an upload to repository.apache.org/service/local/staging/deploy/maven2/
  // Hint: Add -Dgpg.useagent=false helps, if running from a windows machine to avoid hanging while gpg plugin signing process 
  // .. this may happen, if you do not define the pinentry-program in gpg-agent.conf correctly ..
  mvn release:perform 
  
  // You could find more information here: http://www.sonatype.com/books/nexus-book/reference/staging.html
  
5) Close the staging
  // Login and close in Nexus Repo
  https://repository.apache.org/index.html#stagingRepositories
  // More information available: https://www.apache.org/dev/publishing-maven-artifacts.html#close-stage
  
6) Prepare Voting Information and Voting
  ....
  
7) Either Promote / Publish or Drop and Restage
  // http://www.apache.org/dev/publishing-maven-artifacts.html#promote
  // http://www.apache.org/dev/publishing-maven-artifacts.html#drop
  // After Drop "reverse merge the release prepare (i.e. mvn release:rollback if possible),
  // manually delete tag in svn repo (svn delete ..) and drop staged repository in nexus and start again with step 1.
  
8)  Stage the latest documentation 
  // http://maven.apache.org/developers/website/deploy-component-reference-documentation.html
  // SVN Checkout <tagged release version> source
  // Generate and Publish Site
  // multi module 
  mvn site site:stage scm-publish:publish-scm -Dscmpublish.dryRun=true
  mvn clean site site:stage scm-publish:publish-scm -Dusername=<username> -Dpassword=<pw>
  // single module (omit site:stage, which reqires site element definition in distributionManagement)
  mvn site scm-publish:publish-scm -Dscmpublish.dryRun=true
  mvn clean site scm-publish:publish-scm -Dusername=<username> -Dpassword=<pw>
 
9) Distribution 
  // http://www.apache.org/dev/release#upload-ci,
  // http://www.apache.org/dev/release.html#host-GA and 
  // http://www.apache.org/dev/release-publishing.html#distribution
  // - SVN checkout target distribution from https://dist.apache.org/repos/dist/release/turbine/<...>/<...>
  // - SVN checkout released source from https://svn.apache.org/repos/asf/turbine/<..>/tags/<..>
  // - Generate artifacts (check local repo and target for artifacts) from released version:
  mvn clean install package -Papache-release -DcreateChecksum=true
  // generate checksums in dist source/binaries folde. More information here: https://checker.apache.org/doc/README.html and 
  // https://checker.apache.org/dist/verify.html
  
  
  // If not all jars are included (assembly plugin _SHOULD_ run after jar generation), run a second time without clean
  // If no md5 files are in the target folder, check local repo
  
  // - SVN Add <binaries>, <sources> artifacts (jar/zip/tar.gz,asc,md5,sha1 files) to target repo
  // - SVN Remove old releases binaries and sources 
  // After repos/dist is updated an automatic email will be generated, if no update of the release database is done:
  https://reporter.apache.org/addrelease.html?turbine
   
