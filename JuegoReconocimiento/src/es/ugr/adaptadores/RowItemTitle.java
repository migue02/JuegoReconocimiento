package es.ugr.adaptadores;

public class RowItemTitle {
	private String titulo;
	private int imageID;
	
	public RowItemTitle(String tit,int img){
		this.titulo=tit;
		this.imageID=img;
	}
	
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public int getImageID() {
		return imageID;
	}
	public void setImageID(int imageID) {
		this.imageID = imageID;
	}
	
}
