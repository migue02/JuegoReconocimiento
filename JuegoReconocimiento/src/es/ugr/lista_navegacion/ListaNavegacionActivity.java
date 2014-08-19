package es.ugr.lista_navegacion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import es.ugr.dialogs.Ayuda;
import es.ugr.dialogs.FichaAlumno;
import es.ugr.fragments.EjerciciosFragment;
import es.ugr.fragments.GestionAlumnosFragment;
import es.ugr.fragments.ObjetosFragment;
import es.ugr.fragments.ResultadosFragment;
import es.ugr.fragments.SeriesEjerciciosFragment;
import es.ugr.juegoreconocimiento.MainActivity;
import es.ugr.juegoreconocimiento.R;
import es.ugr.utilidades.Globals;

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
		getActionBar().setTitle(
				getResources().getStringArray(R.array.menu_principal)[0]);

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

		if (savedInstanceState == null) {
			String id;
			if (savedInstanceState == null) {
				Bundle extras = getIntent().getExtras();
				if (extras == null) {
					id = null;
				} else {
					id = extras.getString("ID");
				}
			} else {
				id = (String) savedInstanceState.getSerializable("ID");
			}
			onItemSelected(id);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.parent_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Globals g= (Globals)getApplication();
		int nfragment=g.getNFragment();
		
		switch (item.getItemId()) {
		
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
			finish();
			return true;
		case R.id.itemAyuda:
			Ayuda dialogo = new Ayuda(this,nfragment);
			dialogo.show();
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
			Fragment newFragment = null;
			Globals g=(Globals)getApplication();
			g.setNFragment(Integer.parseInt(id));
			
			if (id.equals("0")) {
				NavUtils.navigateUpTo(this,
						new Intent(this, MainActivity.class));
				finish();
			} else if (id.equals("1"))
				newFragment = new GestionAlumnosFragment();
			else if (id.equals("2"))
				newFragment = new ResultadosFragment();
			else if (id.equals("3"))
				newFragment = new EjerciciosFragment();
			else if (id.equals("4"))
				newFragment = new SeriesEjerciciosFragment();
			else if (id.equals("5"))
				newFragment = new ObjetosFragment();
			if (newFragment != null) {
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.ventana_detail_container, newFragment)
						.commit();
				getActionBar()
						.setTitle(
								getResources().getStringArray(
										R.array.menu_principal)[Integer
										.valueOf(id)]);
			}

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ParentActivity.class);
			detailIntent.putExtra("ID", id);
			startActivity(detailIntent);
		}
	}
}
