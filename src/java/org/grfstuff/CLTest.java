package org.grfstuff;

public class CLTest{
 static {
        System.loadLibrary("cltestjni");
    }

   public native static void cltest();
   public static void main(String args[]){
       cltest();
   }
}