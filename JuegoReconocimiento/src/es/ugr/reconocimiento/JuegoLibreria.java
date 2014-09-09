package es.ugr.reconocimiento;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.basedatos.SerieEjerciciosDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Ejercicio;
import es.ugr.objetos.Objeto;
import es.ugr.objetos.SerieEjercicios;
import es.ugr.utilidades.CountDownAnimation;
import es.ugr.utilidades.Utilidades;
import es.ugr.utilidades.CountDownAnimation.CountDownListener;

public class JuegoLibreria {

	private static long timeWhenStopped = 0;

	public static void MostrarAnimacion(Context pContext, TextView ptvTexto,
			String psTexto) {
		CountDownAnimation countDownAnimation = new CountDownAnimation(
				pContext, ptvTexto, 1, psTexto, 1500);
		countDownAnimation.start();
		Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		countDownAnimation.setAnimation(scaleAnimation);
		countDownAnimation.setCountDownListener(new CountDownListener() {
			@Override
			public void onCountDownEnd(CountDownAnimation animation) {
			}
		});
	}

	public static int getEjercicioActual(List<Ejercicio> pLEjercicio,
			Ejercicio pEjercicio) {
		int lnEjercicioActual = -1;
		for (int i = 0; i < pLEjercicio.size(); i++)
			if (pLEjercicio.get(i).getIdEjercicio() == pEjercicio
					.getIdEjercicio()) {
				lnEjercicioActual = i;
				break;
			}
		return lnEjercicioActual;
	}

	public static int getObjetoActual(List<Objeto> pLObjeto, Objeto pObjeto) {
		int lnObjetoActual = -1;
		for (int i = 0; i < pLObjeto.size(); i++)
			if (pLObjeto.get(i).getId() == pObjeto.getId()) {
				lnObjetoActual = i;
				break;
			}
		return lnObjetoActual;
	}

	public static int getDuracionActual(Activity activity) {
		((Chronometer) activity.findViewById(R.id.cronometro)).stop();
		long elapsedMillis = (SystemClock.elapsedRealtime() - ((Chronometer) activity
				.findViewById(R.id.cronometro)).getBase()) / 1000;
		int horas = (int) elapsedMillis / 3600;
		int remainder = (int) elapsedMillis - horas * 3600;
		int mins = remainder / 60;
		remainder = remainder - mins * 60;
		double secs = remainder;
		double tiempo = (horas * 60) + mins + (secs / 10);
		Toast.makeText(activity, "Tiempo: " + tiempo, Toast.LENGTH_SHORT)
				.show();
		return mins;
	}

	public static void iniciarCrono(Activity activity) {
		((Chronometer) activity.findViewById(R.id.cronometro))
				.setBase(SystemClock.elapsedRealtime());
		((Chronometer) activity.findViewById(R.id.cronometro)).start();
		timeWhenStopped = 0;
	}

	public static void pausaCrono(Activity activity) {
		timeWhenStopped = ((Chronometer) activity.findViewById(R.id.cronometro))
				.getBase() - SystemClock.elapsedRealtime();
		((Chronometer) activity.findViewById(R.id.cronometro)).stop();
	}

	public static void renaudaCrono(Activity activity) {
		((Chronometer) activity.findViewById(R.id.cronometro))
				.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
		((Chronometer) activity.findViewById(R.id.cronometro)).start();
	}

	public static void onVerObjetos(Activity activity,
			final ObjetoDataSource dsObjetos, final Ejercicio pEjercicio,
			final EjercicioDataSource dsEjercicios,
			final boolean pbJuegoIniciado, final boolean pbReconocer) {

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		if (pbReconocer)
			builder.setTitle("Objetos reconocer");
		else
			builder.setTitle("Objetos escenario");

		ListView modeList = new ListView(activity);

		ArrayAdapter<Objeto> modeAdapter = null;
		ArrayList<Objeto> llAuxObjetos = null;
		if (pbReconocer)
			llAuxObjetos = new ArrayList<Objeto>(
					dsObjetos.getAllObjetosReconocer(pEjercicio));
		else
			llAuxObjetos = new ArrayList<Objeto>(
					dsObjetos.getAllObjetosEscenario(pEjercicio));

		final ArrayList<Objeto> llObjetos = Utilidades
				.copiaObjetos(llAuxObjetos);
		llAuxObjetos.clear();
		llAuxObjetos = null;
		modeAdapter = new ArrayAdapter<Objeto>(activity,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				llObjetos);
		modeList.setAdapter(modeAdapter);

		builder.setView(modeList);
		final Dialog dialog = builder.create();

		modeList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int location, long arg3) {
				if (!pbJuegoIniciado) {

					if (pbReconocer) {
						String nombre = llObjetos.get(location).getNombre();
						if (dsObjetos.eliminaObjeto(nombre)) {
							if (pEjercicio != null) {
								pEjercicio.eliminaObjetoReconocer(nombre);
								dsEjercicios.modificaEjercicio(pEjercicio);
							}
						}
					} else {
						String nombre = llObjetos.get(location).getNombre();
						if (dsObjetos.eliminaObjeto(nombre)) {
							if (pEjercicio != null) {
								pEjercicio.eliminaObjetoEscenario(nombre);
								dsEjercicios.modificaEjercicio(pEjercicio);
							}
						}
					}
				}

				dialog.dismiss();
				return false;
			}
		});

		dialog.show();

	}

	public static void insertaEnSerie(final Activity activity,
			final Objeto objeto, final Ejercicio pEjercicio) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Series + " + objeto.getNombre());
		SerieEjerciciosDataSource dsSerie = new SerieEjerciciosDataSource(
				activity);
		dsSerie.open();
		final List<SerieEjercicios> listaSeries = dsSerie
				.getAllSeriesEjercicios();
		dsSerie.close();
		ListView modeList = new ListView(activity);
		ArrayAdapter<SerieEjercicios> modeAdapter = new ArrayAdapter<SerieEjercicios>(
				activity, android.R.layout.simple_list_item_1,
				android.R.id.text1, listaSeries);
		modeList.setAdapter(modeAdapter);

		builder.setView(modeList);
		final Dialog dialog = builder.create();

		modeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int location, long id) {
				insertaEnEjercicio(activity, listaSeries.get(location), objeto,
						pEjercicio, location);
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	static int nLocation = -1;

	public static void insertaEnEjercicio(final Activity activity,
			SerieEjercicios serieEjercicios, final Objeto objeto,
			final Ejercicio pEjercicio, final int pnLocation) {
		nLocation = pnLocation;
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Ejercicios - " + serieEjercicios.getNombre() + " - "
				+ objeto.getNombre());
		final EjercicioDataSource ldsEjercicios = new EjercicioDataSource(
				activity);
		ldsEjercicios.open();
		final List<Ejercicio> listaEjercicios = ldsEjercicios
				.getAllEjercicios(serieEjercicios);
		final ListView modeList = new ListView(activity);
		ArrayAdapter<Ejercicio> modeAdapter = new ArrayAdapter<Ejercicio>(
				activity, android.R.layout.simple_list_item_1,
				android.R.id.text1, listaEjercicios);
		modeList.setAdapter(modeAdapter);

		builder.setView(modeList);

		// Add the buttons
		builder.setPositiveButton("Escenario",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (nLocation != -1) {
							listaEjercicios.get(nLocation).getObjetos()
									.add(objeto.getNombre());
							ldsEjercicios.modificaEjercicio(listaEjercicios
									.get(nLocation));
							if (pEjercicio != null
									&& listaEjercicios.get(nLocation)
											.getIdEjercicio() == pEjercicio
											.getIdEjercicio())
								pEjercicio.insertaObjetoEscenario(objeto);
							dialog.dismiss();
							Toast.makeText(activity, "Añadido a escenario",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
		builder.setNegativeButton("Reconocer",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (nLocation != -1) {
							listaEjercicios.get(nLocation)
									.getObjetosReconocer()
									.add(objeto.getNombre());
							listaEjercicios.get(nLocation).getObjetos()
									.add(objeto.getNombre());
							ldsEjercicios.modificaEjercicio(listaEjercicios
									.get(nLocation));
							if (pEjercicio != null
									&& listaEjercicios.get(nLocation)
											.getIdEjercicio() == pEjercicio
											.getIdEjercicio())
								pEjercicio.insertaObjetoReconocer(objeto);
							dialog.dismiss();
							Toast.makeText(activity, "Añadido a reconocer",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		final Dialog dialog = builder.create();

		modeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int location, long id) {
				dialog.setTitle("Insertar " + objeto.getNombre() + " en "
						+ listaEjercicios.get(location).getNombre());
			}
		});
		dialog.show();
	}

}
