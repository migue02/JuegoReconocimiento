package es.ugr.parserXML;

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
	
    public EjercicioParser(String url)
    {
            //this.rssUrl = new URL(url);
            this.fichero=url;

    }
    
    public List<EjerciciosMarker> parse() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
 
        try
        {
            SAXParser parser = factory.newSAXParser();
            EjercicioHandler handler = new EjercicioHandler();
            parser.parse(this.getInputStream2(), handler);
            return handler.getEjercicios();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }
    
    
}
