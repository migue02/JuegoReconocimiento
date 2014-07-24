package es.ugr.gestion_ejercicios;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.mobeta.android.dslv.DragSortListView;

import es.ugr.objetos.*;
import es.ugr.parserXML.EjercicioParser;
import es.ugr.parserXML.EjerciciosMarker;
import es.ugr.utilidades.Sonidos;
import es.ugr.utilidades.Utilidades;
import es.ugr.adapters.AdapterEjercicio;
import es.ugr.basedatos.*;
import es.ugr.juegoreconocimiento.MainActivity;
import es.ugr.juegoreconocimiento.R;
import es.ugr.lista_navegacion.ListaNavegacionActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserDialog;
import es.ugr.bdremota.*;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */

public class EjerciciosLibrary {
	private Dialog dialogo;
	private List<Ejercicio> lEjercicios;
	private EjercicioDataSource dsEjercicio;
	private View ImportarEj, SincronizarEj;
	private AdapterEjercicio adaptador;
	private DragSortListView lv;
	private Sonidos sonidos;

	private Activity activity;
	private View view;
	private Context context;

	public void onCreate(Activity activity2) {
		activity = activity2;

		activity.getActionBar()
				.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		activity.getActionBar().setDisplayHomeAsUpEnabled(true);
		activity.getActionBar().setCustomView(R.layout.action_bar_ejercicios);

		ImportarEj = activity.findViewById(R.id.ImportarEj);
		SincronizarEj = activity.findViewById(R.id.SincronizarEj);

		sonidos = new Sonidos(activity);

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
		lEjercicios = dsEjercicio.getAllEjercicios();

		Utilidades.creaCarpetas(context);//por que?¿?¿?¿
		
		CreaTablaEjer();

		ImportarEj.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final Dialog dialogo = new Dialog(context);
				dialogo.setTitle("Importar Ejercicio");
				dialogo.setContentView(R.layout.dialogo_importar_ej);

				final EditText etFich;
				final EditText etURL;

				etFich = (EditText) dialogo.findViewById(R.id.editTextFich);
				etURL = (EditText) dialogo.findViewById(R.id.editTextURL);
				etURL.setText("http://192.168.1.103/bd_reconocimiento/XML/segun.xml");

				Button impor, cancelar;
				final Button selFich;
				final RadioButton rb1;
				final RadioButton rb2;
				rb1 = (RadioButton) dialogo.findViewById(R.id.radioButtonExp1);
				rb2 = (RadioButton) dialogo.findViewById(R.id.radioButtonExp2);

				impor = (Button) dialogo.findViewById(R.id.aImportar);
				cancelar = (Button) dialogo.findViewById(R.id.cImportar);
				selFich = (Button) dialogo.findViewById(R.id.BotonSelFich);

				selFich.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						FileChooserDialog dialog = new FileChooserDialog(
								dialogo.getContext());
						dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {

							@Override
							public void onFileSelected(Dialog source,
									File folder, String name) {
								source.hide();
								Toast toast = Toast.makeText(
										source.getContext(),
										"File created: " + folder.getName()
												+ "/" + name, Toast.LENGTH_LONG);
								toast.show();
							}

							@Override
							public void onFileSelected(Dialog source, File file) {
								source.hide();
								Toast toast = Toast.makeText(
										source.getContext(),
										"Fichero Seleccionado: "
												+ file.getPath(),
										Toast.LENGTH_LONG);
								toast.show();
								etFich.setText(file.getPath());
							}
						});

						dialog.show();
					}
				});

				impor.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						RetrieveFeed task = new RetrieveFeed();
						if (rb1.isChecked()) {

							task.execute(etFich.getText().toString(), "Fichero");
						}
						if (rb2.isChecked()) {
							task.execute(etURL.getText().toString(), "URL");
						}
						dialogo.dismiss();

					}
				});

				cancelar.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialogo.dismiss();
					}
				});

				rb1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						rb2.setChecked(false);
					}
				});

				rb2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						rb1.setChecked(false);
					}
				});

				// Show the dialog.
				dialogo.show();
			}
		});

		SincronizarEj.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Utilidades.hasInternetConnection(context))
					new SincronizarEjercicios(context).execute();
				else {
					Toast toast = Toast.makeText(context, "No hay conexión",
							Toast.LENGTH_LONG);
					toast.show();
				}
			}
		});
	}

	public void onDestroy() {
		if (dsEjercicio == null)
			dsEjercicio.close();
	}

	public void CreaTablaEjer() {
		
		adaptador = new AdapterEjercicio(context, R.layout.adapter_ejercicios, lEjercicios);
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

		// Lanzar Dialog
		dialogo = new Dialog(context);
		dialogo.setContentView(R.layout.dialogo_ejercicios);
		dialogo.setTitle(lEjercicios.get(pos).getNombre());

		// Modificar elementos dentro del dialogo
		final EditText duracion = (EditText) dialogo
				.findViewById(R.id.DuracionEj);
		duracion.setText(String.valueOf(lEjercicios.get(pos).getDuracion()));
		final TextView descripcion = (TextView) dialogo
				.findViewById(R.id.textDesc);
		descripcion.setText(lEjercicios.get(pos).getDescripcion());

		final ObjetoDataSource ods = new ObjetoDataSource(dialogo.getContext());
		ods.open();

		Objeto obj = new Objeto();

		final TextView escenario = (TextView) dialogo
				.findViewById(R.id.textEscenario);
		escenario.setTextSize(20);

		String textEscenario = new String("");
		for (int i = 0; i < lEjercicios.get(pos).getObjetos().size(); i++) {
			obj = ods.getObjeto(lEjercicios.get(pos).getObjetos().get(i));
			textEscenario = textEscenario + obj.getNombre();
			if (i != lEjercicios.get(pos).getObjetos().size() - 1)
				textEscenario = textEscenario + " ,";
		}
		escenario.setText(textEscenario);
		// Table Layout dentro dialogo

		TableLayout tablaObjetos = (TableLayout) dialogo
				.findViewById(R.id.tablaDiaEjercicios);
		tablaObjetos.removeAllViews();

		TableRow filaObj;
		TextView te1, te2;

		// Para cada ejercicio

		for (int j = 0; j < lEjercicios.get(pos).getObjetosReconocer().size(); j++) {
			filaObj = new TableRow(dialogo.getContext());

			te1 = new TextView(dialogo.getContext());
			te1.setText(String.valueOf(j + 1));
			te1.setPadding(2, 0, 5, 0);
			te1.setTextSize(20);

			te2 = new TextView(dialogo.getContext());
			obj = ods.getObjeto(lEjercicios.get(pos).getObjetosReconocer()
					.get(j));
			te2.setText(obj.getNombre());
			te2.setPadding(2, 0, 5, 0);
			te2.setTextSize(20);

			filaObj.addView(te1);
			filaObj.addView(te2);

			tablaObjetos.addView(filaObj);
		}

		ods.close();

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialogo.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;

		dialogo.show();
		dialogo.getWindow().setAttributes(lp);

		// Boton guardar
		ImageButton guardarSerie = (ImageButton) dialogo
				.findViewById(R.id.guardarDiaEj);
		guardarSerie.setBackgroundResource(R.drawable.selicono);
		guardarSerie.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				lEjercicios.get(pos).setDuracion(
						Integer.parseInt(duracion.getText().toString()));
				dsEjercicio.modificaEjercicio(lEjercicios.get(pos));
				CreaTablaEjer();

				Toast.makeText(context,
						"Tiempo estimado modificado", Toast.LENGTH_LONG).show();
			}
		});

	}

	private class RetrieveFeed extends AsyncTask<String, Integer, Boolean> {

		public List<EjerciciosMarker> ListaEj;

		protected Boolean doInBackground(String... params) {

			EjercicioParser ejercicioparser = new EjercicioParser(params[0],
					params[1]);
			ListaEj = ejercicioparser.parse();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			for (int i = 0; i < ListaEj.size(); i++) {
				dsEjercicio.createEjercicio(ListaEj.get(i).getNombre(),
						new Date(), ListaEj.get(i).getEscenario(),
						ListaEj.get(i).getDescripcion(), ListaEj.get(i)
								.getDuracion(), ListaEj.get(i).getReconocer(),
						ListaEj.get(i).getSonidoDescripcion());
			}

			Toast toast2 = Toast.makeText(context, "Creados "
					+ String.valueOf(ListaEj.size()) + " ejercicios.",
					Toast.LENGTH_LONG);
			toast2.show();
			CreaTablaEjer();

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
