package es.ugr.fragments;


import es.ugr.juegoreconocimiento.R;
import es.ugr.librerias.ResultadosLibrary;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	
	private ResultadosLibrary oLibreria = new ResultadosLibrary();
	private ActionBarDrawerToggle mDrawerToggle;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ResultadosFragment() {
	}
	
	public void setDrawerToggle(ActionBarDrawerToggle mDrawerToggle) {
		this.mDrawerToggle = mDrawerToggle;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.resultados2,
				container, false);
		oLibreria.setView(rootView);
		oLibreria.onCreate(getActivity());
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setHomeButtonEnabled(true);
		return rootView;
	}
	
	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		/*try {
			 drawerOpen = ((DrawerLayout) getActivity().findViewById(
					R.id.drawer_layout)).isDrawerOpen((ListView) getActivity()
					.findViewById(R.id.left_drawer));
		} catch (Exception e) {
		}*/
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.resultados, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		try {
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
		} catch (Exception E) {
		}
		// Handle action buttons
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}	

	@Override
	public void onDestroy() {
		super.onDestroy();
		oLibreria.onDestroy();
	}
	

}
