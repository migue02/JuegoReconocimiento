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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Ejercicio;
import es.ugr.utilidades.JSONParser;

public class SincronizarEjercicios extends AsyncTask<Void, String, String> {
	 
    /**
     * Before starting background thread Show Progress Dialog
     * */
	private Context context;
	private ProgressDialog pDialog;
	private JSONParser jParser;
	private String url_all_ejercicios = "";
	
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EJERCICIOS = "ejercicios";
    private static final String TAG_NOMBRE = "nombre";
    private static final String TAG_FECHA = "fecha";
    
    private JSONArray ejercicios = null;
    private ArrayList<String> NombEjerciciosListRem;
    private ArrayList<Date> FechaEjerciciosListRem;
    
    private List<String> addToLocal;
    private List<String> addToRemote;
    private List<String> updateToLocal;
    private List<String> updateToRemote;    
    
	
	public SincronizarEjercicios(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
		url_all_ejercicios=context.getString(R.string.servidor_remoto)+"get_all_id_ejercicios.php";
		jParser = new JSONParser();
	
		NombEjerciciosListRem=new ArrayList<String>();
		FechaEjerciciosListRem=new ArrayList<Date>();
		
		addToLocal=new ArrayList<String>();
		addToRemote=new ArrayList<String>();
		
		updateToLocal=new ArrayList<String>();
		updateToRemote=new ArrayList<String>();

	}
	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Cargando ejercicios, por favor espere...");
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
        JSONObject json = jParser.makeHttpRequest(url_all_ejercicios, "GET", params);

        // Check your log cat for JSON reponse
        Log.d("All Products: ", json.toString());

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                // products found
                // Getting Array of Products
                ejercicios = json.getJSONArray(TAG_EJERCICIOS);

                // looping through All Products
                for (int i = 0; i < ejercicios.length(); i++) {
                    JSONObject c = ejercicios.getJSONObject(i);

                    String nombre = c.getString(TAG_NOMBRE);
                    String fecha = c.getString(TAG_FECHA);
                
                    

                    // creating new HashMap
                //    HashMap<String, String> map = new HashMap<String, String>();

                    
                   NombEjerciciosListRem.add(nombre);
                   try {
					FechaEjerciciosListRem.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fecha));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                    // adding each child node to HashMap key => value
                   // map.put(TAG_NOMBRE, nombre);
                   // map.put(TAG_FECHA, fecha);
                    
                    // adding HashList to ArrayList
//                    EjerciciosListRem.add(map);
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
        EjercicioDataSource eds=new EjercicioDataSource(context);
        eds.open();
        List<Ejercicio> ejs=eds.getAllEjercicios();
        eds.close();
        
        //Para cada ejercicio que hay en local, 
        for(int i=0;i<ejs.size();i++){
        	int pos=NombEjerciciosListRem.indexOf(ejs.get(i).getNombre());
        	if(pos!=-1){
        		if(ejs.get(i).getFecha().compareTo(FechaEjerciciosListRem.get(pos))>0)
        			updateToRemote.add(ejs.get(i).getNombre());
        	}else
        		addToRemote.add(ejs.get(i).getNombre());

        }
        
        //Para cada ejercicio que hay en remoto
        for(int i=0;i<NombEjerciciosListRem.size();i++){
        	boolean found=false;
        	for(int j=0;j<ejs.size()&&found==false;j++){
        		if(ejs.get(j).getNombre().equals(NombEjerciciosListRem.get(i))){
        			found=true;
        			if(FechaEjerciciosListRem.get(i).compareTo(ejs.get(j).getFecha())>0)
        				updateToLocal.add(NombEjerciciosListRem.get(i));
        		}
        		
        	}
        	if(!found)
        		addToLocal.add(NombEjerciciosListRem.get(i));
        }
        if(addToRemote.size()>0||updateToRemote.size()>0||addToLocal.size()>0||updateToLocal.size()>0){
        	AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        	alertDialog.setTitle("Sincronizar Ejercicios");
        	String mensaje=String.valueOf(addToRemote.size())+" nuevo(s), "+String.valueOf(updateToRemote.size())+" a actualizar en remoto. \n"+String.valueOf(addToLocal.size())+" nuevo(s), "+String.valueOf(updateToLocal.size())+" a actualizar en local. \n \n¿Desea sincronizar?";
        	alertDialog.setMessage(mensaje).setPositiveButton("Si", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					new SubirEjercicios(context).execute(addToRemote,updateToRemote);
			        new DescargarEjercicios(context).execute(addToLocal,updateToLocal);
				}
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
        	
        	AlertDialog alert=alertDialog.create();
	        alert.show();
        }
    }

}


