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

public class hashload implements dbimpl
{
int exit = 0;
   // initialize
   public static void main(String args[])
   {
      hashload load = new hashload();
      // calculate query time
      long startTime = System.currentTimeMillis();
      load.readArguments(args);
      long endTime = System.currentTimeMillis();

      System.out.println("Query time: " + (endTime - startTime) + "ms");
   }


   // reading command line arguments
   public void readArguments(String args[])
   {
      if (args.length == 2)
      {
         if (isInteger(args[1]))
         {
	    createHashFileStructure(Integer.parseInt(args[1]));
            readHeap(args[0], Integer.parseInt(args[1]));
         }
      }
      else
      {
          System.out.println("Error: only pass in two arguments");
      }
   }

   // Create the empty hash file structure and save to file.
   public void createHashFileStructure(int pagesize){
      File hashfileStructure = new File(HASH_FNAME + pagesize + ".structure");
      BufferedReader br = null;
      FileOutputStream fos = null;

      byte[] bucket = new byte[BUCKET_SIZE];
      byte[] record = new byte[BUCKET_INDEX_SIZE + BUCKET_OFFSET_SIZE];


      try{

            fos = new FileOutputStream(hashfileStructure);



	    // Put the empty record indicator to the head of record.
	    String recordPadding = "";
	    // Pad the record until full.
	    for(int i = 1; i<= BUCKET_RECORD_SIZE-1;i++)
	    {
		recordPadding += " ";
	    }
	    // Create a string that will fill up a bucket with empty headers.
	    recordPadding = EMPTY_RECORD_INDICATOR + recordPadding;
	    String dataString = "";
	    for(int i = 1; i<= RECORDS_PER_BUCKET; i++)
	    {
		dataString += recordPadding;
	    }
	    
	    System.out.println("dataString = " + dataString);
	    byte[] emptyRecHead = dataString.getBytes(ENCODING);
	    System.arraycopy(emptyRecHead, 0, bucket, 0, emptyRecHead.length);
	

	    // replace 3 with bucket_quantity when finished testing small size
            for(int i = 1; i <= BUCKET_QUANTITY; i++)
	    {
	        fos.write(bucket);
	    }

	    // test writing to file



	    fos.close();


      }
      catch (Exception e)
      {
            e.printStackTrace();
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

   // read heapfile by page
   public void readHeap(String name, int pagesize)
   {

      File heapfile = new File(HEAP_FNAME + pagesize);
      int intSize = 4;
      int pageCount = 0;
      int recCount = 0;
      int recordLen = 0;
      int rid = 0;
      boolean isNextPage = true;
      boolean isNextRecord = true;
      List<Integer> recOffsetArray = new ArrayList<Integer>();
      List<Integer> hashOffsetArray = new ArrayList<Integer>();

      // Variables I have added to dbquery.java - readHeap()
      
      // Stores the record's heapfile offset.
      int recOffset = 0;
      int hashOffset = 0;

      try
      {

         FileInputStream fis = new FileInputStream(heapfile);
         // reading page by page

            int recNumber = 0;
         while (isNextPage)
         {
            byte[] bPage = new byte[pagesize];
            byte[] bPageNum = new byte[intSize];
            fis.read(bPage, 0, pagesize);
            System.arraycopy(bPage, bPage.length-intSize, bPageNum, 0, intSize);

            // reading by record, return true to read the next record
            isNextRecord = true;
            while (isNextRecord)
            {
	       recNumber ++;
               byte[] bRecord = new byte[RECORD_SIZE];
               byte[] bRid = new byte[intSize];
               try
               {
                  System.arraycopy(bPage, recordLen, bRecord, 0, RECORD_SIZE);
                  System.arraycopy(bRecord, 0, bRid, 0, intSize);
                  rid = ByteBuffer.wrap(bRid).getInt();
                  if (rid != recCount)
                  {
                     isNextRecord = false;
                  }
                  else
                  {
		     // Get the hash offset for the record
                     hashOffset = hashRecord(bRecord, name);


		     // Calculate the HeapFileOffset of the record.
	             recOffset = recNumber * RECORD_SIZE;  
			recOffsetArray.add(recOffset);
			hashOffsetArray.add(hashOffset);
			System.out.println("arrays = " + recOffsetArray[0]); 
                     storeRecordInHash(recOffset, hashOffset, pagesize);
	       
		     // TODO: Store the hashOffset with the Heap File offset.

                     recordLen += RECORD_SIZE;
                  }
                  recCount++;
                  // if recordLen exceeds pagesize, catch this to reset to next page
               }
               catch (ArrayIndexOutOfBoundsException e)
               {
                  isNextRecord = false;
                  recordLen = 0;
                  recCount = 0;
                  rid = 0;
               }
            }
            // check to complete all pages
            if (ByteBuffer.wrap(bPageNum).getInt() != pageCount)
            {
               isNextPage = false;
            }

            pageCount++;
         }
      }
      catch (FileNotFoundException e)
      {
         System.out.println("File: " + HEAP_FNAME + pagesize + " not found.");
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   // returns records containing the argument text from shell
   public int hashRecord(byte[] rec, String input)
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

   public void storeRecordInHash(int recOffset, int hashOffset, int pagesize)
   {
      File hashfileStructure = new File(HASH_FNAME + pagesize + ".structure");
	File hashfile = new File(HASH_FNAME + pagesize);
	File hashfile2 = new File(HASH_FNAME + pagesize + "2");

	//fos2 = new FileOutputStream(hashfile);
      BufferedReader br = null;
      FileOutputStream fos = null;
      String tempString = "";
	FileInputStream fis2 = null;
	FileOutputStream fos2 = null;

      try{
       byte[] bBucket = new byte[BUCKET_SIZE];
       byte[] bRecord = new byte[BUCKET_RECORD_SIZE];
//TODO: PAD STRINGS AND APPEND A 'F' AROUND HERE SOMEHWEEHRJEA
       // Store recOFFset and hashOffset into store String
       String hashOffsetString = FULL_RECORD_INDICATOR + Integer.toString(hashOffset);
       String recOffsetString = Integer.toString(recOffset);
	int hashOffsetLength = BUCKET_INDEX_SIZE - hashOffsetString.length();
	// Pad the index string.
       for(int i = 1; i<=hashOffsetLength;i++)
	{
		hashOffsetString += " ";
	}
	// Pad the heapfile offset string
	for(int i = 1; i<= BUCKET_OFFSET_SIZE - recOffsetString.length(); i++)
	{
	   recOffsetString += " ";
	}
       String storeString = hashOffsetString + recOffsetString;
      
       byte[] record = new byte[BUCKET_RECORD_SIZE];
       // Put store string into recordData.
       byte[] recordData = storeString.getBytes(ENCODING);
       // Copy record data into record.
       System.arraycopy(recordData, 0, record, 0, recordData.length);

       FileInputStream fis = new FileInputStream(hashfileStructure);

       //File hashfile = new File(HASH_FNAME + pagesize);
       fos = new FileOutputStream(hashfile);


       // Read bucket by bucket until we reach desired bucket.
       boolean isNextBucket = true;
       int bucketCount = 1;
      // int hashIndex = 2;
	byte[] readRemainder = null;
       while(isNextBucket)
       {
           if(bucketCount +1 == BUCKET_QUANTITY)
	   {
	       System.out.println("last bucket");
	       isNextBucket = false;
	   }
	   if(bucketCount == hashOffset)
	   {
		boolean inserted = false;
	       // Read record and check if empty. Just scrapping it now for testing.
	       for(int i = 1; i<=RECORDS_PER_BUCKET; i++)
	       {
	          fis.read(bRecord, 0, bRecord.length);
		  tempString = new String(bRecord);
		System.out.println("tempString = " + tempString);
		  if(tempString.charAt(0) == 'E' && inserted == false)
		  {
                      System.out.println("Record " + i + " is empty. Inserting!    bucket size =  " + (BUCKET_SIZE - (BUCKET_RECORD_SIZE*i)));
		      fos.write(record, 0, record.length);
		      fis.read(readRemainder = new byte[BUCKET_SIZE - (BUCKET_RECORD_SIZE * i)], 0, readRemainder.length);
		      fos.write(readRemainder, 0, readRemainder.length);
		      //inserted = true;
		      break;
		      
		  }
		  if(tempString.charAt(0) == 'F')
		  {
		      System.out.println("record not empty, add going to next one");
			fos.write(bRecord, 0, bRecord.length);
			if(i == RECORDS_PER_BUCKET && inserted == false)
			{
			   if(bucketCount == BUCKET_QUANTITY)
			   {
				isNextBucket = false;
				break;
			   }
			   bucketCount++;
			   i = 0;
				
			}
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
	// Write data back into hash structure.
	fis2 = new FileInputStream(hashfile);
	fos2 = new FileOutputStream(hashfileStructure);
	byte[] writeToStructure = new byte[BUCKET_SIZE];
//TODO CHANGE SO INDEX != 1 anymor

	   for(int i = 1; i<= BUCKET_QUANTITY; i++)
	   {	
		fis2.read(writeToStructure, 0, writeToStructure.length);

		//System.out.println("writeToStructure = " + new String(writeToStructure));
		fos2.write(writeToStructure, 0, writeToStructure.length);
	   }
exit ++;
if(exit == 25){

System.exit(0);
}

     //  System.out.println("Completed");
       }
	catch(Exception e){}
       System.out.println("Finished that record. recOffset was: " + recOffset + " & hashOffset = " + hashOffset );

 
   }
}
