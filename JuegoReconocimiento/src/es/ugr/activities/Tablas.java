package es.ugr.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import es.ugr.basedatos.*;
import es.ugr.objetos.*;
import es.ugr.objetos.TiposPropios.Periodo;
import es.ugr.juegoreconocimiento.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ViewAnimator;
import es.ugr.adaptadores.*;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodr�guez
 * @mail miguee02@gmail.com
 * 
 */
public class Tablas extends Activity {
	private static Context contexto;
	private int fechaTipo, graficaTipo;
	private ViewAnimator va;
	private List<Integer> listaAlumnos;
	private List<Integer> listaSeries;
	private SparseArray<GrupoDeItemsResultado> grupos = new SparseArray<GrupoDeItemsResultado>();
	private int posAnimation = 0;
	private int totalAnimation = 0;
	private List<String> nombSeries = new ArrayList<String>();
	private List<String> nombAlumnos = new ArrayList<String>();	
	private Menu menu;
	private MenuItem titulo, subtitulo;

	private String[] mMonthred = new String[] { "Ene", "Feb", "Mar", "Abr",
			"May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" };

	private String[] mMonth = new String[] { "Enero", "Febrero", "Marza",
			"Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
			"Octubre", "Noviembre", "Diciembre" };
	private String[] semana = new String[] { "Domingo", "Lunes", "Martes",
			"Mi�rcoles", "Jueves", "Viernes", "S�bado" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contexto = getApplicationContext();
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		setContentView(R.layout.dialogo_tabla);

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		this.menu = menu;
		titulo = this.menu.findItem(R.id.itemTitulo);
		subtitulo = this.menu.findItem(R.id.itemInfo);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.resultado_detalle, menu);
	    this.menu = menu;
		titulo = this.menu.findItem(R.id.itemTitulo);
		subtitulo = this.menu.findItem(R.id.itemInfo);
		InicioResultados();	
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
	    case R.id.itemDerecha:
	    	tablaAnterior();
	        break;
	    case R.id.itemIzquierda:
	    	tablaSiguiente();
	        break;   
	    case android.R.id.home:
	    	finish();
			return true;
	    }
		return super.onOptionsItemSelected(item);
	}
	
	private void tablaAnterior(){
		va.showPrevious();
		posAnimation = (posAnimation - 1);
		if (posAnimation < 0)
			posAnimation += totalAnimation;
		if (graficaTipo == 0) {
			titulo.setTitle("Ranking de Alumnos" + "("
					+ String.valueOf(posAnimation + 1) + "/"
					+ String.valueOf(totalAnimation) + "): "
					+ nombSeries.get(posAnimation));
		} else {
			titulo.setTitle("Resultados Alumno" + "("
					+ String.valueOf(posAnimation + 1) + "/"
					+ String.valueOf(totalAnimation) + "): "
					+ nombAlumnos.get(posAnimation));
		}
	}
	
	private void tablaSiguiente(){
		va.showNext();
		posAnimation = (posAnimation + 1) % totalAnimation;
		if (graficaTipo == 0) {
			titulo.setTitle("Ranking de Alumnos" + "("
					+ String.valueOf(posAnimation + 1) + "/"
					+ String.valueOf(totalAnimation) + "): "
					+ nombSeries.get(posAnimation));
		} else {
			titulo.setTitle("Resultados Alumno" + "("
					+ String.valueOf(posAnimation + 1) + "/"
					+ String.valueOf(totalAnimation) + "): "
					+ nombAlumnos.get(posAnimation));
		}
	}
	
	private void InicioResultados() {
		va = (ViewAnimator) findViewById(R.id.viewAnimatorTab);
		// va.addView(new Button(this));
		Animation slide_in_left, slide_out_right;
		slide_in_left = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_in_left);
		slide_out_right = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_out_right);
		va.setInAnimation(slide_in_left);
		va.setOutAnimation(slide_out_right);

		// Obtener parametros del Intent
		Bundle extras = getIntent().getExtras();
		fechaTipo = extras.getInt("tipoFecha");
		listaAlumnos = extras.getIntegerArrayList("listaAlumnos");
		listaSeries = extras.getIntegerArrayList("listaSeries");
		graficaTipo = extras.getInt("tipoGrafica");

		Calendar cal;
		Calendar calAnt;
		switch (fechaTipo) {
		case Periodo.Semana:
			calAnt = Calendar.getInstance();
			cal = Calendar.getInstance();
			calAnt.setTime(restaFechaDias(fechaTipo - 1));
			subtitulo.setTitle("�ltima semana: "
					+ calAnt.get(Calendar.DAY_OF_MONTH) + "/"
					+ mMonthred[calAnt.get(Calendar.MONTH)] + "/"
					+ calAnt.get(Calendar.YEAR) + " - "
					+ cal.get(Calendar.DAY_OF_MONTH) + "/"
					+ mMonthred[cal.get(Calendar.MONTH)] + "/"
					+ cal.get(Calendar.YEAR));
			break;
		case Periodo.Mes:
			calAnt = Calendar.getInstance();
			cal = Calendar.getInstance();
			calAnt.setTime(restaFechaDias(fechaTipo - 1));
			subtitulo.setTitle("�ltimo mes: "
					+ calAnt.get(Calendar.DAY_OF_MONTH) + "/"
					+ mMonthred[calAnt.get(Calendar.MONTH)] + "/"
					+ calAnt.get(Calendar.YEAR) + " - "
					+ cal.get(Calendar.DAY_OF_MONTH) + "/"
					+ mMonthred[cal.get(Calendar.MONTH)] + "/"
					+ cal.get(Calendar.YEAR));
			break;
		case Periodo.SeisMeses:
			calAnt = Calendar.getInstance();
			cal = Calendar.getInstance();
			calAnt.setTime(restaFechaMeses(fechaTipo - 1));
			subtitulo.setTitle("�ltimos 6 meses: "
					+ mMonthred[calAnt.get(Calendar.MONTH)] + "/"
					+ calAnt.get(Calendar.YEAR) + " - "
					+ mMonthred[cal.get(Calendar.MONTH)] + "/"
					+ cal.get(Calendar.YEAR));
			break;
		}

		// Si es ranking
		if (graficaTipo == 0) {

			totalAnimation = listaSeries.size();
			for (int i = 0; i < listaSeries.size(); i++)
				TablaRanking(fechaTipo, listaSeries.get(i));
			titulo.setTitle("Ranking de Alumnos" + "("
					+ String.valueOf(posAnimation + 1) + "/"
					+ String.valueOf(totalAnimation) + "): "
					+ nombSeries.get(posAnimation));

		} else if (graficaTipo == 1) {
			totalAnimation = listaAlumnos.size();
			for (int i = 0; i < listaAlumnos.size(); i++)
				TablaAlumno(fechaTipo, listaAlumnos.get(i));
			titulo.setTitle("Resultados Alumno" + "("
					+ String.valueOf(posAnimation + 1) + "/"
					+ String.valueOf(totalAnimation) + "): "
					+ nombAlumnos.get(posAnimation));
		}

	}

	public static Context getContext() {
		return contexto;
	}

	public void TablaRanking(int fechaTipo, int idSerie) {

		grupos = new SparseArray<GrupoDeItemsResultado>();
		ExpandableListView listView = new ExpandableListView(this);
		AdaptadorResultado adapter = new AdaptadorResultado(this, grupos, graficaTipo);
		listView.setAdapter(adapter);

		List<List<Resultado>> listaFinal = new ArrayList<List<Resultado>>();
		ResultadoDataSource rds = new ResultadoDataSource(this);
		AlumnoDataSource ads = new AlumnoDataSource(this);
		SerieEjerciciosDataSource seds = new SerieEjerciciosDataSource(this);

		rds.open();
		ads.open();
		seds.open();

		SerieEjercicios se = seds.getSerieEjercicios(idSerie);

		// A�adir nombre para titulo
		nombSeries.add(se.getNombre());

		for (int i = 0; i < listaAlumnos.size(); i++) {
			listaFinal.add(rds.getResultadosAlumno(
					ads.getAlumnos(listaAlumnos.get(i)), se, fechaTipo));
		}

		rds.close();
		ads.close();
		seds.close();

		// Crear listas
		List<List<Resultado>> listaValores = new ArrayList<List<Resultado>>();
		for (int k = 0; k < fechaTipo; k++)
			listaValores.add(new ArrayList<Resultado>());// Inicializa con todos
															// a 0

		for (int i = 0; i < listaFinal.size(); i++) {// Para cada alumno

			for (int j = 0; j < listaFinal.get(i).size(); j++) {// Para cada
																// resultado de
																// dias
																// distintos de
																// alumno
				int distancia = 0;
				switch (fechaTipo) {
				case Periodo.Semana:
					distancia = (fechaTipo - 1)
							- diferenciaDias(new Date(),
									listaFinal.get(i).get(j)
											.getFechaRealizacion());
					break;
				case Periodo.Mes:
					distancia = (fechaTipo - 1)
							- diferenciaDias(new Date(),
									listaFinal.get(i).get(j)
											.getFechaRealizacion());
					break;

				case Periodo.SeisMeses:
					distancia = (fechaTipo - 1)
							- diferenciaMeses(new Date(), listaFinal.get(i)
									.get(j).getFechaRealizacion());

					break;
				default:
					break;
				}

				listaValores.get(distancia).add(listaFinal.get(i).get(j));
			}
		}

		List<GrupoDeItemsResultado> grupo = new ArrayList<GrupoDeItemsResultado>(fechaTipo);

		// Crear Datos

		int pos = 0;
		for (int i = 0; i < fechaTipo; i++) {

			if (listaValores.get(i).size() > 0) {
				// Fecha
				Calendar cal;
				switch (fechaTipo) {
				case Periodo.Semana:
					cal = Calendar.getInstance();
					cal.setTime(restaFechaDias(fechaTipo - 1 - i));
					grupo.add(new GrupoDeItemsResultado(semana[cal
							.get(Calendar.DAY_OF_WEEK) - 1]
							+ ", "
							+ String.valueOf(cal.get(Calendar.DAY_OF_MONTH))));
					break;
				case Periodo.Mes:
					cal = Calendar.getInstance();
					cal.setTime(restaFechaDias(fechaTipo - 1 - i));
					grupo.add(new GrupoDeItemsResultado(semana[cal
							.get(Calendar.DAY_OF_WEEK) - 1]
							+ ", "
							+ cal.get(Calendar.DAY_OF_MONTH)
							+ " "
							+ mMonth[cal.get(Calendar.MONTH)]));
					break;
				case Periodo.SeisMeses:
					cal = Calendar.getInstance();
					cal.setTime(restaFechaMeses(fechaTipo - 1 - i));
					grupo.add(new GrupoDeItemsResultado(mMonth[cal.get(Calendar.MONTH)]
							+ ", " + cal.get(Calendar.YEAR)));
					break;
				}

				// grupo.add(new
				// GrupoDeItems(dateFormat.format(restaFechaDias(fechaTipo-1-i)).toString()));
				for (int j = 0; j < listaValores.get(i).size(); j++)
					grupo.get(pos).children.add(listaValores.get(i).get(j));
				grupos.append(pos, grupo.get(pos));
				pos++;
			}
		}

		va.addView(listView);

	}

	public void TablaAlumno(int fechaTipo, int idAlumno) {

		grupos = new SparseArray<GrupoDeItemsResultado>();
		ExpandableListView listView = new ExpandableListView(this);
		AdaptadorResultado adapter = new AdaptadorResultado(this, grupos, graficaTipo);
		listView.setAdapter(adapter);

		List<List<Resultado>> listaFinal = new ArrayList<List<Resultado>>();
		ResultadoDataSource rds = new ResultadoDataSource(this);
		AlumnoDataSource ads = new AlumnoDataSource(this);
		SerieEjerciciosDataSource seds = new SerieEjerciciosDataSource(this);

		rds.open();
		ads.open();
		seds.open();

		Alumno al = ads.getAlumnos(idAlumno);

		// A�adir nombre para titulo
		nombAlumnos.add(al.getNombre());

		for (int i = 0; i < listaSeries.size(); i++) {
			listaFinal.add(rds.getResultadosAlumno(al,
					seds.getSerieEjercicios(listaSeries.get(i)), fechaTipo));
		}

		rds.close();
		ads.close();
		seds.close();

		// Crear listas
		List<List<Resultado>> listaValores = new ArrayList<List<Resultado>>();
		for (int k = 0; k < fechaTipo; k++)
			listaValores.add(new ArrayList<Resultado>());// Inicializa con todos
															// a 0

		for (int i = 0; i < listaFinal.size(); i++) {// Para cada alumno

			for (int j = 0; j < listaFinal.get(i).size(); j++) {// Para cada
																// resultado de
																// dias
																// distintos de
																// alumno
				int distancia = 0;
				switch (fechaTipo) {
				case Periodo.Semana:
					distancia = (fechaTipo - 1)
							- diferenciaDias(new Date(),
									listaFinal.get(i).get(j)
											.getFechaRealizacion());
					break;
				case Periodo.Mes:
					distancia = (fechaTipo - 1)
							- diferenciaDias(new Date(),
									listaFinal.get(i).get(j)
											.getFechaRealizacion());
					break;

				case Periodo.SeisMeses:
					distancia = (fechaTipo - 1)
							- diferenciaMeses(new Date(), listaFinal.get(i)
									.get(j).getFechaRealizacion());
					break;
				default:
					break;
				}

				listaValores.get(distancia).add(listaFinal.get(i).get(j));
			}
		}

		List<GrupoDeItemsResultado> grupo = new ArrayList<GrupoDeItemsResultado>(fechaTipo);

		// Crear Datos

		int pos = 0;
		for (int i = 0; i < fechaTipo; i++) {

			if (listaValores.get(i).size() > 0) {
				// Fecha
				Calendar cal;
				switch (fechaTipo) {
				case Periodo.Semana:
					cal = Calendar.getInstance();
					cal.setTime(restaFechaDias(fechaTipo - 1 - i));
					grupo.add(new GrupoDeItemsResultado(semana[cal
							.get(Calendar.DAY_OF_WEEK) - 1]
							+ ", "
							+ String.valueOf(cal.get(Calendar.DAY_OF_MONTH))));
					break;
				case Periodo.Mes:
					cal = Calendar.getInstance();
					cal.setTime(restaFechaDias(fechaTipo - 1 - i));
					grupo.add(new GrupoDeItemsResultado(semana[cal
							.get(Calendar.DAY_OF_WEEK) - 1]
							+ ", "
							+ cal.get(Calendar.DAY_OF_MONTH)
							+ " "
							+ mMonth[cal.get(Calendar.MONTH)]));
					break;
				case Periodo.SeisMeses:
					cal = Calendar.getInstance();
					cal.setTime(restaFechaMeses(fechaTipo - 1 - i));
					grupo.add(new GrupoDeItemsResultado(mMonth[cal.get(Calendar.MONTH)]
							+ ", " + cal.get(Calendar.YEAR)));
					break;
				}

				for (int j = 0; j < listaValores.get(i).size(); j++)
					grupo.get(pos).children.add(listaValores.get(i).get(j));
				grupos.append(pos, grupo.get(pos));
				pos++;
			}
		}

		va.addView(listView);

	}

	private int diferenciaDias(Date date1, Date date2) {
		final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

		int diffInDays = (int) ((date1.getTime() - date2.getTime()) / DAY_IN_MILLIS);
		return diffInDays;
	}

	private int diferenciaMeses(Date date1, Date date2) {
		Calendar cal1 = new GregorianCalendar();
		cal1.setTime(date1);
		int mes1 = cal1.get(Calendar.MONTH);
		int a�o1 = cal1.get(Calendar.YEAR);

		Calendar cal2 = new GregorianCalendar();
		cal2.setTime(date2);
		int mes2 = cal2.get(Calendar.MONTH);
		int a�o2 = cal2.get(Calendar.YEAR);

		mes1 = mes1 + (a�o1 - a�o2) * 11;
		int meses = mes1 - mes2;

		return meses;
	}

	private Date restaFechaDias(int ndias) {
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -ndias);
		d.setTime(c.getTime().getTime());
		return d;
	}

	private Date restaFechaMeses(int meses) {
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -meses);
		d.setTime(c.getTime().getTime());
		return d;
	}

}
