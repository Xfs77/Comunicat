import play.*;
import play.libs.*;

import java.util.*;

import models.*;
import play.data.format.Formatters;
import play.data.format.Formatters.*;
import play.db.jpa.JPA;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Global extends GlobalSettings {
    
    public void onStart(Application app) {
        // Register our DateFormater
    Formatters.register(Date.class, new SimpleFormatter<Date>() {
    
     private final static String PATTERN = "dd/MM/yyyy";
            
     public Date parse(String text, Locale locale) 
        throws java.text.ParseException {     
       if(text == null || text.trim().isEmpty()) {
         return null;
       }
       SimpleDateFormat sdf = 
         new SimpleDateFormat(PATTERN, locale);    
       sdf.setLenient(false);  
       return sdf.parse(text);                     
     }
            
     public String print(Date value, Locale locale){
       if(value == null) {
        return "";
       }
       return new SimpleDateFormat(PATTERN, locale)
                  .format(value);                   
     }
            
    });
    	
    	Formatters.register(Comunitat.class, new Formatters.SimpleFormatter<Comunitat>()
                     {
        
                      
         public Comunitat parse(String text, Locale locale) 
            throws java.text.ParseException {   
        	 Comunitat comunitat=Comunitat.recercaPerNif(text);
     	 	return comunitat;                     
         }
                
        
		@Override
		public String print(Comunitat arg0, Locale arg1) {
			// TODO Auto-generated method stub
			return arg0.nif;
		}
                
        });
    	
    	Formatters.register(EstatNota.class, new Formatters.SimpleFormatter<EstatNota>()
                {
   
                 
    public EstatNota parse(String text, Locale locale) 
       throws java.text.ParseException {     
	 	String t =text;
    	EstatNota estat= EstatNota.recerca(text);
    	return estat;                     
    }
           
   
	@Override
	public String print(EstatNota arg0, Locale arg1) {
		// TODO Auto-generated method stub
		return arg0.estat;
	}
           
   });
        
        Formatters.register(Element.class, new Formatters.SimpleFormatter<Element>()
                {
   
                 
    public Element parse(String text, Locale locale) 
       throws java.text.ParseException {     
    	final String comunitat = text.substring(0,text.indexOf('&'));
		final String element=text.substring(text.indexOf('&')+1);
		Comunitat c = (Comunitat.recercaPerNif(comunitat));
		Element	e = (Element.recercaPerCodi(c,element));     
		return e;
    }
           
  


	@Override
	public String print(Element arg0, Locale arg1) {
		// TODO Auto-generated method stub
		return (arg0.comunitat.nif+"&"+arg0.codi);
	}
           
   });
       
    
    
    Formatters.register(Usuari.class, new Formatters.SimpleFormatter<Usuari>()
            {

             
public Usuari parse(String text, Locale locale) 
   throws java.text.ParseException {     
	Usuari	u = (Usuari.recercaPerDni(text));     
	return u;
}
       



@Override
public String print(Usuari arg0, Locale arg1) {
	// TODO Auto-generated method stub
	return (arg0.dni);
}
       
});
   
    
    Formatters.register(TipusVei.class, new Formatters.SimpleFormatter<TipusVei>()
            {

             
public TipusVei parse(String text, Locale locale) 
   throws java.text.ParseException {     
	TipusVei t = (TipusVei.recerca(text));     
	return t;
}
       



@Override
public String print(TipusVei arg0, Locale arg1) {
	// TODO Auto-generated method stub
	return (arg0.tipus);
}
       
});



    Formatters.register(Nota.class, new Formatters.SimpleFormatter<Nota>()
            {

             
public Nota parse(String text, Locale locale) 
   throws java.text.ParseException {     
	Nota n = (Nota.recercaPerCodi(Integer.parseInt(text)));     
	return n;
}

@Override
public String print(Nota arg0, Locale arg1) {
	// TODO Auto-generated method stub
	return Integer.toString(arg0.codi);
}
       



});
	
    Formatters.register(MovimentNota.class, new Formatters.SimpleFormatter<MovimentNota>()
            {

             
        
public MovimentNota parse(String arg1, Locale locale) 
throws java.text.ParseException {     
	final String nota = arg1.substring(0,arg1.indexOf('&'));
	final String moviment=arg1.substring(arg1.indexOf('&')+1);
	Nota n = (Nota.recercaPerCodi(Integer.parseInt(nota)));
	MovimentNota m = (MovimentNota.recercaPerCodi(n,Integer.parseInt(moviment)));
	return m;	
}
    



@Override
public String print(MovimentNota arg0, Locale arg1) {
	// TODO Auto-generated method stub
	return (Integer.toString(arg0.nota.codi)+"&"+Integer.toString(arg0.codi));
}
    
});
    
}
 
    
}