package es.ugr.adaptadores;

import es.ugr.basedatos.AlumnoDataSource;
import es.ugr.basedatos.SerieEjerciciosDataSource;
import es.ugr.objetos.*;
import es.ugr.objetos.TiposPropios.Sexo;

import es.ugr.juegoreconocimiento.R;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class Adaptador extends BaseExpandableListAdapter {
	private final SparseArray<GrupoDeItems> grupos;
	private int tipoTabla;
	public LayoutInflater inflater;
	public Activity activity;

	// Constructor
	public Adaptador(Activity act, SparseArray<GrupoDeItems> grupos, int tipoTabla) {
		activity = act;
		this.grupos = grupos;
		inflater = act.getLayoutInflater();
		this.tipoTabla=tipoTabla;
	}

	// Nos devuelve los datos asociados a un subitem en base
	// a la posición
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return grupos.get(groupPosition).children.get(childPosition);
	}

	// Devuelve el id de un item o subitem en base a la
	// posición de item y subitem
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	// En base a la posición del item y de subitem nos devuelve
	// el objeto view correspondiente y el layout para los subitems
	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final Resultado children = (Resultado) getChild(groupPosition, childPosition);
		TextView textvw = null;
		convertView = inflater.inflate(R.layout.subitems_layout, null);
		
		
		//Si es el primer elemento poner Titulos
		if(childPosition==0){	
		TableLayout tl=(TableLayout) convertView.findViewById(R.id.tableLayoutTablas);
		
		TableRow cabecera=new TableRow(convertView.getContext());
		
		TextView titidAl,tittotales,titaciertos,titfallos,titpuntuacion,titduracion;
		
		titidAl=new TextView(convertView.getContext());
		tittotales=new TextView(convertView.getContext());
		titaciertos=new TextView(convertView.getContext());
		titfallos=new TextView(convertView.getContext());
		titpuntuacion=new TextView(convertView.getContext());
		titduracion=new TextView(convertView.getContext());
		
		if (tipoTabla==0)
			titidAl.setText("Alumno");
		else
			titidAl.setText("Secuencia");
		
		
		titidAl.setPadding(2, 0, 5, 0);
		
		tittotales.setText("Total");
		tittotales.setPadding(2, 0, 5, 0);
		
		titaciertos.setText("Aciertos");
		titaciertos.setPadding(2, 0, 5, 0);
		
		titfallos.setText("Fallos");
		titfallos.setPadding(2, 0, 5, 0);
		
		titpuntuacion.setText("Puntuación");
		titpuntuacion.setPadding(2, 0, 5, 0);
		
		titduracion.setText("Duración");
		titduracion.setPadding(2, 0, 5, 0);
		
		cabecera.addView(new TextView(convertView.getContext()));
		cabecera.addView(titidAl);
		cabecera.addView(tittotales);
		cabecera.addView(titaciertos);
		cabecera.addView(titfallos);
		cabecera.addView(titduracion);
		cabecera.addView(titpuntuacion);
		
		
		tl.addView(cabecera, 0);
		
		
		
		}
		
		
		TableRow row=(TableRow) convertView.findViewById(R.id.tableRowTablas);
				
		//Declaracion elementos tabla
		TextView idAl,totales,aciertos,fallos,puntuacion,duracion;
		ImageView imgSexo;
		

		imgSexo=(ImageView)convertView.findViewById(R.id.imageViewSexo);
		idAl=(TextView)convertView.findViewById(R.id.textViewNombre);
		totales=(TextView)convertView.findViewById(R.id.textViewTotales);
		aciertos=(TextView)convertView.findViewById(R.id.TextViewAciertos);
		fallos=(TextView)convertView.findViewById(R.id.TextViewFallos);
		puntuacion=(TextView)convertView.findViewById(R.id.TextViewPuntuacion);
		duracion=(TextView)convertView.findViewById(R.id.TextViewDuracion);

		
		//Asignacion valores elementos tabla
		
		if(tipoTabla==0){
		AlumnoDataSource ads=new AlumnoDataSource(convertView.getContext());
		ads.open();
		Alumno alum=ads.getAlumnos(children.getIdAlumno());
		ads.close();
		
		if(alum.getSexo()==Sexo.Mujer)
			imgSexo.setImageResource(R.drawable.girl);
		else
			imgSexo.setImageResource(R.drawable.boy);
			
		idAl.setText(alum.getNombre()+" "+alum.getApellidos());
		
		}
		else{
			SerieEjerciciosDataSource seds=new SerieEjerciciosDataSource(convertView.getContext());
			seds.open();
			SerieEjercicios ser=seds.getSerieEjercicios(children.getIdEjercicio());
			seds.close();
			
			imgSexo.setImageResource(R.drawable.ic5);
			idAl.setText(ser.getNombre());
		}
		totales.setText(String.valueOf(children.getAciertos()+children.getFallos()));
		aciertos.setText(String.valueOf(children.getAciertos()));
		fallos.setText(String.valueOf(children.getFallos()));
		puntuacion.setText(String.valueOf(children.getPuntuacion()));
		duracion.setText(String.valueOf(children.getDuracion()));
		
		
		
		
		//pading
		
      	idAl.setPadding(0, 0, 2, 0);
      	idAl.setTextColor(convertView.getContext().getResources().getColor(R.color.texto_tabla));
      	totales.setPadding(0, 0, 2, 0);
      	totales.setTextColor(convertView.getContext().getResources().getColor(R.color.texto_tabla));
      	aciertos.setPadding(0, 0, 2, 0);
      	aciertos.setTextColor(convertView.getContext().getResources().getColor(R.color.texto_tabla));
      	fallos.setPadding(0, 0, 2, 0);
      	fallos.setTextColor(convertView.getContext().getResources().getColor(R.color.texto_tabla));
      	puntuacion.setPadding(0, 0, 2, 0);
      	puntuacion.setTextColor(convertView.getContext().getResources().getColor(R.color.texto_tabla));
      	duracion.setPadding(0, 0, 2, 0);
      	duracion.setTextColor(convertView.getContext().getResources().getColor(R.color.texto_tabla));
		
		
      	 if(childPosition%2==0)
       		 row.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.tabla1));
       	 else
       		 row.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.tabla2));
		

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Toast.makeText(activity, children + " - Item: " + groupPosition + " - Subitem:" + childPosition, Toast.LENGTH_SHORT).show();
			}
		});
		return convertView;
	}

	// Nos devuelve la cantidad de subitems que tiene un ítem
	@Override
	public int getChildrenCount(int groupPosition) {
		return grupos.get(groupPosition).children.size();
	}

	//Los datos de un ítem especificado por groupPosition
	@Override
	public Object getGroup(int groupPosition) {
		return grupos.get(groupPosition);
	}

	//La cantidad de ítem que tenemos definidos
	@Override
	public int getGroupCount() {
		return grupos.size();
	}

	//Método que se invoca al contraer un ítem
	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	//Método que se invoca al expandir un ítem
	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	//Devuelve el id de un ítem
	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	//Obtenemos el layout para los ítems
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.items_layout, null);
		}
		GrupoDeItems grupo = (GrupoDeItems) getGroup(groupPosition);
		((CheckedTextView) convertView).setText(grupo.string);
		((CheckedTextView) convertView).setChecked(isExpanded);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	//Nos informa si es seleccionable o no un ítem o subitem
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}
