package es.ugr.gestion_resultados;


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
public class ResultadosFragment extends Fragment {
	
	private ResultadosLibrary resultados = new ResultadosLibrary();

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ResultadosFragment() {
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.resultados2,
				container, false);
		resultados.setView(rootView);
		resultados.onCreate(getActivity());
		return rootView;
	}
	
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		resultados.onDestroy();
	}
	

}
