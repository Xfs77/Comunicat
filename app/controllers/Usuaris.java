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
import models.EstatReunio;
import models.Login;
import models.Page;
import models.PasswordGenerator;
import models.Reunio;
import models.ReunionsFiltre;
import models.TipusVei;
import models.Usuari;
import models.UsuarisFiltre;
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

@Security.Authenticated(Secured.class)
public class Usuaris extends Controller {

	private static Form<Usuari> usuariForm = Form.form(Usuari.class);
	private static Form<UsuarisFiltre> usuariFiltreForm = Form.form(UsuarisFiltre.class);


	@Transactional(readOnly = true)
	public static Result llistarUsuaris(int page) {
		Page p;
		try {
			p = Usuari.llistarUsuaris(page);
			List l = p.getList();
			//return ok(llista_usuaris.render(l, p));
			return ok();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return notFound();
		}
	}
	
	@Transactional(readOnly = true)
	public static Result llistarUsuarisFiltrats(int page) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<UsuarisFiltre> boundForm = usuariFiltreForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			 return badRequest();
		} else {
			UsuarisFiltre filtre = boundForm.get();
			Page p = Usuari.llistarUsuarisFiltrats(1,filtre);
			List<Usuari> l = p.getList();
			List<Comunitat> lc=null;
			try {
				lc = Comunitat.accesComunitats();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ok(llista_usuaris.render(l, p, boundForm, lc));
		}
	}

	public static Result nouUsuari() {
		Usuari usuari = new Usuari();
		usuariForm.fill(usuari);
		return ok(detalls_usuari.render(usuariForm,true));
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
		List<Element> le=null;
		try{
			le=Element.obtenirElements();
		}
		catch(Exception e){
		}
		return ok(assignacio_elements.render(usuari,lce,le));

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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element	e = null;
		try {
			e=(Element.recercaPerCodi(c,element));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
		Usuari.assignarElement(usuari, e, t);
		flash("success", String.format(Messages.get("success.element_assignar"), e.codi));
		}
		catch(Exception e2){
			flash("error", String.format(Messages.get("error.element_assignar")+" ("+e2.getCause().getCause().toString(), e.codi));

		}

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
			  
			  try {
				  Usuari usuari=Usuari.recercaPerDni(session().get("dni"));
				  usuari.password=canviPassword.nou1;
				  Usuari.guardarUsuari(usuari,false);
				  flash("success", Messages.get("ok_canvi_password"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				flash("error", Messages.get("no_canvi_password"));
			}
			  

			  return redirect(
		            routes.Application.index());
		    }
		    
	}
	@Transactional(readOnly = true)
	public static Result detallUsuari(Usuari usuari) {
		Form<Usuari> filledForm = usuariForm.fill(usuari);
		return ok(detalls_usuari.render(filledForm,false));
	}

	@Transactional
	public static Result guardarUsuari(boolean nou) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<Usuari> boundForm = usuariForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			flash("error", Messages.get("constraint.formulari"));
			return badRequest(detalls_usuari.render(boundForm,nou));
		} else {
			Usuari usuariForm = boundForm.get();
		
			try{
				Usuari.guardarUsuari(usuariForm,nou);
			}
			catch (Exception e){
				flash("error", String.format(
						 Messages.get("error.guardar_usuari")+" ("+e.getLocalizedMessage()+")", usuariForm.nom+" "+usuariForm.cognoms));
				return badRequest(detalls_usuari.render(boundForm,nou));
			}
			
			flash("success", String.format(
					 Messages.get("success.guardar_usuari"), usuariForm.nom+" "+usuariForm.cognoms));

			return redirect(routes.Usuaris.llistarUsuarisFiltrats(1));
		}
	}
	
	@Transactional
	public static Result borrarUsuari(Usuari usuari) {
		if (usuari == null) {
			return notFound();
		}
		try {
			Usuari.borrarUsuari(usuari);
			flash("success", String.format(
					String.format(Messages.get("success.borrar_usuari"),
							usuari.nom+" "+usuari.cognoms)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			flash("error", String.format(
					String.format(Messages.get("error.borrar_usuari")+" ("+e.getCause().getCause().toString()+")",
							usuari.nom+" "+usuari.cognoms)));
			e.printStackTrace();
		}
		
		return redirect(routes.Usuaris.llistarUsuarisFiltrats(1));
	}
	
	@Transactional
	public static Result borrarElementAssignat(Usuari usuari,Element element,TipusVei tipus ) {
		if (usuari==null || element== null) {
			return notFound();
		}
		try {
			Usuari.borrarElementAssignat(usuari,element,tipus);
			flash("success", String.format(
					String.format(Messages.get("success.borrar_assignat"),
							usuari.nom+" "+usuari.cognoms,element.codi,element.comunitat.nom)));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			flash("error", String.format(
					String.format(Messages.get("error.borrar_assignat")+" ("+e.getCause().getCause().toString()+")",
							usuari.nom+" "+usuari.cognoms,element.codi,element.comunitat.nom)));
			e.printStackTrace();
		}
	
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
			flash("success", String.format(
					String.format(Messages.get("ok_enviament_mail"),usuari.nom+" "+usuari.cognoms)));
			return redirect(routes.Usuaris.llistarUsuarisFiltrats(1));
		
			
		} catch (Exception e) {
			flash("error", String.format(
					String.format(Messages.get("no_enviament_mail")+" ("+e.getLocalizedMessage().toString()+")",
							usuari.nom+" "+usuari.cognoms)));
			return redirect(routes.Usuaris.llistarUsuarisFiltrats(1));

			
			

		}


	}

}