package org.apache.fulcrum.jce.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

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
 * Command line tool for encrypting/decrypting files
 * 
 * file [enc|dec] passwd [file]*
 * string [enc|dec] passwd plaintext
 *
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 * @version $Id$
 */

public class Main	
{
    /**
     * Allows testing on the command lnie
     * @param args
     */
    public static void main( String[] args )
    {
        try
        {
            if( args.length < 3 )
            {
                printHelp();
                throw new IllegalArgumentException("Invalid command line");
            }        
            
            String operationMode = args[0];
            
            if( operationMode.equals("file") )
            {
                processFiles(args); 
            }
            else if( operationMode.equals("string") )
            {
                processString(args); 
            }

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    public static void printHelp()
    {
        System.out.println("Main file [enc|dec|auto] passwd [file]*");
        System.out.println("Main string [enc|dec] passwd ");
    }
    
    /**
     * Decrypt/encrypt a list of files
     * @param args the command line
     * @throws Exception the operation failed
     */
    public static void processFiles(String[] args)
		throws Exception
	{
	    String cipherMode = args[1]; 
	    char[] password = args[2].toCharArray();
	    
	    for( int i=3; i<args.length; i++ )
	    {
	        File file = new File(args[i]);
	        processFile(cipherMode,password,file);
	    }                                                                                             
	}

    /**
     * Decrypt/encrypt a single file
     * @param cipherMode the mode
     * @param password the passwors 
     * @param fileName the file to process
     * @throws Exception the operation failed
     */
    public static void processFile(String cipherMode, char[] password, File file)
		throws Exception
	{
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        if( cipherMode.equals("dec") )
        {
            System.out.println("Decrypting " + file.getAbsolutePath() );
            InputStream cis = CryptoUtil.getCryptoStreamFactory().getInputStream(fis,password);
            CryptoUtil.copy(cis,baos);
            cis.close();
            fis.close();
        }
        else if( cipherMode.equals("auto") )
        {
            System.out.println("Auto-decrypting " + file.getAbsolutePath() );
            InputStream cis = CryptoUtil.getCryptoStreamFactory().getSmartInputStream(fis,password);
            CryptoUtil.copy(cis,baos);
            cis.close();
            fis.close();            
        }
        else if( cipherMode.equals("enc") )
        {
            System.out.println("Enrypting " + file.getAbsolutePath() );
            OutputStream cos = CryptoUtil.getCryptoStreamFactory().getOutputStream(baos,password);            
            CryptoUtil.copy(fis,cos);
            cos.close();            
            fis.close();
        }
        else
        {
            String msg = "Don't know what to do with : " + cipherMode;
            throw new IllegalArgumentException(msg);
        }
        
        FileOutputStream fos = new FileOutputStream(file);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        CryptoUtil.copy(bais,fos);
        fos.close();            
	}    

    /**
     * Decrypt/encrypt a string
     * @param args the command line
     * @throws Exception the operation failed
     */
    public static void processString(String[] args)
		throws Exception
	{
        String cipherMode = args[1];
	    char[] password = args[2].toCharArray();
	    String value = args[3];
	    String result = null;
	    
	    if( cipherMode.equals("dec") )
	    {
	        result = CryptoUtil.decryptString(value,password);
	    }
	    else
        {
	        result = CryptoUtil.encryptString(value,password);
        }
	    
	    System.out.println( result );
	}
}