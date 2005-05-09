package org.apache.fulcrum.yaafi.cli;

/*
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
 */

/**
* Extremly simply command line parsing class.
*/

public class Getopt
{
    /** the prefix for determining command line parameters, e.g "-" or "--" */
    private String prefix;
    
    /** the command line parameters */
	private String[] args;

	/**
	 * Constructor
	 * @param args the command line parameters
	 */
	public Getopt( String[] args )
	{
	    this(args,"--");
	}

	/**
	 * Constructor.
	 * 
	 * @param args the command line parameters
	 * @param prefix the prefix for command line paramters
	 */
	public Getopt( String[] args, String prefix )
	{
	    this.prefix = prefix;
	    
	    if( args == null )
	    {
	        this.args = new String[0];
	    }
	    else
	    {
	        this.args = args;
	    }
	}
	
    /**
     * @param option the option we are looking for
     * @return is the given option contained in the command line arguments?
     */

    public boolean contains( String option )
    {
        return( this.find(option) >= 0 ? true : false );
    }

    /** 
     * @return the number of command line arguments
     */
    public int length()
    {
        return this.args.length;
    }
    
    /**
     * Returns the string value for the given option.
     * @param option the option
     * @return the associated value
     */
    public String getStringValue( String option )
    {
        return this.getValue(option);
    }

    /**
     * Returns the string value for the given option.
     * @param option the option
     * @param defaultValue the default value if the option is not defined
     * @return the associated value
     */
    public String getStringValue( String option, String defaultValue )
    {
        return this.getValue(option,defaultValue);
    }

    /**
     * Returns the boolean value for the given option.
     * @param option the option
     * @return the associated value
     */

    public boolean getBooleanValue( String option )
    {
        return Boolean.valueOf(this.getValue(option)).booleanValue();
    }

    /**
     * Returns the boolean value for the given option.
     * @param option the option
     * @param defaultValue the default value if the option is not defined
     * @return the associated value
     */
    public boolean getBooleanValue( String option, boolean defaultValue )
    {
        String temp = Boolean.toString(defaultValue);
        return Boolean.valueOf(this.getValue(option,temp)).booleanValue();
    }

    /**
     * Get the given argument.
     * @param index the index of the command line argument
     * @return the commandl ine argument 
     */
    private String getArg( int index )
    {
        return this.args[index];
    }
    
    /**
     * @option the option 
     * @return the index of the give option or -1 otherwise
     */
    private int find( String option )
    {
        String strOption = this.prefix + option;

        // Iterate through all command line arguments and look for "-[chOption]"

        for( int i = 0; i < args.length; i++)
        {
            if ( args[i].equals( strOption ) )
            {
                return i;
            }
        }

        return -1;
    }

    /**
     * Determines if a value is defined for the given option 
     * @param option the given option
     * @return true if a value is defined
     */
    private boolean hasValue( String option )
    {
        return this.hasValue( this.find(option) );
    }

    /**
     * Determines if a value is defined for the given option 
     * @param option the given option
     * @return true if a value is defined
     */
    private boolean hasValue( int index )
    {
        String value = null;
        
        if( (index+1) < this.length() )
        {
            value = this.getArg(index+1);
            
            if( value.startsWith(this.prefix) )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }        
    }

    /**
     * Get the value of a command line option
     * @param option the option
     * @param defaultValue the default value if the option was not found
     * @return the value of the option
     */
    private String getValue( String option )
    {
        String value = this.getValue(option,null);
        
        if( value == null )
        {
            // the options is there but no value was defined by the caller
            String msg = "No value supplied for " + this.prefix + option;
            throw new IllegalArgumentException( msg );
        }
        else
        {
            return value;
        }
    }
    
    /**
     * Get the value of a command line option
     * @param option the option
     * @param defaultValue the default value if the option was not found
     * @return the value of the option
     */
    private String getValue( String option, String defaultValue )
    {
        int index = this.find(option);
        
        if( index < 0 )
        {
            // the option is not found
            return defaultValue;
        }
        
        if( this.hasValue(index) )
        {
            // a value is available for this option
            return this.getArg(index+1);
        }
        else
        {
            // the options is there but no value was defined by the caller
            String msg = "No value supplied for " + this.prefix + option;
            throw new IllegalArgumentException( msg );
        }
    }    
}

