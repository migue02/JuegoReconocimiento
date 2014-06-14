package es.ugr.adaptadores;

import java.util.List;

import es.ugr.juegoreconocimiento.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class adaptadorTitle extends ArrayAdapter<RowItemTitle>{

	
	Context context;
	
	public adaptadorTitle(Context context, int resource, List<RowItemTitle> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	
	
	 private class ViewHolder {
	        TextView txtTitle;
	        ImageView imageView;

	    }
	     
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder = null;
	        RowItemTitle rowItem = getItem(position);
	         
	        LayoutInflater mInflater = (LayoutInflater) context
	                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.fragment_item, null);
	            //convertView.setBackgroundDrawable(convertView.getResources().getDrawable(R.drawable.listaredondeada));
	            holder = new ViewHolder();
	           
	            holder.txtTitle = (TextView) convertView.findViewById(R.id.textFragment);
	            holder.imageView = (ImageView) convertView.findViewById(R.id.imageFragment);
	            convertView.setTag(holder);
	        } else
	            holder = (ViewHolder) convertView.getTag();
	                 
	        holder.txtTitle.setText(rowItem.getTitulo());
	        holder.imageView.setImageResource(rowItem.getImageID());
	         
	        return convertView;
	    }
	
	

}
