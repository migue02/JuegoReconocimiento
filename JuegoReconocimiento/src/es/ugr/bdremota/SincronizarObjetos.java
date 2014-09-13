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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Objeto;
import es.ugr.utilidades.JSONParser;
import es.ugr.utilidades.Utilidades;

public class SincronizarObjetos extends AsyncTask<Void, String, String> {

	/**
	 * Before starting background thread Show Progress Dialog
	 * */
	private Context context;
	private ProgressDialog pDialog;
	private JSONParser jParser;
	private String url_all_objetos = "";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_OBJETOS = "objetos";
	private static final String TAG_NOMBRE = "nombre";
	private static final String TAG_FECHA = "fecha";

	private JSONArray Objetos = null;
	private ArrayList<String> NombObjetosListRem;
	private ArrayList<Date> FechaObjetosListRem;

	private List<String> addToLocal;
	private List<String> addToRemote;
	private List<String> updateToLocal;
	private List<String> updateToRemote;

	private Runnable rCreaTabla;

	public SincronizarObjetos(Context context, Runnable creaTabla) {
		this.context = context;
		url_all_objetos = context.getString(R.string.servidor_remoto)
				+ "get_all_id_objetos.php";
		jParser = new JSONParser();

		NombObjetosListRem = new ArrayList<String>();
		FechaObjetosListRem = new ArrayList<Date>();

		addToLocal = new ArrayList<String>();
		addToRemote = new ArrayList<String>();

		updateToLocal = new ArrayList<String>();
		updateToRemote = new ArrayList<String>();

		rCreaTabla = creaTabla;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Cargando...");
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
		JSONObject json = jParser.makeHttpRequest(url_all_objetos, "GET",
				params);

		try {
			// Checking for SUCCESS TAG
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1) {
				Objetos = json.getJSONArray(TAG_OBJETOS);

				for (int i = 0; i < Objetos.length(); i++) {
					JSONObject c = Objetos.getJSONObject(i);

					String nombre = c.getString(TAG_NOMBRE);
					String fecha = c.getString(TAG_FECHA);

					NombObjetosListRem.add(nombre);
					try {
						FechaObjetosListRem.add(new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss").parse(fecha));
					} catch (ParseException e) {
						e.printStackTrace();
					}
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
		pDialog.dismiss();
		ObjetoDataSource ods = new ObjetoDataSource(context);
		ods.open();
		ArrayList<Objeto> objs = ods.getAllObjetos();
		ods.close();

		// Para cada Objeto que hay en local,
		for (int i = 0; i < objs.size(); i++) {
			int pos = NombObjetosListRem.indexOf(objs.get(i).getNombre());
			if (pos != -1) {
				if (objs.get(i).getFecha()
						.compareTo(FechaObjetosListRem.get(pos)) > 0)
					updateToRemote.add(objs.get(i).getNombre());
			} else
				addToRemote.add(objs.get(i).getNombre());

		}

		// Para cada Objeto que hay en remoto
		for (int i = 0; i < NombObjetosListRem.size(); i++) {
			boolean found = false;
			for (int j = 0; j < objs.size() && found == false; j++) {
				if (objs.get(j).getNombre().equals(NombObjetosListRem.get(i))) {
					found = true;
					if (FechaObjetosListRem.get(i).compareTo(
							objs.get(j).getFecha()) > 0)
						updateToLocal.add(NombObjetosListRem.get(i));
				}

			}
			if (!found)
				addToLocal.add(NombObjetosListRem.get(i));
		}

		Utilidades.LiberaImagenes(objs);

		if (addToRemote.size() > 0 || updateToRemote.size() > 0
				|| addToLocal.size() > 0 || updateToLocal.size() > 0) {
			/*
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
			alertDialog.setTitle("Sincronizar Objetos");
			String mensaje = String.valueOf(addToRemote.size()) + " nuevo(s), "
					+ String.valueOf(updateToRemote.size())
					+ " a actualizar en remoto. \n"
					+ String.valueOf(addToLocal.size()) + " nuevo(s), "
					+ String.valueOf(updateToLocal.size())
					+ " a actualizar en local. \n \n¿Desea sincronizar?";
			alertDialog
					.setMessage(mensaje)
					.setPositiveButton("Si",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									new SubirObjetos(context).execute(
											addToRemote, updateToRemote);
									new DescargarObjetos(context, rCreaTabla)
											.execute(addToLocal, updateToLocal);
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});

			AlertDialog alert = alertDialog.create();
			alert.show();*/
			
			final Dialog dialogo=new Dialog(context);
			dialogo.getWindow().setBackgroundDrawableResource(R.color.white);
			dialogo.setTitle("¿Desea sincronizar los objetos?");
			dialogo.setContentView(R.layout.dialogo_sincronizar);
			((TextView)dialogo.findViewById(R.id.toServer)).setText(String.valueOf(addToRemote.size()+updateToRemote.size()));
			((TextView)dialogo.findViewById(R.id.toTablet)).setText(String.valueOf(addToLocal.size()+updateToLocal.size()));
			((Button)dialogo.findViewById(R.id.aSincronizar)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new SubirObjetos(context).execute(
							addToRemote, updateToRemote);
					new DescargarObjetos(context, rCreaTabla)
							.execute(addToLocal, updateToLocal);
					dialogo.dismiss();
				}
			});
			
			((Button)dialogo.findViewById(R.id.cSincronizar)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogo.dismiss();
				}
			});
			
			dialogo.show();

			
			
			
		}else{
			
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Sincronizar objetos");
			builder.setMessage("Los objetos están sincronizados.");
			builder.setPositiveButton("Aceptar", null);
			builder.create();
			builder.show();

		}

	}

}
