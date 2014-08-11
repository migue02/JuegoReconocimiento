package es.ugr.parserXML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import es.ugr.bdremota.DescargarFicheros;





public class EjercicioHandler extends DefaultHandler{

	public String fichero_local;
	
	private List<EjerciciosMarker> listaEjercicios;
	private ArrayList<String> escenario;
	private ArrayList<String> reconocer;
	private EjerciciosMarker ejercicioActual;
    private StringBuilder sbText;
    public Boolean parsingError = false;
    
    public EjercicioHandler(String fichero){
    	if(!fichero.equals(""))
    		fichero_local=fichero;
    }
    
	public List<EjerciciosMarker> getEjercicios(){
		return listaEjercicios;
	}
	
	@Override
	public void startDocument() throws SAXException{
		
        super.startDocument();
        
        listaEjercicios=new ArrayList<EjerciciosMarker>();
        sbText=new StringBuilder();
        
	}
	
	
	 @Override
	    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		 super.startElement(uri, localName, name, attributes);
		 
	        if (localName.equals("ejercicio")) {
	            ejercicioActual= new EjerciciosMarker();
	        } else if(localName.equals("escenario")){
	        	escenario=new ArrayList<String>();
	        } else if(localName.equals("reconocer")){
	        	reconocer=new ArrayList<String>();
	        }
	 	}
	 
	    @Override
	    public void characters(char[] ch, int start, int length)
	                   throws SAXException {
	 
	        super.characters(ch, start, length);
	 
	        if (this.ejercicioActual != null)
	            sbText.append(ch, start, length);
	    }
		
		
		@Override
	    public void endElement(String uri, String localName, String name)
	                   throws SAXException {
	 
	        super.endElement(uri, localName, name);
	        int valueint;
	        if (this.ejercicioActual != null) {
	 
	            if (localName.equals("nombre")) {
	                 ejercicioActual.setNombre(sbText.toString().trim());
	            } else if (localName.equals("objetoes")){
	            	escenario.add(sbText.toString().trim());
	            } else if (localName.equals("escenario")){
	            	ejercicioActual.setEscenario(escenario);
	            } else if (localName.equals("descripcion")) {
	            	 ejercicioActual.setDescripcion(sbText.toString().trim());	
	            } else if (localName.equals("duracion")) {
	            	valueint=Integer.parseInt(sbText.toString().trim());
	                ejercicioActual.setDuracion(valueint);
	            } else if (localName.equals("objetore")){
	            	reconocer.add(sbText.toString().trim());
	            } else if (localName.equals("reconocer")){
	            	ejercicioActual.setReconocer(reconocer);
	            }  else if (localName.equals("ejercicio")) {
	                listaEjercicios.add(ejercicioActual);
	            } else if(localName.equals("sonido_descripcion_rem")){
                    if (!sbText.toString().trim().equals("")){
                    	String sonido_descripcion_local="/mnt/sdcard/JuegoReconocimiento/sounds/"+ejercicioActual.getNombre()+".mp3";
                    	new DescargarFicheros().execute(sbText.toString().trim(),sonido_descripcion_local);
                    	ejercicioActual.setSonidoDescripcion(sonido_descripcion_local);
                    } else
                    	ejercicioActual.setSonidoDescripcion("");
	            	
	            } else if(localName.equals("sonido_descripcion_loc")){
                    if (!sbText.toString().trim().equals("")){
                    	String sonido_descripcion_local="/mnt/sdcard/JuegoReconocimiento/sounds/"+ejercicioActual.getNombre()+".mp3";
                    	sonido_descripcion_local=sonido_descripcion_local.replaceAll("\\s+", "_");
                    	try {
							copy(new File(new File(fichero_local).getParent()+sbText.toString().trim()),new File(sonido_descripcion_local));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    	//new DescargarFicheros().execute(sbText.toString().trim(),sonido_descripcion_local);
                    	ejercicioActual.setSonidoDescripcion(sonido_descripcion_local);
                    } else
                    	ejercicioActual.setSonidoDescripcion("");
	            	
	            }
	 
	            sbText.setLength(0);
	        }
	    }
		
	 
	 
		public void copy(File src, File dst) throws IOException {
		    InputStream in = new FileInputStream(src);
		    OutputStream out = new FileOutputStream(dst);

		    // Transfer bytes from in to out
		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0) {
		        out.write(buf, 0, len);
		    }
		    in.close();
		    out.close();
		}
		
}
