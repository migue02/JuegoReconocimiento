package es.ugr.bdremota;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.juegoreconocimiento.Ejercicios;
import es.ugr.juegoreconocimiento.R;
import es.ugr.utilidades.JSONParser;

public class DescargarEjercicios extends AsyncTask<List<String>, String, String> {
	 
    /**
     * Before starting background thread Show Progress Dialog
     * */
	
	private Context context;
	private ProgressDialog pDialog;
	private JSONParser jParser;
	private String url_get_ejercicio = "";
	
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EJERCICIOS = "ejercicio";
    private static final String TAG_NOMBRE = "nombre";
    private static final String TAG_OBJETOS = "objetos";
    private static final String TAG_FECHA = "fecha";
    private static final String TAG_DESCRIPCION = "descripcion";
    private static final String TAG_DURACION = "duracion";
    private static final String TAG_OBJETOS_RECONOCER = "objetosReconocer";
    private static final String TAG_SONIDO_DESCRIPCION = "sonido_descripcion";
    
    private JSONArray ejercicios = null;
	
    private EjercicioDataSource eds;
    
    public DescargarEjercicios(Context context){
    	this.context=context;
    	url_get_ejercicio=context.getString(R.string.servidor_remoto)+"get_ejercicio.php";
    	eds=new EjercicioDataSource(context);
    	eds.open();
    	jParser=new JSONParser();
    }
    
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Sincronizando ejercicios, por favor espere...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    

    /**
     * getting All products from url
     * */
@Override
protected String doInBackground(List<String>... params) {
        // Building Parameters
		for(int i=0;i<params[0].size();i++){
			Insertar(params[0].get(i));
		}
		
		for(int i=0;i<params[1].size();i++)
			Modificar(params[1].get(i));
        return "Añadidos: "+params[0].size()+" nuevos ejercicios; Actualizados: "+params[1].size()+" ejercicios.";
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
      

    protected void onPostExecute(String msg) {
        pDialog.dismiss();
        eds.close();
        Ejercicios ej=(Ejercicios)context;
        ej.CreaTablaEjer();
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.show();

    }
    
    
    private void Insertar(String nombreFila){
        List<NameValuePair> parametros = new ArrayList<NameValuePair>();
        parametros.add(new BasicNameValuePair("nombre",nombreFila));
        
        // getting JSON string from URL
        
        JSONObject json = jParser.makeHttpRequest(url_get_ejercicio, "GET", parametros);

        // Check your log cat for JSON reponse
        Log.d("Crear ejercicio Local: ", json.toString());

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                // products found
                // Getting Array of Products
                ejercicios = json.getJSONArray(TAG_EJERCICIOS);

                // looping through All Products
                for (int j = 0; j < ejercicios.length(); j++) {
                    JSONObject c = ejercicios.getJSONObject(j);

                    // Storing each json item in variable
                   // String id = c.getString(TAG_ID);
                    String nombre = c.getString(TAG_NOMBRE);
                    Date fecha=new Date();
                    try {
					    fecha=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getString(TAG_FECHA));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    ArrayList<String> objetos=es.ugr.utilidades.Utilidades.ArrayListFromJson(c.getString(TAG_OBJETOS));
                    String descripcion = c.getString(TAG_DESCRIPCION);
                    Integer duracion = Integer.parseInt(c.getString(TAG_DURACION));
                    ArrayList<String> objetosReconocer=es.ugr.utilidades.Utilidades.ArrayListFromJson(c.getString(TAG_OBJETOS_RECONOCER));
                    String sonido_descripcion=c.getString(TAG_SONIDO_DESCRIPCION);
                    String sonido_descripcion_local="";
                    if (!sonido_descripcion.equals("")){
                    	String ruta=context.getString(R.string.pathSounds);
                    	sonido_descripcion_local=ruta+"/"+nombre+".mp3";
                    	//sonido_descripcion_local="/mnt/sdcard/JuegoReconocimiento/sonidos/"+nombre+".mp3";
                    	new DescargarFicheros().execute(sonido_descripcion,sonido_descripcion_local);
                    	
                    }
                    eds.createEjercicio(nombre, fecha, objetos, descripcion, duracion, objetosReconocer, sonido_descripcion_local);
                }
            } 
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    
    
    
    
    private void Modificar(String nombreFila){
        List<NameValuePair> parametros = new ArrayList<NameValuePair>();
        parametros.add(new BasicNameValuePair("nombre",nombreFila));
        
        // getting JSON string from URL
        
        JSONObject json = jParser.makeHttpRequest(url_get_ejercicio, "GET", parametros);
        //json2.

        // Check your log cat for JSON reponse
        Log.d("Crear ejercicio Local: ", json.toString());

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                // products found
                // Getting Array of Products
                ejercicios = json.getJSONArray(TAG_EJERCICIOS);

                // looping through All Products
                for (int j = 0; j < ejercicios.length(); j++) {
                    JSONObject c = ejercicios.getJSONObject(j);

                    // Storing each json item in variable
                   // String id = c.getString(TAG_ID);
                    String nombre = c.getString(TAG_NOMBRE);
                    Date fecha=new Date();
                    try {
					    fecha=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getString(TAG_FECHA));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    ArrayList<String> objetos=es.ugr.utilidades.Utilidades.ArrayListFromJson(c.getString(TAG_OBJETOS));
                    String descripcion = c.getString(TAG_DESCRIPCION);
                    Integer duracion = Integer.parseInt(c.getString(TAG_DURACION));
                    ArrayList<String> objetosReconocer=es.ugr.utilidades.Utilidades.ArrayListFromJson(c.getString(TAG_OBJETOS_RECONOCER));
                    String sonido_descripcion=c.getString(TAG_SONIDO_DESCRIPCION);
                    String sonido_descripcion_local="";
                    if (!sonido_descripcion.equals("")){
                    	sonido_descripcion_local="/mnt/sdcard/JuegoReconocimiento/sonidos/"+nombre+".mp3";
                    	new DescargarFicheros().execute(sonido_descripcion,sonido_descripcion_local);
                    	
                    }
                    eds.modificaEjercicio(nombre, fecha, objetos, descripcion, duracion, objetosReconocer, sonido_descripcion_local);
                   
                }
            } 
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
