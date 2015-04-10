import play.*;
import play.libs.*;

import java.util.*;

import models.*;
import play.data.format.Formatters;
import play.data.format.Formatters.*;

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
       
    }
}