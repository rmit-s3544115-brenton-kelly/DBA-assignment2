/**
 *  Database Systems - HEAP IMPLEMENTATION
 *
 *  *NOTE*
 *  This file belongs to RMIT, Database Applications, Semester 1, 
 *  Assignment 1 Sample Solution: dbimpl.java
 *
 *  It has been used in this assignment to assist
 *  reading the data from the HEAP File created in Assignment 1
 *
 *  Note that this file has been modified with some additional
 *  variables in order to implement the Hash File.
 *  
 */

public interface dbimpl
{

   // Variables I have added:
   // Name of hashfile.
   public static final String HASH_FNAME = "hash.";
   // Quantity of buckets used to store records.
   public static final int BUCKET_QUANTITY = 10000;
   // Size of each bucket.
   public static final int BUCKET_SIZE = 100;
   // Size of each index obtained from hashing.
   public static final int BUCKET_INDEX_SIZE = 8;
   // Size of the offset value for Heap File.
   public static final int BUCKET_OFFSET_SIZE = 12;
   // Total size of a Bucket.
   public static final int BUCKET_RECORD_SIZE = BUCKET_INDEX_SIZE + BUCKET_OFFSET_SIZE;
   // Amount of records per bucket.
   public static final int RECORDS_PER_BUCKET = 5;
   // Indicators to put at head of Bucket.
   public static final String EMPTY_RECORD_INDICATOR = "E";
   public static final String FULL_RECORD_INDICATOR = "F";
 
   public static final String HEAP_FNAME = "heap.";
   public static final String ENCODING = "utf-8";

   // fixed/variable lengths
   public static final int RECORD_SIZE = 297;
   public static final int RID_SIZE = 4;
   public static final int REGISTER_NAME_SIZE = 14;
   public static final int BN_NAME_SIZE = 200;
   public static final int BN_STATUS_SIZE = 12;
   public static final int BN_REG_DT_SIZE = 10;
   public static final int BN_CANCEL_DT_SIZE = 10;
   public static final int BN_RENEW_DT_SIZE = 10;
   public static final int BN_STATE_NUM_SIZE = 10;
   public static final int BN_STATE_OF_REG_SIZE = 3;
   public static final int BN_ABN_SIZE = 20;
   public static final int EOF_PAGENUM_SIZE = 4;

   public static final int BN_NAME_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE;

   public static final int BN_STATUS_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE;

   public static final int BN_REG_DT_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE
                           + BN_STATUS_SIZE;

   public static final int BN_CANCEL_DT_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE
                           + BN_STATUS_SIZE
                           + BN_REG_DT_SIZE;

   public static final int BN_RENEW_DT_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE
                           + BN_STATUS_SIZE
                           + BN_REG_DT_SIZE
                           + BN_CANCEL_DT_SIZE;

   public static final int BN_STATE_NUM_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE
                           + BN_STATUS_SIZE
                           + BN_REG_DT_SIZE
                           + BN_CANCEL_DT_SIZE
                           + BN_RENEW_DT_SIZE;

   public static final int BN_STATE_OF_REG_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE
                           + BN_STATUS_SIZE
                           + BN_REG_DT_SIZE
                           + BN_CANCEL_DT_SIZE
                           + BN_RENEW_DT_SIZE
                           + BN_STATE_NUM_SIZE;

   public static final int BN_ABN_OFFSET = RID_SIZE
                           + REGISTER_NAME_SIZE
                           + BN_NAME_SIZE
                           + BN_STATUS_SIZE
                           + BN_REG_DT_SIZE
                           + BN_CANCEL_DT_SIZE
                           + BN_RENEW_DT_SIZE
                           + BN_STATE_NUM_SIZE
                           + BN_STATE_OF_REG_SIZE;

   public void readArguments(String args[]);

   public boolean isInteger(String s);

}
