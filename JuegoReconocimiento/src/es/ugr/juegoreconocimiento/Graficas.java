package es.ugr.juegoreconocimiento;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import es.ugr.basedatos.AlumnoDataSource;
import es.ugr.basedatos.ResultadoDataSource;
import es.ugr.basedatos.SerieEjerciciosDataSource;
import es.ugr.objetos.*;
import es.ugr.objetos.TiposPropios.Periodo;
import es.ugr.juegoreconocimiento.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.location.GpsStatus.NmeaListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.MonthDisplayHelper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;




/**
 * @author Namir Sayed-Ahmad Baraza
 * @mail namirsab@gmail.com
 *
 */
public class Graficas extends Activity {
	private LinearLayout ll;
	private static Context contexto;
	private int fechaTipo,graficaTipo;
	private ViewAnimator va;
	private List<Integer> listaAlumnos;
	private List<Integer> listaSeries;
	private int posAnimation=0;
	private int totalAnimation=0;
	private List<String> nombSeries=new ArrayList<String>();
	private List<String> nombAlumnos=new ArrayList<String>();
	
	 private String[] mMonth = new String[] {
		        "Ene", "Feb" , "Mar", "Abr", "May", "Jun",
		        "Jul", "Ago" , "Sep", "Oct", "Nov", "Dic"
		    };
	 private String[] semana=new String[] {
			 "Do","Lu","Ma","Mi","Ju","Vi","Sa"
	 };
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contexto=getApplicationContext();
		
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
	    getActionBar().setCustomView(R.layout.mibarragraf);
	    
		setContentView(R.layout.dialogo_graficos);
		
		ImageView principal=(ImageView)findViewById(R.id.principalGraf);
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
		
	    final TextView titulo=(TextView)findViewById(R.id.TitGraf);
	    final TextView subtitulo=(TextView)findViewById(R.id.TitEjerGraf);
	    
	    final SimpleDateFormat formatoDia;
		final SimpleDateFormat formatoMes;
		final SimpleDateFormat formatoAño;
	    formatoDia=new SimpleDateFormat("dd");
	    formatoMes=new SimpleDateFormat("MM");
	    formatoAño=new SimpleDateFormat("yyyy");
	    
	    
        va=(ViewAnimator)findViewById(R.id.viewAnimator1);
       
        //va.addView(new Button(this));
        Animation slide_in_left, slide_out_right;
        slide_in_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        va.setInAnimation(slide_in_left);
        va.setOutAnimation(slide_out_right);
        
        //Inicializacion botones
        
        ImageButton ant=new ImageButton(this);
        ant=(ImageButton)findViewById(R.id.GraphAnteriorGraf);
        ant.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				va.showPrevious();
				posAnimation=(posAnimation-1);
				if (posAnimation<0)
					posAnimation+=totalAnimation;
				
				
				if(graficaTipo==0){
					titulo.setText("Ranking Alumnos"+"("+String.valueOf(posAnimation+1)+"/"+String.valueOf(totalAnimation)+")");

				}
				else{
					titulo.setText("Resultados Alumno"+"("+String.valueOf(posAnimation+1)+"/"+String.valueOf(totalAnimation)+")");
				}
			}
		});
        
        ImageButton sig=new ImageButton(this);
        sig=(ImageButton)findViewById(R.id.GraphSiguienteGraf);
        sig.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				va.showNext();
				posAnimation=(posAnimation+1)%totalAnimation;
				
				
				if(graficaTipo==0){
					titulo.setText("Ranking de Alumnos"+"("+String.valueOf(posAnimation+1)+"/"+String.valueOf(totalAnimation)+")");
				}
				else{
					titulo.setText("Resultados Alumno"+"("+String.valueOf(posAnimation+1)+"/"+String.valueOf(totalAnimation)+")");
				}
			}
		});
        
        
        //FIn viewAnimator
		
        
        //Obtener parametros del Intent
		ll=(LinearLayout)findViewById(R.id.linearGraficos);
		Bundle extras = getIntent().getExtras();
	    fechaTipo=extras.getInt("tipoFecha"); 
	    listaAlumnos=extras.getIntegerArrayList("listaAlumnos");
	    listaSeries=extras.getIntegerArrayList("listaSeries");
	    graficaTipo=extras.getInt("tipoGrafica");


		switch (fechaTipo) {
		case Periodo.Semana:
			Date fechaAnt=restaDias(Periodo.Semana-1);
			Date fechaPos=new Date();
			Integer.valueOf(formatoMes.format(fechaAnt));
			subtitulo.setText("Última semana: "+formatoDia.format(fechaAnt).toString()+"/"+mMonth[Integer.valueOf(formatoMes.format(fechaAnt))]+"/"+formatoAño.format(fechaAnt)+" - "+formatoDia.format(fechaPos).toString()+"/"+mMonth[Integer.valueOf(formatoMes.format(fechaPos))]+"/"+formatoAño.format(fechaPos));
		break;	
		case Periodo.Mes:
			Date fechaAnt2=restaDias(Periodo.Mes-1);
			Date fechaPos2=new Date();
			Integer.valueOf(formatoMes.format(fechaAnt2));
			subtitulo.setText("Último mes: "+formatoDia.format(fechaAnt2).toString()+"/"+mMonth[Integer.valueOf(formatoMes.format(fechaAnt2))]+"/"+formatoAño.format(fechaAnt2)+" - "+formatoDia.format(fechaPos2).toString()+"/"+mMonth[Integer.valueOf(formatoMes.format(fechaPos2))]+"/"+formatoAño.format(fechaPos2));
		break;	
		case Periodo.SeisMeses:
			Date fechaAnt3=restaMeses(Periodo.SeisMeses-1);
			Date fechaPos3=new Date();
			Integer.valueOf(formatoMes.format(fechaAnt3));
			subtitulo.setText("Últimos 6 meses: "+mMonth[Integer.valueOf(formatoMes.format(fechaAnt3))]+"/"+formatoAño.format(fechaAnt3)+" - "+mMonth[Integer.valueOf(formatoMes.format(fechaPos3))]+"/"+formatoAño.format(fechaPos3));
		break;
		}
	    //Si es ranking
	    if(graficaTipo==0){
	    //Toast.makeText(getApplicationContext(),"Radio "+graficaTipo, Toast.LENGTH_SHORT).show();
		    totalAnimation=listaSeries.size();
		for(int i=0;i<listaSeries.size();i++)
			GraficoRanking(fechaTipo,listaSeries.get(i));
		titulo.setText("Ranking de Alumnos"+"("+String.valueOf(posAnimation+1)+"/"+String.valueOf(totalAnimation)+")");
	    }
	    else if(graficaTipo==1){
		    totalAnimation=listaAlumnos.size();	    
			for(int i=0;i<listaAlumnos.size();i++)
				GraficoAlumno(fechaTipo,listaAlumnos.get(i));
			titulo.setText("Resultados Alumno"+"("+String.valueOf(posAnimation+1)+"/"+String.valueOf(totalAnimation)+")");
	    }

	}
	
	public static Context getContext() {
	      //  return instance.getApplicationContext();
	      return contexto;
	    }
	
	 public void GraficoRanking(int fechaTipo,int idSerie){
		 
		 
		 
		 //Añadimos Lista Resultados
		 
		 List<List<Resultado>> listaFinal=new ArrayList<List<Resultado>>();
		 ResultadoDataSource rds=new ResultadoDataSource(this);
		 AlumnoDataSource ads=new AlumnoDataSource(this);
		 SerieEjerciciosDataSource seds=new SerieEjerciciosDataSource(this);
		 
		 rds.open();
		 ads.open();
		 seds.open();
		 
		 SerieEjercicios se=seds.getSerieEjercicios(idSerie);
		 
		 nombSeries.add(se.getNombre());
		 for(int i=0;i<listaAlumnos.size();i++){
			 listaFinal.add(rds.getResultadosAlumno(ads.getAlumnos(listaAlumnos.get(i)),se,fechaTipo));
		 }

		 
		 rds.close();
		 ads.close();
		 seds.close();
		 
		 
		 //A partir de una lista de listas de resultados
		 
		 
		 List<List<Double>> listaValores=new ArrayList<List<Double>>();
		 for(int i=0;i<listaFinal.size();i++){

			 	
			 	listaValores.add(new ArrayList<Double>(Collections.nCopies(fechaTipo, 0.0)));//Inicializa con todos a 0
			 	for(int j=0;j<listaFinal.get(i).size();j++){
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
			 				
			 		if(listaFinal.get(i).get(j).getPuntuacion()>0.0)
			 			listaValores.get(i).set(distancia, listaFinal.get(i).get(j).getPuntuacion());
			 	}
		 }
		
		// LinearLayout chart_container=(LinearLayout)findViewById(R.id.layoutResultados);
		 final GraphicalView mChart; 
 
		int[] colores={Color.BLUE,Color.RED,Color.MAGENTA,Color.MAGENTA,Color.GREEN,Color.rgb(255, 0, 0)}; 
		
		 
		 AlumnoDataSource ads2=new AlumnoDataSource(this);
		 ads2.open();
	 
	        // Creating an  XYSeries for Income
		 List<XYSeries> listaXYSeries=new ArrayList<XYSeries>();
		 for(int i=0;i<listaFinal.size();i++){
			 listaXYSeries.add(new XYSeries(ads2.getAlumnos(listaAlumnos.get(i)).getNombre()));
		        for(int j=0;j<fechaTipo;j++){
		        			listaXYSeries.get(i).add(j,listaValores.get(i).get(j));
			        }
		 }
		 ads2.close();

	 
	        // Creating a dataset to hold each series
	        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	        


	        List<XYSeriesRenderer> listaRenderer=new ArrayList<XYSeriesRenderer>();
	        for(int i=0;i<listaFinal.size();i++){
	        	dataset.addSeries(listaXYSeries.get(i));
	        	listaRenderer.add(new XYSeriesRenderer());
	        	listaRenderer.get(i).setColor(colores[i]);
	        	listaRenderer.get(i).setFillPoints(true);
	        	//listaRenderer.get(i).set
	        	listaRenderer.get(i).setLineWidth(2);
	        	listaRenderer.get(i).setDisplayChartValues(true);
	        	listaRenderer.get(i).setChartValuesTextSize(20);
	        	listaRenderer.get(i).setChartValuesTextAlign(Align.CENTER);

	        	
	        	
	        	
	        
	        }	
	        	
	 
	        // Creating a XYMultipleSeriesRenderer to customize the whole chart
	        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
	        multiRenderer.setXLabels(0);
	        multiRenderer.setBarSpacing(0.25f);
	        multiRenderer.setChartTitle(se.getNombre());
	        multiRenderer.setChartTitleTextSize(25);
	        multiRenderer.setShowGrid(true);
	        
	        
	        multiRenderer.setApplyBackgroundColor(true);
	        multiRenderer.setBackgroundColor(getResources().getColor(R.color.degradado2));
	        multiRenderer.setMarginsColor(getResources().getColor(R.color.degradado1));
	        
	        int[] margins = {40, 40, 40, 40};
	        multiRenderer.setMargins(margins);
	        //multiRenderer.setM
	        
	        multiRenderer.setAxesColor(getResources().getColor(R.color.azul_oscuro));
	        multiRenderer.setGridColor(getResources().getColor(R.color.azul_oscuro));
	        
	        multiRenderer.setLabelsColor(getResources().getColor(R.color.azul_oscuro));
	        multiRenderer.setXLabelsColor(getResources().getColor(R.color.azul_oscuro));//Color de cada texto de fecha
	        multiRenderer.setYLabelsColor(0, getResources().getColor(R.color.azul_oscuro));//Colo de cada texto de puntuacion
	       // multiRenderer.setYLabelsColor(1, Color.BLACK);
	       
	       
	        //multiRenderer.setLabelsTextSize(15);
	        
	        switch (fechaTipo) {
			case Periodo.Semana:
				multiRenderer.setXTitle("Día");
				multiRenderer.setXAxisMax(fechaTipo);
				break;
			case Periodo.Mes:
				multiRenderer.setXTitle("Día");
				multiRenderer.setXAxisMax(fechaTipo);
				break;
				
			case Periodo.SeisMeses:
				multiRenderer.setXTitle("Mes");
				multiRenderer.setXAxisMax(fechaTipo);
				break;
			default:
				break;
			}
	        multiRenderer.setYTitle("Puntuación");
	        multiRenderer.setZoomButtonsVisible(true);
	        multiRenderer.setXAxisMin(-1);
	        
	        multiRenderer.setYAxisMin(0);
	        multiRenderer.setYAxisMax(10);
	        multiRenderer.setPanEnabled(true, false); //Evitar que se mueva verticalmente
	        multiRenderer.setLegendTextSize(25);
	        multiRenderer.setFitLegend(true);
	        
	      //  multiRenderer.setRange(new double[] { -1, 7, 0, 10 });
	        //multiRenderer.setMarginsColor(Color.MAGENTA);

	        SimpleDateFormat dateFormat = null;
	        switch (fechaTipo) {
			case Periodo.Semana:
				dateFormat=new SimpleDateFormat("dd");
				break;
			case Periodo.Mes:
				dateFormat=new SimpleDateFormat("dd");
				break;
			case Periodo.SeisMeses:
				dateFormat=new SimpleDateFormat("MM");
				break;
			
			default:
				break;
			}
	        
	        //new SimpleDateFormat("dd/MM/yyyy");
	        for(int i=0; i< fechaTipo;i++){
		        Calendar calen = Calendar.getInstance();
		        if(fechaTipo==Periodo.Semana||fechaTipo==Periodo.Mes){
		        	calen.add(Calendar.DATE, -(fechaTipo-1-i));
		            multiRenderer.addXTextLabel(i,semana[calen.get(Calendar.DAY_OF_WEEK)-1]+"-"+dateFormat.format(calen.getTime()).toString());
		        }
		        if(fechaTipo==Periodo.SeisMeses){
		        	calen.add(Calendar.MONTH, -(fechaTipo-1-i));
		        	multiRenderer.addXTextLabel(i, mMonth[Integer.valueOf(dateFormat.format(calen.getTime()).toString())]);
		        }

	        }
	 
	        // Adding incomeRenderer and expenseRenderer to multipleRenderer
	        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
	        // should be same
	        for(int i=0;i<listaRenderer.size();i++)
	        	multiRenderer.addSeriesRenderer(listaRenderer.get(i));
	        	
	       
		  
	        //mChart=(GraphicalView)ChartFactory.getBarChartView(getBaseContext(), dataset, multiRenderer, Type.DEFAULT);
	        mChart=(GraphicalView)ChartFactory.getBarChartView(getContext(), dataset, multiRenderer, Type.DEFAULT);
	        va.addView(mChart);
	        
			  //ll.addView(mChart);
			  
			  mChart.setBackgroundColor(Color.WHITE);	  
	 }
	

	 public void GraficoAlumno(int fechaTipo,int idAlumno){
		 
		 
		 int maximo=0;
		 //Añadimos Lista Resultados
		 
		 List<List<Resultado>> listaFinal=new ArrayList<List<Resultado>>();
		 ResultadoDataSource rds=new ResultadoDataSource(this);
		 AlumnoDataSource ads=new AlumnoDataSource(this);
		 SerieEjerciciosDataSource seds=new SerieEjerciciosDataSource(this);
		 
		 rds.open();
		 ads.open();
		 seds.open();
		 
		 Alumno al=ads.getAlumnos(idAlumno);
		 
		 nombAlumnos.add(al.getNombre());
		 
		 for(int i=0;i<listaSeries.size();i++){
			 listaFinal.add(rds.getResultadosAlumno(al,seds.getSerieEjercicios(listaSeries.get(i)),fechaTipo));
		 }
		 rds.close();
		 ads.close();
		 seds.close();
		 
		 
		 //A partir de una lista de listas de resultados
		 

		 
		 List<Integer> listaValores=new ArrayList<Integer>(Collections.nCopies(fechaTipo*listaSeries.size(), 0));
		 List<Integer> listaAciertos=new ArrayList<Integer>(Collections.nCopies(fechaTipo*listaSeries.size(), 0));
		 
		 for(int i=0;i<listaFinal.size();i++){

			 	
			 	
			 	for(int j=0;j<listaFinal.get(i).size();j++){
			 		
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
			 		
			 		int vAciertos=listaFinal.get(i).get(j).getAciertos();
			 		int vTotales=listaFinal.get(i).get(j).getAciertos()+listaFinal.get(i).get(j).getFallos();
			 		if (vTotales>maximo)
			 			maximo=vTotales;
			 		//listaValores.set(j*listaFinal.size()+i, vTotales);
			 		//listaAciertos.set(j*listaFinal.size()+i,vAciertos );
			 		
			 		listaValores.set(distancia*listaFinal.size()+i, vTotales);
			 		listaAciertos.set(distancia*listaFinal.size()+i,vAciertos );
			 	}
		 }
		
		// LinearLayout chart_container=(LinearLayout)findViewById(R.id.layoutResultados);
		 final GraphicalView mChart; 

		
		 
		int[] colores={Color.BLUE,Color.GREEN,Color.MAGENTA,Color.YELLOW,Color.RED,Color.rgb(200, 0, 0)}; 
		
		 
		 SerieEjerciciosDataSource sed2=new SerieEjerciciosDataSource(this);
		 sed2.open();
	 
	        // Creating an  XYSeries for Income
		 XYSeries listaXYSeriesTotales=new XYSeries("Totales");
		 XYSeries listaXYSeriesAciertos=new XYSeries("Aciertos");
		 

        for(int j=0;j<fechaTipo*listaSeries.size();j++){
        			listaXYSeriesTotales.add(j,listaValores.get(j));
        			listaXYSeriesAciertos.add(j,listaAciertos.get(j));
	        }
		
		 sed2.close();

	 
	        // Creating a dataset to hold each series
	        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	        
	        


	        XYSeriesRenderer RendererTotales=new XYSeriesRenderer();
	        XYSeriesRenderer RendererAciertos=new XYSeriesRenderer();
	        //int n=0;

        	dataset.addSeries(listaXYSeriesTotales);
        	dataset.addSeries(listaXYSeriesAciertos);
	        	
	        	//dataset.addSeries(listaXYSeries.get(i));
	        	
        	RendererTotales.setColor(colores[0]);
        	RendererTotales.setFillPoints(true);
        	RendererTotales.setLineWidth(2);
        	RendererTotales.setDisplayChartValues(true);
        	RendererTotales.setChartValuesTextSize(20);
        	RendererTotales.setChartValuesTextAlign(Align.CENTER);

	        	

	        	
        	RendererAciertos.setColor(colores[1]);
        	RendererAciertos.setFillPoints(true);
        	RendererAciertos.setLineWidth(2);
        	RendererAciertos.setDisplayChartValues(true);
        	RendererAciertos.setChartValuesTextSize(20);
        	RendererAciertos.setChartValuesTextAlign(Align.CENTER);

	        	
	        	
	        	
	        
	
	        	
	 
	        // Creating a XYMultipleSeriesRenderer to customize the whole chart
	        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
	        multiRenderer.setXLabels(0);
	        multiRenderer.setBarSpacing(0.25f);
	        multiRenderer.setChartTitle(al.getNombre());
	        multiRenderer.setChartTitleTextSize(25);
	        multiRenderer.setShowGrid(true);
	        
	        
	        
	        multiRenderer.setApplyBackgroundColor(true);
	        multiRenderer.setBackgroundColor(getResources().getColor(R.color.degradado2));
	        multiRenderer.setMarginsColor(getResources().getColor(R.color.degradado1));
	        
	        int[] margins = {40, 40, 40, 40};
	        multiRenderer.setMargins(margins);
	        //multiRenderer.setM
	        
	        multiRenderer.setAxesColor(getResources().getColor(R.color.azul_oscuro));
	        multiRenderer.setGridColor(getResources().getColor(R.color.azul_oscuro));
	        
	        multiRenderer.setLabelsColor(getResources().getColor(R.color.azul_oscuro));
	        multiRenderer.setXLabelsColor(getResources().getColor(R.color.azul_oscuro));//Color de cada texto de fecha
	        multiRenderer.setYLabelsColor(0, getResources().getColor(R.color.azul_oscuro));//Colo de cada texto de puntuacion
	        
	        
	        
	        
	        switch (fechaTipo) {
			case Periodo.Semana:
				multiRenderer.setXTitle("Secuencia Ejercicios-Día");
				multiRenderer.setXAxisMax(fechaTipo);
				break;
			case Periodo.Mes:
				multiRenderer.setXTitle("Secuencia Ejercicios-Día");
				multiRenderer.setXAxisMax(fechaTipo);
				break;
				
			case Periodo.SeisMeses:
				multiRenderer.setXTitle("Secuencia Ejercicios-Mes");
				multiRenderer.setXAxisMax(fechaTipo);
				break;
			default:
				break;
			}
	        multiRenderer.setYTitle("Aciertos/Totales");
	        multiRenderer.setZoomButtonsVisible(true);
	        multiRenderer.setXAxisMin(-1);
	        
	        multiRenderer.setYAxisMin(0);
	        multiRenderer.setYAxisMax(maximo+5);
	        multiRenderer.setPanEnabled(true, false); //Evitar que se mueva verticalmente
	        multiRenderer.setLegendTextSize(25);
	        multiRenderer.setFitLegend(true);
	        multiRenderer.setXLabelsPadding(18f);
	        
	      //  multiRenderer.setRange(new double[] { -1, 7, 0, 10 });
	        //multiRenderer.setMarginsColor(Color.MAGENTA);

	        SimpleDateFormat dateFormat = null;
	        switch (fechaTipo) {
			case Periodo.Semana:
				dateFormat=new SimpleDateFormat("dd/MM/yyyy");
				break;
			case Periodo.Mes:
				dateFormat=new SimpleDateFormat("dd/MM");
				break;
			case Periodo.SeisMeses:
				dateFormat=new SimpleDateFormat("MM/yyyy");
				break;
			
			default:
				break;
			}
	        seds.open();
	        //new SimpleDateFormat("dd/MM/yyyy");
	        for(int i=0; i< fechaTipo;i++){
	        	for(int j=0;j<listaSeries.size();j++){
				        Calendar calen = Calendar.getInstance();
				        if(fechaTipo==Periodo.Semana||fechaTipo==Periodo.Mes)
				        	calen.add(Calendar.DATE, -(fechaTipo-1-i));
				        if(fechaTipo==Periodo.SeisMeses)
				        	calen.add(Calendar.MONTH, -(fechaTipo-1-i));
			            if(j==0)
			            {
			            	//multiRenderer.setBarSpacing(4.5f);
			            	multiRenderer.addXTextLabel(i*listaSeries.size()+j,seds.getSerieEjercicios(listaSeries.get(j)).getNombre()+"\n"+dateFormat.format(calen.getTime()).toString());
			            	
			            }
			            else
			            	multiRenderer.addXTextLabel(i*listaSeries.size()+j, seds.getSerieEjercicios(listaSeries.get(j)).getNombre());
	        	}
	        }
	        seds.close();
	        // Adding incomeRenderer and expenseRenderer to multipleRenderer
	        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
	        // should be same

        	multiRenderer.addSeriesRenderer(RendererTotales);
        	multiRenderer.addSeriesRenderer(RendererAciertos);
	        


	        //mChart=(GraphicalView)ChartFactory.getBarChartView(getBaseContext(), dataset, multiRenderer, Type.DEFAULT);
	        mChart=(GraphicalView)ChartFactory.getBarChartView(getContext(), dataset, multiRenderer, Type.STACKED);
	        //mChart=(GraphicalView)ChartFactory.getCombinedXYChartView(context, dataset, renderer, types)
	        //mChart=(GraphicalView)ChartFactory.getLineChartView(getContext(), dataset, multiRenderer);
	        va.addView(mChart);
	        
			  //ll.addView(mChart);
			  
			  mChart.setBackgroundColor(Color.WHITE);
			  
	 }
	
	
	 
	 
	 private int diferenciaDias(Date date1,Date date2){
		 final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

		 int diffInDays = (int) ((date1.getTime() - date2.getTime())/ DAY_IN_MILLIS );
		 return diffInDays;
	 }
	 
	 private Date restaDias(int ndias){
		 	Date d=new Date();
		    Calendar c = Calendar.getInstance();
		    c.add(Calendar.DATE, -ndias);
		    d.setTime( c.getTime().getTime() );
		    return d;
	 }

	 private Date restaMeses(int meses){
		 	Date d=new Date();
		    Calendar c = Calendar.getInstance();
		    c.add(Calendar.MONTH, -meses);
		    d.setTime( c.getTime().getTime() );
		    return d;
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
	
}
