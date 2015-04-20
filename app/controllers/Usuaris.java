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

import org.hibernate.JDBCException;
import org.hibernate.exception.SQLGrammarException;

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
					"L'usuari %s s'ha registrat correctament",
					usuariForm.dni));

			return redirect(routes.Usuaris.llistarUsuaris(1));
		}
	}

}