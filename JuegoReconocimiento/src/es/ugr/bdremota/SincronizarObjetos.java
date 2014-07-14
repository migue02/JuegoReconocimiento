package es.ugr.bdremota;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.objetos.Objeto;
import es.ugr.utilidades.JSONParser;

public class SincronizarObjetos extends AsyncTask<Void, String, String> {
	 
    /**
     * Before starting background thread Show Progress Dialog
     * */
	private Context context;
	private ProgressDialog pDialog;
	private JSONParser jParser;
	private String url_all_objetos = "http://192.168.1.103/bd_reconocimiento/get_all_id_objetos.php";
	
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_OBJETOS = "objetos";
    private static final String TAG_NOMBRE = "nombre";
    private static final String TAG_FECHA = "fecha";
    
    private JSONArray Objetos = null;
    private ArrayList<String> NombObjetosListRem;
    private ArrayList<Date> FechaObjetosListRem;
    
 //   private ArrayList<String> NombObjetosListLoc;
   // private ArrayList<Date> FechaObjetosListLoc;
    
    private List<String> addToLocal;
    private List<String> addToRemote;
    private List<String> updateToLocal;
    private List<String> updateToRemote;    
    
	
	public SincronizarObjetos(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
		jParser = new JSONParser();
		//this.context.getResources().getString(R.string.servidor_remoto);
	//	NombObjetosListLoc=new ArrayList<String>();
	//	FechaObjetosListLoc=new ArrayList<Date>();
		
		NombObjetosListRem=new ArrayList<String>();
		FechaObjetosListRem=new ArrayList<Date>();
		
		addToLocal=new ArrayList<String>();
		addToRemote=new ArrayList<String>();
		
		updateToLocal=new ArrayList<String>();
		updateToRemote=new ArrayList<String>();

	}
	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Cargando objetos, por favor espere...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }


    /**
     * getting All products from url
     * */
    protected String doInBackground(Void... param) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        // getting JSON string from URL
        JSONObject json = jParser.makeHttpRequest(url_all_objetos, "GET", params);

        // Check your log cat for JSON reponse
        Log.d("All Products: ", json.toString());

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                // products found
                // Getting Array of Products
                Objetos = json.getJSONArray(TAG_OBJETOS);

                // looping through All Products
                for (int i = 0; i < Objetos.length(); i++) {
                    JSONObject c = Objetos.getJSONObject(i);

                    String nombre = c.getString(TAG_NOMBRE);
                    String fecha = c.getString(TAG_FECHA);
                
                    

                    // creating new HashMap
                //    HashMap<String, String> map = new HashMap<String, String>();

                    
                   NombObjetosListRem.add(nombre);
                   try {
					FechaObjetosListRem.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fecha));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                    // adding each child node to HashMap key => value
                   // map.put(TAG_NOMBRE, nombre);
                   // map.put(TAG_FECHA, fecha);
                    
                    // adding HashList to ArrayList
//                    ObjetosListRem.add(map);
                }
            } 
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after getting all products
        pDialog.dismiss();
        ObjetoDataSource ods=new ObjetoDataSource(context);
        ods.open();
        List<Objeto> objs=ods.getAllObjetos();
        ods.close();
        
        //Para cada Objeto que hay en local, 
        for(int i=0;i<objs.size();i++){
        	int pos=NombObjetosListRem.indexOf(objs.get(i).getNombre());
        	if(pos!=-1){
        		if(objs.get(i).getFecha().compareTo(FechaObjetosListRem.get(pos))>0)
        			updateToRemote.add(objs.get(i).getNombre());
        	}else
        		addToRemote.add(objs.get(i).getNombre());

        }
        
        //Para cada Objeto que hay en remoto
        for(int i=0;i<NombObjetosListRem.size();i++){
        	boolean found=false;
        	for(int j=0;j<objs.size()&&found==false;j++){
        		if(objs.get(j).getNombre().equals(NombObjetosListRem.get(i))){
        			found=true;
        			if(FechaObjetosListRem.get(i).compareTo(objs.get(j).getFecha())>0)
        				updateToLocal.add(NombObjetosListRem.get(i));
        		}
        		
        	}
        	if(!found)
        		addToLocal.add(NombObjetosListRem.get(i));
        }
        
        new SubirObjetos(context).execute(addToRemote,updateToRemote);
        new DescargarObjetos(context).execute(addToLocal,updateToLocal);
        
    }

}


