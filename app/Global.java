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
        Formatters.register(Comunitat.class, new Formatters.SimpleFormatter<Comunitat>()
                     {
        
                      
         public Comunitat parse(String text, Locale locale) 
            throws java.text.ParseException {     
     	 	return Comunitat.recercaPerNif(text);                     
         }
                
        
		@Override
		public String print(Comunitat arg0, Locale arg1) {
			// TODO Auto-generated method stub
			return arg0.nif;
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
}

}