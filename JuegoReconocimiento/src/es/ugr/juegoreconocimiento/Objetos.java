package es.ugr.juegoreconocimiento;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mobeta.android.dslv.DragSortListView;

import es.ugr.adaptadores.RowItemTitle;
import es.ugr.adaptadores.adaptadorTitle;
import es.ugr.objetos.*;
import es.ugr.parserXML.EjercicioParser;
import es.ugr.parserXML.EjerciciosMarker;
import es.ugr.utilidades.Sonidos;
import es.ugr.basedatos.*;
import es.ugr.juegoreconocimiento.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserDialog;




/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */

public class Objetos extends Activity {
	 private ListView listView;
	 private Context micontexto;
	 private Dialog dialogo;
	 private List<Objeto> lo;
	 private ObjetoDataSource ods;
	 private adaptadorSelObj adaptador;
	 private ListView lv;
	 private Sonidos sonidos;
	// private Typeface font;	
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
	    getActionBar().setCustomView(R.layout.mibarraejer);

		setContentView(R.layout.objetos);
		

		
	    ImageView principal=(ImageView)findViewById(R.id.principalEj);
	    principal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		sonidos=new Sonidos(this);
	    listView=(ListView)findViewById(R.id.listViewObj);
		
        ods=new ObjetoDataSource(this);
        ods.open();
		CreaLista();
		
		lv = (ListView) findViewById(R.id.ListaSelObj); 

        lo=ods.getAllObjetos();
		
		
		

		CreaTablaObj(); 
		


	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
		ods.close();
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
		    //playerNavegacion.start();
		    sonidos.playNavegacion();
          
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
			Intent EjerciciosIntent=new Intent(getApplicationContext(), Objetos.class);
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
	
	
	private void CreaTablaObj(){
		
		micontexto=this;

        adaptador = new adaptadorSelObj(this, R.layout.drag_obj, lo);
       lv.setAdapter(adaptador);

        lv.setOnItemClickListener(new OnItemClickListener() {
        	
        	@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        			long arg3) {
        		// TODO Auto-generated method stub
        		int itemposicion=arg2;
        		//playerRow.start();
        		sonidos.playClickRow();
        		MostrarDescripcion(itemposicion);
        	}
		});
        
		
		
		
		
	}


	private void MostrarDescripcion(final int pos){
		/*	
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
				le.get(pos).setDuracion(Integer.parseInt(duracion.getText().toString()));
				eds.modificaEjercicio(le.get(pos));
				CreaTablaEjer();
			     
				Toast.makeText(getApplicationContext(),"Tiempo estimado modificado", Toast.LENGTH_LONG).show();
			}
		});
		

*/
	}
	
	
	


	
	public class adaptadorSelObj extends ArrayAdapter<Objeto>{

		
		Context context;
		
		public adaptadorSelObj(Context context, int resource, List<Objeto> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			this.context=context;
		}
		
		
		 private class ViewHolder {
			 ImageView img;
			 TextView nombre;
		    }
		 
		 
		 
		    public View getView(int position, View convertView, ViewGroup parent) {
		        ViewHolder holder = null;
		        Objeto rowItem = getItem(position);
		         
		        LayoutInflater mInflater = (LayoutInflater) context
		                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		        if (convertView == null) {
		            convertView = mInflater.inflate(R.layout.drag_obj, null);
		          
		            //convertView.setBackgroundDrawable(convertView.getResources().getDrawable(R.drawable.listaredondeada));
		            holder = new ViewHolder();
		           
		            holder.img = (ImageView) convertView.findViewById(R.id.imgSelObj);
		            holder.nombre = (TextView) convertView.findViewById(R.id.NombSelObj);
		         
		            convertView.setTag(holder);
		        } else
		            holder = (ViewHolder) convertView.getTag();
		        if (rowItem.getImagen()!=null)
		        	holder.img.setImageBitmap(rowItem.getImagen());
		        else
		        	holder.img.setImageResource(R.drawable.ic6);
		        
		        holder.nombre.setText(rowItem.getNombre());

		         
		        return convertView;
		    }
		
		

	}

	
	

}
