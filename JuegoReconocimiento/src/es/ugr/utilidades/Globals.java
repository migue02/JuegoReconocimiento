package es.ugr.utilidades;

import android.app.Application;

public class Globals extends Application{
   private int nfragment=0;
   public int width = 320, height = 240;
 
   public int getNFragment(){
     return this.nfragment;
   }
 
   public void setNFragment(int d){
     this.nfragment=d;
   }
}
