$Id$

The Fulcrum Website Instructions
--------------------------------

The Fulcrum web site is based on .xml files which are transformed
into .html files using Maven.

<http://maven.apache.org/>

Using scm-publish Plugin, cft. http://commons.apache.org/site-publish.html for publishing:

Check first, that turbine.site.path matches the target pubScmUrl (e.g. by investigating the result of mvn help:effective-pom)

Once you have the site checked out locally, cd into your fulcrum-site directory and execute:

mvn site

This will build the documentation into the target/site/ directory. The output will show you which files got re-generated.

If you would like to make modifications to the web site documents, you simply need to edit the files in the xdocs/ directory.

Once you have built your documentation and confirmed that your changes are ok, you can check your .xml files back into Subversion.

To test what files would be deleted or updated run:

mvn site:stage scm-publish:publish-scm -Dscmpublish.dryRun=true

To deploy the site execute:

mvn site:stage scm-publish:publish-scm

To do this you need an account on the people.apache.org machine!!



