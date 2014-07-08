package es.ugr.objetos;


import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	private String sonidoDescripcion;
	private String sonidoAyuda;
	private String sonidoNombre;
	
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
		this.sonidoDescripcion = sonidoDescripcion;
		this.sonidoAyuda = sonidoAyuda;
		this.sonidoNombre = sonidoNombre;
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
		return sonidoDescripcion;
	}

	public void setSonidoDescripcion(String sonidoDescripcion) {
		this.sonidoDescripcion = sonidoDescripcion;
	}

	public String getSonidoAyuda() {
		return sonidoAyuda;
	}

	public void setSonidoAyuda(String sonidoAyuda) {
		this.sonidoAyuda = sonidoAyuda;
	}

	public String getSonidoNombre() {
		return sonidoNombre;
	}

	public void setSonidoNombre(String sonidoNombre) {
		this.sonidoNombre = sonidoNombre;
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
		sonidoAyuda="";
		sonidoDescripcion="";
		sonidoNombre="";
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
		sonidoAyuda="";
		sonidoDescripcion="";
		sonidoNombre="";
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
