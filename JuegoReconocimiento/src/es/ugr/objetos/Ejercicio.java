package es.ugr.objetos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Ejercicio {
	
	private int idEjercicio;
	private String nombre;
	private Date fecha;
	private ArrayList<String> objetos;//escenario
	private String descripcion;
	private int duracion;
	private ArrayList<String> objetosReconocer;
	private int orden;
	private String sonido_descripcion;
	
	public Ejercicio() {
		this.idEjercicio=-1;
		this.nombre="";
		this.fecha = new Date();
		this.objetos=new ArrayList<String>();
		this.duracion=0;
		this.descripcion = "";
		this.objetosReconocer=new ArrayList<String>();
		this.sonido_descripcion="";
	}
	
	public Ejercicio(Ejercicio pEjercicio) {
		super();
		this.idEjercicio = pEjercicio.idEjercicio;
		this.nombre = pEjercicio.nombre;
		this.fecha = pEjercicio.fecha;
		this.objetos = new ArrayList<String>(pEjercicio.objetos);
		this.descripcion = pEjercicio.descripcion;
		this.duracion = pEjercicio.duracion;
		this.objetosReconocer = new ArrayList<String>(pEjercicio.objetosReconocer);
		this.orden = pEjercicio.orden;
		this.sonido_descripcion = pEjercicio.sonido_descripcion;
	}
	
	public Ejercicio(String nombre, Date fecha, ArrayList<String> objetos,
			String descripcion, int duracion,
			ArrayList<String> objetosReconocer,
			String sonido_descripcion) {
		super();
		this.nombre = nombre;
		this.fecha = fecha;
		this.objetos = objetos;
		this.descripcion = descripcion;
		this.duracion = duracion;
		this.objetosReconocer = objetosReconocer;
		this.orden = 0;
		this.sonido_descripcion = sonido_descripcion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public String getFechaAsString() {
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return date.format(fecha);
	}

	public String getSonido_descripcion() {
		return sonido_descripcion;
	}

	public void setSonido_descripcion(String sonido_descripcion) {
		this.sonido_descripcion = sonido_descripcion;
	}

	public Ejercicio(int idEjercicio, String nombre, String objetos, String descripcion, int duracion, String objetosReconocer) {
		try {
			this.idEjercicio = idEjercicio;
			this.nombre = nombre;
			this.fecha = new Date();
			this.sonido_descripcion="";
			this.objetos = es.ugr.utilidades.Utilidades.ArrayListFromJson(objetos);	
			this.descripcion = descripcion;
			this.duracion=duracion;
			this.objetosReconocer=es.ugr.utilidades.Utilidades.ArrayListFromJson(objetosReconocer);
		}catch (Exception e){
			new Ejercicio();
		}
	}

	public ArrayList<String> getObjetosReconocer() {
		return objetosReconocer;
	}

	public void setObjetosReconocer(ArrayList<String> objetosReconocer) {
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

	public ArrayList<String> getObjetos() {
		return objetos;
	}

	public void setObjetos(ArrayList<String> objetos) {
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

	public void eliminaObjetoReconocer(String nombre) {
		for (int i=0;i<objetosReconocer.size();i++)
			if(objetosReconocer.get(i).equals(nombre)){
				objetosReconocer.remove(i);
				eliminaObjetoEscenario(nombre);
			}
	}

	public void eliminaObjetoEscenario(String nombre) {
		for (int i=0;i<objetos.size();i++)
			if(objetos.get(i).equals(nombre)){
				objetos.remove(i);
				eliminaObjetoReconocer(nombre);
			}
	}

	public void setIdEjercicio(long idEjercicio) {
		this.idEjercicio = (int)idEjercicio;
	}

	public void insertaObjetoEscenario(Objeto objeto) {
		if (!objetos.contains(objeto.getNombre()))
			objetos.add(objeto.getNombre());
	}

	public void insertaObjetoReconocer(Objeto objeto) {
		if (!objetosReconocer.contains(objeto.getNombre())){
			objetosReconocer.add(objeto.getNombre());
			insertaObjetoEscenario(objeto);
		}
	}

}
