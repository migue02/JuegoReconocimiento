package es.ugr.lista_navegacion;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import es.ugr.lista_navegacion.ContenidoBarraPrincipal;
import es.ugr.adaptadores.Lista_generica_adaptador;
import es.ugr.fragments.EjerciciosFragment;
import es.ugr.fragments.GestionAlumnosFragment;
import es.ugr.fragments.ObjetosFragment;
import es.ugr.fragments.ResultadosFragment;
import es.ugr.fragments.SeriesEjerciciosFragment;
import es.ugr.juegoreconocimiento.MainActivity;
import es.ugr.juegoreconocimiento.R;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */
public class ParentActivity extends FragmentActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mListaMenuPrincipal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parent_activity);

		mTitle = mDrawerTitle = getTitle();
		mListaMenuPrincipal = getResources().getStringArray(
				R.array.menu_principal);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setBackgroundResource(R.drawable.listaredondeada);
		mDrawerList.setAdapter(new Lista_generica_adaptador(this,
				R.layout.item_navegacion, ContenidoBarraPrincipal.ITEMS) {

			@Override
			public void onEntrada(Object entrada, View view) {
				if (entrada != null) {
					TextView texto_superior_entrada = (TextView) view
							.findViewById(R.id.textNavegacion);
					if (texto_superior_entrada != null)
						texto_superior_entrada
								.setText(((ContenidoBarraPrincipal.Item) entrada).titulo);

					ImageView imagen_entrada = (ImageView) view
							.findViewById(R.id.imageNavegacion);
					if (imagen_entrada != null)
						Picasso.with(ParentActivity.this)
								.load(((ContenidoBarraPrincipal.Item) entrada).idImagen)
								.into(imagen_entrada);
				}
			}
		});

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

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
			selectItem(Integer.valueOf(id));
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
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this,
					ListaNavegacionActivity.class));
			finish();
			return true;
		case R.id.itemAyuda:

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private void selectItem(int position) { // update the main content by
											// replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
			finish();
			break;
		case 1:
			fragment = new GestionAlumnosFragment();
			((GestionAlumnosFragment) fragment).setDrawerToggle(mDrawerToggle);
			break;
		case 2:
			fragment = new ResultadosFragment();
			((ResultadosFragment) fragment).setDrawerToggle(mDrawerToggle);
			break;
		case 3:
			fragment = new EjerciciosFragment();
			((EjerciciosFragment) fragment).setDrawerToggle(mDrawerToggle);
			break;
		case 4:
			fragment = new SeriesEjerciciosFragment();
			((SeriesEjerciciosFragment) fragment)
					.setDrawerToggle(mDrawerToggle);
			break;
		case 5:
			fragment = new ObjetosFragment();
			((ObjetosFragment) fragment).setDrawerToggle(mDrawerToggle);
			break;
		default:
			break;

		}
		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			setTitle(mListaMenuPrincipal[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}
}