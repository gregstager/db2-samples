//***************************************************************************
// (c) Copyright IBM Corp. 2007 All rights reserved.
// 
// The following sample of source code ("Sample") is owned by International 
// Business Machines Corporation or one of its subsidiaries ("IBM") and is 
// copyrighted and licensed, not sold. You may use, copy, modify, and 
// distribute the Sample in any form without payment to IBM, for the purpose of 
// assisting you in the development of your applications.
// 
// The Sample code is provided to you on an "AS IS" basis, without warranty of 
// any kind. IBM HEREBY EXPRESSLY DISCLAIMS ALL WARRANTIES, EITHER EXPRESS OR 
// IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. Some jurisdictions do 
// not allow for the exclusion or limitation of implied warranties, so the above 
// limitations or exclusions may not apply to you. IBM shall not be liable for 
// any damages you suffer as a result of using, copying, modifying or 
// distributing the Sample, even if IBM has been advised of the possibility of 
// such damages.
//***************************************************************************
//
// SOURCE FILE NAME: Util.java
//
// SAMPLE: Utilities for JDBC sample programs
//
//         This sample has 3 classes:
//         1. Data - Display the data in the table
//         2. Db - Connect to or disconnect from the 'sample' database
//         3. JdbcException - Handle Java Exceptions
//
// JAVA 2 CLASSES USED:
//         DriverManager
//         Connection
//         Exception
//
// OUTPUT FILE: None
//***************************************************************************
//
// For more information on the sample programs, see the README file.
//
// For information on developing JDBC applications, see the Application
// Development Guide.
//
// For information on using SQL statements, see the SQL Reference.
//
// For the latest information on programming, compiling, and running DB2
// applications, visit the DB2 application development website at
//     http://www.software.ibm.com/data/db2/udb/ad
//**************************************************************************/

import java.lang.*;
import java.util.*;
import java.sql.*;
import java.math.BigDecimal;

class Data
{
  public static String format(String strData, int finalLen) throws Exception
  {
    String finalStr;
    if (finalLen <= strData.length())
    {
      finalStr = strData.substring(0, finalLen);
    }
    else
    {
      finalStr = strData;
      for (int i = strData.length(); i < finalLen; i++)
      {
        finalStr = finalStr + " ";
      }
    }
    return (finalStr);
  } // format(String, int)

  public static String format(int intData, int finalLen) throws Exception
  {
    String strData = String.valueOf(intData);
    String finalStr;
    if (finalLen <= strData.length())
    {
      finalStr = strData.substring(0, finalLen);
    }
    else
    {
      finalStr = "";
      for (int i = 0; i < finalLen - strData.length(); i++)
      {
        finalStr = finalStr + " ";
      }
      finalStr = finalStr + strData;
    }
    return (finalStr);
  } // format(int, int)

  public static String format(Integer integerData, int finalLen)
  throws Exception
  {
    int intData;
    String finalStr;

    intData = integerData.intValue();
    finalStr = format(intData, finalLen);

    return (finalStr);
  } // format(Integer, int)

  public static String format(double doubData, int precision, int scale)
  throws Exception
  {
    BigDecimal decData = new BigDecimal(doubData);
    decData = decData.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
    String strData = decData.toString();

    // prepare the final string
    int finalLen = precision + 1;
    String finalStr;
    if (finalLen <= strData.length())
    {
      finalStr = strData.substring(0, finalLen);
    }
    else
    {
      finalStr = "";
      for (int i = 0; i < finalLen - strData.length(); i++)
      {
        finalStr = finalStr + " ";
      }
      finalStr = finalStr + strData;
    }

    return (finalStr);
  } // format(double, int, int)

  public static String format(BigDecimal decData, int precision, int scale)
  throws Exception
  {
    decData = decData.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
    String strData = decData.toString();

    // prepare the final string
    int finalLen = precision + 1;
    String finalStr;
    if (finalLen <= strData.length())
    {
      finalStr = strData.substring(0, finalLen);
    }
    else
    {
      finalStr = "";
      for (int i = 0; i < finalLen - strData.length(); i++)
      {
        finalStr = finalStr + " ";
      }
      finalStr = finalStr + strData;
    }

    return (finalStr);
  } // format(BigDecimal, int, int)

  public static String format(Double doubleData, int precision, int scale)
  throws Exception
  {
    double doubData;
    String finalStr;

    doubData = doubleData.doubleValue();
    return (format(doubData, precision, scale));
  } // format(Double, int, int)
} // Data

class Db
{
  public String alias;
  public String server;
  public int portNumber = -1; // < 0 use universal type 2 connection
                              // > 0 use universal type 4 connection
  public String userId;
  public String password;
  public Connection con = null;

  public Db()
  {
  }

  public Db(String argv[]) throws Exception
  {
    if( argv.length > 5 ||
        ( argv.length == 1 &&
          ( argv[0].equals( "?" )               ||
            argv[0].equals( "-?" )              ||
            argv[0].equals( "/?" )              ||
            argv[0].equalsIgnoreCase( "-h" )    ||
            argv[0].equalsIgnoreCase( "/h" )    ||
            argv[0].equalsIgnoreCase( "-help" ) ||
            argv[0].equalsIgnoreCase( "/help" ) ) ) )
    {
      throw new Exception(
        "Usage: prog_name [dbAlias] [userId passwd] (use universal JDBC type 2 driver)\n" +
        "       prog_name [dbAlias] server portNum userId passwd (use universal JDBC type 4 driver)" );
    }

    switch (argv.length)
    {
      case 0:  // Type 2, use all defaults
        alias = "sample";
        userId = "";
        password = "";
        break;
      case 1:  // Type 2, dbAlias specified
        alias = argv[0];
        userId = "";
        password = "";
        break;
      case 2:  // Type 2, userId & passwd specified
        alias = "sample";
        userId = argv[0];
        password = argv[1];
        break;
      case 3:  // Type 2, dbAlias, userId & passwd specified
        alias = argv[0];
        userId = argv[1];
        password = argv[2];
        break;
      case 4:  // Type 4, use default dbAlias
        alias = "sample";
        server = argv[0];
        portNumber = Integer.valueOf( argv[1] ).intValue();
        userId = argv[2];
        password = argv[3];
        break;
      case 5:  // Type 4, everything specified
        alias = argv[0];
        server = argv[1];
        portNumber = Integer.valueOf( argv[2] ).intValue();
        userId = argv[3];
        password = argv[4];
        break;
    }
  } // Db Constructor

  public Connection connect() throws Exception
  {
    String url = null;

    // In Partitioned Database environment, set this to the node number
    // to which you wish to connect (leave as "0" in non-Partitioned Database environment)
    String nodeNumber = "0";

    Properties props = new Properties();

    if ( portNumber < 0 )
    {
      url = "jdbc:db2:" + alias;
      System.out.println(
        "  Connect to '" + alias + "' database using JDBC Universal type 2 driver." );
      Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
    }
    else
    {
      url = "jdbc:db2://" + server + ":" + portNumber + "/" + alias;
      System.out.println(
        "  Connect to '" + alias + "' database using JDBC Universal type 4 driver." );
      Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
    }

    if( null != userId )
    {
      props.setProperty("user", userId);
      props.setProperty("password", password);
    }

    props.setProperty("CONNECTNODE", nodeNumber);

    con = DriverManager.getConnection( url, props );

    // enable transactions
    con.setAutoCommit(false);
    return con;
  } // connect

  public void disconnect() throws Exception
  {
    System.out.println();
    System.out.println("  Disconnect from '" + alias + "' database.");

    // makes all changes made since the previous commit/rollback permanent
    // and releases any database locks currrently held by the Connection.
    con.commit();

    // immediately disconnects from database and releases JDBC resources
    con.close();
  } // disconnect
} // Db

class JdbcException extends Exception
{
  Connection conn;

  public JdbcException(Exception e)
  {
    super(e.getMessage());
    conn = null;
  }

  public JdbcException(Exception e, Connection con)
  {
    super(e.getMessage());
    conn = con;
  }

  public void handle()
  {
    System.out.println(getMessage());
    System.out.println();

    if (conn != null)
    {
      try
      {
        System.out.println("--Rollback the transaction-----");
        conn.rollback();
        System.out.println("  Rollback done!");
      }
      catch (Exception e)
      {
      };
    }
  } // handle

  public void handleExpectedErr()
  {
    System.out.println();
    System.out.println(
      "**************** Expected Error ******************\n");
    System.out.println(getMessage());
    System.out.println(
      "**************************************************");
  } // handleExpectedError
} // JdbcException

