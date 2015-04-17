package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Comunitat;
import models.Element;
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

import org.hibernate.exception.SQLGrammarException;


//@Security.Authenticated(Secured.class)
public class Comunitats extends Controller {

	private static Form<Comunitat> comunitatForm = Form.form(Comunitat.class);
	private static Form<Element> elementForm = Form.form(Element.class);


	public static Result novaComunitat(Comunitat pare) {
		Comunitat c = new Comunitat(pare);
		comunitatForm.fill(c);
		return ok(detalls_comunitat.render(comunitatForm, pare));
	}

	@Transactional(readOnly = true)
	public static Result detallComunitat(Comunitat comunitat) {
		Form<Comunitat> filledForm = comunitatForm.fill(comunitat);
		return ok(detalls_comunitat.render(filledForm, comunitat.pare));
	}

	@Transactional(readOnly = true)
	public static Result llistarComunitats(int page) {
		Comunitat pare=Comunitat.recercaPerNif("ARREL");
		Page p = Comunitat.llistarSubComunitats(pare, page);
		List l = p.getList();
		return ok(llista_comunitats.render(l, p, pare));
	}	
	
	@Transactional(readOnly = true)
	public static Result llistarSubComunitats(Comunitat pare, int page) {
		Page p = Comunitat.llistarSubComunitats(pare, page);
		List l = p.getList();
		return ok(llista_comunitats.render(l, p, pare));
		
	}

	@Transactional
	public static Result borrarComunitat(Comunitat comunitat) {
		if (comunitat == null) {
			return notFound(String.format("La Comunitat %s no existeix.",
					comunitat.nom));
		}
		Comunitat.borrarComunitat(comunitat);
		return redirect(routes.Comunitats.llistarSubComunitats(comunitat.pare,
				1));
	}

	@Transactional
	public static Result guardarComunitat(Comunitat pare) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<Comunitat> boundForm = comunitatForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			flash("error", Messages.get("constraint.formulari"));
			return badRequest(detalls_comunitat.render(boundForm, pare));
		} else {
			Comunitat comunitat = boundForm.get();
			comunitat.pare = pare;
			comunitat.guardarComunitat();
			flash("success", String.format(
					"La comunitat %s s'ha registrat correctament",
					comunitat.nom));

			return redirect(routes.Comunitats.llistarSubComunitats(pare, 1));
		}
	}
	
	@Transactional(readOnly = true)
	public static Result llistarElements(Comunitat comunitat, int page) {
		Page p = Element.llistarElements(comunitat, page);
		List l = p.getList();
		return ok(llista_elements.render(l, p, comunitat));
	}	
	
	@Transactional
	public static Result borrarElement(Element element) {
		if (element == null) {
			return notFound(String.format("L'element %s no existeix.",
					element.codi));
		}
		Element.borrarElement(element);
		return redirect(routes.Comunitats.llistarElements(element.comunitat,
				1));
	}
	
	
	@Transactional(readOnly = true)
	public static Result detallElement(Element element) {
		Form<Element> filledForm = elementForm.fill(element);
		return ok(detalls_element.render(filledForm, element.comunitat));
	}
	
	@Transactional
	public static Result guardarElement(Comunitat comunitat) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<Element> boundForm = elementForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			flash("error", Messages.get("constraint.formulari"));
			return badRequest(detalls_element.render(boundForm, comunitat));
		} 
		else {
			Element element = boundForm.get();
			element.comunitat=comunitat;
			element.guardarElement();
			Element e=element;
			flash("success", String.format(
					"L'element %s s'ha registrat correctament",
					element.codi));

			return redirect(routes.Comunitats.llistarElements(comunitat, 1));
		}
	}
	
	
}