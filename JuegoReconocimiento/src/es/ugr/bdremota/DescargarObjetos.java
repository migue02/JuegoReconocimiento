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
import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.utilidades.JSONParser;

public class DescargarObjetos extends AsyncTask<List<String>, String, String> {

	/**
	 * Before starting background thread Show Progress Dialog
	 * */

	private Context context;
	private ProgressDialog pDialog;
	private JSONParser jParser;
	private String url_get_objeto = "";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_OBJETO = "objeto";
	private static final String TAG_NOMBRE = "nombre";
	private static final String TAG_DESCRIPCION = "descripcion";
	private static final String TAG_FECHA = "fecha";
	private static final String TAG_KEYPOINTS = "keypoints";
	private static final String TAG_DESCRIPTORES = "descriptores";
	private static final String TAG_COLS = "cols";
	private static final String TAG_ROWS = "rows";
	private static final String TAG_IMAGEN = "imagen";
	private static final String TAG_SONIDO_DESCRIPCION = "sonido_descripcion";
	private static final String TAG_SONIDO_AYUDA = "sonido_ayuda";
	private static final String TAG_SONIDO_NOMBRE = "sonido_nombre";

	private JSONArray objetos = null;

	private ObjetoDataSource ods;
	private Runnable rCreaTabla;

	public DescargarObjetos(Context context, Runnable creaTabla) {
		this.context = context;
		rCreaTabla = creaTabla;
		url_get_objeto = context.getString(R.string.servidor_remoto)
				+ "get_objeto.php";
		ods = new ObjetoDataSource(context);
		ods.open();
		jParser = new JSONParser();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Sincronizando objetos, por favor espere...");
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
		for (int i = 0; i < params[0].size(); i++) {
			InsertarModificar(params[0].get(i), true);
		}

		for (int i = 0; i < params[1].size(); i++)
			InsertarModificar(params[1].get(i), false);
		return "Añadidos: " + params[0].size()
				+ " nuevos objetos; Actualizados: " + params[1].size()
				+ " objetos.";
	}

	/**
	 * After completing background task Dismiss the progress dialog
	 * **/

	protected void onPostExecute(String msg) {
		pDialog.dismiss();
		ods.close();
		// Objetos obj=(Objetos)context;
		// obj.CreaTablaEjer();
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		toast.show();
		
		rCreaTabla.run();

	}

	private void InsertarModificar(String nombreFila, boolean insertar) {
		List<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add(new BasicNameValuePair("nombre", nombreFila));

		// getting JSON string from URL

		JSONObject json = jParser.makeHttpRequest(url_get_objeto, "GET",
				parametros);

		// Check your log cat for JSON reponse
		Log.d("Crear objeto Local: ", json.toString());

		try {
			// Checking for SUCCESS TAG
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1) {
				// products found
				// Getting Array of Products
				objetos = json.getJSONArray(TAG_OBJETO);

				// looping through All Products
				for (int j = 0; j < objetos.length(); j++) {
					JSONObject c = objetos.getJSONObject(j);

					// Storing each json item in variable
					// String id = c.getString(TAG_ID);
					String nombre = c.getString(TAG_NOMBRE);
					Date fecha = new Date();
					try {
						fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.parse(c.getString(TAG_FECHA));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					String descripcion = c.getString(TAG_DESCRIPCION);
					String keypoints = c.getString(TAG_KEYPOINTS);
					String descriptores = c.getString(TAG_DESCRIPTORES);
					int cols = Integer.valueOf(c.getString(TAG_COLS));
					int rows = Integer.valueOf(c.getString(TAG_ROWS));
					String imagen = c.getString(TAG_IMAGEN);
					String ruta_imagen_local = "";
					if (!imagen.equals("")) {
						ruta_imagen_local = context
								.getString(R.string.pathImages)
								+ "/"
								+ nombre
								+ ".png";
						new DescargarFicheros().execute(imagen,
								ruta_imagen_local);
					}
					String sonido_descripcion = c
							.getString(TAG_SONIDO_DESCRIPCION);
					String ruta_descripcion_local = "";
					if (!sonido_descripcion.equals("")) {
						ruta_descripcion_local = context
								.getString(R.string.pathSounds)
								+ "/"
								+ nombre
								+ "_Descripcion.mp3";
						new DescargarFicheros().execute(sonido_descripcion,
								ruta_descripcion_local);
					}

					String sonido_ayuda = c.getString(TAG_SONIDO_AYUDA);
					String ruta_ayuda_local = "";
					if (!sonido_ayuda.equals("")) {
						ruta_ayuda_local = context
								.getString(R.string.pathSounds)
								+ "/"
								+ nombre
								+ "_Ayuda.mp3";
						new DescargarFicheros().execute(sonido_ayuda,
								ruta_ayuda_local);
					}

					String sonido_nombre = c.getString(TAG_SONIDO_NOMBRE);
					String ruta_nombre_local = "";
					if (!sonido_nombre.equals("")) {
						ruta_nombre_local = context
								.getString(R.string.pathSounds)
								+ "/"
								+ nombre
								+ "_Nombre.mp3";
						new DescargarFicheros().execute(sonido_nombre,
								ruta_nombre_local);
					}
					if (insertar == true)
						ods.createObjeto(nombre, descripcion, fecha, keypoints,
								descriptores, cols, rows, ruta_imagen_local,
								ruta_descripcion_local, ruta_ayuda_local,
								ruta_nombre_local);
					else
						ods.modificaObjeto(nombre, descripcion, fecha,
								keypoints, descriptores, cols, rows,
								ruta_imagen_local, ruta_descripcion_local,
								ruta_ayuda_local, ruta_nombre_local);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
