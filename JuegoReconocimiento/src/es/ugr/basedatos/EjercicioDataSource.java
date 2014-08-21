package es.ugr.basedatos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import es.ugr.objetos.Ejercicio;
import es.ugr.objetos.SerieEjercicios;

public class EjercicioDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_EJERCICIO_ID,
			MySQLiteHelper.COLUMN_EJERCICIO_NOMBRE,
			MySQLiteHelper.COLUMN_EJERCICIO_DESCRIPCION,
			MySQLiteHelper.COLUMN_EJERCICIO_OBJETOS,
			MySQLiteHelper.COLUMN_EJERCICIO_FECHA,
			MySQLiteHelper.COLUMN_EJERCICIO_DURACION,
			MySQLiteHelper.COLUMN_EJERCICIO_OBJETOS_REC,
			MySQLiteHelper.COLUMN_EJERCICIO_SONIDO_DESCRIPCION};

	public EjercicioDataSource(Context context) {
		Log.w("Creando...", "Creando bd");
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		database.execSQL(dbHelper.getSqlCreateEjercicio());
		database.execSQL(dbHelper.sqlenableForeingKeys);
	}

	public void close() {
		dbHelper.close();
	}
	
	public void dropTableEjercicios() {
		Log.w("Deleting...", "Borrando tabla ejercicio");
		database.execSQL(dbHelper.getSqlDropEjercicio());
		database.execSQL(dbHelper.getSqlCreateEjercicio());
	}
	
	private ContentValues createValues(Ejercicio ejercicio){
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_EJERCICIO_NOMBRE, ejercicio.getNombre());
		values.put(MySQLiteHelper.COLUMN_EJERCICIO_OBJETOS,
				es.ugr.utilidades.Utilidades.ArrayListToJson(ejercicio.getObjetos()));
		values.put(MySQLiteHelper.COLUMN_EJERCICIO_DESCRIPCION,ejercicio.getDescripcion());
		values.put(MySQLiteHelper.COLUMN_EJERCICIO_FECHA,ejercicio.getFechaAsString());
		values.put(MySQLiteHelper.COLUMN_EJERCICIO_DURACION,ejercicio.getDuracion());
		values.put(MySQLiteHelper.COLUMN_EJERCICIO_OBJETOS_REC,
				es.ugr.utilidades.Utilidades.ArrayListToJson(ejercicio.getObjetosReconocer()));
		values.put(MySQLiteHelper.COLUMN_EJERCICIO_SONIDO_DESCRIPCION,ejercicio.getSonido_descripcion());
		return values;
	}

	public Ejercicio createEjercicio(Ejercicio ejercicio) {
		ContentValues values = new ContentValues();
		values = createValues(ejercicio);		
		ejercicio.setIdEjercicio((int) database.insert(MySQLiteHelper.TABLE_EJERCICIO, null, values));
		return ejercicio;
	}

	
	public Ejercicio createEjercicio(String nombre, Date fecha, ArrayList<String> objetos,
			String descripcion, int duracion,
			ArrayList<String> objetosReconocer,
			String sonido_descripcion) {

		ContentValues values = new ContentValues();
		Ejercicio ejercicio = new Ejercicio(nombre, fecha, objetos, descripcion, duracion,objetosReconocer,sonido_descripcion);
		values = createValues(ejercicio);
		
		long insertId = database.insert(MySQLiteHelper.TABLE_EJERCICIO, null,
				values); // Se inserta un ejercicio y se deuelve su id
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EJERCICIO,

		allColumns, MySQLiteHelper.COLUMN_EJERCICIO_ID + " = " + insertId,
				null, null, null, null);// devuelve el ejercicio que se acaba de
										// insertar

		cursor.moveToFirst();
		Ejercicio newEjercicio = cursorToEjercicio(cursor);
		cursor.close();
		return newEjercicio;
	}
	

	public boolean modificaEjercicio(String nombre, Date fecha, ArrayList<String> objetos,
			String descripcion, int duracion,
			ArrayList<String> objetosReconocer,
			String sonido_descripcion) {
		
		ContentValues values = new ContentValues();
		Ejercicio ejercicio = new Ejercicio(nombre, new Date(), objetos, descripcion, duracion,objetosReconocer,sonido_descripcion);
		values = createValues(ejercicio);
		
		boolean mod=database.update(MySQLiteHelper.TABLE_EJERCICIO, values, MySQLiteHelper.COLUMN_EJERCICIO_NOMBRE+" = "+"'"+nombre+"'", null)>0;

		return mod;
		
	}
	
	public boolean modificaEjercicio(Ejercicio ejercicio) {
		ContentValues values = new ContentValues();
		ejercicio.setFecha(new Date());
		values = createValues(ejercicio);

		return database.update(MySQLiteHelper.TABLE_EJERCICIO, values,
				MySQLiteHelper.COLUMN_EJERCICIO_NOMBRE + " = " +"'"+ejercicio.getNombre()+"'", null) > 0;
	}
/*
	public boolean modificaEjercicio(int id, String nombre,
			ArrayList<Integer> objetos, String descripcion, int duracion) {

		ContentValues values = new ContentValues();
		Ejercicio ejercicio = new Ejercicio(-1, nombre, objetos, descripcion, duracion, objetos);
		values = createValues(ejercicio);

		return database.update(MySQLiteHelper.TABLE_EJERCICIO, values,
				MySQLiteHelper.COLUMN_EJERCICIO_ID + " = " + id, null) > 0;
	}*/

	public boolean eliminaEjercicio(int id) {
		return database.delete(MySQLiteHelper.TABLE_EJERCICIO,
				MySQLiteHelper.COLUMN_EJERCICIO_ID + " = " + id, null) > 0;
	}

	public boolean eliminaTodosEjercicios() {
		return database.delete(MySQLiteHelper.TABLE_EJERCICIO, null, null) > 0;
	}
	
	public void dropTableEjericio() {
		database.execSQL(dbHelper.getSqlDropEjercicio());
		database.execSQL(dbHelper.getSqlCreateEjercicio());
	}

	public List<Ejercicio> getAllEjercicios() {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EJERCICIO,
				allColumns, null, null, null, null, MySQLiteHelper.COLUMN_EJERCICIO_ORDEN);

		List<Ejercicio> ejercicios = new ArrayList<Ejercicio>();
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Ejercicio ejercicio = cursorToEjercicio(cursor);
				ejercicios.add(ejercicio);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return ejercicios;
	}

	public List<Ejercicio> getAllEjercicios(SerieEjercicios serie) {
		List<Ejercicio> ejercicios = new ArrayList<Ejercicio>();		
		for(int i=0;i< serie.getEjercicios().size() ;i++)
			ejercicios.add(getEjercicios(serie.getEjercicios().get(i)));
		return ejercicios;
	}
	
	public Ejercicio getEjercicios(int id) {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EJERCICIO,
				allColumns, MySQLiteHelper.COLUMN_EJERCICIO_ID + " = " + id,
				null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			Ejercicio ejercicio = cursorToEjercicio(cursor);
			cursor.close();
			return ejercicio;
		}
		return null;
	}
	
	
	public Ejercicio getEjercicios(String nombre) {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EJERCICIO,
				allColumns, MySQLiteHelper.COLUMN_EJERCICIO_NOMBRE + " = " +"'"+nombre+"'",
				null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			Ejercicio ejercicio = cursorToEjercicio(cursor);
			cursor.close();
			return ejercicio;
		}
		return null;
	}
	
	
	public int getDuracion(int id) {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EJERCICIO, 
				new String [] {MySQLiteHelper.COLUMN_EJERCICIO_DURACION}, 
				MySQLiteHelper.COLUMN_EJERCICIO_ID + " = " + id, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getInt(0);
		}
		return 0;
	}

	public boolean actualizaOrden(Ejercicio ejercicio, int posicion){
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_EJERCICIO_ORDEN, posicion);
		
		return database.update(MySQLiteHelper.TABLE_EJERCICIO, values,
				MySQLiteHelper.COLUMN_EJERCICIO_ID + " = " + ejercicio.getIdEjercicio(), null) > 0;
	}
	
	private Ejercicio cursorToEjercicio(Cursor cursor) {
		Ejercicio ejercicio = new Ejercicio();
		ejercicio.setIdEjercicio(cursor.getLong(0));
		ejercicio.setNombre(cursor.getString(1));
		ejercicio.setDescripcion(cursor.getString(2));
		ejercicio.setObjetos(es.ugr.utilidades.Utilidades.ArrayListFromJson(cursor.getString(3)));
		try {
			ejercicio.setFecha(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor
					.getString(4)));
		} catch (ParseException e) {
			Log.e("ERROR_FECHA", "Error al obtener la fecha");
			e.printStackTrace();
			ejercicio.setFecha(new Date());
		}		
		ejercicio.setDuracion(cursor.getInt(5));
		
		ejercicio.setObjetosReconocer(es.ugr.utilidades.Utilidades.ArrayListFromJson(cursor.getString(6)));
		ejercicio.setSonido_descripcion(cursor.getString(7));
		return ejercicio;
	}

}
