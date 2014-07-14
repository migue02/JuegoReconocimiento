package es.ugr.basedatos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import es.ugr.objetos.Ejercicio;
import es.ugr.objetos.Objeto;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class implements the DataBase of the application and all of the possible
 * operations that you can do with the DataBase
 * 
 */
public class ObjetoDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_OBJETO_ID,
			MySQLiteHelper.COLUMN_OBJETO_NOMBRE,
			MySQLiteHelper.COLUMN_OBJETO_DESCRIPCION,
			MySQLiteHelper.COLUMN_OBJETO_FECHA,
			MySQLiteHelper.COLUMN_OBJETO_KEYPOINTS,
			MySQLiteHelper.COLUMN_OBJETO_DESPCRIPTORES,
			MySQLiteHelper.COLUMN_OBJETO_COLS,
			MySQLiteHelper.COLUMN_OBJETO_ROWS,
			MySQLiteHelper.COLUMN_OBJETO_IMAGEN,
			MySQLiteHelper.COLUMN_OBJETO_SONIDO_DESCRIPCION,
			MySQLiteHelper.COLUMN_OBJETO_SONIDO_AYUDA,
			MySQLiteHelper.COLUMN_OBJETO_SONIDO_NOMBRE};
	
	private String[] simpleColumns = { MySQLiteHelper.COLUMN_OBJETO_ID,
			MySQLiteHelper.COLUMN_OBJETO_NOMBRE,
			MySQLiteHelper.COLUMN_OBJETO_IMAGEN};

	public ObjetoDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		database.execSQL(dbHelper.sqlenableForeingKeys);
		database.execSQL(dbHelper.getSqlCreateObjeto());
	}

	public void close() {
		dbHelper.close();
	}
	
	private ContentValues createValues(Objeto objeto){
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_OBJETO_NOMBRE, objeto.getNombre());
		values.put(MySQLiteHelper.COLUMN_OBJETO_DESCRIPCION, objeto.getDescripcion());
		values.put(MySQLiteHelper.COLUMN_OBJETO_FECHA, objeto.getFechaAsString());
		values.put(MySQLiteHelper.COLUMN_OBJETO_KEYPOINTS, objeto.getKeypoints());
		values.put(MySQLiteHelper.COLUMN_OBJETO_DESPCRIPTORES, objeto.getDescriptores());
		values.put(MySQLiteHelper.COLUMN_OBJETO_COLS, objeto.getCols());
		values.put(MySQLiteHelper.COLUMN_OBJETO_ROWS, objeto.getRows());
		values.put(MySQLiteHelper.COLUMN_OBJETO_IMAGEN, objeto.getPathImagen());
		values.put(MySQLiteHelper.COLUMN_OBJETO_SONIDO_AYUDA, objeto.getSonidoAyuda());
		values.put(MySQLiteHelper.COLUMN_OBJETO_SONIDO_DESCRIPCION, objeto.getSonidoDescripcion());
		values.put(MySQLiteHelper.COLUMN_OBJETO_SONIDO_NOMBRE, objeto.getSonidoNombre());
		return values;
	}

	public Objeto createObjeto(Objeto objeto) {
		ContentValues values = new ContentValues();
		values = createValues(objeto);		
		objeto.setId((int) database.insert(MySQLiteHelper.TABLE_OBJETO, null, values));
		if (objeto.getId() != -1)
			objeto.guardarImagen();
		return objeto;
	}
	
	
	public Objeto createObjeto(String nombre, String descripcion, Date fecha, String keypoints, 
			String descriptores, int cols, int rows, String imagen, String sonido_descripcion, String sonido_ayuda, String sonido_nombre) {

		ContentValues values = new ContentValues();
		Objeto objeto = new Objeto(-1, nombre, descripcion, fecha, keypoints, descriptores, cols, rows, imagen, sonido_descripcion, sonido_ayuda, sonido_nombre);
		values = createValues(objeto);

		long insertId = database.insert(MySQLiteHelper.TABLE_OBJETO, null,
				values); // Se inserta un objeto y se deuelve su id
		Log.w("Creando...", "Objeto " + nombre + " creado con id " + insertId);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_OBJETO,

		allColumns, MySQLiteHelper.COLUMN_OBJETO_ID + " = " + insertId, null,
				null, null, null);// devuelve el objeto que se acaba de insertar

		cursor.moveToFirst();
		Objeto newObjeto = cursorToObjeto(cursor);
		cursor.close();
		return newObjeto;
	}
	
	/*
	public Objeto createObjeto(String nombre, String keypoints,
			String descriptores, int cols, int rows) {

		ContentValues values = new ContentValues();
		Objeto objeto = new Objeto(-1, nombre, keypoints, descriptores, cols, rows, null);
		values = createValues(objeto);

		long insertId = database.insert(MySQLiteHelper.TABLE_OBJETO, null,
				values); // Se inserta un objeto y se deuelve su id
		Log.w("Creando...", "Objeto " + nombre + " creado con id " + insertId);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_OBJETO,

		allColumns, MySQLiteHelper.COLUMN_OBJETO_ID + " = " + insertId, null,
				null, null, null);// devuelve el objeto que se acaba de insertar

		cursor.moveToFirst();
		Objeto newObjeto = cursorToObjeto(cursor);
		cursor.close();
		return newObjeto;
	}
	*/
	public boolean modificaObjeto(String nombre, String descripcion, Date fecha, String keypoints, 
			String descriptores, int cols, int rows, String imagen, String sonido_descripcion, String sonido_ayuda, String sonido_nombre) {
		ContentValues values = new ContentValues();
		Objeto objeto = new Objeto(-1, nombre, descripcion, fecha, keypoints, descriptores, cols, rows, imagen, sonido_descripcion, sonido_ayuda, sonido_nombre);
		values = createValues(objeto);
		

		return database.update(MySQLiteHelper.TABLE_OBJETO, values,
				MySQLiteHelper.COLUMN_OBJETO_NOMBRE+ " = '" + nombre+"'", null) > 0;
	}
	
	public boolean modificaObjeto(Objeto objeto) {
		ContentValues values = new ContentValues();
		values = createValues(objeto);	
		objeto.guardarImagen();
		
		return database.update(MySQLiteHelper.TABLE_OBJETO, values,
				MySQLiteHelper.COLUMN_OBJETO_NOMBRE + " = '" + objeto.getNombre()+"'", null) > 0;
	}

	public boolean eliminaObjeto(int id) {
		return database.delete(MySQLiteHelper.TABLE_OBJETO,
				MySQLiteHelper.COLUMN_OBJETO_ID + " = " + id, null) > 0;
	}
	
	public boolean eliminaObjeto(String nombre) {
		return database.delete(MySQLiteHelper.TABLE_OBJETO,
				MySQLiteHelper.COLUMN_OBJETO_NOMBRE + " = '" + nombre+"'", null) > 0;
	}

	public boolean eliminaTodosObjetos() {
		return database.delete(MySQLiteHelper.TABLE_OBJETO, null, null) > 0;
	}

	public boolean eliminaTodosObjetos(int id) {
		return database.delete(MySQLiteHelper.TABLE_OBJETO, 
				MySQLiteHelper.COLUMN_OBJETO_ID + " > " + id, null) > 0;
	}
	
	
	
	public void dropTableObjeto() {
		Log.w("Deleting...", "Borrando tabla objetos");
		database.execSQL(dbHelper.getSqlDropObjeto());
		database.execSQL(dbHelper.getSqlCreateObjeto());
	}

	public ArrayList<Objeto> getAllObjetos() {
		ArrayList<Objeto> objetos = new ArrayList<Objeto>();
		Log.w("Obteniendo...", "Obteniendo todos los objetos...");
		Cursor cursor = database.query(MySQLiteHelper.TABLE_OBJETO, allColumns,
				null, null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Objeto objeto = cursorToObjeto(cursor);
				objetos.add(objeto);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return objetos;
	}
	
	public ArrayList<Objeto> getAllObjetos(int id) {
		ArrayList<Objeto> objetos = new ArrayList<Objeto>();
		Log.w("Obteniendo...", "Obteniendo todos los objetos...");
		Cursor cursor = database.query(MySQLiteHelper.TABLE_OBJETO, allColumns,
				MySQLiteHelper.COLUMN_OBJETO_ID + " >= " + id, null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Objeto objeto = cursorToObjeto(cursor);
				objetos.add(objeto);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return objetos;
	}
	
	public ArrayList<Objeto> getAllObjetosEscenario(Ejercicio ejercicio) {	
		ArrayList<Objeto> objetos = new ArrayList<Objeto>();
		for (int i=0; i<ejercicio.getObjetos().size(); i++)
			objetos.add(getObjeto(ejercicio.getObjetos().get(i)));
		return objetos;
	}
	
	public ArrayList<Objeto> getAllObjetosReconocer(Ejercicio ejercicio) {	
		ArrayList<Objeto> objetosEscenario = new ArrayList<Objeto>();
		for (int i=0; i<ejercicio.getObjetosReconocer().size(); i++)
			objetosEscenario.add(getObjeto(ejercicio.getObjetosReconocer().get(i)));
		return objetosEscenario;
	}

	public Objeto getObjeto(long id) {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_OBJETO, allColumns,
				MySQLiteHelper.COLUMN_OBJETO_ID + " = " + id, null, null, null,
				null);

		if (cursor != null && cursor.getCount() > 0) {
			Objeto objeto = new Objeto();
			cursor.moveToFirst();
			objeto = cursorToObjeto(cursor);
			cursor.close();
			return objeto;
		} else
			return null;
	}

	public Objeto getObjeto(String nombre) {

		Cursor cursor = database.query(MySQLiteHelper.TABLE_OBJETO, allColumns,
				MySQLiteHelper.COLUMN_OBJETO_NOMBRE + " = '" + nombre + "'",
				null, null, null, null);// devuelve el objeto que se pide

		if (cursor != null && cursor.getCount() > 0) {
			Objeto objeto = new Objeto();
			cursor.moveToFirst();
			objeto = cursorToObjeto(cursor);
			cursor.close();
			return objeto;
		} else
			return null;
	}

	private Objeto cursorToObjeto(Cursor cursor) {
		Objeto objeto = new Objeto();
		objeto.setId(cursor.getLong(0));
		objeto.setNombre(cursor.getString(1));
		objeto.setDescripcion(cursor.getString(2));
		try {
			objeto.setFecha(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor
					.getString(3)));
		} catch (ParseException e) {
			Log.e("ERROR_FECHA", "Error al obtener la fecha");
			e.printStackTrace();
			objeto.setFecha(new Date());
		}		
		objeto.setKeypoints(cursor.getString(4));
		objeto.setDescriptores(cursor.getString(5));
		objeto.setCols(cursor.getInt(6));
		objeto.setRows(cursor.getInt(7));
		objeto.setPathImagen(cursor.getString(8));
		objeto.setImagenFromPath();
		objeto.setSonidoDescripcion(cursor.getString(9));
		objeto.setSonidoAyuda(cursor.getString(10));
		objeto.setSonidoNombre(cursor.getString(11));
		objeto.setMats();
		return objeto;
	}

	public Objeto getObjetoSimple(int id) {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_OBJETO, simpleColumns,
				MySQLiteHelper.COLUMN_OBJETO_ID + " = " + id, null, null, null,
				null);

		if (cursor != null && cursor.getCount() > 0) {
			Objeto objeto = new Objeto();
			cursor.moveToFirst();
			objeto.setId(cursor.getLong(0));
			objeto.setNombre(cursor.getString(1));
			/*try{
				objeto.setImagenAsByteArray(cursor.getBlob(2));
			}catch (Exception ex){
				Log.e("ERROR_IMAGEN_OBJETO", "Error al obtener la imagen del objeto");
				ex.printStackTrace();
			}*/
			objeto.setPathImagen(cursor.getString(2));
			objeto.setImagenFromPath();
			cursor.close();
			return objeto;
		} else
		return null;
	}

}
