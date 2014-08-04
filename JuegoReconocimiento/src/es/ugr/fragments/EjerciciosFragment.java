package es.ugr.fragments;

import es.ugr.juegoreconocimiento.R;
import es.ugr.librerias.EjerciciosLibrary;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */

public class EjerciciosFragment extends Fragment {

	private EjerciciosLibrary oLibreria = new EjerciciosLibrary();
	private ActionBarDrawerToggle mDrawerToggle;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public EjerciciosFragment() {
	}

	public void setDrawerToggle(ActionBarDrawerToggle mDrawerToggle) {
		this.mDrawerToggle = mDrawerToggle;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.ejercicios, container, false);
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
		try {
			boolean drawerOpen = ((DrawerLayout) getActivity().findViewById(
					R.id.drawer_layout)).isDrawerOpen((ListView) getActivity()
					.findViewById(R.id.left_drawer));
			menu.findItem(R.id.itemImportar).setVisible(!drawerOpen);
			menu.findItem(R.id.itemSincronizar).setVisible(!drawerOpen);
		} catch (Exception e) {
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.ejercicios, menu);
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
		case R.id.itemImportar:
			try {
				oLibreria.importarEjercicios();
			} catch (Exception e) {
				Toast.makeText(getActivity(),
						"Ha ocurrido un problema al importar los ejercicios",
						Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.itemSincronizar:
			try {
				oLibreria.sincronizarEjercicios();
			} catch (Exception e) {
				Toast.makeText(getActivity(),
						"Ha ocurrido un problema al sincronizar los ejericios",
						Toast.LENGTH_LONG).show();
			}
			return true;
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
