package es.ugr.reconocimiento;

import java.util.LinkedList;
import java.util.List;

import es.ugr.basedatos.AlumnoDataSource;
import es.ugr.basedatos.SerieEjerciciosDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Alumno;
import es.ugr.objetos.SerieEjercicios;
import es.ugr.objetos.TiposPropios.Sexo;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EmpezarJuego extends Activity {
	
	private Boolean ciclico=false;
	private AlumnoDataSource alumnoDS;
	private SerieEjerciciosDataSource serieDS;
	private List<Alumno> listaAlumnos;
	private List<SerieEjercicios> listaSeries;
	private ListView listViewAlumnos;
	private ListView listViewSeries;
	private Alumno alumnoSeleccionado;
	private SerieEjercicios serieSeleccionada;
	private Spinner spinnerAl,spinnerSer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
	    getActionBar().setCustomView(R.layout.mibarraemp);
		setContentView(R.layout.activity_empezar_juego);
	
		ImageView principal=(ImageView)findViewById(R.id.principalEmp);
	    principal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		alumnoSeleccionado = new Alumno();
		serieSeleccionada = new SerieEjercicios();
		alumnoDS = new AlumnoDataSource(this);
		serieDS = new SerieEjerciciosDataSource(this);
		alumnoDS.open();
		serieDS.open();
		
		listaSeries = serieDS.getAllSeriesEjercicios();
		spinnerSer=(Spinner)findViewById(R.id.spinnerSerie);
		
		//ArrayAdapter<SerieEjercicios> spinner_adapter_ser=new ArrayAdapter<SerieEjercicios>(this, android.R.layout.simple_spinner_item,listaSeries);
		ArrayAdapter<SerieEjercicios> spinner_adapter_ser=new ArrayAdapter<SerieEjercicios>(this, R.layout.spinner_layout,listaSeries);
		//spinner_adapter_ser.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_adapter_ser.setDropDownViewResource(R.layout.spinner_layout);
		spinnerSer.setAdapter(spinner_adapter_ser);
		spinnerSer.setOnItemSelectedListener(new MyOnItemSelectedListenerSerie());


		/*
		ArrayAdapter<SerieEjercicios> adapterEjercicios = new ArrayAdapter<SerieEjercicios>(this,
				android.R.layout.simple_list_item_1, listaSeries);
		
		listViewSeries = (ListView)findViewById(R.id.listaSeries);
		listViewSeries.setAdapter(adapterEjercicios);
		
		listViewSeries.setOnItemClickListener(new OnItemClickListener() {		

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int location, long id) {
				serieSeleccionada = listaSeries.get(location);	
				TextView tv = (TextView)findViewById(R.id.lblSerie);
				tv.setText(serieSeleccionada.getNombre());
			}});*/
		
		listaAlumnos = alumnoDS.getAllAlumnos();
		spinnerAl=(Spinner)findViewById(R.id.spinnerAlumno);
		
		ArrayAdapter<Alumno> spinner_adapter_al=new ArrayAdapter<Alumno>(this, R.layout.spinner_layout,listaAlumnos);
		spinner_adapter_al.setDropDownViewResource(R.layout.spinner_layout);
		spinnerAl.setAdapter(spinner_adapter_al);
		spinnerAl.setOnItemSelectedListener(new MyOnItemSelectedListenerAlumno());
	/*	ArrayAdapter<Alumno> adapterAlumnos = new ArrayAdapter<Alumno>(this,
				android.R.layout.simple_list_item_1, listaAlumnos);
		
		listViewAlumnos = (ListView)findViewById(R.id.listaAlumnos);
		listViewAlumnos.setAdapter(adapterAlumnos);
		
		listViewAlumnos.setOnItemClickListener(new OnItemClickListener() {		

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int location, long id) {
				alumnoSeleccionado = listaAlumnos.get(location);
				TextView tv = (TextView)findViewById(R.id.lblALumno);
				tv.setText(alumnoSeleccionado.getNombre());
				if (alumnoSeleccionado.getSexo() == Sexo.Hombre)
					tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.male, 0, 0, 0);
				else
					tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.female, 0, 0, 0);
			}});
	*/	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.empezar_juego, menu);
		return true;
	}

	public void setCiclico(View v){
		v.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha));
		ImageView btnCiclico = (ImageView) findViewById(R.id.btnCiclico);
		TextView textoCiclico=(TextView) findViewById(R.id.textoCiclico);
		ciclico=!ciclico;
		if (ciclico){
			btnCiclico.setImageResource(R.drawable.ciclico_pulsado);
			textoCiclico.setText("Modo Cíclico: ON");
		}
		else{
			btnCiclico.setImageResource(R.drawable.ciclico);
			textoCiclico.setText("Modo Cíclico: OFF");
		}
	}
	
	
	public void empezarJuego(View v){
		if(alumnoSeleccionado.getIdAlumno() != -1 && serieSeleccionada.getIdSerie() != -1){
			v.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha));
			Intent myIntent = new Intent(EmpezarJuego.this,
					ReconocimientoObjeto.class);
			myIntent.putExtra("Alumno", alumnoSeleccionado);
			myIntent.putExtra("Serie", serieSeleccionada);
			myIntent.putExtra("Ciclico", ciclico);
			finish();
			startActivity(myIntent);
		}else{
			Toast.makeText(getApplicationContext(),"Debe elegir un alumno y una serie para poder jugar" , Toast.LENGTH_LONG).show();
		}
			
	}
	
	
	@Override
	protected void onResume() {
		alumnoDS.open();
		serieDS.open();
		super.onResume();
	}
	

	@Override
	protected void onPause() {
		alumnoDS.close();
		serieDS.close();
		super.onPause();
	}
	
	
	 public class MyOnItemSelectedListenerAlumno implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			if (parent.getId() == R.id.spinnerAlumno) {
				alumnoSeleccionado = ((Alumno) parent.getItemAtPosition(pos));
			}
			//Podemos hacer varios ifs o un switchs por si tenemos varios spinners.
		}
		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}
	 
	 
	 
	 public class MyOnItemSelectedListenerSerie implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			if (parent.getId() == R.id.spinnerSerie) {
				serieSeleccionada = ((SerieEjercicios) parent.getItemAtPosition(pos));
			}
			//Podemos hacer varios ifs o un switchs por si tenemos varios spinners.
		}
		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}
	
}
