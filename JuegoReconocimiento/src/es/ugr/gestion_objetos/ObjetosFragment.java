package es.ugr.gestion_objetos;

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

public class ObjetosFragment extends Fragment {
	private ObjetosLibrary objetos = new ObjetosLibrary();

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ObjetosFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.objetos2, container, false);
		objetos.setView(rootView);
		objetos.onCreate(getActivity());
		return rootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		objetos.onDestroy();
	}
}
