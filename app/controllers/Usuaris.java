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

	private static Form<Usuari> UsuariForm = Form.form(Usuari.class);


	@Transactional(readOnly = true)
	public static Result llistarUsuaris(int page) {
		Page p = Usuari.llistarUsuaris(page);
		List l = p.getList();
		return ok(llista_usuaris.render(l, p));
	}
}