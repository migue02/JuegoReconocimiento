package es.ugr.objetos;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

import es.ugr.utilidades.Utilidades;

public class Objeto {
	private long id;
	private String nombre;
	private String keypoints;
	private String descriptores;
	public MatOfKeyPoint matKeyPoints;
	public Mat matDescriptores;
	private int cols;
	private int rows;

	public Objeto() {
		id=-1;
		nombre="";
		keypoints="";
		descriptores="";
		cols=0;
		rows=0;
	}

	public Objeto(long id, String nombre, String keypoints,
			String descriptores, int cols, int rows) {
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
}
