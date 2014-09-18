package es.ugr.bdremota;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Ejercicio;
import es.ugr.utilidades.JSONParser;

class SubirEjercicios extends AsyncTask<List<String>, String, String> {

	/**
	 * Before starting background thread Show Progress Dialog
	 * */

	private Context context;
	private ProgressDialog pDialog;
	private JSONParser jParser;
	private static String url_create_ejercicio = "";
	private static String url_update_ejercicio = "";
	private static String url_sounds = "";

	private static final String TAG_SUCCESS = "success";

	private EjercicioDataSource eds;

	public SubirEjercicios(Context context) {
		this.context = context;
		url_create_ejercicio = context.getString(R.string.servidor_remoto)
				+ "create_ejercicio.php";
		url_update_ejercicio = context.getString(R.string.servidor_remoto)
				+ "update_ejercicio.php";
		url_sounds = context.getString(R.string.servidor_remoto) + "sounds/";
		eds = new EjercicioDataSource(context);
		eds.open();
		jParser = new JSONParser();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Sincronizando...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
	}

	/**
	 * Creating product
	 * */
	protected String doInBackground(List<String>... args) {

		for (int i = 0; i < args[0].size(); i++) {
			Insertar(eds.getEjercicios(args[0].get(i)));
		}

		for (int i = 0; i < args[1].size(); i++) {
			Modificar(eds.getEjercicios(args[1].get(i)));
		}

		return "Subidos: " + args[0].size()
				+ " nuevos ejercicios; Actualizados: " + args[1].size()
				+ " ejercicios.";
	}

	/**
	 * After completing background task Dismiss the progress dialog
	 * **/
	protected void onPostExecute(String file_url) {
		// dismiss the dialog once done
		pDialog.dismiss();

		eds.close();
	}

	private void Insertar(Ejercicio ej) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("nombre", ej.getNombre()));
		params.add(new BasicNameValuePair("fecha", ej.getFechaAsString()));
		params.add(new BasicNameValuePair("objetos", JSONParser
				.ArrayListToJson(ej.getObjetos())));
		params.add(new BasicNameValuePair("descripcion", ej.getDescripcion()));
		params.add(new BasicNameValuePair("duracion", String.valueOf(ej
				.getDuracion())));
		params.add(new BasicNameValuePair("objetosReconocer", JSONParser
				.ArrayListToJson(ej.getObjetosReconocer())));
		if (!ej.getSonido_descripcion().equals("")) {
			String nombre_fich = ej.getNombre() + ".mp3";
			nombre_fich = nombre_fich.trim().replaceAll("\\s+", "_"); // sustituye
																		// espacios
																		// por
																		// barras_baja
			new SubirFicherosSFTP(context).execute(ej.getSonido_descripcion(),
					nombre_fich, "sounds");
			params.add(new BasicNameValuePair("sonido_descripcion", url_sounds
					+ nombre_fich));
		}

		// getting JSON Object
		// Note that create product url accepts POST method
		JSONObject json = jParser.makeHttpRequest(url_create_ejercicio, "POST",
				params);

		// check log cat fro response
		Log.d("Create Response", json.toString());

		// check for success tag
		try {
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1) {
				// successfully created product
				// closing this screen
				// Toast toast = Toast.makeText(AllEjerciciosActivity.this,
				// "Creado ejercicios.", Toast.LENGTH_LONG);
				// toast.show();
			} else {
				// failed to create product
				// json.getString("message")
				// Toast toast = Toast.makeText(AllEjerciciosActivity.this,
				// "Error al crear:", Toast.LENGTH_LONG);
				// toast.show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void Modificar(Ejercicio ej) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("nombre", ej.getNombre()));
		params.add(new BasicNameValuePair("fecha", ej.getFechaAsString()));
		params.add(new BasicNameValuePair("objetos", JSONParser
				.ArrayListToJson(ej.getObjetos())));
		params.add(new BasicNameValuePair("descripcion", ej.getDescripcion()));
		params.add(new BasicNameValuePair("duracion", String.valueOf(ej
				.getDuracion())));
		params.add(new BasicNameValuePair("objetosReconocer", JSONParser
				.ArrayListToJson(ej.getObjetosReconocer())));
		if (!ej.getSonido_descripcion().equals("")) {
			String nombre_fich = ej.getNombre() + ".mp3";
			nombre_fich = nombre_fich.trim().replaceAll("\\s+", "_"); // sustituye
																		// espacios
																		// por
																		// barras_baja
			new SubirFicherosSFTP(context).execute(ej.getSonido_descripcion(),
					nombre_fich, "sounds");
			params.add(new BasicNameValuePair("sonido_descripcion", url_sounds
					+ ej.getNombre() + ".mp3"));
		}

		// getting JSON Object
		// Note that create product url accepts POST method
		JSONObject json = jParser.makeHttpRequest(url_update_ejercicio, "POST",
				params);

		// check log cat fro response
		Log.d("Create Response", json.toString());

		// check for success tag
		try {
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1) {
				// successfully created product
				// closing this screen
				// Toast toast = Toast.makeText(AllEjerciciosActivity.this,
				// "Creado ejercicios.", Toast.LENGTH_LONG);
				// toast.show();
			} else {
				// failed to create product
				// json.getString("message")
				// Toast toast = Toast.makeText(AllEjerciciosActivity.this,
				// "Error al crear:", Toast.LENGTH_LONG);
				// toast.show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}