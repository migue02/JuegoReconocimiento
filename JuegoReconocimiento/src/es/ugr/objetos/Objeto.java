package es.ugr.objetos;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

import es.ugr.juegoreconocimiento.R;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.Log;
import es.ugr.utilidades.Utilidades;

public class Objeto{
	private long id;
	private String nombre;
	private String descripcion;
	private Date fecha;
	private String keypoints;
	private String descriptores;
	public MatOfKeyPoint matKeyPoints;
	public Mat matDescriptores;
	private int cols;
	private int rows;
	private Bitmap imagen;
	private String pathImagen;
	private String pathSonidoDescripcion;
	private String pathSonidoAyuda;
	private String pathSonidoNombre;
	
	private MediaPlayer player = new MediaPlayer();

	private void playSonido(String path, String pathError){
		if(player.isPlaying()){
			while(player.isPlaying());
			player.release();
			player = new MediaPlayer();
		}		
		try{
			if (path.length() > 0)
				player.setDataSource(path);
			else
				player.setDataSource(pathError);
		    player.prepare();
		    player.start();
		} catch (IOException e){
			try{
				player.setDataSource(pathError);
			    player.prepare();
			    player.start();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		
	}
	
	public void playSonidoDescripcion(Context context){
		playSonido(pathSonidoDescripcion,context.getString(R.string.pathSounds)+"/descripcion.mp3");
	}
	
	public void playSonidoAyuda(Context context){
		playSonido(pathSonidoAyuda,context.getString(R.string.pathSounds)+"/ayuda.mp3");	
	}
	
	public void playSonidoNombre(Context context){
		playSonido(pathSonidoNombre,context.getString(R.string.pathSounds)+"/nombre.mp3");
	}
	
	public void setImagenFromPath(){
		try {
			File imageFile = new File(pathImagen+"/"+nombre+".png");
			imagen = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
			Log.e("IMAGEN_CREADA", "Imagen creada en "+pathImagen);
		} catch (Exception e) {
			Log.e("ERROR_CREAR_IMAGEN", "Error al crear la imagen en "+pathImagen);
		}
	}
	
	public void guardarImagen(){
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(pathImagen+"/"+nombre+".png"); //el path es /mnt/sdcard/JuegoReconocimiento/imagenes/
			imagen.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{
				out.close();
			} catch(Throwable ignore) {}
		}

	}
	
	public Objeto(long id, String nombre, String descripcion, Date fecha, String keypoints,
			String descriptores, int cols, int rows, String pathImagen,
			String sonidoDescripcion, String sonidoAyuda, String sonidoNombre) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.fecha = fecha;
		this.keypoints = keypoints;
		this.descriptores = descriptores;
		matDescriptores = new Mat();
		matKeyPoints = new MatOfKeyPoint();
		matKeyPoints = Utilidades.keypointsFromJson(this.keypoints);
		matDescriptores = Utilidades.matFromJson(this.descriptores);
		this.cols = cols;
		this.rows = rows;
		this.pathImagen = pathImagen;
		this.pathSonidoDescripcion = sonidoDescripcion;
		this.pathSonidoAyuda = sonidoAyuda;
		this.pathSonidoNombre = sonidoNombre;
		imagen = null;
	}

	public Date getFecha() {
		return fecha;
	}
	
	public String getFechaAsString() {
		SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
		return date.format(fecha);
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getPathImagen() {
		return pathImagen;
	}

	public void setPathImagen(String pathImagen) {
		this.pathImagen = pathImagen;
	}

	public String getSonidoDescripcion() {
		return pathSonidoDescripcion;
	}

	public void setSonidoDescripcion(String sonidoDescripcion) {
		this.pathSonidoDescripcion = sonidoDescripcion;
	}

	public String getSonidoAyuda() {
		return pathSonidoAyuda;
	}

	public void setSonidoAyuda(String sonidoAyuda) {
		this.pathSonidoAyuda = sonidoAyuda;
	}

	public String getSonidoNombre() {
		return pathSonidoNombre;
	}

	public void setSonidoNombre(String sonidoNombre) {
		this.pathSonidoNombre = sonidoNombre;
	}
	

	public Objeto() {
		id=-1;
		nombre="";
		descripcion="";
		fecha = new Date();
		keypoints="";
		descriptores="";
		cols=0;
		rows=0;
		imagen=null;
		pathSonidoAyuda="";
		pathSonidoDescripcion="";
		pathSonidoNombre="";
	}

	public Objeto(long id, String nombre, String keypoints,
			String descriptores, int cols, int rows, Bitmap imagen) {
		this.id = id;
		this.nombre = nombre;
		this.keypoints = keypoints;
		this.descriptores = descriptores;
		matDescriptores = new Mat();
		matKeyPoints = new MatOfKeyPoint();
		matKeyPoints = Utilidades.keypointsFromJson(this.keypoints);
		matDescriptores = Utilidades.matFromJson(this.descriptores);
		this.cols = cols;
		this.rows = rows;
		this.imagen=imagen;
		this.fecha = new Date();
		pathSonidoAyuda="";
		pathSonidoDescripcion="";
		pathSonidoNombre="";
	}

	public Bitmap getImagen() {
		return imagen;
	}

	public void setImagen(Bitmap imagen) {
		this.imagen = imagen;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getKeypoints() {
		return keypoints;
	}

	public void setKeypoints(String keypoints) {
		this.keypoints = keypoints;
	}

	public String getDescriptores() {
		return descriptores;
	}

	public void setDescriptores(String descriptores) {
		this.descriptores = descriptores;
	}	
	
	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return id + ".- " + nombre +" KPnts "+ matKeyPoints.size() ;
	}

	public void setMats() {
		matDescriptores = new Mat();
		matKeyPoints = new MatOfKeyPoint();
		matKeyPoints = Utilidades.keypointsFromJson(this.keypoints);
		matDescriptores = Utilidades.matFromJson(this.descriptores);
	}

	public byte[] getImagenAsByteArray() {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
	}

	public void setImagenAsByteArray(byte[] blob) {
		imagen = BitmapFactory.decodeByteArray(blob, 0, blob.length);
	}
}
