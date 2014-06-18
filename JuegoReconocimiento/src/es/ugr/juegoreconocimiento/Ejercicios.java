package es.ugr.juegoreconocimiento;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;


import es.ugr.adaptadores.RowItemTitle;
import es.ugr.adaptadores.adaptadorTitle;
import es.ugr.objetos.*;
import es.ugr.parserXML.EjercicioParser;
import es.ugr.parserXML.EjerciciosMarker;
import es.ugr.basedatos.*;
import es.ugr.juegoreconocimiento.R;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog.Builder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewDebug.IntToString;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;



/**
 * @author Namir Sayed-Ahmad Baraza
 * @mail namirsab@gmail.com
 *
 */
public class Ejercicios extends Activity {
	 private ListView listView;
	 private TableLayout tablaEjercicios;
	 private Context micontexto;
	 private Dialog dialogo;
	 private List<Ejercicio> le;
	 private EjercicioDataSource eds;
	 private ScrollView scrollejer;
	 private View  ImportarEj;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
	    getActionBar().setCustomView(R.layout.mibarraejer);
	    

		setContentView(R.layout.ejercicios);
		
	    ImageView principal=(ImageView)findViewById(R.id.principalEj);
	    principal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	    
	    
		ImportarEj=findViewById(R.id.ImportarEj);
		scrollejer=(ScrollView)findViewById(R.id.scrollEjer);
		scrollejer.setBackgroundResource(R.drawable.tabla);
		listView=(ListView)findViewById(R.id.listViewEjer);
		
		
		
		
        tablaEjercicios=(TableLayout)findViewById(R.id.tabla_ejer);
        //tablaEjercicios.setBackgroundDrawable(getResources().getDrawable(R.drawable.tabla));
        eds=new EjercicioDataSource(this);
        eds.open();
		CreaLista();
		CreaTablaEjer();
        //final Context micontexto=this;   
		
		
		ImportarEj.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				RetrieveFeed task = new RetrieveFeed();
			    task.execute("https://dl.dropboxusercontent.com/u/123539/parkingpositions.xml");
			    //task.execute("https://www.dropbox.com/s/8wrw1jwpndcl1zk/primer.xml");
			}
		});

	}
	
	
	
	
	
	
	/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		getActionBar().setHomeButtonEnabled(true);
	switch(item.getItemId()) {
	    case android.R.id.home:
	    	finish();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}*/
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.ejercicios, menu);
		//return super.onCreateOptionsMenu(menu);
		return true;
	}*/
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		eds.close();
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
	
	
	
	
		
	private void CreaTablaEjer(){

		tablaEjercicios.removeAllViews();
        TableRow row;
        ImageView f1;
        TextView t2,t3,tit1,tit2,tit3;
        le=eds.getAllEjercicios();
        //Cabecera
        
        row = new TableRow(this);
        
       
        //row.setBackgroundColor(getResources().getColor(R.color.tabla2));
        row.setBackgroundResource(R.color.degradado1r);
        
        
        
        
        
        row.setLayoutParams(new LayoutParams(
        LayoutParams.FILL_PARENT,
        LayoutParams.WRAP_CONTENT)); 
        
    	TableRow.LayoutParams tableRowParams=
         		  new TableRow.LayoutParams
         (TableRow.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
    	//(TableRow.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT);



         		int leftMargin=20;
         		int topMargin=0;
         		int rightMargin=20;
         		int bottomMargin=0;

         		tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
         		tableRowParams.gravity=Gravity.CENTER_VERTICAL;
      //   		tableRowParams.weight=1;
        
        
        tit1=new TextView(this);
        tit2=new TextView(this);
        tit3=new TextView(this);
        
   	 tit1.setText("");
   	 tit1.setLayoutParams(tableRowParams);
   	 tit1.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
   	 
       
   	 tit2.setText("Nombre");
   	 tit2.setLayoutParams(tableRowParams);
   	 tit2.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
   	 
   	 tit3.setText("TºEstimado");
   	 tit3.setLayoutParams(tableRowParams);
   	 tit3.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
   	 

   	 
   	 row.addView(tit1);
   	 row.addView(tit2);
   	 row.addView(tit3);

   	
   	tablaEjercicios.addView(row, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT)); 
    
   	
   	
   	
   	
        //Cada ejercicio
        for(int i=0;i<le.size();i++){
       	 row=new TableRow(this);
       	 row.setTag(i);
       	// row.setId(i);
       	 if(i%2==0)
           	 row.setBackgroundResource(R.drawable.tablerowsel);
       	 else
           	 row.setBackgroundResource(R.drawable.tablerowsel2);
       	 
            row.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT)); 
            
            

           		
            
            
       	 f1=new ImageView(this);
       	 f1.setImageResource(R.drawable.ic6);
       	 f1.setMaxHeight(10);
       	 f1.setMaxWidth(5);
      	 f1.setLayoutParams(tableRowParams);
       	 
       	 //f1.setLayoutParams();
       	 

             		
       	 
       	 
       	 t2=new TextView(this);
       	 t2.setText(le.get(i).getNombre());
       	 t2.setLayoutParams(tableRowParams);
       	 t2.setTextAppearance(getApplicationContext(), R.style.TextoTabla);
       	 
       	 /*
       	 Calendar c = Calendar.getInstance();
       	 SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss a");
       	 String strDate = sdf.format(c.getTime());*/
       	 
       	 t3=new TextView(this);
       	 t3.setText(String.valueOf(le.get(i).getDuracion()));
       	 t3.setTextAppearance(getApplicationContext(), R.style.TextoTabla);
       	 t3.setLayoutParams(tableRowParams);
       	 

       	 micontexto=this;
       	 

       	 
       	 //Si pulsa un TableRow
       	 OnClickListener miclicklistener= new OnClickListener() {
       		
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//mivista=v;
					
					TableRow tr=(TableRow)v;
					MostrarDescripcion((Integer)tr.getTag());
					//CrearModificarSeriesEjercicios(true,lse.get((Integer)tr.getTag()),false);


				}
			};
       	 row.setOnClickListener(miclicklistener);
       	
       	//Se añade a la fila del tableRow los componentes 
       	 row.addView(f1);
       	 row.addView(t2);
       	 row.addView(t3);


     
       	 tablaEjercicios.addView(row);

        }
       	
  
		
	}
	

	private void MostrarDescripcion(final int pos){
		
		//Obtener datos del table Row

		//final EjercicioDataSource eds=new EjercicioDataSource(micontexto);

		
		//Lanzar Dialog
		dialogo = new Dialog(micontexto);
		dialogo.setContentView(R.layout.dialogo_ejercicios);
		dialogo.setTitle(le.get(pos).getNombre());
		
		//Modificar elementos dentro del dialogo
		final EditText duracion=(EditText)dialogo.findViewById(R.id.DuracionEj);
		duracion.setText(String.valueOf(le.get(pos).getDuracion()));
		final TextView descripcion=(TextView)dialogo.findViewById(R.id.textDesc);
		descripcion.setText(le.get(pos).getDescripcion());
		
        final ObjetoDataSource ods=new ObjetoDataSource(dialogo.getContext());
        ods.open();
        
        Objeto obj=new Objeto();
		
		
		final TextView escenario=(TextView)dialogo.findViewById(R.id.textEscenario);
		escenario.setTextSize(20);
		
		String textEscenario=new String("");
		for(int i=0;i<le.get(pos).getObjetos().size();i++){
			obj=ods.getObjeto(le.get(pos).getObjetos().get(i));
			textEscenario=textEscenario+obj.getNombre();
			if(i!=le.get(pos).getObjetos().size()-1)
				textEscenario=textEscenario+" ,";
		}
		escenario.setText(textEscenario);
		//Table Layout dentro dialogo
		
		TableLayout tablaObjetos=(TableLayout)dialogo.findViewById(R.id.tablaDiaEjercicios);
		tablaObjetos.removeAllViews();


        TableRow filaObj;
        TextView te1,te2;
         
         //Cabecera
         
         filaObj = new TableRow(dialogo.getContext());

         
         
    	//Para cada ejercicio
    	
    	for(int j=0;j<le.get(pos).getObjetosReconocer().size();j++){
        	 filaObj=new TableRow(dialogo.getContext());
        	// row.setId(i);
        	 
        	 te1=new TextView(dialogo.getContext());
        	 te1.setText(String.valueOf(j+1));
        	 te1.setPadding(2, 0, 5, 0);
        	 te1.setTextSize(20);
        	 
        	 
        	 te2=new TextView(dialogo.getContext());
        	 obj=ods.getObjeto(le.get(pos).getObjetosReconocer().get(j));
        	 te2.setText(obj.getNombre());
        	 te2.setPadding(2, 0, 5, 0);
        	 te2.setTextSize(20);
        	 
        	 filaObj.addView(te1);
        	 filaObj.addView(te2);
        	 
        	 tablaObjetos.addView(filaObj);
    	}
		
    	ods.close();
       		
		WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
		lp.copyFrom(dialogo.getWindow().getAttributes());
		lp.width=WindowManager.LayoutParams.MATCH_PARENT;
		lp.height=WindowManager.LayoutParams.MATCH_PARENT;
		

		dialogo.show();
		dialogo.getWindow().setAttributes(lp);	
    		

		
	
		
		
		//Boton guardar
		ImageButton guardarSerie=(ImageButton)dialogo.findViewById(R.id.guardarDiaEj);
		guardarSerie.setBackgroundResource(R.drawable.selicono);
		guardarSerie.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				le.get(pos).setDuracion(Double.valueOf(duracion.getText().toString()));
				eds.modificaEjercicio(le.get(pos));
				Toast.makeText(getApplicationContext(),"Tiempo estimado modificado", Toast.LENGTH_LONG).show();
			}
		});
		


	}
	
	
	
	
	private class RetrieveFeed extends AsyncTask<String,Integer,Boolean> {
		
		public List<EjerciciosMarker> ListaEj;
		 
	    protected Boolean doInBackground(String... params) {
	 
	    	 EjercicioParser ejercicioparser = new EjercicioParser(params[0]);
	    	 ListaEj = ejercicioparser.parse();
	    	    
	 
	        return true;
	    }
	 

		}


}
