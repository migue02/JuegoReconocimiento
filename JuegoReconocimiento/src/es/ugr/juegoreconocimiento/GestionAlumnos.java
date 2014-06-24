package es.ugr.juegoreconocimiento;

import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import es.ugr.adaptadores.RowItemTitle;
import es.ugr.adaptadores.adaptadorTitle;

import es.ugr.juegoreconocimiento.R;

import android.app.ActionBar.LayoutParams;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import es.ugr.objetos.*;
import es.ugr.basedatos.*;
import es.ugr.objetos.TiposPropios.Sexo;

/**
 * @author Namir Sayed-Ahmad Baraza
 * @mail namirsab@gmail.com
 *
 */
public class GestionAlumnos extends Activity {
	 private ListView listView ;
	 private DatePickerDialog Fecha;
	 private TextView mFecha;
	 private AlumnoDataSource ads;
	 private TableLayout tablaAlumnos;
	 private List<Alumno>ls;
	 private Sexo sexo;
	 private Context micontexto;
	 private ScrollView scrollAl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
	    getActionBar().setCustomView(R.layout.mibarraalum);
		setContentView(R.layout.gestion_alumnos);
		
		ImageView principal=(ImageView)findViewById(R.id.principalAl);
	    principal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		

        ads=new AlumnoDataSource(this);
        ads.open();
        
		//Crea la listView
		listView=(ListView)findViewById(R.id.listViewResul);
		CreaLista();
         
		scrollAl=(ScrollView)findViewById(R.id.scrollAl);
		scrollAl.setBackgroundResource(R.drawable.tabla);
         //Fin List_View

         tablaAlumnos=(TableLayout)findViewById(R.id.tabla_alumnos);
         CreaTablaAlumnos();
        	
         
         micontexto=this;
        	//Si pulsa el botón de añadir, sacar Dialogo
        	
            View BotonAniadir=findViewById(R.id.aniadir_alumno);
            BotonAniadir.setBackgroundResource(R.drawable.selicono);
            final Context micontexto=this;
            BotonAniadir.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Alumno nuevo=new Alumno();
					CrearModificarAlumnos(true, nuevo);

					//FinAniadir
				}
			});
			
        	 
	 
         
         
         

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ads.close();
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
	
	
	
	
	private void CreaTablaAlumnos(){
        TableRow row;
        ImageView f1,f2;
        TextView t3,t4,t5,tit1,tit2,tit3,tit4,tit5;
        
        tablaAlumnos.removeAllViews();
        tablaAlumnos.setColumnStretchable(4, true);
        
        //Inicia dataSource

        //Actualiza lista de alumnos         
        ls=ads.getAllAlumnos();
        
        //Cabecera
        
        
        row = new TableRow(this);
        
       
        row.setBackgroundColor(getResources().getColor(R.color.tituloTabla));
        row.setLayoutParams(new LayoutParams(
        LayoutParams.FILL_PARENT,
        LayoutParams.WRAP_CONTENT)); 
 
        
        TableRow.LayoutParams tableRowParams=
         		  new TableRow.LayoutParams
         (TableRow.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
    
     		int leftMargin=20;
     		int topMargin=5;
     		int rightMargin=20;
     		int bottomMargin=5;

     		tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
     		tableRowParams.gravity=Gravity.CENTER_VERTICAL;
        
        
            TableRow.LayoutParams ImgParams=
           		  new TableRow.LayoutParams
           (TableRow.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);

       		ImgParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
       		ImgParams.gravity=Gravity.CENTER_VERTICAL;
       		ImgParams.width=70;
       		ImgParams.height=70;
       		
            TableRow.LayoutParams tableRowEliminar=
           		  new TableRow.LayoutParams
           (TableRow.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
       		tableRowEliminar.width=70;
       		tableRowEliminar.height=70;

       		tableRowEliminar.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
       		tableRowEliminar.gravity=Gravity.RIGHT;
       		
       		
        
        tit1=new TextView(this);
        tit2=new TextView(this);
        tit3=new TextView(this);
        tit4=new TextView(this);
        tit5=new TextView(this);
        
   	 tit1.setText("Sexo");
   	 tit1.setLayoutParams(tableRowParams);
  	 tit1.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
       
   	 tit2.setText("Nombre");
   	 tit2.setLayoutParams(tableRowParams);
  	 tit2.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
   	 
   	 tit3.setText("Apellidos");
   	 tit3.setLayoutParams(tableRowParams);
  	 tit3.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
   	 
   	 tit4.setText("Edad");
   	 tit4.setLayoutParams(tableRowParams);
  	 tit4.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
   	 
   	 tit5.setText("Borrar");
   	 //tit5.setLayoutParams(tableRowEliminar);
   	 tit5.setLayoutParams(tableRowParams);
   	 tit5.setGravity(Gravity.RIGHT);
  	 tit5.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
   	 
   	 row.addView(tit1);
   	 row.addView(tit2);
   	 row.addView(tit3);
   	 row.addView(tit4);
   	 row.addView(tit5);
   	 
   	tablaAlumnos.addView(row, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
        
        //Cada alumno
        //for(int i=0;i<nom.length;i++){
        for(int i=0;i<ls.size();i++){	 
       	 row=new TableRow(this);
       	 row.setTag(i);
       	if(i%2==0)
          	 row.setBackgroundResource(R.drawable.tablerowsel);
      	 else
          	 row.setBackgroundResource(R.drawable.tablerowsel2);
       	 
            row.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT)); 
            

       	 
       	 f1=new ImageView(this);
       	 switch (ls.get(i).getSexo()) {
		case Hombre:
			f1.setImageResource(R.drawable.boy);
			break;
		case Mujer:
			f1.setImageResource(R.drawable.girl);
			break;
		default:
			f1.setImageResource(R.drawable.varon);
			break;
		}
       	 
       	 f1.setLayoutParams(ImgParams);
       	 
       	 
       	 f2=new ImageView(this);
       	 f2.setImageResource(R.drawable.delmarron);
       	 f2.setLayoutParams(tableRowEliminar);
       	 //f2.setLayoutParams(ImgParams);
       	 f2.setBackgroundResource(R.drawable.selicono);

       	 
       	 
       	 t3=new TextView(this);
       	 //t3.setText(nom[i]);
       	 t3.setText(ls.get(i).getNombre());
       	t3.setLayoutParams(tableRowParams);
      	 t3.setTextAppearance(getApplicationContext(), R.style.TextoTablaSeries);
       	 
       	 
       	 t4=new TextView(this);
       	 t4.setText(ls.get(i).getApellidos());
       	 t4.setLayoutParams(tableRowParams);
      	 t4.setTextAppearance(getApplicationContext(), R.style.TextoTablaSeries);
       	 
       	 t5=new TextView(this);
       	 t5.setText(anios(ls.get(i).getFecha_nac_AsDate())+" Años");
       	 t5.setLayoutParams(tableRowParams);
      	 t5.setTextAppearance(getApplicationContext(), R.style.TextoTablaSeries);
       	 final Context context=this;
       	 
       	 
       	 //Si pulsa la Papelera
       	 f2.setOnClickListener(new OnClickListener() {
		
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					final TableRow trow=(TableRow)arg0.getParent();
					TextView tnom=(TextView)trow.getChildAt(1);
					TextView tape=(TextView)trow.getChildAt(2);
					AlertDialog.Builder alerta=new AlertDialog.Builder(context);
					alerta.setTitle("Eliminar");
					alerta.setMessage("Se eliminará el alumno: "+tnom.getText()+" "+tape.getText());
					alerta.setCancelable(false);
					alerta.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							boolean borra;
							borra=ads.borraAlumno(ls.get((Integer)trow.getTag()).getIdAlumno());
							if (borra==true){
							Toast.makeText(getApplicationContext(),"Borrado", Toast.LENGTH_LONG).show();
							CreaTablaAlumnos();
							}
						}
					});
					
					
					alerta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					
					alerta.show();
				}
			});
       	 
       	 
       	 
       	 //Si pulsa un TableRow
       	 OnClickListener miclicklistener= new OnClickListener() {
       		
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TableRow trow=(TableRow)v;
					final Alumno modifica=ls.get((Integer)trow.getTag());
					CrearModificarAlumnos(false, modifica);
					
					
					
					
					//Obtener datos del table Row
				}
			};
       	 row.setOnClickListener(miclicklistener);
       	 
       	 
       	 row.addView(f1);
       	 row.addView(t3);
       	 row.addView(t4);
       	 row.addView(t5);
       	 row.addView(f2);
       	tablaAlumnos.addView(row);
       	
        }
	}
	
		
	private class PickDate implements DatePickerDialog.OnDateSetListener {


		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			 view.updateDate(year, monthOfYear, dayOfMonth);
			 int mes=monthOfYear+1;
		        mFecha.setText(dayOfMonth+"/"+mes+"/"+year);
		        Fecha.hide();
		}
	    
	}
	
	
	
	private void CrearModificarAlumnos(final boolean insertar, final Alumno alumno){
	
		//Lanzar Dialog
		final Dialog dialog = new Dialog(micontexto);
		dialog.setContentView(R.layout.dialogo_alumnos);
		if (insertar==false)
			dialog.setTitle("Modificar Alumno");
		else
			dialog.setTitle("Crear Alumno");
		
		
		
		//Captacion de los componentes
		final Button GuardarDia, CancelarDialog;
		final EditText enombre;
		final EditText eapellidos;
		final EditText eobservaciones;
		final View chico,chica;
		final ImageView imPrincipal;
		TextView efecha;
		
		//Componentes del layout
		enombre=(EditText)dialog.findViewById(R.id.daNombre);
		eapellidos=(EditText)dialog.findViewById(R.id.daApellidos);
		efecha=(TextView)dialog.findViewById(R.id.MuestraFecha);
		eobservaciones=(EditText)dialog.findViewById(R.id.daObserva);					
		chico=dialog.findViewById(R.id.pulsaChico);
		chica=dialog.findViewById(R.id.pulsaChica);
		GuardarDia=(Button)dialog.findViewById(R.id.gAlumnos);
		CancelarDialog=(Button)dialog.findViewById(R.id.cAlumnos);
		imPrincipal=(ImageView)dialog.findViewById(R.id.AlumPrin);
		
		
		//Asignacion de los valores del tipo alumno
		enombre.setText(alumno.getNombre());
		eapellidos.setText(alumno.getApellidos());
		efecha.setText(alumno.getFecha_nac_AsStrign());
		eobservaciones.setText(alumno.getObservaciones());
		switch (alumno.getSexo()) {
		case Hombre:
			
			chico.setSelected(true);
			chica.setSelected(false);
			imPrincipal.setImageResource(R.drawable.boy);
			sexo=Sexo.Hombre;
			break;
		case Mujer:

			chica.setSelected(true);
			chico.setSelected(false);
			imPrincipal.setImageResource(R.drawable.girl);
			sexo=Sexo.Mujer;
			break;

		default:
			chico.setSelected(true);
			chica.setSelected(false);
			imPrincipal.setImageResource(R.drawable.boy);
			sexo=Sexo.Hombre;
			break;
		}

		

		
		//Controlador Fecha
		Button cFecha;
		
		
		cFecha=(Button)dialog.findViewById(R.id.CambiarFecha);
		mFecha=(TextView)dialog.findViewById(R.id.MuestraFecha);
		Fecha=null;
		cFecha.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Calendar dtTxt=null;
				String preExistingDate=(String)mFecha.getText().toString();
				 if(preExistingDate != null && !preExistingDate.equals("")){
		              StringTokenizer st = new StringTokenizer(preExistingDate,"/");
		                  String initialDate = st.nextToken();
		                  String initialMonth = st.nextToken();
		                  String initialYear = st.nextToken();
		                  if(Fecha == null)
		                  Fecha = new DatePickerDialog(arg0.getContext(),
		                                   new PickDate(),Integer.parseInt(initialYear),
		                                   Integer.parseInt(initialMonth)-1,
		                                   Integer.parseInt(initialDate));
		                  Fecha.updateDate(Integer.parseInt(initialYear),
		                                   Integer.parseInt(initialMonth)-1,
		                                   Integer.parseInt(initialDate));
				 } else {
		              dtTxt = Calendar.getInstance();
		              if(Fecha == null)
		              Fecha = new DatePickerDialog(arg0.getContext(),new PickDate(),dtTxt.get(Calendar.YEAR),dtTxt.get(Calendar.MONTH),
		                                                  dtTxt.get(Calendar.DAY_OF_MONTH));
		              Fecha.updateDate(dtTxt.get(Calendar.YEAR),dtTxt.get(Calendar.MONTH),
                              dtTxt.get(Calendar.DAY_OF_MONTH));
		          }
				 Fecha.show();
			}
		});
		
		//FechaF
		
		WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width=WindowManager.LayoutParams.MATCH_PARENT;
		lp.height=WindowManager.LayoutParams.MATCH_PARENT;
		
		//dialog.getWindow().setLayout(760, 480);
		dialog.show();
		dialog.getWindow().setAttributes(lp);

		
		GuardarDia.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//Guardar los cambios, primero en un Alumno, y luego en BD
				//alumno.setIdAlumno(modifica.getIdAlumno());
				alumno.setNombre(enombre.getText().toString());
				alumno.setApellidos(eapellidos.getText().toString());
				alumno.setObservaciones(eobservaciones.getText().toString());
				alumno.setSexo(sexo);
				SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
				Date fecha = null;
				try {

				fecha = formatoDelTexto.parse(mFecha.getText().toString());

				} catch (ParseException ex) {

				ex.printStackTrace();

				}
				alumno.setFecha_nac(fecha);
				
				
				//Si es modificar
				boolean correcto=false;
				if (insertar==false){
				correcto=ads.modificaAlumno(alumno);
				if (correcto==true)
					 Toast.makeText(getApplicationContext(),
			                 "Alumno modificado" , Toast.LENGTH_SHORT)
			                 .show();
				}else{
					//Si es insertar
					correcto=ads.createAlumno(alumno) != null;
				if (correcto==true)
						 Toast.makeText(getApplicationContext(),
				                 "Alumno creado" , Toast.LENGTH_SHORT)
				                 .show();
				}
				dialog.dismiss();
				CreaTablaAlumnos();
			}
		});
		
		CancelarDialog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				  dialog.dismiss();
			}
		});
		//listener Botones e imagen Sexo
		
		chico.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				chico.setSelected(true);
				chica.setSelected(false);
				imPrincipal.setImageResource(R.drawable.boy);
				sexo=Sexo.Hombre;
				
			}
		});
		
		chica.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				chica.setSelected(true);
				chico.setSelected(false);
				imPrincipal.setImageResource(R.drawable.girl);
				sexo=Sexo.Mujer;
				
			}
		});
		
		//Toast.makeText(getApplicationContext(),str1, Toast.LENGTH_LONG).show();

		
	}
		
	private int anios(Date d) {
		
	    Calendar dob = Calendar.getInstance();
	    dob.setTime(d);
	    Calendar today = Calendar.getInstance();
 
	    int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

	    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
	        age--; 
	    }

	    return age;  
	}


}
