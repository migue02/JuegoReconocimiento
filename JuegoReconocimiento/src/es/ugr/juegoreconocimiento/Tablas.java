package es.ugr.juegoreconocimiento;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import es.ugr.basedatos.*;
import es.ugr.objetos.*;
import es.ugr.objetos.TiposPropios.Periodo;
import es.ugr.juegoreconocimiento.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;
import es.ugr.adaptadores.*;




/**
 * @author Namir Sayed-Ahmad Baraza
 * @mail namirsab@gmail.com
 *
 */
public class Tablas extends Activity {
	private LinearLayout ll;
	private static Context contexto;
	private int fechaTipo,graficaTipo;
	private ViewAnimator va;
	private List<Integer> listaAlumnos;
	private List<Integer> listaSeries;
	private SparseArray<GrupoDeItems> grupos = new SparseArray<GrupoDeItems>();
	private int posAnimation=0;
	private int totalAnimation=0;
	private List<String> nombSeries=new ArrayList<String>();
	private List<String> nombAlumnos=new ArrayList<String>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contexto=getApplicationContext();
		
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
	    getActionBar().setCustomView(R.layout.mibarratab);
	    
		setContentView(R.layout.dialogo_tabla);
		
		ImageView principal=(ImageView)findViewById(R.id.principalTab);
	    principal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		InicioResultados();
		
	}

	private void InicioResultados(){
		//ViewAnimator
		
        va=(ViewAnimator)findViewById(R.id.viewAnimatorTab);
       
        //va.addView(new Button(this));
        Animation slide_in_left, slide_out_right;
        slide_in_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        va.setInAnimation(slide_in_left);
        va.setOutAnimation(slide_out_right);
        
        //Inicializacion botones
        
        ImageButton ant=new ImageButton(this);
        ant=(ImageButton)findViewById(R.id.GraphAnteriorTab);
	    final TextView titulo=(TextView)findViewById(R.id.TitTab);
        final TextView subtitulo=(TextView)findViewById(R.id.TitEjerTab);

        final SimpleDateFormat formato=new SimpleDateFormat("dd/MM/yyyy");
        
        
        ant.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				va.showPrevious();
				posAnimation=(posAnimation-1);
				if (posAnimation<0)
					posAnimation+=totalAnimation;
				
				if(graficaTipo==0){
					titulo.setText("Ranking de Alumnos"+"("+String.valueOf(posAnimation+1)+"/"+String.valueOf(totalAnimation)+")");
					subtitulo.setText(nombSeries.get(posAnimation)+" "+formato.format(restaFechaDias(fechaTipo))+"-"+formato.format(new Date())+" ");
				}
				else{
					titulo.setText("Resultados Alumno"+"("+String.valueOf(posAnimation+1)+"/"+String.valueOf(totalAnimation)+")");
					subtitulo.setText(nombAlumnos.get(posAnimation)+" "+formato.format(restaFechaDias(fechaTipo))+"-"+formato.format(new Date())+" ");
				}
				
				
			}
		});
        
        ImageButton sig=new ImageButton(this);
        sig=(ImageButton)findViewById(R.id.GraphSiguienteTab);
        sig.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				va.showNext();
				posAnimation=(posAnimation+1)%totalAnimation;
				if(graficaTipo==0){
					titulo.setText("Ranking de Alumnos"+"("+String.valueOf(posAnimation+1)+"/"+String.valueOf(totalAnimation)+")");
					subtitulo.setText(nombSeries.get(posAnimation)+" "+formato.format(restaFechaDias(fechaTipo))+"-"+formato.format(new Date())+" ");
				}
				else{
					titulo.setText("Resultados Alumno"+"("+String.valueOf(posAnimation+1)+"/"+String.valueOf(totalAnimation)+")");
					subtitulo.setText(nombAlumnos.get(posAnimation)+" "+formato.format(restaFechaDias(fechaTipo))+"-"+formato.format(new Date())+" ");
				}
			}
		});
        
        
        //FIn viewAnimator
		
        
        //Obtener parametros del Intent
		ll=(LinearLayout)findViewById(R.id.linearTablaRes);
		Bundle extras = getIntent().getExtras();
	    fechaTipo=extras.getInt("tipoFecha"); 
	    listaAlumnos=extras.getIntegerArrayList("listaAlumnos");
	    listaSeries=extras.getIntegerArrayList("listaSeries");
	    graficaTipo=extras.getInt("tipoGrafica");
	    

	    
	    //Si es ranking
	    if(graficaTipo==0){
	    	
	    totalAnimation=listaSeries.size();
		for(int i=0;i<listaSeries.size();i++)
			TablaRanking(fechaTipo,listaSeries.get(i));
		titulo.setText("Ranking de Alumnos"+"("+String.valueOf(posAnimation+1)+"/"+String.valueOf(totalAnimation)+")");
		subtitulo.setText(nombSeries.get(posAnimation)+" "+formato.format(restaFechaDias(fechaTipo))+"-"+formato.format(new Date()));
		
	    }
	    else if(graficaTipo==1){		    
		    totalAnimation=listaAlumnos.size();
			for(int i=0;i<listaAlumnos.size();i++)
				TablaAlumno(fechaTipo,listaAlumnos.get(i));
			titulo.setText("Resultados Alumno"+"("+String.valueOf(posAnimation+1)+"/"+String.valueOf(totalAnimation)+")");
			subtitulo.setText(nombAlumnos.get(posAnimation)+" "+formato.format(restaFechaDias(fechaTipo))+"-"+formato.format(new Date()));
	    }
	    
		
	    

	}
	
	public static Context getContext() {
	      //  return instance.getApplicationContext();
	      return contexto;
	    }
	
	 public void TablaRanking(int fechaTipo,int idSerie){
		 
		grupos = new SparseArray<GrupoDeItems>(); 
		ExpandableListView listView = new ExpandableListView(this);
		Adaptador adapter = new Adaptador(this, grupos,graficaTipo);
		listView.setAdapter(adapter);
		
		
		
		
		List<List<Resultado>> listaFinal=new ArrayList<List<Resultado>>();
		ResultadoDataSource rds=new ResultadoDataSource(this);
		AlumnoDataSource ads=new AlumnoDataSource(this);
		SerieEjerciciosDataSource seds=new SerieEjerciciosDataSource(this);
		 
		 rds.open();
		 ads.open();
		 seds.open();
		 
		 SerieEjercicios se=seds.getSerieEjercicios(idSerie);
		 
		 //Añadir nombre para titulo
		 nombSeries.add(se.getNombre());
		 
		 for(int i=0;i<listaAlumnos.size();i++){			 
			 listaFinal.add(rds.getResultadosAlumno(ads.getAlumnos(listaAlumnos.get(i)),se,fechaTipo));
		 }

		 
		 rds.close();
		 ads.close();
		 seds.close();

		
		//Crear listas
		 List<List<Resultado>> listaValores=new ArrayList<List<Resultado>>();
		 for(int k=0;k<fechaTipo;k++)
			 listaValores.add(new ArrayList<Resultado>());//Inicializa con todos a 0
		 
		 for(int i=0;i<listaFinal.size();i++){//Para cada alumno
	 				 	
			 	for(int j=0;j<listaFinal.get(i).size();j++){//Para cada resultado de dias distintos de alumno
			 		int distancia=0;
			 		switch (fechaTipo) {
					case Periodo.Semana:
						distancia=(fechaTipo-1)-diferenciaDias(new Date(), listaFinal.get(i).get(j).getFechaRealizacion());
						break;
					case Periodo.Mes:
						distancia=(fechaTipo-1)-diferenciaDias(new Date(), listaFinal.get(i).get(j).getFechaRealizacion());
						break;
						
					case Periodo.SeisMeses:
						distancia=(fechaTipo-1)-diferenciaMeses(new Date(), listaFinal.get(i).get(j).getFechaRealizacion());
						
						break;
					default:
						break;
					}
			 				

			 		listaValores.get(distancia).add(listaFinal.get(i).get(j));
			 	}
		 }
		 
		List<GrupoDeItems> grupo= new ArrayList<GrupoDeItems>(fechaTipo);
		
		//Crear Datos 
		
		int pos=0;
		for(int i=0;i<fechaTipo;i++){
			
				if(listaValores.get(i).size()>0){
					//Fecha
			        SimpleDateFormat dateFormat = null;
			        switch (fechaTipo) {
					case Periodo.Semana:
						dateFormat=new SimpleDateFormat("dd/MM/yyyy");
						grupo.add(new GrupoDeItems(dateFormat.format(restaFechaDias(fechaTipo-1-i)).toString()));
						break;
					case Periodo.Mes:
						dateFormat=new SimpleDateFormat("dd/MM");
						grupo.add(new GrupoDeItems(dateFormat.format(restaFechaDias(fechaTipo-1-i)).toString()));
						break;
					case Periodo.SeisMeses:
						dateFormat=new SimpleDateFormat("MM/yyyy");
						grupo.add(new GrupoDeItems(dateFormat.format(restaFechaMeses(fechaTipo-1-i)).toString()));
						break;
			        }	
						
				//grupo.add(new GrupoDeItems(dateFormat.format(restaFechaDias(fechaTipo-1-i)).toString()));
				for(int j=0;j<listaValores.get(i).size();j++)
						grupo.get(pos).children.add(listaValores.get(i).get(j));
				grupos.append(pos, grupo.get(pos));
				pos++;
				}
		}

		
		
		
		
		va.addView(listView);
		 

	 }

	 
	 

	 public void TablaAlumno(int fechaTipo,int idAlumno){
		 
			grupos = new SparseArray<GrupoDeItems>(); 
			ExpandableListView listView = new ExpandableListView(this);
			Adaptador adapter = new Adaptador(this, grupos,graficaTipo);
			listView.setAdapter(adapter);
			
			
			
			
			List<List<Resultado>> listaFinal=new ArrayList<List<Resultado>>();
			 ResultadoDataSource rds=new ResultadoDataSource(this);
			 AlumnoDataSource ads=new AlumnoDataSource(this);
			 SerieEjerciciosDataSource seds=new SerieEjerciciosDataSource(this);
			 
			 rds.open();
			 ads.open();
			 seds.open();
			 
			 Alumno al=ads.getAlumnos(idAlumno);
			 
			 //Añadir nombre para titulo
			 nombAlumnos.add(al.getNombre());
			 
			 for(int i=0;i<listaSeries.size();i++){
				 listaFinal.add(rds.getResultadosAlumno(al,seds.getSerieEjercicios(listaSeries.get(i)),fechaTipo));
			 }

			 
			 rds.close();
			 ads.close();
			 seds.close();


			
			//Crear listas
			 List<List<Resultado>> listaValores=new ArrayList<List<Resultado>>();
			 for(int k=0;k<fechaTipo;k++)
				 listaValores.add(new ArrayList<Resultado>());//Inicializa con todos a 0
			 
			 for(int i=0;i<listaFinal.size();i++){//Para cada alumno
		 				 	
				 	for(int j=0;j<listaFinal.get(i).size();j++){//Para cada resultado de dias distintos de alumno
				 		int distancia=0;
				 		switch (fechaTipo) {
						case Periodo.Semana:
							distancia=(fechaTipo-1)-diferenciaDias(new Date(), listaFinal.get(i).get(j).getFechaRealizacion());
							break;
						case Periodo.Mes:
							distancia=(fechaTipo-1)-diferenciaDias(new Date(), listaFinal.get(i).get(j).getFechaRealizacion());
							break;
							
						case Periodo.SeisMeses:
							distancia=(fechaTipo-1)-diferenciaMeses(new Date(), listaFinal.get(i).get(j).getFechaRealizacion());
							break;
						default:
							break;
						}
				 				

				 		listaValores.get(distancia).add(listaFinal.get(i).get(j));
				 	}
			 }
			 
			List<GrupoDeItems> grupo= new ArrayList<GrupoDeItems>(fechaTipo);
			
			//Crear Datos 
			
			int pos=0;
			for(int i=0;i<fechaTipo;i++){
				
					if(listaValores.get(i).size()>0){
						//Fecha
				        SimpleDateFormat dateFormat = null;
				        switch (fechaTipo) {
						case Periodo.Semana:
							dateFormat=new SimpleDateFormat("dd/MM/yyyy");
							grupo.add(new GrupoDeItems(dateFormat.format(restaFechaDias(fechaTipo-1-i)).toString()));
							break;
						case Periodo.Mes:
							dateFormat=new SimpleDateFormat("dd/MM");
							grupo.add(new GrupoDeItems(dateFormat.format(restaFechaDias(fechaTipo-1-i)).toString()));
							break;
						case Periodo.SeisMeses:
							dateFormat=new SimpleDateFormat("MM/yyyy");
							grupo.add(new GrupoDeItems(dateFormat.format(restaFechaMeses(fechaTipo-1-i)).toString()));
							break;
				        }	
							

					for(int j=0;j<listaValores.get(i).size();j++)
							grupo.get(pos).children.add(listaValores.get(i).get(j));
					grupos.append(pos, grupo.get(pos));
					pos++;
					}
			}

			
			
			
			
			va.addView(listView);
		 
	 }
	
	
	 
	 
	 private int diferenciaDias(Date date1,Date date2){
		 final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

		 int diffInDays = (int) ((date1.getTime() - date2.getTime())/ DAY_IN_MILLIS );
		 return diffInDays;
	 }
	 


	 
	 
	 private int diferenciaMeses(Date date1,Date date2){ 
		 Calendar cal1 = new GregorianCalendar();
		 cal1.setTime(date1);
		 int mes1=cal1.get(Calendar.MONTH);
		 int año1=cal1.get(Calendar.YEAR);
		 
		 Calendar cal2 = new GregorianCalendar();
		 cal2.setTime(date2);
		 int mes2=cal2.get(Calendar.MONTH);
		 int año2=cal2.get(Calendar.YEAR);
		 
		 mes1=mes1+(año1-año2)*11;
		 int meses=mes1-mes2;
		 
		 
		return meses;
	 }
	 
	 
	 private Date restaFechaDias(int ndias){
		 	Date d=new Date();
		    Calendar c = Calendar.getInstance();
		    c.add(Calendar.DATE, -ndias);
		    d.setTime( c.getTime().getTime() );
		    return d;
	 }
	 
	 private Date restaFechaMeses(int meses){
		 	Date d=new Date();
		    Calendar c = Calendar.getInstance();
		    c.add(Calendar.MONTH, -meses);
		    d.setTime( c.getTime().getTime() );
		    return d;
	 }
	 
	
}
