package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import models.Comunitat;
import models.Element;
import models.ElementVei;
import models.Page;
import models.Usuari;
import play.i18n.Messages;
import play.mvc.Security;
import play.data.Form;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import play.mvc.Controller;
import be.objectify.as.AsyncTransactional;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import com.google.common.io.Files;

import play.mvc.Http.MultipartFormData;
import play.db.jpa.Transactional;
import views.html.*;

import org.hibernate.JDBCException;
import org.hibernate.exception.SQLGrammarException;

@Security.Authenticated(Secured.class)
public class Comunitats extends Controller {

	private static Form<Comunitat> comunitatForm = Form.form(Comunitat.class);
	private static Form<Element> elementForm = Form.form(Element.class);

	@Restrict({@Group("A")})
	@Transactional(readOnly = true)
	public static Result novaComunitat(Comunitat pare) {
		Comunitat comunitat = new Comunitat(pare);
		comunitatForm.fill(comunitat);
		List<Usuari> lu=null;
		try {
			lu = Usuari.obtenirUsuarisPresi();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok(detalls_comunitat.render(comunitatForm, pare, lu,true));
	}
	@Restrict({@Group("A")})
	@Transactional(readOnly = true)
	public static Result detallComunitat(Comunitat comunitat) {
		Form<Comunitat> filledForm = comunitatForm.fill(comunitat);
		List<Usuari> lu = null;
		try {
			lu=Usuari.obtenirUsuarisPresi();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok(detalls_comunitat.render(filledForm, comunitat.pare, lu,false));
	}
	@Restrict({@Group("A"),@Group("O")})
	@AsyncTransactional
	public static Result llistarComunitats(int page) {
		Comunitat pare;
		String s="";
		try {
			pare = Comunitat.recercaPerNif("ARREL");
			Page p = Comunitat.llistarSubComunitats(pare, page);
			List l = p.getList();
			return ok(llista_comunitats.render(l, p, pare));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notFound();
	}
	@Restrict({@Group("A"),@Group("O")})
	@Transactional(readOnly = true)
	public static Result llistarSubComunitats(Comunitat pare, int page) {
		Page p;
		try {
			p = Comunitat.llistarSubComunitats(pare, page);
			List l = p.getList();
			return ok(llista_comunitats.render(l, p, pare));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			flash("error",Messages.get("error.consulta_subcomunitats"));
			return  redirect(routes.Comunitats.llistarSubComunitats(pare.pare, 1));
		}

	}
	@Restrict({@Group("A"),@Group("O")})
	@Transactional(readOnly = true)
	public static Result llistarContactes(Comunitat comunitat, int page) {
		Page p;
		try {
			p = Comunitat.llistarContactes(comunitat, page);
			List l = p.getList();
			return ok(llista_contactes.render(l, p, comunitat));
		} catch (Exception e) {
			// TODO Auto-generated catch block
				flash("error",Messages.get("no_contactes"));
				return redirect(routes.Comunitats.llistarSubComunitats(comunitat.pare, 1));
			
		}
		

	}
	
	
	@Restrict({@Group("A"),@Group("P")})
	@Transactional
	public static Result llistarUsuarisAssignats(Element element, int page) {
		List<ElementVei> e=element.elementsVei;
		//Collections.sort(e,Element.ElementComparator);
		Page p=new Page(element.elementsVei,page);
		return ok(llista_usuaris_assignats.render(element.elementsVei,element,p));
	}
	
	
	@Restrict({@Group("A")})
	@Transactional
	public static Result borrarComunitat(Comunitat comunitat) {
		
		try{
		Comunitat.borrarComunitat(comunitat);
		flash("success", String.format(Messages.get("success.comunitat_borrar"), comunitat.nom));
		}
		catch(Exception e){
			flash("error", String.format(Messages.get("error.comunitat_borrar"), comunitat.nom)+" (" +e.getCause().getCause().toString()+")");
		}
		return redirect(routes.Comunitats.llistarSubComunitats(comunitat.pare, 1));
	}
	@Restrict({@Group("A")})
	@Transactional
	public static Result guardarComunitat(Comunitat pare,boolean nou) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<Comunitat> boundForm = comunitatForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			flash("error", Messages.get("constraint.formulari"));
			List<Usuari> lu=null;
			try {
				lu=Usuari.obtenirUsuaris();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			return badRequest(detalls_comunitat.render(boundForm, pare, lu,nou));
		} else {
			Comunitat comunitatForm = boundForm.get();
			try {
				Comunitat.guardarComunitat(comunitatForm, pare,nou);
				flash("success", String.format(Messages.get("success.comunitat_guardar"), comunitatForm.nom));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				flash("error", String.format(Messages.get("error.comunitat_guardar"), comunitatForm.nom+" ("+e.getLocalizedMessage()+")"));
			}
		

			return redirect(routes.Comunitats.llistarSubComunitats(pare, 1));
		}
	}
	@Restrict({@Group("A")})
	public static Result nouElement(Comunitat comunitat) {
		Element element = new Element(comunitat);
		elementForm.fill(element);
		return ok(detalls_element.render(elementForm, comunitat,true));
	}
	@Restrict({@Group("A"),@Group("P")})
	@Transactional(readOnly = true)
	public static Result llistarElements(Comunitat comunitat, int page) {
		Page p;
		try {
			p = Element.llistarElements(comunitat, page);
			List<Element> l = p.getList();
			return ok(llista_elements.render(l, p, comunitat));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			flash("error",Messages.get("error.consulta_elements"));
			return redirect(routes.Comunitats.llistarSubComunitats(comunitat.pare, 1));		}
		
	}
	@Restrict({@Group("A")})
	@Transactional
	public static Result borrarElement(Element element) {
		if (element == null) {
			return notFound(String.format("L'element %s no existeix.", element.codi));
		}

		try {
			Element.borrarElement(element);
			flash("success", String.format(Messages.get("success.element_borrar"), element.codi));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flash("error", String.format(Messages.get("error.element_borrar"), element.codi)+" (" +e.getCause().getCause().toString()+")");

		}
		return redirect(routes.Comunitats.llistarComunitats(1));
	}
	@Restrict({@Group("A")})
	@Transactional(readOnly = true)
	public static Result detallElement(Element element) {
		Form<Element> filledForm = elementForm.fill(element);
		return ok(detalls_element.render(filledForm, element.comunitat,false));
	}
	@Restrict({@Group("A")})
	@Transactional
	public static Result guardarElement(Comunitat comunitat,boolean nou) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<Element> boundForm = elementForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			flash("error", Messages.get("constraint.formulari"));
			return badRequest(detalls_element.render(boundForm, comunitat,nou));
		} else {
			Element element = boundForm.get();
			element.comunitat = comunitat;
			try {
				Element.guardarElement(element,nou);
				flash("success", String.format(Messages.get("success.element_guardar"), element.codi));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				flash("error", String.format(Messages.get("error.element_guardar"), element.codi));

			}

			return redirect(routes.Comunitats.llistarElements(comunitat, 1));
		}
	}

}