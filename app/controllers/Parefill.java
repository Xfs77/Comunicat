package controllers;

import java.util.List;

import models.Comunitat;
import models.Element;
import models.Fill;
import models.Pare;
import models.Usuari;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.detalls_comunitat;


@Security.Authenticated(Secured.class)
public class Parefill extends Controller {

	
	@Transactional
	public static Result borrarPare() {
		try{
			Pare.borrarPare();
		}catch(Exception e){
					return TODO;

		}
		return ok("");

	}
	
	@Transactional
	public static Result Fills() {
		Fill.Fills();;
		return ok("");
	}
}