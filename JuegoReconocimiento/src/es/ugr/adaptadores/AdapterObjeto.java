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
import es.ugr.objetos.Objeto;

public class AdapterObjeto extends ArrayAdapter<Objeto> {

	private Context context;

	public AdapterObjeto(Context context, int resource, List<Objeto> objects) {
		super(context, resource, objects);
		this.context = context;
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
			convertView = mInflater.inflate(R.layout.adapter_objeto, null);
			holder = new ViewHolder();

			holder.img = (ImageView) convertView.findViewById(R.id.imgSelObj);
			holder.nombre = (TextView) convertView
					.findViewById(R.id.NombSelObj);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		if (rowItem.getImagen() != null)
			holder.img.setImageBitmap(rowItem.getImagen());
		else
			Picasso.with(convertView.getContext()).load(R.drawable.objetos_64).into(holder.img);

		holder.nombre.setText(rowItem.getNombre());

		return convertView;
	}

}
