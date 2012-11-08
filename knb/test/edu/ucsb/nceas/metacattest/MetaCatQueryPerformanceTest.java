package edu.ucsb.nceas.metacattest;


import java.io.FileReader;
import java.io.Reader;
import edu.ucsb.nceas.metacat.client.Metacat;
import edu.ucsb.nceas.metacat.client.MetacatAuthException;
import edu.ucsb.nceas.metacat.client.MetacatFactory;
import edu.ucsb.nceas.metacat.client.MetacatInaccessibleException;
import edu.ucsb.nceas.utilities.IOUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
public class MetaCatQueryPerformanceTest extends TestCase{
	
	 /**
     * Create a suite of tests to be run together
     */
    public static Test suite()
    {
       TestSuite suite = new TestSuite();
       return suite;
    }
    
    public static void main(String[] arg)
    {
    	if (arg != null & arg.length == 2)
    	{
    		String metacatURL = arg[0];
    	    String queryFilePath = arg[1];
    	    query(metacatURL, queryFilePath);
    	}
    	else
    	{
    		System.out.println("Usage: java MetaCatQueryPerformance metacatURL queryFilePath");
    	}
    }
    public static void query(String metacatURL, String queryFile)
    {
        try 
        {
        	Thread.sleep(120000);
            Metacat m = MetacatFactory.createMetacatConnection(metacatURL);;
            FileReader fr = new FileReader(queryFile);
            Reader r = m.query(fr);
            //System.out.println("Starting query...");
            String result = IOUtil.getAsString(r, true);
            //System.out.println("Query result:\n" + result);
                
        } catch (Exception e) {
        	System.err.println("General exception:\n" + e.getMessage());
        }
    }
}
