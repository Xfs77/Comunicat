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
import models.ElementVei;
import models.Login;
import models.Page;
import models.PasswordGenerator;
import models.TipusVei;
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
import org.postgresql.util.PSQLException;

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
		
		List<Comunitat> lce=null;
		try {
				lce=Comunitat.llistarComunitatsElements();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return ok(assignacio_elements.render(usuari,lce,Element.obtenirElements()));

	}
	
	@Transactional
	public static Result realitzarAssignacioElements(Usuari usuari)  {
	    final MultipartFormData values = request().body().asMultipartFormData();
		Map<String, String[]> text1=values.asFormUrlEncoded();
		String text=text1.get("element")[0];
		final String comunitat = text.substring(0,text.indexOf('&'));
		final String element=text.substring(text.indexOf('&')+1, text.length());
		String tipus=text1.get("tipus")[0];
		TipusVei t= TipusVei.recerca(tipus);
		Comunitat c=null;
		try {
			c = (Comunitat.recercaPerNif(comunitat));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Element	e = (Element.recercaPerCodi(c,element));
		Usuari.assignarElement(usuari, e, t);

		return redirect(
            routes.Usuaris.llistarElementsAssignats(usuari,1));
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
			  try {
				Usuari.guardarUsuari(usuari);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		
			try{
				Usuari.guardarUsuari(usuariForm);
			}
			catch (Exception e){
				flash("error", Messages.get("error_enviament_email"));
				return badRequest(detalls_usuari.render(boundForm));
			}
			
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
	public static Result borrarElementAssignat(Usuari usuari,Element element,TipusVei tipus ) throws Exception {
		if (usuari==null || element== null) {
			return notFound(String.format("El registre a eliminar no existeix."));
		}
		Usuari.borrarElementAssignat(usuari,element,tipus);
		flash("success", String.format(
				String.format("L'usuari %s s'ha desasignat de l'element %s de la comunitat %s.",
						usuari.nom+" "+usuari.cognoms,element.codi,element.comunitat.nom)));

		return redirect(routes.Usuaris.llistarElementsAssignats(usuari,1));
	}
	
	@Transactional
	public static Result llistarElementsAssignats(Usuari usuari, int page) {
		List<ElementVei> e=usuari.elementsVei;
		//Collections.sort(e,Element.ElementComparator);
		Page p=new Page(usuari.elementsVei,page);
		return ok(llista_elements_assignats.render(usuari.elementsVei,usuari,p));
	}
		

	@Transactional
	public static Result correuAlta(Usuari usuari)  {
		
		try {
			
			Usuari.correuAlta(usuari);
			flash("success", Messages.get("ok_enviament_mail"));
			return redirect(routes.Usuaris.llistarUsuaris(1));
		
			
		} catch (Exception e) {
			flash("error", e.getCause().toString());
			return redirect(routes.Usuaris.llistarUsuaris(1));

			
			

		}


	}

}