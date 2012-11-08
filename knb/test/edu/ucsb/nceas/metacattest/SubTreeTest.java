/**
 *  '$RCSfile$'
 *  Copyright: 2000 Regents of the University of California and the
 *              National Center for Ecological Analysis and Synthesis
 *    Purpose: To test the ReplicationServerList class by JUnit
 *    Authors: Jing Tao
 *
 *   '$Author: daigle $'
 *     '$Date: 2009-08-25 07:42:25 +1000 (Tue, 25 Aug 2009) $'
 * '$Revision: 5035 $'
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.ucsb.nceas.metacattest;

import edu.ucsb.nceas.MCTestCase;
import edu.ucsb.nceas.metacat.*;
import edu.ucsb.nceas.metacat.database.*;
import edu.ucsb.nceas.metacat.replication.*;
import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.metacat.util.MetacatUtil;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;


/**
 * A JUnit test for testing Step class processing
 */
public class SubTreeTest extends MCTestCase
{
  private SubTree tree = null;
  private static final Log log = LogFactory.getLog("edu.ucsb.nceas.metacattest.ReplicationServerListTest");
  /* Initialize properties*/
  static
  {
	  try
	  {
		  PropertyService.getInstance();
	  }
	  catch(Exception e)
	  {
		  System.err.println("Exception in initialize option in MetacatServletNetTest "+e.getMessage());
	  }
  }
  /**
   * Constructor to build the test
   *
   * @param name the name of the test method
   */
  public SubTreeTest(String name)
  {
    super(name);
  }

  /**
   * Constructor to build the test
   *
   * @param name the name of the test method
   * @param tree the subtree
   */
  public SubTreeTest(String name, SubTree myTree)
  {
    super(name);
    this.tree = myTree;
  }

  /**
   * Establish a testing framework by initializing appropriate objects
   */
  public void setUp()
 {

 }

  /**
   * Release any objects after tests are complete
   */
  public void tearDown()
  {
    //DBConnectionPool will be release
    DBConnectionPool.release();
  }

  /**
   * Create a suite of tests to be run together
   */
  public static Test suite()
  {
     //Get DBConnection pool, this is only for junit test.
    //Because DBConnection is singleton class. So there is only one DBConnection
    //pool in the program
    try
    {
      DBConnectionPool pool = DBConnectionPool.getInstance();
    }//try
    catch (Exception e)
    {
        log.debug("Error in ReplicationServerList() to get" +
                        " DBConnection pool"+e.getMessage());
    }//catch

    TestSuite suite = new TestSuite();

    try
    {

      //create a new subtree
      SubTree subTree = new SubTree("eml.349", "distribution1", 118214, 118223);

      //Doing test test cases
      suite.addTest(new SubTreeTest("initialize"));
      System.out.println("before adding testGetSubTreeNodeStack() into suite");
      suite.addTest(new SubTreeTest("testGetSubTreeNodeStack", subTree));
      System.out.println("here!!");


    }//try
    catch (Exception e)
    {
        log.debug("Error in SubTreeTest.suite: "+
                                e.getMessage());
    }//catch
    return suite;
 }



  /**
   * Run an initial test that always passes to check that the test
   * harness is working.
   */
  public void initialize()
  {
    System.out.println("in initialize");
    assertTrue(1 == 1);
  }

  /**
   * Test the method getSubTreeNodeStack
   */
  public void testGetSubTreeNodeStack()
  {
    Stack nodeStack = null;
    try{
      nodeStack = tree.getSubTreeNodeStack();
    }//try
    catch (Exception e)
    {
        log.debug("Error in SubTreeTest.suite: "+ e.getMessage());
    }//catch

    while (nodeStack != null && !nodeStack.empty())
    {
      NodeRecord node =(NodeRecord)nodeStack.pop();
      String nodeType = node.getNodeType();
      if ( nodeType != null && nodeType.equals("ELEMENT"))
      {
          log.debug("Elment: "+ node.getNodeName());
      }
      else if (nodeType != null && nodeType.equals("ATTRIBUTE"))
      {
          log.debug("Attribute: "  +node.getNodeName() +
                                 " = " + node.getNodeData());
      }
      else
      {
          log.debug("text: " + node.getNodeData());
      }
    }
   
  }
}
