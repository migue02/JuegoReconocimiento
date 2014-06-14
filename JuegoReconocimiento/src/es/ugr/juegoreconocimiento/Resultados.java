package es.ugr.juegoreconocimiento;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import es.ugr.juegoreconocimiento.R;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import es.ugr.basedatos.*;
import es.ugr.objetos.*;
import es.ugr.objetos.TiposPropios.Periodo;

/**
 * @author Namir Sayed-Ahmad Baraza
 * @mail namirsab@gmail.com
 *
 */
public class Resultados extends Activity {
	private ListView listView ;
	private Button semana,mes,año,mGrafica,mTabla,mXLS;
	private int fecha=Periodo.Semana;
	private List<Boolean> alSelec,serSelec;
	private TableLayout tl;
	private File exportacion;
	List<Integer> listaIdAlumnos;
	List<Integer> listaIdSeries;
	int radioSelec=0;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resultados);
		InicioResultados();
		//InsertaResultado();

	}

	private void InicioResultados(){

		
		
		listView=(ListView)findViewById(R.id.listViewResul);
		CreaLista();
		
		alSelec=new ArrayList<Boolean>();
		serSelec=new ArrayList<Boolean>();
		
		//Captación de los botones

		semana=new Button(this);
		semana=(Button)findViewById(R.id.filSemana);
		
		mes=new Button(this);
		mes=(Button)findViewById(R.id.filMes);
		
		año=new Button(this);
		año=(Button)findViewById(R.id.filAnio);
		
		semana.setBackgroundColor(getResources().getColor(R.color.tabla1));
		mes.setBackgroundColor(getResources().getColor(R.color.white));
		año.setBackgroundColor(getResources().getColor(R.color.white));
		
		mGrafica=(Button)findViewById(R.id.ResulGrafica);
		mTabla=(Button)findViewById(R.id.MuestraTabla);
		mXLS=(Button)findViewById(R.id.ResulXLS);
		
		
		semana.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fecha=Periodo.Semana;
				semana.setBackgroundColor(getResources().getColor(R.color.tabla1));
				mes.setBackgroundColor(getResources().getColor(R.color.white));
				año.setBackgroundColor(getResources().getColor(R.color.white));
			}
		});
		
		
		mes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fecha=Periodo.Mes;
				semana.setBackgroundColor(getResources().getColor(R.color.white));
				mes.setBackgroundColor(getResources().getColor(R.color.tabla1));
				año.setBackgroundColor(getResources().getColor(R.color.white));
			}
		});
		
		año.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fecha=Periodo.SeisMeses;
				semana.setBackgroundColor(getResources().getColor(R.color.white));
				mes.setBackgroundColor(getResources().getColor(R.color.white));
				año.setBackgroundColor(getResources().getColor(R.color.tabla1));
			}
		});
		tl=(TableLayout)findViewById(R.id.ResulTabla);
		TableRow row=new TableRow(this);
		TextView tit1,tit2,tit3,tit4;
        tit1=new TextView(this);
        tit2=new TextView(this);
        tit3=new TextView(this);
        tit4=new TextView(this);
        
      	 tit1.setText("Alumno");
       	 tit1.setPadding(2, 0, 5, 0);
       	 
       	 tit2.setText("");
       	 tit2.setPadding(2, 0, 5, 0);
       	 
       	 tit3.setText("Serie");
       	 tit3.setPadding(2, 0, 5, 0);
       	 
       	 tit4.setText("");
       	 tit4.setPadding(2, 0, 5, 0);
       	 
		row.addView(tit1);
		row.addView(tit2);
		row.addView(tit3);
		row.addView(tit4);
		
		tl.addView(row);
		

		
		
		AlumnoDataSource ads=new AlumnoDataSource(this);
		SerieEjerciciosDataSource seds=new SerieEjerciciosDataSource(this);
		ads.open();
		seds.open();
		final List<Alumno> la=ads.getAllAlumnos();
		final List<SerieEjercicios> lse=seds.getAllSeriesEjercicios();
		ads.close();
		seds.close();
		
		
		//Añadir campo todos
		row=new TableRow(this);
		row.setBackgroundColor(getResources().getColor(R.color.tabla_resaltado));
		CheckBox cbTodosAlumnos=new CheckBox(this);
		TextView tvTodosAlumnos=new TextView(this);
		CheckBox cbTodasSeries=new CheckBox(this);
		TextView tvTodasSeries=new TextView(this);
		
		row.addView(cbTodosAlumnos);
		tvTodosAlumnos.setText("Todos los alumnos");
		row.addView(tvTodosAlumnos);
		row.addView(cbTodasSeries);
		tvTodasSeries.setText("Todas las Series");
		row.addView(tvTodasSeries);
		
		cbTodosAlumnos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox micb=(CheckBox)v;
				if (micb.isChecked())
					for(int i=0;i<la.size();i++){
						 CheckBox cb = (CheckBox)((TableRow)tl.getChildAt(i+2)).getChildAt(0);
						 cb.setChecked(true);
						 cb.setEnabled(false);
						 alSelec.set(i, true);
					}
				else
					for(int i=0;i<la.size();i++){
						 CheckBox cb = (CheckBox)((TableRow)tl.getChildAt(i+2)).getChildAt(0);
						 cb.setChecked(false);
						 cb.setEnabled(true);
						 alSelec.set(i, false);
					}
			}
		});
		
		cbTodasSeries.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox micb=(CheckBox)v;
				if (micb.isChecked())
					for(int i=0;i<lse.size();i++){
						 CheckBox cb = (CheckBox)((TableRow)tl.getChildAt(i+2)).getChildAt(2);
						 cb.setChecked(true);
						 cb.setEnabled(false);
						 serSelec.set(i, true);
					}
				else
					for(int i=0;i<lse.size();i++){
						 CheckBox cb = (CheckBox)((TableRow)tl.getChildAt(i+2)).getChildAt(2);
						 cb.setChecked(false);
						 cb.setEnabled(true);
						 serSelec.set(i, false);
					}
			}
		});
		
		
		tl.addView(row);
		
		int maximo=0;
		maximo=Math.max(la.size(), lse.size());
		
		for(int i=0;i<maximo;i++){
			 row=new TableRow(this);
			 row.setTag(i);
	       	 if(i%2==0)
	       		 row.setBackgroundColor(getResources().getColor(R.color.tabla1));
	       	 else
	       		 row.setBackgroundColor(getResources().getColor(R.color.tabla2));
	       	 
	            row.setLayoutParams(new LayoutParams(
	                    LayoutParams.FILL_PARENT,
	                    LayoutParams.WRAP_CONTENT)); 
	            
	         TextView alum=new TextView(this);
	         CheckBox cb2=new CheckBox(this);
	         TextView serie=new TextView(this);
	         
	         if(i<la.size()){
		         CheckBox cb=new CheckBox(this);	
	        	 alum.setText(la.get(i).getNombre()+" "+la.get(i).getApellidos());
	        	 row.addView(cb);
		         row.addView(alum);
		         alSelec.add(false);
		         final int pos=i;
		         cb.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						CheckBox aux=(CheckBox)v;
						alSelec.set(pos, aux.isChecked());
					}
				});
	         }
	         else{
	        	 TextView vacio=new TextView(this);
	        	 row.addView(vacio);
		         row.addView(alum);	        	  
	         }
	         if(i<lse.size()){
	        	 serie.setText(lse.get(i).getNombre());
		         row.addView(cb2);
	        	 row.addView(serie);
	        	 serSelec.add(false);
	        	 final int pos2=i;
		         cb2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						CheckBox aux=(CheckBox)v;
						serSelec.set(pos2, aux.isChecked());
					}
				});
	         }
	         else{
	        	 TextView vacio=new TextView(this);
	        	 row.addView(vacio);
		         row.addView(serie);	  
	         }

	         tl.addView(row);
		}
		
		mGrafica.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent graficaIntent=new Intent(getApplicationContext(), Graficas.class);

				
				//RadioButton
				RadioGroup rg=(RadioGroup)findViewById(R.id.radioGroup);		
				int radioButtonID = rg.getCheckedRadioButtonId();
				View radioButton = rg.findViewById(radioButtonID);
				int radioSelec = rg.indexOfChild(radioButton);
				
				
				
				graficaIntent.putExtra("tipoFecha", fecha);
				//Lista de alumnos seleccionados
				listaIdAlumnos=new ArrayList<Integer>();
				for(int i=0;i<alSelec.size();i++){
					if(alSelec.get(i)==true)
						listaIdAlumnos.add(la.get(i).getIdAlumno());
				}
				
				listaIdSeries=new ArrayList<Integer>();
				for(int i=0;i<serSelec.size();i++){
					if(serSelec.get(i)==true)
						listaIdSeries.add(lse.get(i).getIdSerie());
				}

				
				if(listaIdAlumnos.size()==0||listaIdSeries.size()==0){
					Toast.makeText(v.getContext(), "Seleccionar al menos un Alumno-Serie", Toast.LENGTH_LONG).show();
				}
				else{
				graficaIntent.putExtra("tipoGrafica", radioSelec);
				graficaIntent.putIntegerArrayListExtra("listaAlumnos", (ArrayList<Integer>) listaIdAlumnos);
				graficaIntent.putIntegerArrayListExtra("listaSeries", (ArrayList<Integer>) listaIdSeries);
				startActivity(graficaIntent);
				}
			}
		});
		
		mTabla.setOnClickListener(new OnClickListener() {

			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tablaIntent=new Intent(getApplicationContext(), Tablas.class);

				
				//RadioButton
				RadioGroup rg=(RadioGroup)findViewById(R.id.radioGroup);		
				int radioButtonID = rg.getCheckedRadioButtonId();
				View radioButton = rg.findViewById(radioButtonID);
				radioSelec = rg.indexOfChild(radioButton);
				
				
				
				tablaIntent.putExtra("tipoFecha", fecha);
				//Lista de alumnos seleccionados
				listaIdAlumnos=new ArrayList<Integer>();
				for(int i=0;i<alSelec.size();i++){
					if(alSelec.get(i)==true)
						listaIdAlumnos.add(la.get(i).getIdAlumno());
				}
				
				listaIdSeries=new ArrayList<Integer>();
				for(int i=0;i<serSelec.size();i++){
					if(serSelec.get(i)==true)
						listaIdSeries.add(lse.get(i).getIdSerie());
				}

				
				if(listaIdAlumnos.size()==0||listaIdSeries.size()==0){
					Toast.makeText(v.getContext(), "Seleccionar al menos un Alumno-Serie", Toast.LENGTH_LONG).show();
				}
				else{
				tablaIntent.putExtra("tipoGrafica", radioSelec);
				tablaIntent.putIntegerArrayListExtra("listaAlumnos", (ArrayList<Integer>) listaIdAlumnos);
				tablaIntent.putIntegerArrayListExtra("listaSeries", (ArrayList<Integer>) listaIdSeries);
				startActivity(tablaIntent);
				}
				
				
				
			}
		});
		
	
		mXLS.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//Lanzar Dialog
				//final Dialog dialog = new Dialog(getApplicationContext());
				final Dialog dialog = new Dialog(v.getContext());
				dialog.setContentView(R.layout.dialogo_exportar);
				dialog.setTitle("Exportar...");
				
				Button expor,cancelar;
				final RadioButton rb1;
				final RadioButton rb2;
				rb1=(RadioButton)dialog.findViewById(R.id.radioButtonExp1);
				rb2=(RadioButton)dialog.findViewById(R.id.radioButtonExp2);
				
				expor=(Button)dialog.findViewById(R.id.aExportar);
				cancelar=(Button)dialog.findViewById(R.id.cExportar);
				expor.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int tipoExport=0;
						if(rb1.isChecked())
							tipoExport=1;
						if(rb2.isChecked()){
							tipoExport=2;
							
							listaIdAlumnos=new ArrayList<Integer>();
							for(int i=0;i<alSelec.size();i++){
								if(alSelec.get(i)==true)
									listaIdAlumnos.add(la.get(i).getIdAlumno());
							}
							
							listaIdSeries=new ArrayList<Integer>();
							for(int i=0;i<serSelec.size();i++){
								if(serSelec.get(i)==true)
									listaIdSeries.add(lse.get(i).getIdSerie());
							}
							
							
						}
						ExportarXLS(tipoExport);
						dialog.dismiss();
					}
				});
				dialog.show();
				
				cancelar.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
			}
		});
	
	}
	
	private void CreaLista(){
		String[] values = new String[] { "Menú Principal","Gestion Alumnos","Resultados/Estadísticas","Ejercicios"
        };
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		 android.R.layout.simple_list_item_1, android.R.id.text1, values);
		
		
		
		// Assign adapter to ListView
		 listView.setAdapter(adapter);
		 listView.setOnItemClickListener(new OnItemClickListener() {
			 

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
          int itemPosition     = arg2;
          
          // ListView Clicked item value
          String  itemValue    = (String) listView.getItemAtPosition(arg2);
             
           // Show Alert
          switch (itemPosition) {
		case 0:
			finish();
			break;
		case 1:
			
			Intent AlumnoIntent=new Intent(getApplicationContext(), GestionAlumnos.class);
			startActivity(AlumnoIntent);
			finish();
			break;
		case 2:
			Intent ResultadosIntent=new Intent(getApplicationContext(), Resultados.class);
			startActivity(ResultadosIntent);
			finish();
			
			break;
		case 3:
			Intent SeriesIntent=new Intent(getApplicationContext(), SeriesEjercicios.class);
			startActivity(SeriesIntent);
			finish();
			break;

		default:
			break;
		}

	}

});
	}
	
	private void InsertaResultado(){
		
		ResultadoDataSource rds=new ResultadoDataSource(this);
		rds.open();
		 Calendar cal = Calendar.getInstance();
		 cal.set(Calendar.DAY_OF_MONTH,24);
		 cal.set(Calendar.MONTH,4);
		 cal.set(Calendar.YEAR,2014);

		 Date d = cal.getTime();
		 
		 cal = Calendar.getInstance();
		 cal.set(Calendar.DAY_OF_MONTH,25);
		 cal.set(Calendar.MONTH,4);
		 cal.set(Calendar.YEAR,2014);


		 Date d2 = cal.getTime();
		 
		 
		 cal = Calendar.getInstance();
		 cal.set(Calendar.DAY_OF_MONTH,26);
		 cal.set(Calendar.MONTH,4);
		 cal.set(Calendar.YEAR,2014);

		 Date d3 = cal.getTime();
		 
		
		Resultado r=new Resultado(1, 1, 1, d, 0, 30, 18, 12, 7.2);	
		Resultado r2=new Resultado(2, 1, 1, d2, 0, 30, 18, 12, 8.9);
		Resultado r3=new Resultado(3, 2, 1, d3, 0, 30, 18, 12, 6.1);
		Resultado r4=new Resultado(4, 2, 1, d2, 0, 30, 18, 12, 5.4);
		
		rds.createResultado(r);
		rds.createResultado(r2);
		rds.createResultado(r3);
		rds.createResultado(r4);
		
		rds.close();
	}
	
	
	
	//Funciones privadas para el uso de excel
	
	
	public WritableWorkbook createWorkbook(String fileName){
	    //exports must use a temp file while writing to avoid memory hogging
	    WorkbookSettings wbSettings = new WorkbookSettings(); 				
	    wbSettings.setUseTemporaryFileDuringWrite(true);   
	 
	    //get the sdcard's directory
	    File sdCard = Environment.getExternalStorageDirectory();
	    //add on the your app's path
	    File dir = new File(sdCard.getAbsolutePath() + "/XLS");
	    //make them in case they're not there
	    boolean crea=dir.mkdirs();
	    //create a standard java.io.File object for the Workbook to use
	    exportacion = new File(dir,fileName);
	 
	    WritableWorkbook wb = null;
	 
	    try{
		//create a new WritableWorkbook using the java.io.File and
		//WorkbookSettings from above
		//wb = Workbook.createWorkbook(wbfile,wbSettings);
	    	wb = Workbook.createWorkbook(exportacion,wbSettings);
	    }catch(IOException ex){

	    }
	 
	    return wb;	
	}
	
	
	public WritableSheet createSheet(WritableWorkbook wb, 
		    String sheetName, int sheetIndex){
		    //create a new WritableSheet and return it
		    return wb.createSheet(sheetName, sheetIndex);
		}
	
	/**
	 * 
	 * @param columnPosition - column to place new cell in
	 * @param rowPosition - row to place new cell in
	 * @param contents - string value to place in cell
	 * @param headerCell - whether to give this cell special formatting
	 * @param sheet - WritableSheet to place cell in
	 * @throws RowsExceededException - thrown if adding cell exceeds .xls row limit
	 * @throws WriteException - Idunno, might be thrown
	 */
	public void writeCell(int columnPosition, int rowPosition, String contents, boolean headerCell,
	  	WritableSheet sheet) throws RowsExceededException, WriteException{
	    //create a new cell with contents at position
	    Label newCell = new Label(columnPosition,rowPosition,contents);
	 
	    if (headerCell){
	    	//give header cells size 10 Arial bolded 	
	    	WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
	    	WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
	    	//center align the cells' contents
	    	headerFormat.setAlignment(Alignment.CENTRE);
	        newCell.setCellFormat(headerFormat);
	    }
	 
	    sheet.addCell(newCell);
	}
	
	
	
	
	void ExportarXLS(int tipoExpor){
		
		List<Resultado> lr=new ArrayList<Resultado>();
		String textoDesc=new String();
		
		ResultadoDataSource rds=new ResultadoDataSource(getApplicationContext());
		AlumnoDataSource ads=new AlumnoDataSource(getApplicationContext());
		SerieEjerciciosDataSource seds=new SerieEjerciciosDataSource(getApplicationContext());
		
		rds.open();
		ads.open();
		seds.open();
		
		textoDesc=new String("");
		
		if(tipoExpor==1){
			lr=rds.getAllResultados();
			textoDesc=new String("todos");
			
		}
		
		if(tipoExpor==2){
			if(radioSelec==0){

				
				
				
				for(int i=0;i<listaIdSeries.size();i++)
					for(int j=0;j<listaIdAlumnos.size();j++){
						List<Resultado> listaSerie=rds.getResultadosAlumno(ads.getAlumnos(listaIdAlumnos.get(j)), seds.getSerieEjercicios(listaIdSeries.get(i)), fecha);
						for(int k=0;k<listaSerie.size();k++)
							lr.add(listaSerie.get(k));
						
					}
				switch (fecha) {
				case Periodo.Semana:
					textoDesc="UltimaSemana";
					break;
				case Periodo.Mes:
					textoDesc="UltimoMes";
					break;
				case Periodo.SeisMeses:
					textoDesc="Ultimos6Meses";
					break;
				default:
					break;
				}

			}
			else{
					for(int j=0;j<listaIdAlumnos.size();j++)
						for(int i=0;i<listaIdSeries.size();i++){
						List<Resultado> listaSerie=rds.getResultadosAlumno(ads.getAlumnos(listaIdAlumnos.get(j)), seds.getSerieEjercicios(listaIdSeries.get(i)), fecha);
						for(int k=0;k<listaSerie.size();k++)
							lr.add(listaSerie.get(k));
						
					}
				
				
			}
			
		}
		

	
		
		
	
		
		
		Date now=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy_HH.mm.ss");
		
		WritableWorkbook ww=createWorkbook(textoDesc+"Resultados"+"-"+sdf.format(now)+".xls");
		WritableSheet ws=createSheet(ww, "Resultados", 0);
		try {
			writeCell(0, 0, "Nombre Alumno", true, ws);
			writeCell(1, 0, "Apellidos Alumno", true, ws);
			writeCell(2, 0, "Serie Ejercicios", true, ws);
			writeCell(3, 0, "Fecha", true, ws);
			writeCell(4, 0, "Duración", true, ws);
			writeCell(5, 0, "Nº Objetos", true, ws);
			writeCell(6, 0, "Aciertos", true, ws);
			writeCell(7, 0, "Fallos", true, ws);
			writeCell(8, 0, "Puntuación", true, ws);
			
			for(int i=0;i<lr.size();i++){
				writeCell(0, i+1, ads.getAlumnos(lr.get(i).getIdAlumno()).getNombre(), false, ws);
				writeCell(1, i+1, ads.getAlumnos(lr.get(i).getIdAlumno()).getApellidos(), false, ws);
				writeCell(2, i+1, seds.getSerieEjercicios(lr.get(i).getIdEjercicio()).getNombre(), false, ws);
				writeCell(3, i+1, lr.get(i).getFechaRealizacion_AsStrign(), false, ws);
				writeCell(4, i+1, String.valueOf(lr.get(i).getDuracion()), false, ws);
				writeCell(5, i+1, String.valueOf(lr.get(i).getNumeroObjetosReconocer()), false, ws);
				writeCell(6, i+1, String.valueOf(lr.get(i).getAciertos()), false, ws);
				writeCell(7, i+1, String.valueOf(lr.get(i).getFallos()), false, ws);
				writeCell(8, i+1, String.valueOf(lr.get(i).getPuntuacion()), false, ws);
				
				
			}
			
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    
		try {
			ww.write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ww.close();
			Toast.makeText(getApplicationContext(),"Creado "+exportacion.toString(), Toast.LENGTH_LONG).show();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
