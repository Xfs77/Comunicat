package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Comunitat;
import models.Page;
import models.Usuari;
import play.i18n.Messages;
import play.mvc.Security;
import play.data.Form;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import play.mvc.Controller;

import com.google.common.io.Files;

import play.mvc.Http.MultipartFormData;


import play.db.jpa.Transactional;
import views.html.*;




//@Security.Authenticated(Secured.class)
public class Comunitats  extends Controller {

	private static final Form<Comunitat> comunitatForm = Form.form(Comunitat.class);

	@Transactional(readOnly=true)
    public static Result llistarComunitats(String id, int page) {
		Page p =Comunitat.llistarComunitats(id, page);
		List<Comunitat> list = p.getList();
		return ok(llista_comunitats.render(list,p,id));
	}
	
	public static Result novaComunitat(String pare) {
	    return ok(detalls_comunitat.render(comunitatForm, pare));
	  }
	
	 @Transactional
	 public static Result guardarComunitat(String pare) {
		 play.mvc.Http.Request request = request() ;
		 RequestBody body = request().body();
		 Form<Comunitat> boundForm = comunitatForm.bindFromRequest();
		 if(boundForm.hasErrors()) {
		      flash("error", Messages.get("constraint.formulari"));
		      return badRequest(detalls_comunitat.render(boundForm));
		    }
		 else{
		    comunitat.guardarComunitat();
		    flash("success",
		        String.format("L'usuari %s s'ha registrat correctament", usuari));

		    return redirect(routes.Comunitats.llistarComunitats(pare,1));
		  }
}
}