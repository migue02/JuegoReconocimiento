package es.ugr.parserXML;

public class EjerciciosMarker {

	private String Nombre;
	private String Descripcion;
	private Double Duracion;
	
	public EjerciciosMarker(){
		this.Nombre="";
		this.Descripcion="";
		this.Duracion=0.0;
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
	
}
