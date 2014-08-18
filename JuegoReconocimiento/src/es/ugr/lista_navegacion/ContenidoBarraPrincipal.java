package es.ugr.lista_navegacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.ugr.juegoreconocimiento.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ContenidoBarraPrincipal {

	public static String[] titulos = new String[] {"Menú Principal", "Gestión Alumnos",
			"Resultados/Estadísticas", " Ejercicios", "Serie Ejercicios",
			"Objetos" };
	public static Integer[] images = new Integer[] {R.drawable.anterior, R.drawable.alumnos,
			R.drawable.resultados, R.drawable.ejercicios, R.drawable.series, R.drawable.objeto };

	/**
	 * An array of sample (dummy) items.
	 */
	public static ArrayList<Item> ITEMS = new ArrayList<Item>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, Item> ITEM_MAP = new HashMap<String, Item>();

	static {
		// Add 3 sample items.
		for (int i = 0; i < titulos.length; i++) {
			addItem(new Item(Integer.toString(i), images[i], titulos[i]));
		}
	}

	private static void addItem(Item item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	public static class Item {
		public String id;
		public int idImagen;
		public String titulo;

		public Item(String id, int idImagen, String titulo) {
			this.id = id;
			this.idImagen = idImagen;
			this.titulo = titulo;
		}
	}
}
