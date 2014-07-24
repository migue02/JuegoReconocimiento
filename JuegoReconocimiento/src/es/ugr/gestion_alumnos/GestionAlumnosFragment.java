package es.ugr.gestion_alumnos;


import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import es.ugr.juegoreconocimiento.R;
import android.os.Bundle;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */
public class GestionAlumnosFragment extends Fragment {

	private GestionAlumnosLibrary gestorAlumnos = new GestionAlumnosLibrary();

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public GestionAlumnosFragment() {
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.gestion_alumnos2,
				container, false);
		gestorAlumnos.setView(rootView);
		gestorAlumnos.onCreate(getActivity());
		return rootView;
	}
	
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		gestorAlumnos.onDestroy();
	}

}
