package es.ugr.juegoreconocimiento;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.mobeta.android.dslv.DragSortListView;

import es.ugr.adaptadores.RowItemTitle;
import es.ugr.adaptadores.adaptadorTitle;
import es.ugr.objetos.*;
import es.ugr.parserXML.EjercicioParser;
import es.ugr.parserXML.EjerciciosMarker;
import es.ugr.utilidades.Sonidos;
import es.ugr.utilidades.Utilidades;
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
import es.ugr.bdremota.*;



/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */

public class Ejercicios extends Activity {
	 private ListView listView;
	 private Context context;
	 private Dialog dialogo;
	 private List<Ejercicio> le;
	 private EjercicioDataSource eds;
	 private View  ImportarEj,SincronizarEj;
	 private adaptadorSelEj2 adaptador;
	 private DragSortListView lv;
	 private Sonidos sonidos;
	// private Typeface font;	
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
	    getActionBar().setCustomView(R.layout.mibarraejer);

		setContentView(R.layout.ejercicios);
		context=this;

		
	    ImageView principal=(ImageView)findViewById(R.id.principalEj);
	    principal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	    
	    
		ImportarEj=findViewById(R.id.ImportarEj);
		SincronizarEj=findViewById(R.id.SincronizarEj);
		listView=(ListView)findViewById(R.id.listViewAlum);
		
        eds=new EjercicioDataSource(this);
        eds.open();
		CreaLista();
		
		lv = (DragSortListView) findViewById(R.id.ListaSelEj); 

        lv.setDropListener(onDrop);
        lv.setRemoveListener(onRemove);
        lv.setDragScrollProfile(ssProfile);

		
		
		
        
		CreaTablaEjer(); 
		
	
		ImportarEj.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				final Dialog dialogo=new Dialog(Ejercicios.this);
				dialogo.setTitle("Importar Ejercicio");
				dialogo.setContentView(R.layout.dialogo_importar_ej);
				
				final EditText etFich;
				final EditText etURL;
				
				etFich=(EditText)dialogo.findViewById(R.id.editTextFich);
				etURL=(EditText)dialogo.findViewById(R.id.editTextURL);
				etURL.setText("http://192.168.1.103/bd_reconocimiento/XML/segun.xml");
				
				Button impor,cancelar;
				final Button selFich;
				final RadioButton rb1;
				final RadioButton rb2;
				rb1=(RadioButton)dialogo.findViewById(R.id.radioButtonExp1);
				rb2=(RadioButton)dialogo.findViewById(R.id.radioButtonExp2);
				
				impor=(Button)dialogo.findViewById(R.id.aImportar);
				cancelar=(Button)dialogo.findViewById(R.id.cImportar);
				selFich=(Button)dialogo.findViewById(R.id.BotonSelFich);
				
				selFich.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						FileChooserDialog dialog = new FileChooserDialog(dialogo.getContext());
			    		dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
							
							@Override
							public void onFileSelected(Dialog source, File folder, String name) {
								// TODO Auto-generated method stub
					             source.hide();
					             Toast toast = Toast.makeText(source.getContext(), "File created: " + folder.getName() + "/" + name, Toast.LENGTH_LONG);
					             toast.show();
							}
							
							@Override
							public void onFileSelected(Dialog source, File file) {
								// TODO Auto-generated method stub
					             source.hide();
					             Toast toast = Toast.makeText(source.getContext(), "Fichero Seleccionado: " + file.getPath(), Toast.LENGTH_LONG);
					             toast.show();
					             etFich.setText(file.getPath());
								// RetrieveFeed task = new RetrieveFeed();
								 //task.execute(file.getPath());
							}
						});
			    		
			    		dialog.show();
					}
				});
				
				impor.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						RetrieveFeed task = new RetrieveFeed();
						if(rb1.isChecked()){
							 
							 task.execute(etFich.getText().toString(),"Fichero");
						}
						if(rb2.isChecked()){
							 task.execute(etURL.getText().toString(),"URL");
						}
						dialogo.dismiss();
						
					}
				});
				
				cancelar.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialogo.dismiss();
					}
				});
				
				rb1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						rb2.setChecked(false);
					}
				});
				
				rb2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						rb1.setChecked(false);
					}
				});

				
	    		// Show the dialog.
	            dialogo.show();              
			}
		});
		
		SincronizarEj.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Utilidades.hasInternetConnection(context))
					new SincronizarEjercicios(context).execute();
				else{
					Toast toast=Toast.makeText(context, "No hay conexión", Toast.LENGTH_LONG);
					toast.show();
				}
		   }
		});
		sonidos=new Sonidos(this);

	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
		eds.close();
	}
	

	private void CreaLista(){
		String[] titulos = new String[] { "Menú Principal","Gestión Alumnos","Resultados/Estadísticas"," Ejercicios","Serie Ejercicios", "Objetos"
        };
		Integer[] images=new Integer[]{R.drawable.anterior,R.drawable.ic2,R.drawable.ic1,R.drawable.ic6,R.drawable.ic3,R.drawable.objeto};

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
			Intent EjerciciosIntent=new Intent(getApplicationContext(), Ejercicios.class);
			startActivity(EjerciciosIntent);
			finish();
			break;			
			
		case 4:
			Intent SeriesIntent=new Intent(getApplicationContext(), SeriesEjercicios.class);
			startActivity(SeriesIntent);
			finish();
			break;
		
		case 5:
			Intent ObjetosIntent=new Intent(getApplicationContext(), Objetos.class);
			startActivity(ObjetosIntent);
			finish();
			break;

		default:
			break;
		}

	}

});
	}
	
	
	public void CreaTablaEjer(){
		
        le=eds.getAllEjercicios();
		
		Utilidades.creaCarpetas(context);
        adaptador = new adaptadorSelEj2(this, R.layout.drag_ej, le);
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
			
		//Lanzar Dialog
		dialogo = new Dialog(context);
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
		


	}
	
	
	
	
	private class RetrieveFeed extends AsyncTask<String,Integer,Boolean> {
		
		public List<EjerciciosMarker> ListaEj;
		 
	    protected Boolean doInBackground(String... params) {
	 
	    	 EjercicioParser ejercicioparser = new EjercicioParser(params[0],params[1]);
	    	 ListaEj = ejercicioparser.parse();
	    	    
	 
	        return true;
	    }
	 

	    @Override
	    protected void onPostExecute(Boolean result) {
	    	// TODO Auto-generated method stub
	    	super.onPostExecute(result); 	
	    	for(int i=0;i<ListaEj.size();i++){
				// eds.createEjercicio(ListaEj.get(i).getNombre(), ListaIdObjetos(ListaEj.get(i).getEscenario()), ListaEj.get(i).getDescripcion(), ListaEj.get(i).getDuracion(),ListaIdObjetos(ListaEj.get(i).getReconocer()));
				 eds.createEjercicio(ListaEj.get(i).getNombre(), new Date(), ListaEj.get(i).getEscenario(), ListaEj.get(i).getDescripcion(), ListaEj.get(i).getDuracion(), ListaEj.get(i).getReconocer(), ListaEj.get(i).getSonidoDescripcion());
			 }

			 Toast toast2 = Toast.makeText(getApplicationContext(), "Creados "+String.valueOf(ListaEj.size())+" ejercicios.", Toast.LENGTH_LONG);
			 toast2.show();
			 CreaTablaEjer();
			 

	    }
	    
		}



	
	
    private DragSortListView.DropListener onDrop =
	        new DragSortListView.DropListener() {
	            @Override
	            public void drop(int from, int to) {
	                //Ejercicio item=adapterselej.getItem(from);
	                Ejercicio item=adaptador.getItem(from);
	                
	                adaptador.notifyDataSetChanged();
	                adaptador.remove(item);
	                adaptador.insert(item, to);
	                

	                
	        		for(int i=0;i<le.size();i++)
	        			eds.actualizaOrden(le.get(i), i+1);
	        		
	        		sonidos.playDrop();
	            }
	        };

	    private DragSortListView.RemoveListener onRemove = 
	        new DragSortListView.RemoveListener() {
	            @Override
	            public void remove(int which) {
	            	sonidos.playRemove();
	            	adaptador.remove(adaptador.getItem(which));
	        		for(int i=0;i<le.size();i++)
	        			eds.actualizaOrden(le.get(i), i+1);
	            	//recargaDuracion();
	            }
	        };

	    private DragSortListView.DragScrollProfile ssProfile =
	        new DragSortListView.DragScrollProfile() {
	            @Override
	            public float getSpeed(float w, long t) {
	                if (w > 0.8f) {
	                    // Traverse all views in a millisecond
	                    return ((float) adaptador.getCount()) / 0.001f;
	                } else {
	                    return 10.0f * w;
	                }
	            }
	        };
	
	
	public class adaptadorSelEj2 extends ArrayAdapter<Ejercicio>{

		
		Context context;
		
		public adaptadorSelEj2(Context context, int resource, List<Ejercicio> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			this.context=context;
		}
		
		
		 private class ViewHolder {
			 ImageView img;
			 TextView nombre;
			 TextView duracion;
			 ImageView drag;
			 //TextView texto;
		    }
		 
		 
		 
		    public View getView(int position, View convertView, ViewGroup parent) {
		        ViewHolder holder = null;
		        Ejercicio rowItem = getItem(position);
		         
		        LayoutInflater mInflater = (LayoutInflater) context
		                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		        if (convertView == null) {
		            convertView = mInflater.inflate(R.layout.drag_ej, null);
		          
		            //convertView.setBackgroundDrawable(convertView.getResources().getDrawable(R.drawable.listaredondeada));
		            holder = new ViewHolder();
		           
		            holder.img = (ImageView) convertView.findViewById(R.id.imgSelEj);
		            holder.nombre = (TextView) convertView.findViewById(R.id.NombSelEj);
		            holder.duracion=(TextView) convertView.findViewById(R.id.DurSelEj);
		            holder.drag=(ImageView)convertView.findViewById(R.id.midrag);
		         
		            convertView.setTag(holder);
		        } else
		            holder = (ViewHolder) convertView.getTag();
		                
		        holder.img.setImageResource(R.drawable.ic6);
		        holder.nombre.setText(rowItem.getNombre());
		        holder.duracion.setText(String.valueOf(rowItem.getDuracion())+ " minuto(s)");
		        holder.drag.setImageResource(R.id.drag_handle);

		         
		        return convertView;
		    }
		
		

	}


	

}
