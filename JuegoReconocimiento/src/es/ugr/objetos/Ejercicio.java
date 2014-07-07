package es.ugr.objetos;

import java.util.ArrayList;

public class Ejercicio {
	
	private int idEjercicio;
	private String nombre;
	private ArrayList<Integer> objetos;//escenario
	private String descripcion;
	private int duracion;
	private ArrayList<Integer> objetosReconocer;
	private int orden;
	
	public Ejercicio() {
		this.idEjercicio=-1;
		this.nombre="";
		this.objetos=new ArrayList<Integer>();
		this.duracion=0;
		this.descripcion = "";
		this.objetosReconocer=new ArrayList<Integer>();
	}

	public Ejercicio(int idEjercicio, String nombre, ArrayList<Integer> objetos, String descripcion, int duracion, ArrayList<Integer> objetosReconocer) {
		this.idEjercicio = idEjercicio;
		this.nombre = nombre;
		this.objetos = new ArrayList<Integer>(objetos);
		this.descripcion = descripcion;
		this.duracion=duracion;
		this.objetosReconocer=new ArrayList<Integer>(objetosReconocer);
	}
	
	public Ejercicio(int idEjercicio, String nombre, String objetos, String descripcion, int duracion, String objetosReconocer) {
		try {
			this.idEjercicio = idEjercicio;
			this.nombre = nombre;
			this.objetos = es.ugr.utilidades.Utilidades.ArrayListFromJson(objetos);	
			this.descripcion = descripcion;
			this.duracion=duracion;
			this.objetosReconocer=es.ugr.utilidades.Utilidades.ArrayListFromJson(objetosReconocer);
		}catch (Exception e){
			new Ejercicio();
		}
	}

	public ArrayList<Integer> getObjetosReconocer() {
		return objetosReconocer;
	}

	public void setObjetosReconocer(ArrayList<Integer> objetosReconocer) {
		this.objetosReconocer = objetosReconocer;
	}

	@Override
	public String toString() {
		return nombre + " .Objetos["+objetosReconocer.toString()+"] .Escenario["+objetos.toString()+"]";
		//return "Ejercicio [idEjercicio=" + idEjercicio + ", nombre=" + nombre
			//	+ ", objetos=" + objetos + descripcion + duracion + "]";
	}

	public Integer getIdEjercicio() {
		return idEjercicio;
	}

	public void setIdEjercicio(Integer idEjercicio) {
		this.idEjercicio = idEjercicio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public ArrayList<Integer> getObjetos() {
		return objetos;
	}

	public void setObjetos(ArrayList<Integer> objetos) {
		this.objetos = objetos;
	}	
	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public void eliminaObjetoReconocer(int id) {
		for (int i=0;i<objetosReconocer.size();i++)
			if(objetosReconocer.get(i)==id){
				objetosReconocer.remove(i);
				eliminaObjetoEscenario(id);
			}
	}

	public void eliminaObjetoEscenario(int id) {
		for (int i=0;i<objetos.size();i++)
			if(objetos.get(i)==id){
				objetos.remove(i);
				eliminaObjetoReconocer(id);
			}
	}


}
