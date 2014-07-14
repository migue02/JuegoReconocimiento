package es.ugr.juegoreconocimiento;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.ugr.adaptadores.RowItemTitle;
import es.ugr.adaptadores.adaptadorSelEj;
import es.ugr.adaptadores.adaptadorTitle;
import es.ugr.basedatos.*;
import es.ugr.objetos.*;
import es.ugr.utilidades.Sonidos;

import com.mobeta.android.dslv.DragSortListView;

import es.ugr.juegoreconocimiento.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */
public class SeriesEjercicios extends Activity {
	 private ListView listView;
	 private SerieEjerciciosDataSource seds;
	 private List<SerieEjercicios> lse;
	 private Context micontexto;
	 private Dialog dialogo;
	 private List<Ejercicio> le;
	 private View añadirSerie;
	 private EjercicioDataSource eds;
	 private EditText duracion;
	 private adaptadorSelEj adapterselej;
	 private Sonidos sonidos;
	 private DragSortListView lv;
	 private adaptadorSelSer adaptador;
	 private List<Ejercicio> leaux;
	 private SerieEjercicios serie;
	 //private ArrayAdapter<String> adapterselej;
	 

	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
	    getActionBar().setCustomView(R.layout.mibarraser);
		
	    ImageView principal=(ImageView)findViewById(R.id.principalSer);
	    principal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	    
		setContentView(R.layout.seriejercicios);
	
		sonidos=new Sonidos(this);
		listView=(ListView)findViewById(R.id.listViewSer);
        seds=new SerieEjerciciosDataSource(this);
        añadirSerie=findViewById(R.id.aniadir_serl);
        añadirSerie.setBackgroundResource(R.drawable.selicono);
        seds.open();
        eds=new EjercicioDataSource(this);
        eds.open();
        
		lv = (DragSortListView) findViewById(R.id.ListaSelSer); 

        lv.setDropListener(onDropSer);
        lv.setRemoveListener(onRemoveSer);
        lv.setDragScrollProfile(ssProfileSer);
        lse=seds.getAllSeriesEjercicios();
        		
	
        
		CreaLista();
		CreaTablaSeries();
		
		añadirSerie.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				SerieEjercicios nuevo=new SerieEjercicios();
//				lse.add(nuevo);
				CrearModificarSeriesEjercicios(0, true);
			}
		});
		
        //final Context micontexto=this;          

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		eds.close();
		seds.close();
	}
	

	private void CreaLista(){
		String[] titulos = new String[] { "Menú Principal","Gestión Alumnos","Resultados/Estadísticas"," Ejercicios","Serie Ejercicios", "Objetos"
        };
		Integer[] images=new Integer[]{R.drawable.anterior,R.drawable.ic2,R.drawable.ic1,R.drawable.ic6,R.drawable.ic3,R.drawable.ic5};

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
	
	
	
	
	
	private void CreaTablaSeries(){

		
		micontexto=this;

        adaptador = new adaptadorSelSer(this, R.layout.drag_ser, lse);
        lv.setAdapter(adaptador);
        lv.setOnItemClickListener(new OnItemClickListener() {
        	
        	@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        			long arg3) {
        		// TODO Auto-generated method stub
        		int itemposicion=arg2;
        		//playerRow.start();
        		sonidos.playClickRow();
        		CrearModificarSeriesEjercicios(itemposicion, false);
        		//MostrarDescripcion(itemposicion);
        	}
        	
		});
		
		
		
		
		/*
		tablaSeries.removeAllViews();
		tablaSeries.setColumnStretchable(4, true);
		
        TableRow row;
        ImageView fim,fbor;
        TextView t2,t3,t4,tit1,tit2,tit3,tit4,tit5;
        lse=seds.getAllSeriesEjercicios();
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
        
   	 tit1.setText("");
   	 tit1.setLayoutParams(tableRowParams);
   	 tit1.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
       
   	 tit2.setText("Serie");
   	 tit2.setLayoutParams(tableRowParams);
   	 tit2.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
   	 
   	 tit3.setText("TºEstimado");
   	 tit3.setLayoutParams(tableRowParams);
   	 tit3.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
   	 
   	 tit4.setText("Última modificación");
   	 tit4.setLayoutParams(tableRowParams);
   	 tit4.setTextAppearance(getApplicationContext(), R.style.TituloTabla);

   	 
   	 tit5.setText("Borrar");
   	 tit5.setLayoutParams(tableRowParams);
   	 tit5.setGravity(Gravity.RIGHT);
   	 tit5.setTextAppearance(getApplicationContext(), R.style.TituloTabla);
   	 
   	 row.addView(tit1);
   	 row.addView(tit2);
   	 row.addView(tit3);
   	 row.addView(tit4);
   	 row.addView(tit5);

   	 
   	tablaSeries.addView(row, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT)); 
        
        //Cada serie ejercicios
        for(int i=0;i<lse.size();i++){
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
         
		 fim=new ImageView(this);
		 fim.setImageResource(R.drawable.ic3);
		 //fim.setLayoutParams(tableRowParams);
		 fim.setLayoutParams(ImgParams);   
            
       	 fbor=new ImageView(this);
       	 fbor.setImageResource(R.drawable.delmarron);
      	 //fbor.setLayoutParams(tableRowParams);
       	 //fbor.setLayoutParams(ImgParams);
       	 fbor.setLayoutParams(tableRowEliminar);
       	 fbor.setBackgroundResource(R.drawable.selicono);
       	 
       	 //f1.setLayoutParams();
       	 
       	 t2=new TextView(this);
       	 t2.setText(lse.get(i).getNombre());
       	 t2.setLayoutParams(tableRowParams);
       	 t2.setTextAppearance(getApplicationContext(), R.style.TextoTablaSeries);
       	 

       	 
       	 t3=new TextView(this);
       	 t3.setText(String.valueOf(lse.get(i).getDuracion()));
       	 t3.setLayoutParams(tableRowParams);
       	 t3.setTextAppearance(getApplicationContext(), R.style.TextoTablaSeries);
       	 
       	 
       	 t4=new TextView(this);
       	 t4.setText(lse.get(i).getFecha_modificacion_AsStrign());
       	 t4.setLayoutParams(tableRowParams);
       	 t4.setTextAppearance(getApplicationContext(), R.style.TextoTablaSeries);
       	 micontexto=this;
       	 
       	 
       	 //Si pulsa la Papelera
       	 fbor.setOnClickListener(new OnClickListener() {
		
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					final TableRow trow=(TableRow)arg0.getParent();
					TextView tnom=(TextView)trow.getChildAt(1);
					
					AlertDialog.Builder alerta=new AlertDialog.Builder(micontexto);
					alerta.setTitle("Eliminar");
					alerta.setMessage("Se eliminará la serie: "+tnom.getText());
					alerta.setCancelable(false);
					alerta.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							boolean borrado=false;
							borrado=seds.eliminarSerieEjercicios(lse.get((Integer)trow.getTag()).getIdSerie());
							if(borrado==true){
							 Toast.makeText(getApplicationContext(),"Borrado", Toast.LENGTH_LONG).show();
							 CreaTablaSeries();
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
					//mivista=v;
					TableRow tr=(TableRow)v;
					lse=seds.getAllSeriesEjercicios();
					
					
					//arrayEjer=lse.get((Integer)tr.getTag()).getEjercicios();
					
					CrearModificarSeriesEjercicios(true,lse.get((Integer)tr.getTag()),false);

					//Toast.makeText(getApplicationContext(),str1, Toast.LENGTH_LONG).show();
				}
			};
       	 row.setOnClickListener(miclicklistener);
       	
       	//Se añade a la fila del tableRow los componentes 
       	 row.addView(fim);
       	 row.addView(t2);
       	 row.addView(t3);
       	 row.addView(t4);
       	 row.addView(fbor);

       	tablaSeries.addView(row);
       	
       	
        }
       	
       	//Si pulsa el botón de añadir, sacar Dialogo
       	añadirSerie.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SerieEjercicios nuevaSerie=new SerieEjercicios();
				CrearModificarSeriesEjercicios(true, nuevaSerie,true);
			}
		});


		*/
		
	}
	
	
	
	
	private void CrearModificarSeriesEjercicios(final int pos,final boolean insertar){
		
		//Obtener datos del table Row

		//final EjercicioDataSource eds=new EjercicioDataSource(micontexto);

		

		
		
		//Lanzar Dialog
		dialogo = new Dialog(micontexto);
		dialogo.setContentView(R.layout.dialogo_series);
		
		serie=new SerieEjercicios();
		if (insertar==true){
			dialogo.setTitle("Insertar Serie");
			serie=new SerieEjercicios();
			}
		else{
			dialogo.setTitle("Modificar Serie");
			serie=lse.get(pos);
		}
		//Modificar elementos dentro del dialogo
		final EditText nomSerie=(EditText)dialogo.findViewById(R.id.NomSerie);
		nomSerie.setText(serie.getNombre());
		duracion=(EditText)dialogo.findViewById(R.id.Duracion);
		duracion.setText(String.valueOf(serie.getDuracion()));
		//Table Layout dentro dialogo
		
		
		final DragSortListView lvEj = (DragSortListView) dialogo.findViewById(R.id.ListaSelEj); 

        lvEj.setDropListener(onDropEj);
        lvEj.setRemoveListener(onRemoveEj);
        lvEj.setDragScrollProfile(ssProfileEj);
        
       
        leaux=new ArrayList<Ejercicio>();
       
        for(int i=0;i<serie.getEjercicios().size();i++){
        	leaux.add(eds.getEjercicios(serie.getEjercicios().get(i)));
        }

        
        adapterselej = new adaptadorSelEj(this, R.layout.blist_item_handle_right, leaux);
        

        lvEj.setAdapter(adapterselej);
        	
    		
		WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
		lp.copyFrom(dialogo.getWindow().getAttributes());
		lp.width=WindowManager.LayoutParams.MATCH_PARENT;
		lp.height=WindowManager.LayoutParams.MATCH_PARENT;
		
		//dialog.getWindow().setLayout(760, 480);
		dialogo.show();
		dialogo.getWindow().setAttributes(lp);	
    		
    	
		//Boton aniadir
		View aniadirEjSer=dialogo.findViewById(R.id.laniadir_ser);
		aniadirEjSer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				   le=eds.getAllEjercicios();
	               Toast.makeText(getApplicationContext(),
	  	                 "PopUp" , Toast.LENGTH_LONG)
	  	                 .show();
	               AlertDialog.Builder diaEjer= new AlertDialog.Builder(dialogo.getContext());
	               List<String> nombres=creaLista(le);
	               final CharSequence[] items=nombres.toArray(new String[nombres.size()]);
	               diaEjer.setTitle("Seleccione Ejercicio").setItems(items, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//lse.get(pos).setNombre(nomSerie.getText().toString());
						
						/*
						ArrayList<Integer> listaId=new ArrayList<Integer>();
						for(int i=0;i<lse.get(pos).getEjercicios().size();i++)
							listaId.add(lse.get(pos).getEjercicios().get(i));
						serie.setEjercicios(listaId);
						serie.getEjercicios().add(le.get(which).getIdEjercicio());
						serie.setDuracion(getDuracionAutomatica(serie));
						*/
						serie.getEjercicios().add(le.get(which).getIdEjercicio());
						leaux.clear();
				        for(int i=0;i<serie.getEjercicios().size();i++){
				        	leaux.add(eds.getEjercicios(serie.getEjercicios().get(i)));
				        }
				        recargaDuracion();
				        adapterselej = new adaptadorSelEj(dialogo.getContext(), R.layout.blist_item_handle_right, leaux);
				        

				        lvEj.setAdapter(adapterselej);
						
						//dialogo.dismiss();
						//CrearModificarSeriesEjercicios(false,serie,insertar);
					}
				});
	            //diaEjer.create();
	               diaEjer.show();
			}
		});
		//Boton guardar
		View guardarSerie=dialogo.findViewById(R.id.lguardar_cam);
		
		guardarSerie.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				serie.setNombre(nomSerie.getText().toString());
				serie.setDuracion(Integer.parseInt(duracion.getText().toString()));
				serie.setFecha_modificacion(new Date());
				
				ArrayList<Integer> listaId=new ArrayList<Integer>();
				for(int i=0;i<leaux.size();i++)
					listaId.add(leaux.get(i).getIdEjercicio());
				
				serie.setEjercicios(listaId);
				boolean mo=false;
				if (insertar==false)
					mo=seds.modificaSerieEjercicios(serie);
				else
					mo=seds.createSerieEjercicios(serie) != null;
				if(mo){
					dialogo.dismiss();
					lse=seds.getAllSeriesEjercicios();
					//Como se ha modificado la base de datos, volvemos ha acer una carga de la lista de series de ejercicios
					CreaTablaSeries();
					Toast.makeText(getApplicationContext(),
		  	                 "Modificado" , Toast.LENGTH_LONG)
		  	                 .show();
				}

			}
		});

		
	}

	private List<String> creaLista(List<Ejercicio> le){
		List<String> dev=new ArrayList<String>();
		for(int i=0;i<le.size();i++)
			dev.add(le.get(i).getNombre());		
		return dev;
	}
	
	/*
	private int getDuracionAutomatica(SerieEjercicios se){
		int duracion=0;
		EjercicioDataSource eds_local=new EjercicioDataSource(getApplicationContext());
		eds_local.open();
		for(int i=0;i<se.getEjercicios().size();i++){
			duracion+=eds_local.getEjercicios(se.getEjercicios().get(i)).getDuracion();
		}
			
		eds_local.close();	
		return duracion;
	}
	
		*/
	
	private void recargaDuracion(){
		int dura=0;
		for(int i=0;i<leaux.size();i++){
			dura+=leaux.get(i).getDuracion();
		}
		duracion.setText(String.valueOf(dura));
	}
	

    private DragSortListView.DropListener onDropEj =
	        new DragSortListView.DropListener() {
	            @Override
	            public void drop(int from, int to) {
	                //Ejercicio item=adapterselej.getItem(from);
	                Ejercicio item=adapterselej.getItem(from);
	                
	                adapterselej.notifyDataSetChanged();
	                adapterselej.remove(item);
	                adapterselej.insert(item, to);
	            }
	        };

	    private DragSortListView.RemoveListener onRemoveEj = 
	        new DragSortListView.RemoveListener() {
	            @Override
	            public void remove(int which) {
	            	adapterselej.remove(adapterselej.getItem(which));
	            	recargaDuracion();
	            }
	        };

	    private DragSortListView.DragScrollProfile ssProfileEj =
	        new DragSortListView.DragScrollProfile() {
	            @Override
	            public float getSpeed(float w, long t) {
	                if (w > 0.8f) {
	                    // Traverse all views in a millisecond
	                    return ((float) adapterselej.getCount()) / 0.001f;
	                } else {
	                    return 10.0f * w;
	                }
	            }
	        };
 
	
	        
	        
	        
	        //Serie de ejercicios
	        
	        
	        private DragSortListView.DropListener onDropSer =
	    	        new DragSortListView.DropListener() {
	    	            @Override
	    	            public void drop(int from, int to) {
	    	                //Ejercicio item=adapterselej.getItem(from);
	    	                SerieEjercicios item=adaptador.getItem(from);
	    	                
	    	                adaptador.notifyDataSetChanged();
	    	                adaptador.remove(item);
	    	                adaptador.insert(item, to);
	    	                

	    	                
	    	        		for(int i=0;i<lse.size();i++)
	    	        			seds.actualizaOrden(lse.get(i), i+1);
	    	        		
	    	        		sonidos.playDrop();
	    	                //playerDrop.start();
	    	            }
	    	        };

	    	    private DragSortListView.RemoveListener onRemoveSer = 
	    	        new DragSortListView.RemoveListener() {
	    	            @Override
	    	            public void remove(int which) {
	    	            	sonidos.playRemove();
	    	            	
	    	            	AlertDialog.Builder alerta=new AlertDialog.Builder(micontexto);
	    					alerta.setTitle("Eliminar");
	    					final String nombre=lse.get(which).getNombre();
	    					final int id=lse.get(which).getIdSerie();
	    					adaptador.remove(adaptador.getItem(which));
	    					alerta.setMessage("Se eliminará la serie: "+nombre);
	    					alerta.setCancelable(false);
	    					alerta.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
	    						
	    						@Override
	    						public void onClick(DialogInterface dialog, int which) {
	    							// TODO Auto-generated method stub
	    							boolean borrado=false;
	    							borrado=seds.eliminarSerieEjercicios(id);
	    							if(borrado==true){
	    							// Toast.makeText(getApplicationContext(),"Borrado: "+nombre, Toast.LENGTH_LONG).show();
	    		    	             
	    		    	          //   lse=seds.getAllSeriesEjercicios();
	    							 CreaTablaSeries();
	    		    	        	 for(int i=0;i<lse.size();i++)
	    		    	        		seds.actualizaOrden(lse.get(i), i+1);
	    							}
	    							 
	    						}
	    					});
	    					
	    					alerta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	    						
	    						@Override
	    						public void onClick(DialogInterface dialog, int which) {
	    							// TODO Auto-generated method stub
	    		    	             lse=seds.getAllSeriesEjercicios();
	    		    	             CreaTablaSeries();
	    						}
	    					});
	    					
	    					alerta.show();
	    	            	

	    	            }
	    	        };

	    	    private DragSortListView.DragScrollProfile ssProfileSer =
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
	    	
	        
	        
	    	
	    	public class adaptadorSelSer extends ArrayAdapter<SerieEjercicios>{

	    		
	    		Context context;
	    		
	    		public adaptadorSelSer(Context context, int resource, List<SerieEjercicios> objects) {
	    			super(context, resource, objects);
	    			// TODO Auto-generated constructor stub
	    			this.context=context;
	    		}
	    		
	    		
	    		 private class ViewHolder {
	    			 ImageView img;
	    			 TextView nombre;
	    			 TextView duracion;
	    			 TextView ultimaModificacion;
	    			 ImageView drag;
	    			 //TextView texto;
	    		    }
	    		 
	    		 
	    		 
	    		    public View getView(int position, View convertView, ViewGroup parent) {
	    		        ViewHolder holder = null;
	    		        SerieEjercicios rowItem = getItem(position);
	    		         
	    		        LayoutInflater mInflater = (LayoutInflater) context
	    		                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	    		        if (convertView == null) {
	    		            convertView = mInflater.inflate(R.layout.drag_ser, null);
	    		          
	    		            //convertView.setBackgroundDrawable(convertView.getResources().getDrawable(R.drawable.listaredondeada));
	    		            holder = new ViewHolder();
	    		           
	    		            holder.img = (ImageView) convertView.findViewById(R.id.imgSelSer);
	    		            holder.nombre = (TextView) convertView.findViewById(R.id.NombSelSer);
	    		            holder.duracion=(TextView) convertView.findViewById(R.id.DurSelSer);
	    		            holder.ultimaModificacion=(TextView) convertView.findViewById(R.id.ModiSelSer);
	    		            holder.drag=(ImageView)convertView.findViewById(R.id.midrag);
	    		         
	    		            convertView.setTag(holder);
	    		        } else
	    		            holder = (ViewHolder) convertView.getTag();
	    		                
	    		        holder.img.setImageResource(R.drawable.ic3);
	    		        holder.nombre.setText(rowItem.getNombre());
	    		        holder.duracion.setText(String.valueOf(rowItem.getDuracion())+ " minuto(s)");
	    		        holder.ultimaModificacion.setText(rowItem.getFecha_modificacion_AsStrign());
	    		        holder.drag.setImageResource(R.id.drag_handle);

	    		         
	    		        return convertView;
	    		    }
	    		
	    		

	    	}

	    	
	    	
	
}
