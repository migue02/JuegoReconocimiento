package es.ugr.parserXML;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class EjercicioParser {

	private URL rssUrl;
	
    public EjercicioParser(String url)
    {
        try
        {
            this.rssUrl = new URL(url);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public List<EjerciciosMarker> parse() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
 
        try
        {
            SAXParser parser = factory.newSAXParser();
            EjercicioHandler handler = new EjercicioHandler();
            parser.parse(this.getInputStream(), handler);
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
    
    
    
    
}
