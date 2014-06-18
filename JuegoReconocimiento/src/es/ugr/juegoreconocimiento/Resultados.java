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
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import es.ugr.adaptadores.RowItemTitle;
import es.ugr.adaptadores.adaptadorTitle;
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
	private Button semana,mes,año;
	private Button rRanking,rAlumno;
	private View mGrafica,mTabla,mXLS;
	private int fecha=Periodo.Semana;
	private List<Boolean> alSelec,serSelec;
	private TableLayout tlAl;
	private TableLayout tlSer;
	private File exportacion;
	List<Integer> listaIdAlumnos;
	List<Integer> listaIdSeries;
	private int radioSelec=0;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
	    getActionBar().setCustomView(R.layout.mibarrares);
		
		
		setContentView(R.layout.resultados);
		InicioResultados();
		//InsertaResultado();
		ImageView principal=(ImageView)findViewById(R.id.principalSer);
	    principal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		

	}

	private void CreaLista(){
		String[] titulos = new String[] { "Menú Principal","Gestión Alumnos","Resultados/Estadísticas"," Ejercicios","Serie Ejercicios"
        };
		Integer[] images=new Integer[]{R.drawable.anterior,R.drawable.ic2,R.drawable.ic1,R.drawable.ic6,R.drawable.ic3};

		List<RowItemTitle> rowItems;
		rowItems=new ArrayList<RowItemTitle>();
		for(int i=0;i<titulos.length;i++){
			rowItems.add(new RowItemTitle(titulos[i],images[i]));
		}
		
		

		adaptadorTitle adapter=new adaptadorTitle(this, R.layout.fragment_item, rowItems);
		
		
		// Assign adapter to ListView
		 listView.setAdapter(adapter);
		 listView.setBackgroundResource(R.drawable.listaredondeada);
		 listView.setOnItemClickListener(new OnItemClickListener() {


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
          int itemPosition     = arg2;
          
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
			Intent EjerciciosIntent=new Intent(getApplicationContext(), Ejercicios.class);
			startActivity(EjerciciosIntent);
			finish();
			break;			
			
		case 4:
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
	
	
	
	
	
	private void InicioResultados(){

		
		
		
		LinearLayout ll=(LinearLayout)findViewById(R.id.layoutResultados);
		ll.setBackgroundResource(R.drawable.tabla);
		
		LinearLayout llBG1=(LinearLayout)findViewById(R.id.resultadosButtonG1);
		llBG1.setBackgroundResource(R.drawable.layoutredondeado);
		
		LinearLayout llBG2=(LinearLayout)findViewById(R.id.resultadosButtonG2);
		llBG2.setBackgroundResource(R.drawable.layoutredondeado);
		
		listView=(ListView)findViewById(R.id.listViewResul);
		CreaLista();
		
		alSelec=new ArrayList<Boolean>();
		serSelec=new ArrayList<Boolean>();
		
		//Captación de los botones

		semana=new Button(this);
		semana=(Button)findViewById(R.id.filSemana);
		semana.setSelected(true);
		
		mes=new Button(this);
		mes=(Button)findViewById(R.id.filMes);
		
		año=new Button(this);
		año=(Button)findViewById(R.id.filAnio);
		
		rRanking=(Button)findViewById(R.id.rRanking);
		rRanking.setSelected(true);
		rAlumno=(Button)findViewById(R.id.rAlumno);
		
		rRanking.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rRanking.setSelected(true);
				rAlumno.setSelected(false);
				radioSelec=0;
			}
		});
		
		rAlumno.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rRanking.setSelected(false);
				rAlumno.setSelected(true);
				radioSelec=1;
			}
		});
		

		
		mGrafica=findViewById(R.id.ResulGrafica);
		mTabla=findViewById(R.id.MuestraTabla);
		mXLS=findViewById(R.id.ResulXLS);
		
		
		semana.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fecha=Periodo.Semana;
				//semana.setBackgroundColor(getResources().getColor(R.color.tabla1));
				//mes.setBackgroundColor(getResources().getColor(R.color.white));
				//año.setBackgroundColor(getResources().getColor(R.color.white));
				semana.setSelected(true);
				mes.setSelected(false);
				año.setSelected(false);
				
			}
		});
		
		
		mes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fecha=Periodo.Mes;
				semana.setSelected(false);
				mes.setSelected(true);
				año.setSelected(false);
			}
		});
		
		año.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fecha=Periodo.SeisMeses;
				semana.setSelected(false);
				mes.setSelected(false);
				año.setSelected(true);
			}
		});
		
		tlAl=(TableLayout)findViewById(R.id.ResulTablaAl);
		tlSer=(TableLayout)findViewById(R.id.ResulTablaSer);
		
		TableRow rowAl=new TableRow(this);
		TableRow rowSer=new TableRow(this);
		
		rowAl.setBackgroundResource(R.color.degradado1r);
		rowSer.setBackgroundResource(R.color.degradado1r);
		

		
		//Declaracion tableRowParams
		
    	TableRow.LayoutParams tableRowParams=
       		  new TableRow.LayoutParams
       (TableRow.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);




       		int leftMargin=5;
       		int topMargin=5;
       		int rightMargin=5;
       		int bottomMargin=5;

       		tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
       		tableRowParams.gravity=Gravity.CENTER_VERTICAL;
   
       		
       
       		
        	TableRow.LayoutParams imageParams=
             		  new TableRow.LayoutParams
             (TableRow.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
        	//(TableRow.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT);


			imageParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
			imageParams.gravity=Gravity.CENTER_VERTICAL;
			imageParams.height=40;
			imageParams.width=40;
			   
			
			//Fin declaración TableRowParams
			
			
		TextView tit1,tit2,tit3,tit4;
		
		ImageView imgtit1,imgtit2;
		
		imgtit1=new ImageView(this);
		imgtit2=new ImageView(this);
		

		
//		imgtit1.getLayoutParams().height = 20;
//		imgtit1.getLayoutParams().width = 20;
		
//		imgtit2.getLayoutParams().height = 20;
//		imgtit2.getLayoutParams().width = 20;
		
		
		
        tit1=new TextView(this);
        tit2=new TextView(this);
        tit3=new TextView(this);
        tit4=new TextView(this);
        

		imgtit1.setImageResource(R.drawable.ic2);
		imgtit2.setImageResource(R.drawable.ic3);
		
        imgtit1.setLayoutParams(imageParams);
        imgtit2.setLayoutParams(imageParams);
        
      	 tit1.setText("Alumno");
       	 tit1.setLayoutParams(tableRowParams);
       	 tit1.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
       	 
       	 tit2.setText("");
       	 tit2.setLayoutParams(tableRowParams);
       	 tit2.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
       	 
       	 tit3.setText("Serie");
       	 tit3.setLayoutParams(tableRowParams);
       	 tit3.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
       	 
       	 tit4.setText("");
       	 tit4.setLayoutParams(tableRowParams);
       	 tit4.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
       	 
		rowAl.addView(imgtit1);
		rowAl.addView(tit1);
		rowSer.addView(imgtit2);
		rowSer.addView(tit3);
		
		
		
		tlAl.addView(rowAl);
		tlSer.addView(rowSer);
		
		
		AlumnoDataSource ads=new AlumnoDataSource(this);
		SerieEjerciciosDataSource seds=new SerieEjerciciosDataSource(this);
		ads.open();
		seds.open();
		final List<Alumno> la=ads.getAllAlumnos();
		final List<SerieEjercicios> lse=seds.getAllSeriesEjercicios();
		ads.close();
		seds.close();
		
		
		//Añadir campo todos
		rowAl=new TableRow(this);
		rowSer=new TableRow(this);
		
		rowAl.setBackgroundResource(R.color.tabla_resaltado);
		rowSer.setBackgroundResource(R.color.tabla_resaltado);
		
		CheckBox cbTodosAlumnos=new CheckBox(this);
		TextView tvTodosAlumnos=new TextView(this);
		CheckBox cbTodasSeries=new CheckBox(this);
		TextView tvTodasSeries=new TextView(this);
				
		cbTodosAlumnos.setLayoutParams(tableRowParams);
		tvTodosAlumnos.setLayoutParams(tableRowParams);
		cbTodasSeries.setLayoutParams(tableRowParams);
		tvTodasSeries.setLayoutParams(tableRowParams);
		
		rowAl.addView(cbTodosAlumnos);
		tvTodosAlumnos.setText("Todos los alumnos");
		tvTodosAlumnos.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
		rowAl.addView(tvTodosAlumnos);
		
		rowSer.addView(cbTodasSeries);
		tvTodasSeries.setText("Todas las Series");
		tvTodasSeries.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
		rowSer.addView(tvTodasSeries);
		
		cbTodosAlumnos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox micb=(CheckBox)v;
				if (micb.isChecked())
					for(int i=0;i<la.size();i++){
						 CheckBox cb = (CheckBox)((TableRow)tlAl.getChildAt(i+2)).getChildAt(0);
						 cb.setChecked(true);
						 cb.setEnabled(false);
						 alSelec.set(i, true);
					}
				else
					for(int i=0;i<la.size();i++){
						 CheckBox cb = (CheckBox)((TableRow)tlAl.getChildAt(i+2)).getChildAt(0);
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
				CheckBox micb2=(CheckBox)v;
				if (micb2.isChecked())
					for(int i=0;i<lse.size();i++){
						 CheckBox cb2 = (CheckBox)((TableRow)tlSer.getChildAt(i+2)).getChildAt(0);
						 cb2.setChecked(true);
						 cb2.setEnabled(false);
						 serSelec.set(i, true);
					}
				else
					for(int i=0;i<lse.size();i++){
						 CheckBox cb2 = (CheckBox)((TableRow)tlSer.getChildAt(i+2)).getChildAt(0);
						 cb2.setChecked(false);
						 cb2.setEnabled(true);
						 serSelec.set(i, false);
					}
			}
		});
		
		
		tlAl.addView(rowAl);
		tlSer.addView(rowSer);

		
		for(int i=0;i<la.size();i++){
			 rowAl=new TableRow(this);
			 rowAl.setTag(i);
	       	 if(i%2==0)
	           	 rowAl.setBackgroundResource(R.drawable.tablerowsel);
	       	 else
	           	 rowAl.setBackgroundResource(R.drawable.tablerowsel2);
	       	 
	            rowAl.setLayoutParams(new LayoutParams(
	                    LayoutParams.FILL_PARENT,
	                    LayoutParams.WRAP_CONTENT)); 
	            
	         TextView alum=new TextView(this);
	         CheckBox cb=new CheckBox(this);
	         cb.setLayoutParams(tableRowParams);
        	 //alum.setText(la.get(i).getNombre()+" "+la.get(i).getApellidos());
        	 alum.setText(la.get(i).getApellidos()+", "+la.get(i).getNombre());
        	 alum.setLayoutParams(tableRowParams);
           	 alum.setTextAppearance(getApplicationContext(), R.style.TextoTablaResultados);
        	 rowAl.addView(cb);
	         rowAl.addView(alum);
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

	         tlAl.addView(rowAl);
		}
		
		
		//Series
		
		
		for(int i=0;i<lse.size();i++){
			 rowSer=new TableRow(this);
			 rowSer.setTag(i);
	       	 if(i%2==0)
	           	 rowSer.setBackgroundResource(R.drawable.tablerowsel);
	       	 else
	           	 rowSer.setBackgroundResource(R.drawable.tablerowsel2);
	       	 
	            rowSer.setLayoutParams(new LayoutParams(
	                    LayoutParams.FILL_PARENT,
	                    LayoutParams.WRAP_CONTENT)); 
	            
	         CheckBox cb2=new CheckBox(this);
	         cb2.setLayoutParams(tableRowParams);
	         TextView serie=new TextView(this);
        	 serie.setText(lse.get(i).getNombre());
        	 serie.setLayoutParams(tableRowParams);
           	 serie.setTextAppearance(getApplicationContext(), R.style.TextoTablaResultados);
	         rowSer.addView(cb2);
        	 rowSer.addView(serie);
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
         


	         tlSer.addView(rowSer);
		}
		
		
		//Fin series
		
		
		mGrafica.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent graficaIntent=new Intent(getApplicationContext(), Graficas.class);

				
				//RadioButton
				//RadioGroup rg=(RadioGroup)findViewById(R.id.radioGroup);		
				//int radioButtonID = rg.getCheckedRadioButtonId();
				//View radioButton = rg.findViewById(radioButtonID);
				//int radioSelec = rg.indexOfChild(radioButton);
				//int radioSelec=0;
				
				
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
				/*RadioGroup rg=(RadioGroup)findViewById(R.id.radioGroup);		
				int radioButtonID = rg.getCheckedRadioButtonId();
				View radioButton = rg.findViewById(radioButtonID);
				radioSelec = rg.indexOfChild(radioButton);*/

				
				
				
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
						if(radioSelec==0)
							tipoExport=1;
						if(radioSelec==1){
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
