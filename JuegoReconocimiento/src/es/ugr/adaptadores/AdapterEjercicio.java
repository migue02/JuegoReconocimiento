package es.ugr.adaptadores;

import java.util.List;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Ejercicio;

public class AdapterEjercicio extends ArrayAdapter<Ejercicio> {

	private Context context;

	public AdapterEjercicio(Context context, int resource,
			List<Ejercicio> objects) {
		super(context, resource, objects);
		this.context = context;
	}

	private class ViewHolder {
		TextView nombre;
		TextView duracion;
		ImageView drag;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Ejercicio rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_ejercicios, null);

			holder = new ViewHolder();

			holder.nombre = (TextView) convertView
					.findViewById(R.id.NombSelEj);
			holder.duracion = (TextView) convertView
					.findViewById(R.id.DurSelEj);
			holder.drag = (ImageView) convertView.findViewById(R.id.midrag);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.nombre.setText(rowItem.getNombre());
		holder.duracion.setText(String.valueOf(rowItem.getDuracion())
				+ " minuto(s)");
		Picasso.with(convertView.getContext()).load(R.id.drag_handle).into(holder.drag);

		return convertView;
	}

}