package es.ugr.bdremota;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.chilkatsoft.CkSFtp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Objeto;
import es.ugr.utilidades.JSONParser;

class SubirObjetos extends AsyncTask<List<String>, String, String> {
	 
    /**
     * Before starting background thread Show Progress Dialog
     * */
	
	private Context context;
	private ProgressDialog pDialog;
	private JSONParser jParser;
	private static String url_create_objeto = "";
	private static String url_update_objeto = "";
	
	private static String path_imagen_rem = "";
	private static String path_sound_rem = "";
	
    private static final String TAG_SUCCESS = "success";
	
    private ObjetoDataSource ods;
	
    public SubirObjetos(Context context){
    	this.context=context;
    	url_create_objeto=context.getString(R.string.servidor_remoto)+"create_objeto.php";
    	url_update_objeto=context.getString(R.string.servidor_remoto)+"update_objeto.php";
    	
    	path_imagen_rem=context.getString(R.string.servidor_remoto)+"images/";
    	path_sound_rem=context.getString(R.string.servidor_remoto)+"sounds/";
    	ods=new ObjetoDataSource(context);
    	ods.open();
    	jParser=new JSONParser();
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Sincronizando objetos, por favor espere...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    /**
     * Creating product
     * */
    protected String doInBackground(List<String>... args) {
    	
    	for(int i=0;i<args[0].size();i++){
    		InsertarModificar(ods.getObjeto(args[0].get(i)),true);
    	}
    	
    	for(int i=0;i<args[1].size();i++){
    		InsertarModificar(ods.getObjeto(args[1].get(i)),false);
    	}

        return "Subidos: "+args[0].size()+" nuevos objetos; Actualizados: "+args[1].size()+" objetos.";
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(String file_url) {
        // dismiss the dialog once done
        pDialog.dismiss();
        
        ods.close();
        Toast toast = Toast.makeText(context, file_url, Toast.LENGTH_LONG);
        toast.show();
    }
    
    
    
    
    
    private void InsertarModificar(Objeto obj, boolean insertar){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("nombre", obj.getNombre()));
        params.add(new BasicNameValuePair("descripcion", obj.getDescripcion()));
        params.add(new BasicNameValuePair("fecha", obj.getFechaAsString()));
        params.add(new BasicNameValuePair("keypoints", obj.getKeypoints()));
        params.add(new BasicNameValuePair("descriptores", obj.getDescriptores()));
        params.add(new BasicNameValuePair("cols", String.valueOf(obj.getCols())));
        params.add(new BasicNameValuePair("rows", String.valueOf(obj.getRows())));
        
        
        if(!obj.getSonidoDescripcion().equals("")){
        	String nombre_fich=obj.getNombre()+"Descripcion.mp3";
        	nombre_fich=nombre_fich.replaceAll("\\s+", "_"); //sustituye espacios por barras_baja
        	new SubirFicherosSFTP(context).execute(obj.getSonidoDescripcion(),nombre_fich,"sounds");
        	params.add(new BasicNameValuePair("sonido_descripcion", path_sound_rem+nombre_fich));
        }
        
        if(!obj.getSonidoAyuda().equals("")){
        	String nombre_fich=obj.getNombre()+"Ayuda.mp3";
        	nombre_fich=nombre_fich.replaceAll("\\s+", "_"); //sustituye espacios por barras_baja
        	new SubirFicherosSFTP(context).execute(obj.getSonidoAyuda(),nombre_fich,"sounds");
        	params.add(new BasicNameValuePair("sonido_ayuda",path_sound_rem+nombre_fich));
        }
        
        if(!obj.getSonidoNombre().equals("")){
        	String nombre_fich=obj.getNombre()+"Nombre.mp3";
        	nombre_fich=nombre_fich.replaceAll("\\s+", "_"); //sustituye espacios por barras_baja
        	new SubirFicherosSFTP(context).execute(obj.getSonidoNombre(),nombre_fich,"sounds");
        	params.add(new BasicNameValuePair("sonido_nombre",path_sound_rem+nombre_fich));
        }
        
        if(!obj.getPathImagen().equals("")){
        	String nombre_fich=obj.getNombre()+".png";
        	nombre_fich=nombre_fich.replaceAll("\\s+", "_"); //sustituye espacios por barras_baja
        	new SubirFicherosSFTP(context).execute(obj.getPathImagen(),nombre_fich,"images");
        	//funcion();
        	params.add(new BasicNameValuePair("imagen",path_imagen_rem+nombre_fich));
        }
        
        
        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json=new JSONObject();
        
        if(insertar==true)
        		json = jParser.makeHttpRequest(url_create_objeto,
                "POST", params);
        else
        	json = jParser.makeHttpRequest(url_update_objeto,
                    "POST", params);        	
        
        // check log cat fro response
        Log.d("Create Response", json.toString());

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                // successfully created product 
                // closing this screen
            	// Toast toast = Toast.makeText(AllEjerciciosActivity.this, "Creado ejercicios.", Toast.LENGTH_LONG);
            	// toast.show();
            } else {
                // failed to create product
             //json.getString("message")
           	// Toast toast = Toast.makeText(AllEjerciciosActivity.this, "Error al crear:", Toast.LENGTH_LONG);
           //	 toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    

    
}