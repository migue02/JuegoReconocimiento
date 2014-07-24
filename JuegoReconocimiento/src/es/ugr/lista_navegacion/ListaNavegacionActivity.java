package es.ugr.lista_navegacion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import es.ugr.gestion_alumnos.GestionAlumnosActivity;
import es.ugr.gestion_alumnos.GestionAlumnosFragment;
import es.ugr.gestion_ejercicios.EjerciciosActivity;
import es.ugr.gestion_ejercicios.EjerciciosFragment;
import es.ugr.gestion_objetos.ObjetosActivity;
import es.ugr.gestion_objetos.ObjetosFragment;
import es.ugr.gestion_resultados.ResultadosActivity;
import es.ugr.gestion_resultados.ResultadosFragment;
import es.ugr.gestion_series.SeriesEjerciciosActivity;
import es.ugr.gestion_series.SeriesEjerciciosFragment;
import es.ugr.juegoreconocimiento.MainActivity;
import es.ugr.juegoreconocimiento.R;

/**
 * An activity representing a list of Ventanas. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link DetalleItemActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ListaNavegacionFragment} and the item details (if present) is a
 * {@link DetalleItemFragment}.
 * <p>
 * This activity also implements the required
 * {@link ListaNavegacionFragment.Callbacks} interface to listen for item
 * selections.
 */
public class ListaNavegacionActivity extends FragmentActivity implements
		ListaNavegacionFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ventana_list);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (findViewById(R.id.ventana_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ListaNavegacionFragment) getSupportFragmentManager()
					.findFragmentById(R.id.ventana_list))
					.setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// NavUtils.navigateUpTo(this, new Intent(ListaNavegacionActivity.this,
	// MainActivity.class));

	/**
	 * Callback method from {@link ListaNavegacionFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(DetalleItemFragment.ARG_ITEM_ID, id);
			Fragment newFragment;
			if (id.equals("0"))
				newFragment = new GestionAlumnosFragment();
			else if (id.equals("1"))
				newFragment = new ResultadosFragment();
			else if (id.equals("2"))
				newFragment = new EjerciciosFragment();
			else if (id.equals("3"))
				newFragment = new SeriesEjerciciosFragment();
			else if (id.equals("4"))
				newFragment = new ObjetosFragment();
			else
				newFragment = new DetalleItemFragment();
			newFragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.ventana_detail_container, newFragment)
					.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = null;
			if (id.equals("0"))
				detailIntent = new Intent(this, GestionAlumnosActivity.class);
			else if (id.equals("1"))
				detailIntent = new Intent(this, ResultadosActivity.class);
			else if (id.equals("2"))
				detailIntent = new Intent(this, EjerciciosActivity.class);
			else if (id.equals("3"))
				detailIntent = new Intent(this, SeriesEjerciciosActivity.class);
			else if (id.equals("4"))
				detailIntent = new Intent(this, ObjetosActivity.class);
			else
				detailIntent = new Intent(this, DetalleItemActivity.class);
			detailIntent.putExtra(DetalleItemFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
