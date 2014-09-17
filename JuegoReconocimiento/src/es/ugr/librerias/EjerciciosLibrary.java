package es.ugr.librerias;

import java.util.List;

import com.mobeta.android.dslv.DragSortListView;

import es.ugr.objetos.*;
import es.ugr.utilidades.Ficheros;
import es.ugr.utilidades.Sonidos;
import es.ugr.utilidades.Utilidades;
import es.ugr.activities.MainActivity;
import es.ugr.adaptadores.AdapterEjercicio;
import es.ugr.basedatos.*;
import es.ugr.juegoreconocimiento.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import es.ugr.bdremota.*;
import es.ugr.dialogs.FichaEjercicio;
import es.ugr.dialogs.ImportarEjercicios;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */

public class EjerciciosLibrary {
	private List<Ejercicio> lEjercicios;
	private EjercicioDataSource dsEjercicio;
	private AdapterEjercicio adaptador;
	private DragSortListView lv;
	private Sonidos sonidos;

	private Activity activity;
	private View view;
	private Context context;

	public void onCreate(Activity activity2) {
		activity = activity2;

		sonidos = new Sonidos(activity);

		if (view == null)
			context = activity.findViewById(android.R.id.content).getContext();
		else
			context = view.getContext();

		dsEjercicio = new EjercicioDataSource(context);
		dsEjercicio.open();

		if (view == null) {
			lv = (DragSortListView) activity.findViewById(R.id.ListaSelEj);
		} else {
			lv = (DragSortListView) view.findViewById(R.id.ListaSelEj);
		}

		lv.setDropListener(onDrop);
		lv.setRemoveListener(onRemove);
		lv.setDragScrollProfile(ssProfile);

		Ficheros.creaCarpetas(context);// por que?¿?¿?¿

		CreaTablaEjer();

	}

	public void onDestroy() {
		if (dsEjercicio == null)
			dsEjercicio.close();
	}

	public void sincronizarEjercicios() {
		if (Utilidades.hasInternetConnection(context))
			new SincronizarEjercicios(context, runCreaTabla).execute();
		else
			Toast.makeText(context, "No hay conexión", Toast.LENGTH_LONG)
					.show();
	}

	private Runnable runCreaTabla = new Runnable() {
		public void run() {
			CreaTablaEjer();
		}
	};

	public void importarEjercicios() {
		final ImportarEjercicios dialogo = new ImportarEjercicios(context,
				dsEjercicio, runCreaTabla);
		DialogInterface.OnDismissListener dialogClickListener = new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (dialogo.bFalloSincronizar)
					new AlertDialog.Builder(context)
							.setTitle("Importar fichero XML")
							.setPositiveButton("Aceptar", null)
							.setMessage(
									"Ha ocurrido un problema al importar el fichero elegido"
											+ "\nPor favor compruebe que cumple la estructura requerida")
							.show();
				if (dialogo.bEjercicioExiste)
					new AlertDialog.Builder(context)
							.setTitle("Importar fichero XML")
							.setPositiveButton("Aceptar", null)
							.setMessage(
									"Uno o más ejercicios no se han podido importar ya que existen en la base de datos")
							.show();
			}
		};
		dialogo.setOnDismissListener(dialogClickListener);
		dialogo.show();

	}

	private void CreaTablaEjer() {
		lEjercicios = dsEjercicio.getAllEjercicios();
		adaptador = new AdapterEjercicio(context, R.layout.adapter_ejercicios,
				lEjercicios);
		lv.setAdapter(adaptador);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int itemposicion = arg2;
				sonidos.playClickRow();
				MostrarDescripcion(itemposicion);
			}

		});

	}

	private void MostrarDescripcion(final int pos) {
		try {
			FichaEjercicio ficha = new FichaEjercicio(context, dsEjercicio,
					lEjercicios.get(pos), runCreaTabla);
			ficha.show();
		} catch (Exception e) {
			new AlertDialog.Builder(activity)
					.setTitle("Sincronizar objetos")
					.setPositiveButton("Aceptar", null)
					.setMessage(
							"Hay objetos en el ejercicio que no están sincronizados, por favor sincronice los objetos")
					.show();
		}
	}

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			Ejercicio item = adaptador.getItem(from);

			adaptador.notifyDataSetChanged();
			adaptador.remove(item);
			adaptador.insert(item, to);

			for (int i = 0; i < lEjercicios.size(); i++)
				dsEjercicio.actualizaOrden(lEjercicios.get(i), i + 1);

			sonidos.playDrop();
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			sonidos.playRemove();
			adaptador.remove(adaptador.getItem(which));
			for (int i = 0; i < lEjercicios.size(); i++)
				dsEjercicio.actualizaOrden(lEjercicios.get(i), i + 1);
		}
	};

	private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			if (w > 0.8f) {
				// Traverse all views in a millisecond
				return ((float) adaptador.getCount()) / 0.001f;
			} else {
				return 10.0f * w;
			}
		}
	};

	public void setView(View rootView) {
		view = rootView;
	}

}
