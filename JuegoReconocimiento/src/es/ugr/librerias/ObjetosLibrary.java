package es.ugr.librerias;

import java.util.List;

import es.ugr.adaptadores.AdapterObjeto;
import es.ugr.objetos.*;
import es.ugr.utilidades.Sonidos;
import es.ugr.utilidades.Utilidades;
import es.ugr.basedatos.*;
import es.ugr.bdremota.SincronizarObjetos;
import es.ugr.dialogs.FichaObjeto;
import es.ugr.juegoreconocimiento.R;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */

public class ObjetosLibrary {
	private List<Objeto> lo;
	private ObjetoDataSource ods;
	private AdapterObjeto adaptador;
	private ListView lv;
	private Sonidos sonidos;

	private Context context;
	private Activity activity;
	private View view;

	public void onCreate(Activity activity2) {

		activity = activity2;

		if (view == null)
			context = activity.findViewById(android.R.id.content).getContext();
		else
			context = view.getContext();

		sonidos = new Sonidos(context);

		if (view == null) {
			lv = (ListView) activity.findViewById(R.id.ListaSelObj);
		} else
			lv = (ListView) view.findViewById(R.id.ListaSelObj);

		ods = new ObjetoDataSource(context);
		ods.open();

		CreaTablaObj();
	}

	public void onDestroy() {
		ods.close();
	}

	public void CreaTablaObj() {
		lo = ods.getAllObjetos();
		adaptador = new AdapterObjeto(context, R.layout.adapter_objeto, lo);
		lv.setAdapter(adaptador);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int itemposicion = arg2;
				sonidos.playClickRow();
				MostrarDescripcion(lo.get(itemposicion));
			}
		});

	}
	
	private Runnable runCreaTabla = new Runnable() {
	    public void run() {
	    	CreaTablaObj();
	    }
	};

	public void sincronizarObjetos() {
		if (Utilidades.hasInternetConnection(context))
			new SincronizarObjetos(context, runCreaTabla).execute();
		else
			Toast.makeText(context, "No hay conexión", Toast.LENGTH_LONG)
					.show();
	}

	private void MostrarDescripcion(Objeto objeto) {
		FichaObjeto fichaObjeto = new FichaObjeto(context, objeto);
		fichaObjeto.show();
	}

	public void setView(View rootView) {
		view = rootView;
	}

}
