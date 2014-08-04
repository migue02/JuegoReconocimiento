package es.ugr.librerias;

import java.util.List;

import com.mobeta.android.dslv.DragSortListView;

import es.ugr.juegoreconocimiento.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import es.ugr.objetos.*;
import es.ugr.adapters.AdapterAlumno;
import es.ugr.basedatos.*;
import es.ugr.dialogs.FichaAlumno;
import es.ugr.utilidades.Sonidos;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */
public class GestionAlumnosLibrary {
	private AlumnoDataSource dsAlumno;
	private List<Alumno> ls;
	private Sonidos sonidos;
	private DragSortListView lv;
	private AdapterAlumno adaptador;
	private Activity activity;
	private View view;

	public void onCreate(Activity activity2) {
		activity = activity2;

		dsAlumno = new AlumnoDataSource(activity);
		dsAlumno.open();

		// Crea la listView
		sonidos = new Sonidos(activity);

		if (view == null) { // si se va a crear una actividad
			lv = (DragSortListView) activity.findViewById(R.id.ListaAl);
			/*
			 * activity.findViewById(android.R.id.content).setBackgroundResource(
			 * R.drawable.ugr_background);
			 */
		} else
			lv = (DragSortListView) view.findViewById(R.id.ListaAl);

		lv.setDropListener(onDrop);
		lv.setRemoveListener(onRemove);
		lv.setDragScrollProfile(ssProfile);

		CreaTablaAlumnos();
	}

	public void onDestroy() {
		if (dsAlumno != null)
			dsAlumno.close();
	}

	private void CreaTablaAlumnos() {
		
		ls = dsAlumno.getAllAlumnos();
		adaptador = new AdapterAlumno(activity, R.layout.drag_alum, ls);
		lv.setAdapter(adaptador);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int itemposicion = arg2;
				sonidos.playClickRow();
				CrearModificarAlumnos(false, ls.get(itemposicion));
			}

		});

	}
	
	private Runnable runCreaTabla = new Runnable() {
	    public void run() {
	    	CreaTablaAlumnos();
	    }
	};

	public void CrearModificarAlumnos(final boolean insertar,
			final Alumno alumno) {
		FichaAlumno dialogo = new FichaAlumno(activity.findViewById(android.R.id.content).getContext(), dsAlumno, 
				alumno, insertar, runCreaTabla);
		dialogo.show();
	}

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			// Ejercicio item=adapterselej.getItem(from);
			Alumno item = adaptador.getItem(from);

			adaptador.notifyDataSetChanged();
			adaptador.remove(item);
			adaptador.insert(item, to);

			/*
			 * for(int i=0;i<le.size();i++) eds.actualizaOrden(le.get(i), i+1);
			 */
			sonidos.playDrop();
			for (int i = 0; i < ls.size(); i++)
				dsAlumno.actualizaOrden(ls.get(i), i + 1);

		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			sonidos.playRemove();

			AlertDialog.Builder alerta = new AlertDialog.Builder(activity);
			alerta.setTitle("Eliminar");
			final String nombre = ls.get(which).getNombre();
			final int id = ls.get(which).getIdAlumno();
			adaptador.remove(adaptador.getItem(which));
			alerta.setMessage("Se eliminará la el alumno: " + nombre);
			alerta.setCancelable(false);
			alerta.setPositiveButton("Eliminar",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							boolean borrado = false;
							borrado = dsAlumno.borraAlumno(id);
							if (borrado == true) {
								CreaTablaAlumnos();
								for (int i = 0; i < ls.size(); i++)
									dsAlumno.actualizaOrden(ls.get(i), i + 1);
							}

						}
					});

			alerta.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ls = dsAlumno.getAllAlumnos();
							CreaTablaAlumnos();
						}
					});

			alerta.show();

		}
	};

	private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			if (w > 0.8f) {
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
