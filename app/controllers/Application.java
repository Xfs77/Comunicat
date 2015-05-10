package controllers;

import models.Login;
import play.*;
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	private static final Form<Login> loginForm = Form.form(Login.class);

	
public static Result login() {
		
		return ok(login.render(Form.form(Login.class)));
	
	}

public static Result predeterminado() {
	
	String idiom =lang().language();
	
	return ok(ajax_result.render(idiom));
	
	}

public static Result idioma(String idioma) {
	changeLang(idioma); 
	return ok(index.render("live stream"));
}


public static Result logout() {
    session().clear();
    flash("success", Messages.get("has_desconnectat"));
    return redirect(
        routes.Application.index()
    );
}

@Transactional(readOnly=true)
public static Result authenticate() {  
	  Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
	  if (loginForm.hasErrors()) {
	        return badRequest(login.render(loginForm));
	  } 
	  else {
	        session().clear();
	        session("dni", loginForm.get().dni);
	        return redirect(
	            routes.Application.index());
	    }
	    
}
	
	public static Result index() {
		String idiom =lang().language();
    	return ok(index.render("Your new application is ready."));
    }

	
	 
	  public static Result javascriptRoutes() {
	      response().setContentType("text/javascript");
	      return ok(
	        Routes.javascriptRouter("jsRoutes",
	          // Routes
	          controllers.routes.javascript.Application.index(),
	          controllers.routes.javascript.Application.predeterminado(),
	          controllers.routes.javascript.Application.idioma(),
	          controllers.routes.javascript.Notes.borrarNota()


	        )
	      );
	    }
}
