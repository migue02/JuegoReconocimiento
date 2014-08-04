package es.ugr.librerias;

import java.util.List;

import es.ugr.adapters.AdapterSerieEjercicios;
import es.ugr.basedatos.*;
import es.ugr.dialogs.FichaSerieEjercicios;
import es.ugr.objetos.*;
import es.ugr.utilidades.Sonidos;

import com.mobeta.android.dslv.DragSortListView;

import es.ugr.juegoreconocimiento.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */
public class SeriesEjerciciosLibrary {
	private SerieEjerciciosDataSource dsSerieEjercicios;
	private List<SerieEjercicios> lSerieEjercicios;
	private EjercicioDataSource dsEjercicio;
	private Sonidos sonidos;
	private DragSortListView lv;
	private AdapterSerieEjercicios adapterSerieEjercicios;
	private Activity activity;
	private View view;
	private Context context;

	public void onCreate(Activity activity2) {
		activity = activity2;

		if (view == null)
			context = activity.findViewById(android.R.id.content).getContext();
		else
			context = view.getContext();

		sonidos = new Sonidos(context);
		dsSerieEjercicios = new SerieEjerciciosDataSource(context);
		dsSerieEjercicios.open();
		dsEjercicio = new EjercicioDataSource(context);
		dsEjercicio.open();

		if (view == null) {
			lv = (DragSortListView) activity.findViewById(R.id.ListaSelSer);
		} else {
			lv = (DragSortListView) view.findViewById(R.id.ListaSelSer);
		}

		lv.setDropListener(onDropSer);
		lv.setRemoveListener(onRemoveSer);
		lv.setDragScrollProfile(ssProfileSer);

		CreaTablaSeries();
	}

	public void onDestroy() {
		dsEjercicio.close();
		dsSerieEjercicios.close();
	}

	private void CreaTablaSeries() {
		lSerieEjercicios = dsSerieEjercicios.getAllSeriesEjercicios();
		adapterSerieEjercicios = new AdapterSerieEjercicios(context,
				R.layout.adapter_serie_ejercicios, lSerieEjercicios);
		lv.setAdapter(adapterSerieEjercicios);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int itemposicion = arg2;
				sonidos.playClickRow();
				CrearModificarSeriesEjercicios(lSerieEjercicios.get(itemposicion), false);
			}

		});
	}

	private Runnable runCreaTabla = new Runnable() {
		public void run() {
			CreaTablaSeries();
		}
	};

	public void CrearModificarSeriesEjercicios(SerieEjercicios serie, boolean insertar) {

		FichaSerieEjercicios dialogo = new FichaSerieEjercicios(context,
				dsSerieEjercicios, serie, insertar, runCreaTabla);
		dialogo.show();
		
	}

	private DragSortListView.DropListener onDropSer = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			SerieEjercicios item = adapterSerieEjercicios.getItem(from);

			adapterSerieEjercicios.notifyDataSetChanged();
			adapterSerieEjercicios.remove(item);
			adapterSerieEjercicios.insert(item, to);

			for (int i = 0; i < lSerieEjercicios.size(); i++)
				dsSerieEjercicios
						.actualizaOrden(lSerieEjercicios.get(i), i + 1);

			sonidos.playDrop();
		}
	};

	private DragSortListView.RemoveListener onRemoveSer = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			sonidos.playRemove();

			AlertDialog.Builder alerta = new AlertDialog.Builder(context);
			alerta.setTitle("Eliminar");
			final String nombre = lSerieEjercicios.get(which).getNombre();
			final int id = lSerieEjercicios.get(which).getIdSerie();
			adapterSerieEjercicios
					.remove(adapterSerieEjercicios.getItem(which));
			alerta.setMessage("Se eliminará la serie: " + nombre);
			alerta.setCancelable(false);
			alerta.setPositiveButton("Eliminar",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							boolean borrado = false;
							borrado = dsSerieEjercicios
									.eliminarSerieEjercicios(id);
							if (borrado == true) {
								CreaTablaSeries();
								for (int i = 0; i < lSerieEjercicios.size(); i++)
									dsSerieEjercicios.actualizaOrden(
											lSerieEjercicios.get(i), i + 1);
							}

						}
					});

			alerta.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							lSerieEjercicios = dsSerieEjercicios
									.getAllSeriesEjercicios();
							CreaTablaSeries();
						}
					});

			alerta.show();

		}
	};

	private DragSortListView.DragScrollProfile ssProfileSer = new DragSortListView.DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			if (w > 0.8f) {
				// Traverse all views in a millisecond
				return ((float) adapterSerieEjercicios.getCount()) / 0.001f;
			} else {
				return 10.0f * w;
			}
		}
	};

	public void setView(View rootView) {
		view = rootView;
	}

}
