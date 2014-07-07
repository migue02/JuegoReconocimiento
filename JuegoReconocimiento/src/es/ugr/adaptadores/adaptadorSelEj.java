package es.ugr.adaptadores;

import java.util.List;

import es.ugr.objetos.Ejercicio;
import es.ugr.juegoreconocimiento.R;
import es.ugr.juegoreconocimiento.Ejercicios;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class adaptadorSelEj extends ArrayAdapter<Ejercicio>{

	
	Context context;
	Typeface font;
	
	public adaptadorSelEj(Context context, int resource, List<Ejercicio> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context=context;
		font=Typeface.createFromAsset(context.getAssets(), "wd.ttf");
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
	            convertView = mInflater.inflate(R.layout.blist_item_handle_right, null);
	          
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
	        holder.nombre.setTypeface(font);
	        holder.nombre.setText(rowItem.getNombre());
	        holder.duracion.setTypeface(font);
	        holder.duracion.setText(String.valueOf(rowItem.getDuracion()));
	        holder.drag.setImageResource(R.id.drag_handle);

	         
	        return convertView;
	    }
	
	

}
