package es.ugr.adapters;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Alumno;
import es.ugr.objetos.TiposPropios.Sexo;

public class AdapterAlumno extends ArrayAdapter<Alumno> {

	private Context context;

	public AdapterAlumno(Context context, int resource,
			List<Alumno> objects) {
		super(context, resource, objects);
		this.context = context;
	}

	private class ViewHolder {
		ImageView sexo;
		TextView nombre;
		TextView apellidos;
		TextView edad;
		ImageView drag;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Alumno rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.drag_alum, null);

			// convertView.setBackgroundDrawable(convertView.getResources().getDrawable(R.drawable.listaredondeada));
			holder = new ViewHolder();

			holder.sexo = (ImageView) convertView.findViewById(R.id.sexAl);
			holder.nombre = (TextView) convertView.findViewById(R.id.nomAl);
			holder.apellidos = (TextView) convertView
					.findViewById(R.id.apeAl);
			holder.edad = (TextView) convertView.findViewById(R.id.edadAl);
			holder.drag = (ImageView) convertView.findViewById(R.id.midrag);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		if (rowItem.getSexo() == Sexo.Hombre)
			holder.sexo.setImageResource(R.drawable.boy);
		else
			holder.sexo.setImageResource(R.drawable.girl);

		holder.nombre.setText(rowItem.getNombre());
		holder.apellidos.setText(rowItem.getApellidos());
		holder.edad.setText(anios(rowItem.getFecha_nac_AsDate()) + " Años");
		holder.drag.setImageResource(R.id.drag_handle);

		return convertView;
	}
	
	public int anios(Date d) {

		Calendar dob = Calendar.getInstance();
		dob.setTime(d);
		Calendar today = Calendar.getInstance();

		int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

		if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
			age--;
		}

		return age;
	}

}