import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.ArrayList;
/**
 *  Database Systems - HASH IMPLEMENTATION
 *  
 *  *NOTE*
 *  Methods: readArguments(), isInteger() and readHeap()
 *  have been graciously borrowed from 
 *  Database Applications Semester 1 - 
 *  Assignment 1 Sample Solution, dbquery.java, dbimpl.java
 *  These have been used to retrieve the information from
 *  the heapfile.
 */

public class hashquery implements dbimpl
{
   public static void main(String args[])
   {
      hashload load = new hashload();
      // Calculate query time.
      long startTime = System.currentTimeMillis();
      load.readArguments(args);
      long endTime = System.currentTimeMillis();

      System.out.println("Query time: " + (endTime - startTime) + "ms");
   }


   // Reading command line arguments.
   public void readArguments(String args[])
   {
      if (args.length == 2)
      {
         if (isInteger(args[1]))
         {
	    // Create Hash File skeleton.
	    getHeapOffet(4);
         }
      }
      else
      {
          System.out.println("Error: only pass in two arguments");
      }
   }


   // check if pagesize is a valid integer
   public boolean isInteger(String s)
   {
      boolean isValidInt = false;
      try
      {
         Integer.parseInt(s);
         isValidInt = true;
      }
      catch (NumberFormatException e)
      {
         e.printStackTrace();
      }
      return isValidInt;
   }


   // returns records containing the argument text from shell
   public int hashRecord(byte[] rec)
   {
      int bucketIndex = -1;
      String record = new String(rec);
      // Get the BN_NAME for the record as we will be indexing by this.
      String BN_NAME = record
			.substring(RID_SIZE+REGISTER_NAME_SIZE,
			RID_SIZE+REGISTER_NAME_SIZE+BN_NAME_SIZE);

      // Hashes BN_NAME and limits the index to the quantity of buckets.
      bucketIndex = Math.abs(BN_NAME.hashCode()) % BUCKET_QUANTITY;

      return bucketIndex;

   }

   public void getHeapOffset(int hashOffset)
   {
      
      File hashfile = new File(HASH_FNAME + pagesize);

      BufferedReader br = null;
      FileOutputStream fos = null;
      String tempString = "";


      try{
            byte[] bBucket = new byte[BUCKET_SIZE];
            byte[]i bRecord = new byte[BUCKET_RECORD_SIZE];



            FileInputStream fis = new FileInputStream(hashfile);


            // Read bucket by bucket until we reach desired bucket.
            boolean isNextBucket = true;
            int bucketCount = 1;
            // int hashIndex = 2;
	    byte[] readRemainder = null;
            while(isNextBucket)
            {
               if(bucketCount + 1 == BUCKET_QUANTITY)
	       {
	          isNextBucket = false;
	       }
	       if(bucketCount == hashOffset)
	       {
		 // boolean found = false;
	          // Read record and check if empty. Just scrapping it now for testing.
	          for(int i = 1; i<=RECORDS_PER_BUCKET; i++)
	          {
	             fis.read(bRecord, 0, bRecord.length);
		     tempString = new String(bRecord);
		     if(tempString.charAt(0) == 'F' && found == false)
		     {
                        System.out.println("Potential Index found: ");
		     }
	           }

	           bucketCount++;
	        }
	        else
	        {
	           fis.read(bBucket, 0, bBucket.length);
	           fos.write(bBucket, 0, bBucket.length);
	           bucketCount ++;
	        }
             }
       fis.close();
       fos.close();




      }
   }

}
