package es.ugr.juegoreconocimiento;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ViewAnimator;
import android.widget.TabHost.TabSpec;




/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */
public class Graficas extends Activity {
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
		Bundle extras = getIntent().getExtras();
	    fechaTipo=extras.getInt("tipoFecha"); 
	    listaAlumnos=extras.getIntegerArrayList("listaAlumnos");
	    listaSeries=extras.getIntegerArrayList("listaSeries");
	    graficaTipo=extras.getInt("tipoGrafica");

	    Calendar cal;
	    Calendar calAnt;
		switch (fechaTipo) {
		case Periodo.Semana:
			calAnt=Calendar.getInstance();
			cal=Calendar.getInstance();
			calAnt.setTime(restaDias(fechaTipo-1));
			subtitulo.setText("Última semana: "+calAnt.get(Calendar.DAY_OF_MONTH)+"/"+mMonth[calAnt.get(Calendar.MONTH)]+"/"+calAnt.get(Calendar.YEAR)+" - "+cal.get(Calendar.DAY_OF_MONTH)+"/"+mMonth[cal.get(Calendar.MONTH)]+"/"+cal.get(Calendar.YEAR));
		break;	
		case Periodo.Mes:
			calAnt=Calendar.getInstance();
			cal=Calendar.getInstance();
			calAnt.setTime(restaDias(fechaTipo-1));
			subtitulo.setText("Último mes: "+calAnt.get(Calendar.DAY_OF_MONTH)+"/"+mMonth[calAnt.get(Calendar.MONTH)]+"/"+calAnt.get(Calendar.YEAR)+" - "+cal.get(Calendar.DAY_OF_MONTH)+"/"+mMonth[cal.get(Calendar.MONTH)]+"/"+cal.get(Calendar.YEAR));
		break;	
		case Periodo.SeisMeses:
			calAnt=Calendar.getInstance();
			cal=Calendar.getInstance();
			calAnt.setTime(restaMeses(fechaTipo-1));
			subtitulo.setText("Últimos 6 meses: "+mMonth[calAnt.get(Calendar.MONTH)]+"/"+calAnt.get(Calendar.YEAR)+" - "+mMonth[cal.get(Calendar.MONTH)]+"/"+cal.get(Calendar.YEAR));
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
	        multiRenderer.setBackgroundColor(getResources().getColor(R.color.rellenoTablaResultados));
	        multiRenderer.setMarginsColor(getResources().getColor(R.color.rellenoTabla));
	        
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


	        
	        //new SimpleDateFormat("dd/MM/yyyy");
	        for(int i=0; i< fechaTipo;i++){
		        Calendar calen = Calendar.getInstance();
		        if(fechaTipo==Periodo.Semana||fechaTipo==Periodo.Mes){
		        	calen.add(Calendar.DATE, -(fechaTipo-1-i));
		            multiRenderer.addXTextLabel(i,semana[calen.get(Calendar.DAY_OF_WEEK)-1]+"-"+calen.get(Calendar.DAY_OF_MONTH));
		        }
		        if(fechaTipo==Periodo.SeisMeses){
		        	calen.add(Calendar.MONTH, -(fechaTipo-1-i));
		        	multiRenderer.addXTextLabel(i, mMonth[calen.get(Calendar.MONTH)]);
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
		 
         LayoutInflater mInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
         View myView= mInflater.inflate(R.layout.pestanias, null);
        
         TabHost myTabHost=(TabHost)myView.findViewById(R.id.tabHostPes);
         myTabHost.setup();
		 
		 int[] colores={Color.BLUE,Color.GREEN,Color.MAGENTA,Color.YELLOW,Color.RED,Color.rgb(200, 0, 0)}; 
		 int maximo=0;
		 //Añadimos Lista Resultados
		 
		 List<Resultado> listaFinal=new ArrayList<Resultado>();
		 ResultadoDataSource rds=new ResultadoDataSource(this);
		 AlumnoDataSource ads=new AlumnoDataSource(this);
		 SerieEjerciciosDataSource seds=new SerieEjerciciosDataSource(this);
		 
		 rds.open();
		 ads.open();
		 seds.open();
		 
		 Alumno al=ads.getAlumnos(idAlumno);
		 
		 nombAlumnos.add(al.getNombre());
		 
		 for(int i=0;i<listaSeries.size();i++){
			 
			 maximo=0;
			 listaFinal.clear();
			 SerieEjercicios se=seds.getSerieEjercicios(listaSeries.get(i));
			 listaFinal=(rds.getResultadosAlumno(al,se,fechaTipo));
			 
			 List<Integer> listaValores=new ArrayList<Integer>(Collections.nCopies(fechaTipo, 0));
			 List<Integer> listaAciertos=new ArrayList<Integer>(Collections.nCopies(fechaTipo, 0));
			 
			 	for(int j=0;j<listaFinal.size();j++){
			 		
			 		int distancia=0;
			 		switch (fechaTipo) {
					case Periodo.Semana:
						distancia=(fechaTipo-1)-diferenciaDias(new Date(), listaFinal.get(j).getFechaRealizacion());
						break;
					case Periodo.Mes:
						distancia=(fechaTipo-1)-diferenciaDias(new Date(), listaFinal.get(j).getFechaRealizacion());
						break;
						
					case Periodo.SeisMeses:
						distancia=(fechaTipo-1)-diferenciaMeses(new Date(), listaFinal.get(j).getFechaRealizacion());
						break;
					default:
						break;
					}
			 		
			 		int vAciertos=listaFinal.get(j).getAciertos();
			 		int vTotales=listaFinal.get(j).getAciertos()+listaFinal.get(j).getFallos();
			 		if (vTotales>maximo)
			 			maximo=vTotales;

			 		listaValores.set(distancia, vTotales);
			 		listaAciertos.set(distancia,vAciertos);
			 		
			 		
			 	}
			 	
			 	

			        // Creating an  XYSeries for Income
				 XYSeries listaXYSeriesTotales=new XYSeries("Totales");
				 XYSeries listaXYSeriesAciertos=new XYSeries("Aciertos");
				 

		        for(int j=0;j<fechaTipo;j++){
		        			listaXYSeriesTotales.add(j,listaValores.get(j));
		        			listaXYSeriesAciertos.add(j,listaAciertos.get(j));
			        }
				

			 
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
			        multiRenderer.setBackgroundColor(getResources().getColor(R.color.rellenoTabla));
			        multiRenderer.setMarginsColor(getResources().getColor(R.color.rellenoTablaResultados));
			        
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

			        for(int k=0; k< fechaTipo;k++){
				        Calendar calen = Calendar.getInstance();
				        if(fechaTipo==Periodo.Semana||fechaTipo==Periodo.Mes){
				        	calen.add(Calendar.DATE, -(fechaTipo-1-k));
				            multiRenderer.addXTextLabel(k,semana[calen.get(Calendar.DAY_OF_WEEK)-1]+"-"+calen.get(Calendar.DAY_OF_MONTH));
				        }
				        if(fechaTipo==Periodo.SeisMeses){
				        	calen.add(Calendar.MONTH, -(fechaTipo-1-k));
				        	multiRenderer.addXTextLabel(k, mMonth[calen.get(Calendar.MONTH)]);
				        }

			        }
			        // Adding incomeRenderer and expenseRenderer to multipleRenderer
			        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
			        // should be same

		        	multiRenderer.addSeriesRenderer(RendererTotales);
		        	multiRenderer.addSeriesRenderer(RendererAciertos);
			        


			        //mChart=(GraphicalView)ChartFactory.getBarChartView(getBaseContext(), dataset, multiRenderer, Type.DEFAULT);
		        	GraphicalView mChart=(GraphicalView)ChartFactory.getBarChartView(getContext(), dataset, multiRenderer, Type.STACKED);
			        //mChart=(GraphicalView)ChartFactory.getCombinedXYChartView(context, dataset, renderer, types)
			        //mChart=(GraphicalView)ChartFactory.getLineChartView(getContext(), dataset, multiRenderer);
			       
			 		
			 		////////////////////////////////////////////////////////////////////////////
		        	final GraphicalView fmChart=mChart;
			        TabSpec ts1 = myTabHost.newTabSpec("TAB_TAG_1");
			        
			        /*
			        TextView  txtTab = new TextView(this); 
			        txtTab.setText(se.getNombre()); 
			        txtTab.setPadding(8, 5, 8, 5); 
			        txtTab.setTextColor(Color.BLACK); 
			        txtTab.setTextSize(15); 
			        txtTab.setGravity(Gravity.CENTER);
			        txtTab.setBackgroundResource(R.drawable.seliconoalumno);

			        ts1.setIndicator(txtTab);
			        */
			        ts1.setIndicator(se.getNombre());
			        ts1.setContent(new TabHost.TabContentFactory() {
						
						public View createTabContent(String tag) {
							// TODO Auto-generated method stub
							return fmChart;
						}
					});
			        myTabHost.addTab(ts1);
			 	
		 }
		 rds.close();
		 ads.close();
		 seds.close();
		 
		
		 

	        
	        
	        /*
	        TabHost myTabHost=(TabHost)this.findViewById(R.id.tabHost);
	        myTabHost.setup();
	        
	        TabSpec ts1 = myTabHost.newTabSpec("TAB_TAG_1");
	        ts1.setIndicator("Tab1");
	        ts1.setContent(new TabHost.TabContentFactory() {
				
				public View createTabContent(String tag) {
					// TODO Auto-generated method stub
					return mChart;
				}
			});
	        myTabHost.addTab(ts1);
	        
	        */

	        

	        va.addView(myTabHost);
	        
	        
	        
	        //va.addView(mChart);
	        
			  //ll.addView(mChart);
			  
			//  mChart.setBackgroundColor(Color.WHITE);
			  
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
