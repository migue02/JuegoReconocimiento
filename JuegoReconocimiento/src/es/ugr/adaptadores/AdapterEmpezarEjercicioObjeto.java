package es.ugr.adaptadores;

import java.util.List;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Objeto;

public class AdapterEmpezarEjercicioObjeto extends ArrayAdapter<Objeto> {

	private Context context;

	public AdapterEmpezarEjercicioObjeto(Context context, int resource, List<Objeto> objects) {
		super(context, resource, objects);
		this.context = context;
	}

	private class ViewHolder {
		ImageView img;
		TextView nombre,posicion;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Objeto rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_comienzo_ejercicio_objeto, null);
			holder = new ViewHolder();

			holder.img = (ImageView) convertView.findViewById(R.id.imgSelObj);
			holder.nombre = (TextView) convertView
					.findViewById(R.id.NombSelObj);
			holder.posicion = (TextView) convertView
					.findViewById(R.id.PosSelObj);
			

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		if (rowItem.getImagen() != null)
			holder.img.setImageBitmap(rowItem.getImagen());
		else
			Picasso.with(convertView.getContext()).load(R.drawable.objetos_64).into(holder.img);

		int color_texto=context.getResources().getColor(R.color.verde_ic);
		switch (position%4) {
		case 0:
			color_texto=context.getResources().getColor(R.color.verde_ic);
			break;
			
		case 1:
			color_texto=context.getResources().getColor(R.color.naranja_ic);
			break;
		
		case 2:
			color_texto=context.getResources().getColor(R.color.rojo_ic);
			break;
		
		case 3:
			color_texto=context.getResources().getColor(R.color.amarillo_ic);
			break;
	
		}
		
		//holder.nombre.setTextColor(color_texto);
		//holder.posicion.setTextColor(color_texto);
		
		holder.nombre.setTextColor(context.getResources().getColor(R.color.white));
		holder.posicion.setTextColor(context.getResources().getColor(R.color.white));
		
		holder.nombre.setText(rowItem.getNombre());
		holder.posicion.setText(String.valueOf(position+1)+".");
		
		convertView.setBackgroundResource(R.drawable.roundshape);
		GradientDrawable drawable = (GradientDrawable) convertView.getBackground();
		drawable.setColor(color_texto);

		return convertView;
	}

}
