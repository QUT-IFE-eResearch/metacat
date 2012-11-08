package edu.ucsb.nceas.metacat.stringclient.application;

import edu.ucsb.nceas.metacat.stringclient.MetacatString.MetacatStringServiceLocator;
import edu.ucsb.nceas.metacat.stringclient.MetacatString.MetacatStringPortType;
import java.net.URL;

public class MetacatStringServiceApplication
{
	public static void main(String[] args)
	{
		try
		{
			// Get command-line arguments
			String docid = args[1];
			URL GSH = new java.net.URL(args[0]);

			// Get a reference to the remote web service
			MetacatStringServiceLocator metacatService 
                                    = new MetacatStringServiceLocator();
			MetacatStringPortType metacat = 
                              metacatService.getMetacatStringService(GSH);

			// Call remote method 'add'
                        String message = metacat.read(docid);

			// Print result
			System.out.println(message);
		}catch(Exception e)
		{
			System.out.println("ERROR!");
			e.printStackTrace();
		}
	}
}
