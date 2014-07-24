package es.ugr.gestion_series;

import es.ugr.juegoreconocimiento.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */
public class SeriesEjerciciosFragment extends Fragment {
	private SeriesEjerciciosLibrary seriesEjercicios = new SeriesEjerciciosLibrary();

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SeriesEjerciciosFragment() {
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.serie_ejercicios,
				container, false);
		seriesEjercicios.setView(rootView);
		seriesEjercicios.onCreate(getActivity());
		return rootView;
	}
	
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		seriesEjercicios.onDestroy();
	}	
}
