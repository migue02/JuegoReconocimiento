package es.ugr.objetos;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import es.ugr.utilidades.Utilidades;

public class SerieEjercicios implements Serializable {
	
	private int idSerie;
	private String nombre;
	private ArrayList<Integer> ejercicios;
	private int duracion;
	private Date fecha_modificacion;
	private int orden;
	
	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public SerieEjercicios() {
		this.idSerie=-1;
		this.nombre="";
		this.ejercicios=new ArrayList<Integer>();
		this.duracion=0;
		this.fecha_modificacion= new Date(); 
	}

	public SerieEjercicios(int idSerie, String nombre, ArrayList<Integer> ejercicios, int duracion, Date fecha_modificacion) {
		this.idSerie = idSerie;
		this.nombre = nombre;
		this.ejercicios = new ArrayList<Integer>(ejercicios);
		this.duracion=duracion;
		this.fecha_modificacion= fecha_modificacion; 
	}
	
	public SerieEjercicios(int idSerie, String nombre, String ejercicios, int duracion, Date fecha_modificacion) {
		try {
			this.idSerie = idSerie;
			this.nombre = nombre;
			this.ejercicios = Utilidades.ArrayListFromJsonInt(ejercicios);	
			this.duracion=duracion;
			this.fecha_modificacion= fecha_modificacion; 
		}catch (Exception e){
			new SerieEjercicios();
		}
	}

	@Override
	public String toString() {
		return nombre;
		//return "Serie.-"+ nombre +" "+ getFecha_modificacion_AsStrign();
	}

	public int getIdSerie() {
		return idSerie;
	}

	public void setIdSerie(int idSerie) {
		this.idSerie = idSerie;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public ArrayList<Integer> getEjercicios() {
		return ejercicios;
	}

	public void setEjercicios(ArrayList<Integer> ejercicios) {
		this.ejercicios = ejercicios;
	}
	
	public String getFecha_modificacion_AsStrign() {
		SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
		return date.format(fecha_modificacion);
	}
	
	public Date getFecha_modificacion_AsDate() {
		return fecha_modificacion;
	}
	
	public void setFecha_modificacion(Date fecha_modificacion) {
		this.fecha_modificacion = fecha_modificacion;
	}

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}
	
}
