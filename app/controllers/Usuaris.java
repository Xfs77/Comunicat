package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import models.CanviPassword;
import models.Comunitat;
import models.Element;
import models.Login;
import models.Page;
import models.PasswordGenerator;
import models.Usuari;
import play.i18n.Messages;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
import play.mvc.Security;
import play.data.Form;
import play.data.format.Formatters;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import play.mvc.Controller;

import com.google.common.io.Files;

import play.mvc.Http.MultipartFormData;
import play.db.jpa.Transactional;
import views.html.*;

import org.hibernate.JDBCException;
import org.hibernate.exception.SQLGrammarException;

import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;

//@Security.Authenticated(Secured.class)
public class Usuaris extends Controller {

	private static Form<Usuari> usuariForm = Form.form(Usuari.class);

	@Transactional(readOnly = true)
	public static Result llistarUsuaris(int page) {
		Page p = Usuari.llistarUsuaris(page);
		List l = p.getList();
		return ok(llista_usuaris.render(l, p));
	}

	public static Result nouUsuari() {
		Usuari usuari = new Usuari();
		usuariForm.fill(usuari);
		return ok(detalls_usuari.render(usuariForm));
	}

	public static Result canviPassword () {
		return ok(canvi_password.render(Form.form(CanviPassword.class)));
	}
	
	@Transactional
	public static Result assignarElements(Usuari usuari) {
		
		return ok(assignacio_elements.render(usuari,Comunitat.llistarComunitatsElements(),Element.obtenirElements()));

	}
	
	@Transactional
	public static Result realitzarAssignacioElements(Usuari usuari) {
	    final MultipartFormData values = request().body().asMultipartFormData();
		Map<String, String[]> text1=values.asFormUrlEncoded();
		String text=text1.get("element")[0];
		final String comunitat = text.substring(0,text.indexOf('&'));
		final String element=text.substring(text.indexOf('&')+1, text.length());
		Comunitat c = (Comunitat.recercaPerNif(comunitat));
		Element	e = (Element.recercaPerCodi(c,element));
		Usuari.assignarElement(usuari, e);
	return redirect(
            routes.Application.index());
	}
	@Transactional
	public static Result efectuarCanviPassword() {  
		  Form<CanviPassword> canviPasswordForm = Form.form(CanviPassword.class).bindFromRequest();
		  if (canviPasswordForm.hasErrors()) {
		        return badRequest(canvi_password.render(canviPasswordForm));
		  } 
		  else {
			  CanviPassword canviPassword = canviPasswordForm.get();;
			  Usuari usuari=Usuari.recercaPerDni(session().get("dni"));
			  usuari.password=canviPassword.nou1;
			  Usuari.guardarUsuari(usuari);
			  flash("success", Messages.get("ok_canvi_password"));

			  return redirect(
		            routes.Application.index());
		    }
		    
	}
	@Transactional(readOnly = true)
	public static Result detallUsuari(Usuari usuari) {
		Form<Usuari> filledForm = usuariForm.fill(usuari);
		return ok(detalls_usuari.render(filledForm));
	}

	@Transactional
	public static Result guardarUsuari() {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<Usuari> boundForm = usuariForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			flash("error", Messages.get("constraint.formulari"));
			return badRequest(detalls_usuari.render(boundForm));
		} else {
			Usuari usuariForm = boundForm.get();
		
			Usuari.guardarUsuari(usuariForm);
			flash("success", String.format(
					"L'usuari %s s'ha registrat correctament", usuariForm.dni));

			return redirect(routes.Usuaris.llistarUsuaris(1));
		}
	}
	
	@Transactional
	public static Result borrarUsuari(Usuari usuari) throws Exception {
		if (usuari == null) {
			return notFound(String.format("L'usuari %s no existeix.",
					usuari.nom+" "+usuari.cognoms));
		}
		Usuari.borrarUsuari(usuari);
		flash("success", String.format(
				String.format("L'usuari %s no existeix.",
						usuari.nom+" "+usuari.cognoms)));
		return redirect(routes.Usuaris.llistarUsuaris(1));
	}
	
	@Transactional
	public static Result borrarElementAssignat(Usuari usuari,Element element ) throws Exception {
		if (usuari==null || element== null) {
			return notFound(String.format("El registre a eliminar no existeix."));
		}
		Usuari.borrarElementAssignat(usuari,element);
		flash("success", String.format(
				String.format("L'usuari %s s'ha desasignat de l'element %s de la comunitat %s.",
						usuari.nom+" "+usuari.cognoms,element.codi,element.comunitat.nom)));

		return redirect(routes.Usuaris.llistarUsuaris(1));
	}
	
	@Transactional
	public static Result llistarElementsAssignats(Usuari usuari, int page) {
		List<Element> e=usuari.elements_vei;
		Collections.sort(e,Element.ElementComparator);
		Page p=new Page(usuari.elements_vei,page);
		return ok(llista_elements_assignats.render(usuari.elements_vei,usuari,p));
	}
		

	@Transactional
	public static Result correuAlta(Usuari usuari) {
		
		try {
			
			Usuari.correuAlta(usuari);
			flash("success", Messages.get("ok_enviament_mail"));

			
		} catch (Exception e) {
			flash("error", Messages.get("error_enviament_mail"));
		}

		return redirect(routes.Usuaris.llistarUsuaris(1));

	}

}