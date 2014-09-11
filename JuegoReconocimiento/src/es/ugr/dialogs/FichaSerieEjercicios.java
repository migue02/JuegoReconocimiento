package es.ugr.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mobeta.android.dslv.DragSortListView;

import es.ugr.adaptadores.AdapterEjercicio;
import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.basedatos.SerieEjerciciosDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Ejercicio;
import es.ugr.objetos.SerieEjercicios;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FichaSerieEjercicios extends Dialog {

	private Context context;
	private SerieEjerciciosDataSource dsSerieEjercicios;
	private EjercicioDataSource dsEjercicio;
	private List<Ejercicio> lEjercicios;
	private SerieEjercicios oSerieEjercicios;
	private ArrayList<Ejercicio> leaux;
	private AdapterEjercicio adapterEjercicio;
	private boolean insertar;

	private EditText duracion;
	private Runnable function;
	private Button aniadirEjSer;
	private Button guardarSerie;
	private EditText nomSerie;
	private DragSortListView lvEj;

	public FichaSerieEjercicios(Context context,
			SerieEjerciciosDataSource dsSerie, SerieEjercicios serieEjercicios,
			boolean esNuevo, Runnable function) {
		super(context);
		this.context = context;
		this.dsSerieEjercicios = dsSerie;
		this.oSerieEjercicios = serieEjercicios;
		this.insertar = esNuevo;
		this.function = function;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_serie_ejercicios);

		if (insertar == true)
			setTitle("Insertar Serie");
		else
			setTitle("Modificar Serie");

		// Modificar elementos dentro del dialogo
		nomSerie = (EditText) findViewById(R.id.NomSerie);
		nomSerie.setText(oSerieEjercicios.getNombre());
		duracion = (EditText) findViewById(R.id.Duracion);
		duracion.setText(String.valueOf(oSerieEjercicios.getDuracion()));

		// Table Layout dentro dialogo
		lvEj = (DragSortListView) findViewById(R.id.ListaSelEj);

		lvEj.setDropListener(onDropEj);
		lvEj.setRemoveListener(onRemoveEj);
		lvEj.setDragScrollProfile(ssProfileEj);

		leaux = new ArrayList<Ejercicio>();
		dsEjercicio = new EjercicioDataSource(context);
		dsEjercicio.open();
		for (int i = 0; i < oSerieEjercicios.getEjercicios().size(); i++) {
			leaux.add(dsEjercicio.getEjercicios(oSerieEjercicios
					.getEjercicios().get(i)));
		}

		adapterEjercicio = new AdapterEjercicio(context,
				R.layout.adapter_ejercicios, leaux);

		lvEj.setAdapter(adapterEjercicio);

		aniadirEjSer = (Button)findViewById(R.id.laniadir_ser);
		guardarSerie = (Button)findViewById(R.id.lguardar_cam);

		guardarSerie.setOnClickListener(onGuardarClick);
		aniadirEjSer.setOnClickListener(onAniadirClick);

	}

	@Override
	public void show() {
		super.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(lp);
	}

	View.OnClickListener onGuardarClick = new View.OnClickListener() {
		public void onClick(View v) {
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
				mo = dsSerieEjercicios
						.modificaSerieEjercicios(oSerieEjercicios);
			else
				mo = dsSerieEjercicios.createSerieEjercicios(oSerieEjercicios) != null;
			if (mo) {
				dismiss();
				function.run();
				Toast.makeText(context, "Modificado", Toast.LENGTH_LONG).show();
			}
		}
	};

	View.OnClickListener onAniadirClick = new View.OnClickListener() {
		public void onClick(View v) {
			lEjercicios = dsEjercicio.getAllEjercicios();
			AlertDialog.Builder diaEjer = new AlertDialog.Builder(context);
			List<String> nombres = creaLista(lEjercicios);
			final CharSequence[] items = nombres.toArray(new String[nombres
					.size()]);
			diaEjer.setTitle("Seleccione Ejercicio").setItems(items,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							leaux.add(lEjercicios.get(which));
							recargaDuracion();
							
							adapterEjercicio = new AdapterEjercicio(context,
									R.layout.adapter_ejercicios, leaux);
							lvEj.setAdapter(adapterEjercicio);
						}
					});
			diaEjer.show();
		}
	};

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

}
