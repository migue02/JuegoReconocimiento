package es.ugr.parserXML;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;





public class EjercicioHandler extends DefaultHandler{

	private List<EjerciciosMarker> listaEjercicios;
	private ArrayList<String> escenario;
	private ArrayList<String> reconocer;
	private EjerciciosMarker ejercicioActual;
    private StringBuilder sbText;
    public Boolean parsingError = false;
    
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
	        double value;
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
	            } else if(localName.equals("sonido_descripcion")){
	            	ejercicioActual.setSonidoDescripcion(sbText.toString().trim());
	            }
	 
	            sbText.setLength(0);
	        }
	    }
		
	 
	 
}
