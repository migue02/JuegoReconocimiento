package es.ugr.bdremota;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.utilidades.JSONParser;

public class DescargarEjercicios extends AsyncTask<List<String>, String, String> {
	 
    /**
     * Before starting background thread Show Progress Dialog
     * */
	
	private Context context;
	private ProgressDialog pDialog;
	private JSONParser jParser2;
	private String url_get_ejercicio = "http://192.168.1.103/bd_reconocimiento/get_ejercicio.php";
	
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EJERCICIOS = "ejercicios";
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
    	eds=new EjercicioDataSource(context);
    	eds.open();
    	jParser2=new JSONParser();
    }
    
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Descargando nuevos ejercicios, por favor espere...");
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
        List<NameValuePair> parametros = new ArrayList<NameValuePair>();
        parametros.add(new BasicNameValuePair("nombre",params[0].get(i)));
        
        // getting JSON string from URL
        
        JSONObject json2 = jParser2.makeHttpRequest(url_get_ejercicio, "GET", parametros);

        // Check your log cat for JSON reponse
        Log.d("Crear ejercicio Local: ", json2.toString());

        try {
            // Checking for SUCCESS TAG
            int success = json2.getInt(TAG_SUCCESS);

            if (success == 1) {
                // products found
                // Getting Array of Products
                ejercicios = json2.getJSONArray(TAG_EJERCICIOS);

                // looping through All Products
                for (int j = 0; j < ejercicios.length(); j++) {
                    JSONObject c = ejercicios.getJSONObject(j);

                    // Storing each json item in variable
                   // String id = c.getString(TAG_ID);
                    String nombre = c.getString(TAG_NOMBRE);
                    Date fecha=new Date();
                    try {
					    fecha=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getString(c.getString(TAG_FECHA)));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    ArrayList<String> objetos=es.ugr.utilidades.Utilidades.ArrayListFromJson(c.getString(TAG_OBJETOS));
                    String descripcion = c.getString(TAG_DESCRIPCION);
                    Integer duracion = Integer.parseInt(c.getString(TAG_DURACION));
                    ArrayList<String> objetosReconocer=es.ugr.utilidades.Utilidades.ArrayListFromJson(c.getString(TAG_OBJETOS_RECONOCER));
                    String sonido_descripcion=c.getString(TAG_SONIDO_DESCRIPCION);

                    eds.createEjercicio(nombre, fecha, objetos, descripcion, duracion, objetosReconocer, sonido_descripcion);
                }
            } 
        } catch (JSONException e) {
            e.printStackTrace();
        }

		}
        return null;
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after getting all products
        pDialog.dismiss();
        // updating UI from Background Thread
        eds.close();

    }

}
