package es.ugr.parserXML;

import java.util.ArrayList;

public class EjerciciosMarker {

	private String Nombre;
	private ArrayList<String> Escenario;
	private String Descripcion;
	private Double Duracion;
	private ArrayList<String> Reconocer;
	
	public EjerciciosMarker(){
		this.Nombre="";
		this.Escenario=new ArrayList<String>();
		this.Descripcion="";
		this.Duracion=0.0;
		this.Reconocer=new ArrayList<String>();
	}
	
	public String getNombre() {
		return Nombre;
	}
	
	public void setNombre(String nombre) {
		Nombre = nombre;
	}
	
	public String getDescripcion() {
		return Descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	
	public Double getDuracion() {
		return Duracion;
	}
	
	public void setDuracion(Double duracion) {
		Duracion = duracion;
	}

	public ArrayList<String> getEscenario() {
		return Escenario;
	}

	public void setEscenario(ArrayList<String> escenario) {
		Escenario = escenario;
	}

	public ArrayList<String> getReconocer() {
		return Reconocer;
	}

	public void setReconocer(ArrayList<String> reconocer) {
		Reconocer = reconocer;
	}
	
}
