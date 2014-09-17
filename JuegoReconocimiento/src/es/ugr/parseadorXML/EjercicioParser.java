package es.ugr.parseadorXML;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class EjercicioParser {

	private URL rssUrl;
	private String fichero;
	private boolean local;
	
    public EjercicioParser(String url,String tipo)
    {
            //this.rssUrl = new URL(url);

            if(tipo=="URL"){
            	local=false;
            	try {
					this.rssUrl=new URL(url);
					this.fichero="";//Se importa desde remoto
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            if(tipo=="Fichero"){
            	local=true;//Se importa desde fichero local
                this.fichero=url;
            }

    }
    
    public List<EjerciciosMarker> parse() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
 
        try
        {
            SAXParser parser = factory.newSAXParser();
            EjercicioHandler handler = new EjercicioHandler(this.fichero);
            if (local==true)
            	parser.parse(this.getInputStream2(), handler);
            else
            	parser.parse(this.getInputStream(), handler);
            return handler.getEjercicios();
        }
        catch (Exception e)
        {
            //throw new RuntimeException(e);
        	return null;
        }
    }
    
    
    private InputStream getInputStream()
    {
        try
        {
            return rssUrl.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            //throw new RuntimeException(e);
        	return null;
        }
    }
    
    private InputStream getInputStream2()
    {
        try
        {
        	FileInputStream in; 
        	in = new FileInputStream(fichero);
        	//in = new FileInputStream(fichero);
        	return in;
        }
        catch (IOException e)
        {
            //throw new RuntimeException(e);
        	return null;
        }
    }
    
    
}
