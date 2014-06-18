package es.ugr.parserXML;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;





public class EjercicioHandler extends DefaultHandler{

	private List<EjerciciosMarker> listaEjercicios;
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
	        double value;
	        if (this.ejercicioActual != null) {
	 
	            if (localName.equals("nombre")) {
	                 ejercicioActual.setNombre(sbText.toString());
	            } else if (localName.equals("descripcion")) {
	            	 ejercicioActual.setDescripcion(sbText.toString());	
	            } else if (localName.equals("duracion")) {
	            	value = Double.parseDouble(sbText.toString());	
	                ejercicioActual.setDuracion(value);
	            }  else if (localName.equals("ejercicio")) {
	                listaEjercicios.add(ejercicioActual);
	            }
	 
	            sbText.setLength(0);
	        }
	    }
		
	 
	 
}
