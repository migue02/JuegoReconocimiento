package es.ugr.gestion_alumnos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.mobeta.android.dslv.DragSortListView;

import es.ugr.juegoreconocimiento.MainActivity;
import es.ugr.juegoreconocimiento.R;
import es.ugr.lista_navegacion.ListaNavegacionActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import es.ugr.objetos.*;
import es.ugr.adapters.AdapterAlumno;
import es.ugr.basedatos.*;
import es.ugr.objetos.TiposPropios.Sexo;
import es.ugr.utilidades.Sonidos;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */
public class GestionAlumnosLibrary {
	private DatePickerDialog Fecha;
	private TextView mFecha;
	private AlumnoDataSource ads;
	private List<Alumno> ls;
	private Sexo sexo;
	private Sonidos sonidos;
	private DragSortListView lv;
	private AdapterAlumno adaptador;
	private Activity activity;
	private View view;

	public void onCreate(Activity activity2) {
		activity = activity2;

		activity.getActionBar()
				.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		activity.getActionBar().setDisplayHomeAsUpEnabled(true);
		activity.getActionBar().setCustomView(
				R.layout.action_bar_gestion_alumnos);

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

		ads = new AlumnoDataSource(activity);
		ads.open();

		// Crea la listView
		sonidos = new Sonidos(activity);

		if (view == null) { // si se va a crear una actividad
			lv = (DragSortListView) activity.findViewById(R.id.ListaAl);
			/*activity.findViewById(android.R.id.content).setBackgroundResource(
					R.drawable.ugr_background);*/
		} else
			lv = (DragSortListView) view.findViewById(R.id.ListaAl);

		lv.setDropListener(onDrop);
		lv.setRemoveListener(onRemove);
		lv.setDragScrollProfile(ssProfile);

		ls = ads.getAllAlumnos();

		View BotonAniadir=activity.findViewById(R.id.aniadir_alumno);
        BotonAniadir.setBackgroundResource(R.drawable.selicono);
        BotonAniadir.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				CrearModificarAlumnos(true, new Alumno());
			}
		});
		
		CreaTablaAlumnos();
	}

	public void onDestroy() {
		if (ads != null)
			ads.close();
	}

	private void CreaTablaAlumnos() {

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

	private class PickDate implements DatePickerDialog.OnDateSetListener {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			view.updateDate(year, monthOfYear, dayOfMonth);
			int mes = monthOfYear + 1;
			mFecha.setText(dayOfMonth + "/" + mes + "/" + year);
			Fecha.hide();
		}

	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	private void CrearModificarAlumnos(final boolean insertar,
			final Alumno alumno) {

		// Lanzar Dialog
		final Dialog dialog = new Dialog(activity);
		dialog.setContentView(R.layout.dialogo_alumnos);
		if (insertar == false)
			dialog.setTitle("Modificar Alumno");
		else
			dialog.setTitle("Crear Alumno");

		// Captacion de los componentes
		final Button GuardarDia, CancelarDialog;
		final EditText enombre;
		final EditText eapellidos;
		final EditText eobservaciones;
		final ImageButton chico, chica;
		final ImageView imPrincipal;
		TextView efecha;

		// Componentes del layout
		enombre = (EditText) dialog.findViewById(R.id.daNombre);
		eapellidos = (EditText) dialog.findViewById(R.id.daApellidos);
		efecha = (TextView) dialog.findViewById(R.id.MuestraFecha);
		eobservaciones = (EditText) dialog.findViewById(R.id.daObserva);
		chico = (ImageButton) dialog.findViewById(R.id.BotonHombre);
		chica = (ImageButton) dialog.findViewById(R.id.BotonMujer);
		GuardarDia = (Button) dialog.findViewById(R.id.gAlumnos);
		CancelarDialog = (Button) dialog.findViewById(R.id.cAlumnos);
		imPrincipal = (ImageView) dialog.findViewById(R.id.AlumPrin);

		// Asignacion de los valores del tipo alumno
		enombre.setText(alumno.getNombre());
		eapellidos.setText(alumno.getApellidos());
		efecha.setText(alumno.getFecha_nac_AsStrign());
		eobservaciones.setText(alumno.getObservaciones());
		switch (alumno.getSexo()) {
		case Hombre:
			chico.setSelected(true);
			chica.setSelected(false);
			imPrincipal.setImageResource(R.drawable.boy_amp);
			sexo = Sexo.Hombre;
			break;
		case Mujer:
			chica.setSelected(true);
			chico.setSelected(false);
			imPrincipal.setImageBitmap(decodeSampledBitmapFromResource(
					activity.getResources(), R.drawable.girl_amp, 120, 120));
			// imPrincipal.setImageResource(R.drawable.girl_amp);
			sexo = Sexo.Mujer;
			break;
		default:
			chico.setSelected(true);
			chica.setSelected(false);
			imPrincipal.setImageResource(R.drawable.boy_amp);
			sexo = Sexo.Hombre;
			break;
		}

		// Controlador Fecha
		Button cFecha;

		cFecha = (Button) dialog.findViewById(R.id.CambiarFecha);
		mFecha = (TextView) dialog.findViewById(R.id.MuestraFecha);
		Fecha = null;
		cFecha.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Calendar dtTxt = null;
				String preExistingDate = (String) mFecha.getText().toString();
				if (preExistingDate != null && !preExistingDate.equals("")) {
					StringTokenizer st = new StringTokenizer(preExistingDate,
							"/");
					String initialDate = st.nextToken();
					String initialMonth = st.nextToken();
					String initialYear = st.nextToken();
					if (Fecha == null)
						Fecha = new DatePickerDialog(arg0.getContext(),
								new PickDate(), Integer.parseInt(initialYear),
								Integer.parseInt(initialMonth) - 1, Integer
										.parseInt(initialDate));
					Fecha.updateDate(Integer.parseInt(initialYear),
							Integer.parseInt(initialMonth) - 1,
							Integer.parseInt(initialDate));
				} else {
					dtTxt = Calendar.getInstance();
					if (Fecha == null)
						Fecha = new DatePickerDialog(arg0.getContext(),
								new PickDate(), dtTxt.get(Calendar.YEAR), dtTxt
										.get(Calendar.MONTH), dtTxt
										.get(Calendar.DAY_OF_MONTH));
					Fecha.updateDate(dtTxt.get(Calendar.YEAR),
							dtTxt.get(Calendar.MONTH),
							dtTxt.get(Calendar.DAY_OF_MONTH));
				}
				Fecha.show();
			}
		});

		// FechaF

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;

		// dialog.getWindow().setLayout(760, 480);
		dialog.show();
		dialog.getWindow().setAttributes(lp);

		GuardarDia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Guardar los cambios, primero en un Alumno, y luego en BD
				// alumno.setIdAlumno(modifica.getIdAlumno());
				alumno.setNombre(enombre.getText().toString());
				alumno.setApellidos(eapellidos.getText().toString());
				alumno.setObservaciones(eobservaciones.getText().toString());
				alumno.setSexo(sexo);
				SimpleDateFormat formatoDelTexto = new SimpleDateFormat(
						"dd/MM/yyyy");
				Date fecha = null;
				try {

					fecha = formatoDelTexto.parse(mFecha.getText().toString());

				} catch (ParseException ex) {

					ex.printStackTrace();

				}
				alumno.setFecha_nac(fecha);

				// Si es modificar
				boolean correcto = false;
				if (insertar == false) {
					correcto = ads.modificaAlumno(alumno);
					if (correcto == true)
						Toast.makeText(activity, "Alumno modificado",
								Toast.LENGTH_SHORT).show();
				} else {
					// Si es insertar
					correcto = ads.createAlumno(alumno) != null;
					if (correcto == true)
						Toast.makeText(activity, "Alumno creado",
								Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();
				ls = ads.getAllAlumnos();
				CreaTablaAlumnos();
			}
		});

		CancelarDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		// listener Botones e imagen Sexo

		chico.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				chico.setSelected(true);
				chica.setSelected(false);
				imPrincipal.setImageBitmap(decodeSampledBitmapFromResource(
						activity.getResources(), R.drawable.boy_amp, 120, 120));
				// imPrincipal.setImageResource(R.drawable.boy);
				sexo = Sexo.Hombre;
			}
		});

		chica.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				chica.setSelected(true);
				chico.setSelected(false);
				imPrincipal.setImageBitmap(decodeSampledBitmapFromResource(
						activity.getResources(), R.drawable.girl_amp, 120, 120));
				// imPrincipal.setImageResource(R.drawable.girl);
				sexo = Sexo.Mujer;
			}
		});

		// Toast.makeText(getApplicationContext(),str1,
		// Toast.LENGTH_LONG).show();

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
				ads.actualizaOrden(ls.get(i), i + 1);

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
							// TODO Auto-generated method stub
							boolean borrado = false;
							borrado = ads.borraAlumno(id);
							if (borrado == true) {
								CreaTablaAlumnos();
								for (int i = 0; i < ls.size(); i++)
									ads.actualizaOrden(ls.get(i), i + 1);
							}

						}
					});

			alerta.setNegativeButton("Cancelar",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							ls = ads.getAllAlumnos();
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
