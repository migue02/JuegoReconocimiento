package es.ugr.reconocimiento;

import java.util.List;

import es.ugr.basedatos.AlumnoDataSource;
import es.ugr.basedatos.SerieEjerciciosDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Alumno;
import es.ugr.objetos.SerieEjercicios;
import es.ugr.objetos.TiposPropios.Sexo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import es.ugr.objetos.TiposPropios;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empezar_juego);
	
		alumnoSeleccionado = new Alumno();
		serieSeleccionada = new SerieEjercicios();
		alumnoDS = new AlumnoDataSource(this);
		serieDS = new SerieEjerciciosDataSource(this);
		alumnoDS.open();
		serieDS.open();
		
		listaSeries = serieDS.getAllSeriesEjercicios();
		
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
			}});
		
		listaAlumnos = alumnoDS.getAllAlumnos();
		
		ArrayAdapter<Alumno> adapterAlumnos = new ArrayAdapter<Alumno>(this,
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
		ciclico=!ciclico;
		if (ciclico)
			btnCiclico.setImageResource(R.drawable.ciclico_pulsado);
		else
			btnCiclico.setImageResource(R.drawable.ciclico);
	}
	
	public void empezarJuego(View v){
		if(alumnoSeleccionado != null && serieSeleccionada != null){
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
	
}
