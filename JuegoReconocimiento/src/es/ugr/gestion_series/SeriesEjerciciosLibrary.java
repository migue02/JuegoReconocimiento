package es.ugr.gestion_series;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.ugr.adapters.AdapterEjercicio;
import es.ugr.adapters.AdapterSerieEjercicios;
import es.ugr.basedatos.*;
import es.ugr.objetos.*;
import es.ugr.utilidades.Sonidos;

import com.mobeta.android.dslv.DragSortListView;

import es.ugr.juegoreconocimiento.MainActivity;
import es.ugr.juegoreconocimiento.R;
import es.ugr.lista_navegacion.ListaNavegacionActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
	private Dialog dialogo;
	private List<Ejercicio> lEjercicios;
	private View añadirSerie;
	private EjercicioDataSource dsEjercicio;
	private EditText duracion;
	private AdapterEjercicio adapterEjercicio;
	private Sonidos sonidos;
	private DragSortListView lv;
	private AdapterSerieEjercicios adapterSerieEjercicios;
	private List<Ejercicio> leaux;
	private SerieEjercicios oSerieEjercicios;

	private Activity activity;
	private View view;
	private Context context;

	public void onCreate(Activity activity2) {
		activity = activity2;

		activity.getActionBar()
				.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		activity.getActionBar().setDisplayHomeAsUpEnabled(true);
		activity.getActionBar().setCustomView(
				R.layout.action_bar_serie_ejercicios);

		ImageButton mHome = (ImageButton) activity.findViewById(R.id.btnHome);
		mHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent;
				if (view == null)
					intent = new Intent(activity, ListaNavegacionActivity.class);
				else
					intent = new Intent(activity, MainActivity.class);
				activity.startActivity(intent);
				activity.finish();
			}
		});

		añadirSerie = activity.findViewById(R.id.aniadir_serl);
		añadirSerie.setBackgroundResource(R.drawable.selicono);
		añadirSerie.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CrearModificarSeriesEjercicios(0, true);
			}
		});

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
		lSerieEjercicios = dsSerieEjercicios.getAllSeriesEjercicios();

		CreaTablaSeries();
	}

	public void onDestroy() {
		dsEjercicio.close();
		dsSerieEjercicios.close();
	}

	private void CreaTablaSeries() {
		adapterSerieEjercicios = new AdapterSerieEjercicios(context,
				R.layout.adapter_serie_ejercicios, lSerieEjercicios);
		lv.setAdapter(adapterSerieEjercicios);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int itemposicion = arg2;
				sonidos.playClickRow();
				CrearModificarSeriesEjercicios(itemposicion, false);
			}

		});
	}

	private void CrearModificarSeriesEjercicios(final int pos,
			final boolean insertar) {
		// Lanzar Dialog
		dialogo = new Dialog(context);
		dialogo.setContentView(R.layout.dialogo_serie_ejercicios);

		oSerieEjercicios = new SerieEjercicios();
		if (insertar == true) {
			dialogo.setTitle("Insertar Serie");
			oSerieEjercicios = new SerieEjercicios();
		} else {
			dialogo.setTitle("Modificar Serie");
			oSerieEjercicios = lSerieEjercicios.get(pos);
		}
		// Modificar elementos dentro del dialogo
		final EditText nomSerie = (EditText) dialogo
				.findViewById(R.id.NomSerie);
		nomSerie.setText(oSerieEjercicios.getNombre());
		duracion = (EditText) dialogo.findViewById(R.id.Duracion);
		duracion.setText(String.valueOf(oSerieEjercicios.getDuracion()));
		// Table Layout dentro dialogo

		final DragSortListView lvEj = (DragSortListView) dialogo
				.findViewById(R.id.ListaSelEj);

		lvEj.setDropListener(onDropEj);
		lvEj.setRemoveListener(onRemoveEj);
		lvEj.setDragScrollProfile(ssProfileEj);

		leaux = new ArrayList<Ejercicio>();

		for (int i = 0; i < oSerieEjercicios.getEjercicios().size(); i++) {
			leaux.add(dsEjercicio.getEjercicios(oSerieEjercicios.getEjercicios().get(i)));
		}

		adapterEjercicio = new AdapterEjercicio(context,
				R.layout.adapter_ejercicios, leaux);

		lvEj.setAdapter(adapterEjercicio);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialogo.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;

		// dialog.getWindow().setLayout(760, 480);
		dialogo.show();
		dialogo.getWindow().setAttributes(lp);

		// Boton aniadir
		View aniadirEjSer = dialogo.findViewById(R.id.laniadir_ser);
		aniadirEjSer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lEjercicios = dsEjercicio.getAllEjercicios();
				AlertDialog.Builder diaEjer = new AlertDialog.Builder(dialogo
						.getContext());
				List<String> nombres = creaLista(lEjercicios);
				final CharSequence[] items = nombres.toArray(new String[nombres
						.size()]);
				diaEjer.setTitle("Seleccione Ejercicio").setItems(items,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								oSerieEjercicios.getEjercicios()
										.add(lEjercicios.get(which)
												.getIdEjercicio());
								leaux.clear();
								for (int i = 0; i < oSerieEjercicios.getEjercicios()
										.size(); i++) {
									leaux.add(dsEjercicio.getEjercicios(oSerieEjercicios
											.getEjercicios().get(i)));
								}
								recargaDuracion();
								adapterEjercicio = new AdapterEjercicio(dialogo
										.getContext(),
										R.layout.adapter_ejercicios, leaux);

								lvEj.setAdapter(adapterEjercicio);
							}
						});
				diaEjer.show();
			}
		});
		// Boton guardar
		View guardarSerie = dialogo.findViewById(R.id.lguardar_cam);

		guardarSerie.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				oSerieEjercicios.setNombre(nomSerie.getText().toString());
				oSerieEjercicios.setDuracion(Integer.parseInt(duracion.getText()
						.toString()));
				oSerieEjercicios.setFecha_modificacion(new Date());

				ArrayList<Integer> listaId = new ArrayList<Integer>();
				for (int i = 0; i < leaux.size(); i++)
					listaId.add(leaux.get(i).getIdEjercicio());

				oSerieEjercicios.setEjercicios(listaId);
				boolean mo = false;
				if (insertar == false)
					mo = dsSerieEjercicios.modificaSerieEjercicios(oSerieEjercicios);
				else
					mo = dsSerieEjercicios.createSerieEjercicios(oSerieEjercicios) != null;
				if (mo) {
					dialogo.dismiss();
					lSerieEjercicios = dsSerieEjercicios
							.getAllSeriesEjercicios();
					// Como se ha modificado la base de datos, volvemos ha acer
					// una carga de la lista de series de ejercicios
					CreaTablaSeries();
					Toast.makeText(context, "Modificado",
							Toast.LENGTH_LONG).show();
				}

			}
		});

	}

	private List<String> creaLista(List<Ejercicio> le) {
		List<String> dev = new ArrayList<String>();
		for (int i = 0; i < le.size(); i++)
			dev.add(le.get(i).getNombre());
		return dev;
	}

	private void recargaDuracion() {
		int dura = 0;
		for (int i = 0; i < leaux.size(); i++) {
			dura += leaux.get(i).getDuracion();
		}
		duracion.setText(String.valueOf(dura));
	}

	private DragSortListView.DropListener onDropEj = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			Ejercicio item = adapterEjercicio.getItem(from);
			adapterEjercicio.notifyDataSetChanged();
			adapterEjercicio.remove(item);
			adapterEjercicio.insert(item, to);
		}
	};

	private DragSortListView.RemoveListener onRemoveEj = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			adapterEjercicio.remove(adapterEjercicio.getItem(which));
			recargaDuracion();
		}
	};

	private DragSortListView.DragScrollProfile ssProfileEj = new DragSortListView.DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			if (w > 0.8f) {
				// Traverse all views in a millisecond
				return ((float) adapterEjercicio.getCount()) / 0.001f;
			} else {
				return 10.0f * w;
			}
		}
	};

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
			adapterSerieEjercicios.remove(adapterSerieEjercicios.getItem(which));
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
