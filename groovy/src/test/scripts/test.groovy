/**
  * Copyright 2004 Apache Software Foundation
  * Licensed  under the  Apache License,  Version 2.0  (the "License");
  * you may not use  this file  except in  compliance with the License.
  * You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed  under the  License is distributed on an "AS IS" BASIS,
  * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
  * implied.
  *
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  * Simple Groovy script to
  *
  * +) access the Avalon runtime properties
  * +) create and manipulate Java objects
  * +) create and delete a file
  * +) return a result
  *
  * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
  *
  */
 
import java.io.File;
import java.util.Properties;
import java.io.FileOutputStream;
import org.apache.avalon.framework.logger.Logger
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.context.Context

// 1) parese the arguments

Integer foo = (Integer) args[0];

// 2) access the avalonContext

File applicationDir = avalonContext.getApplicationDir();
File tempDir = avalonContext.getTempDir();
Configuration configuration = avalonContext.getConfiguration();
Parameters parameters = avalonContext.getParameters();
ServiceManager serviceManager = avalonContext.getServiceManager();
Logger logger = avalonContext.getLogger();
Context context = avalonContext.getContext();

avalonContext.getLogger().debug( "Logging from within a Groovy script ...:-)" );
avalonContext.getLogger().debug( "Application directory = " + applicationDir.getAbsolutePath() );
avalonContext.getLogger().debug( "Temp directory = " + tempDir.getAbsolutePath() );
avalonContext.getLogger().debug( "Parameters = " + Parameters.toProperties(parameters).toString() );
avalonContext.getLogger().debug( "Context = " + ((File) context.get("urn:avalon:home")).getAbsolutePath() );

// 3) create a property instance

Properties props = new Properties();
props.setProperty( "foo", foo.toString() );

assert( props.size() == 1 );

// 4) Store the properties in the temp directory 

File file = new File( "temp/test.properties" );
FileOutputStream  fos = new FileOutputStream( file );
props.save( fos, "Test" );
fos.close();

assert ( file.exists() );

// 5) delete the file

file.delete();

assert ( file.exists() == false );


// 6) return a result

Integer result = foo*2;
assert( result == foo+foo );

return result;