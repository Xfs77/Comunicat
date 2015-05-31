package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import com.google.common.io.Files;

import models.Comunitat;
import models.Document;
import models.EstatReunio;
import models.Nota;
import models.EstatNota;
import models.MovimentNota;
import models.Nota;
import models.Nota;
import models.Page;
import models.Reunio;
import models.ReunionsFiltre;
import models.Usuari;
import play.Play;
import play.data.Form;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.RequestBody;
import views.html.*;

@Security.Authenticated(Secured.class)
public class Reunions extends Controller {

	private static Form<Reunio> reunioForm = Form.form(Reunio.class);
	private static Form<ReunionsFiltre> reunioFiltreForm = Form.form(ReunionsFiltre.class);
	private static Form<Document> documentForm = Form.form(Document.class);

	@Restrict({@Group("A"),@Group("P")})
	@Transactional(readOnly = true)
	public static Result novaReunio() {
		Reunio reunio = new Reunio();
		EstatReunio er = EstatReunio.recerca("Pendent");
		reunio.estat = er;
		Form<Reunio> filledForm = reunioForm.fill(reunio);
		String dni = session("dni");
		Usuari usuari = null;
		try {
			usuari = Usuari.recercaPerDni(dni);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<Comunitat> lc = null;
		if (usuari.administrador == true) {
			try {
				lc = Comunitat.obtenirComunitats();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			lc = usuari.accesComunitats;
		}
		return ok(detalls_reunio.render(filledForm, lc, EstatReunio.obtenirEstatsReunio(), true));
	}

	/*@Transactional(readOnly = true)
	public static Result llistarReunions(int page) {
		Page p;
		try {
			p = Reunio.llistarReunions(page);
			List<Reunio> l = p.getList();
			Form<ReunionsFiltre> filtre = reunioFiltreForm.fill(new ReunionsFiltre());
			List<Comunitat> lc = null;
			try {
				lc = Comunitat.accesComunitats();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ok(llista_reunions.render(l, p, filtre, lc, EstatReunio.obtenirEstatsReunio()));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			return badRequest();
		}

	}
*/
	@Restrict({@Group("A"),@Group("O")})
	@Transactional(readOnly = true)
	public static Result llistarReunionsFiltrades(int page) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<ReunionsFiltre> boundForm = reunioFiltreForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			return badRequest();
		} else {
			ReunionsFiltre filtre = boundForm.get();
			Page p=null;
			List<Reunio> l=null;
			List<Comunitat> lc = null;
			try {
				lc = Comunitat.accesComunitats();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				p = Reunio.llistarReunionsFiltrades(page, filtre);
				l = p.getList();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();

			}
			return ok(llista_reunions.render(l, p, boundForm, lc, EstatReunio.obtenirEstatsReunio()));

		}
	}
	@Restrict({@Group("A"),@Group("P")})
	@Transactional(readOnly = true)
	public static Result detallReunio(Reunio reunio) {
		Form<Reunio> filledForm = reunioForm.fill(reunio);
		String dni = session("dni");
		Usuari usuari = null;
		try {
			usuari = Usuari.recercaPerDni(dni);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<EstatReunio> ler = EstatReunio.obtenirEstatsReunio();
		List<Comunitat> lc = null;
		if (usuari.administrador == true) {
			try {
				lc = Comunitat.obtenirComunitats();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			lc = usuari.accesComunitats;
		}
		return ok(detalls_reunio.render(filledForm, lc, ler, false));
	}

	@Restrict({@Group("A"),@Group("P")})
	@Transactional
	public static Result guardarReunio(boolean nou) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<Reunio> boundForm = reunioForm.bindFromRequest();
		List<Comunitat> lc = null;

		if (boundForm.hasErrors()) {
			String dni = session("dni");
			Usuari usuari = null;
			try {
				usuari = Usuari.recercaPerDni(dni);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (usuari.administrador == true) {
				try {
					lc = Comunitat.obtenirComunitats();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				lc = usuari.accesComunitats;
			}
			flash("error", Messages.get("constraint.formulari"));
			return badRequest(detalls_reunio.render(boundForm, lc, EstatReunio.obtenirEstatsReunio(), nou));
		} else {
			Reunio reunioForm = boundForm.get();
			try {
				Reunio.guardarReunio(reunioForm, nou);
				flash("success", String.format(Messages.get("success.guardar_reunio"), reunioForm.descripcio));

				return redirect(routes.Reunions.llistarReunionsFiltrades(1));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				flash("error", String.format(Messages.get("error.guardar_reunio"), reunioForm.descripcio));
				return badRequest(detalls_reunio.render(boundForm, lc, EstatReunio.obtenirEstatsReunio(), nou));

			}
		}
	}
	@Restrict({@Group("A"),@Group("P")})
	@Transactional
	public static Result borrarReunio(Reunio reunio) {
		if (reunio == null) {
			return notFound(String.format(Messages.get("error.borrar_reunio"), reunio.descripcio));
		}
		try {
			Reunio.borrarReunio(reunio);
			flash("success", String.format(Messages.get("success.borrar_reunio"), reunio.descripcio));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			flash("error", String.format(Messages.get("error.borrar_reunio") + " ("
					+ e.getCause().getCause().toString() + ")", reunio.descripcio));
		}
		return redirect(routes.Reunions.llistarReunionsFiltrades(1));
	}
	@Restrict({@Group("A"),@Group("P")})
	@Transactional(readOnly = true)
	public static Result nouDocument(Reunio reunio) {
		Document document = new Document();
		document.reunio = reunio;
		Form<Document> filledForm = documentForm.fill(document);
		return ok(detalls_document.render(filledForm, document.reunio, true));
	}
	@Restrict({@Group("A"),@Group("O")})
	@Transactional(readOnly = true)
	public static Result llistarDocuments(Reunio reunio, int page) {
		Page p = null;
		try {
			p = Document.llistarDocuments(reunio, page);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Document> l = p.getList();
		String host="";
		try{
		String direccion= Play.application().getFile("/public/").getAbsolutePath()+"--"+Play.application().getFile("/public/").getPath()+"--"+InetAddress.getLocalHost().getAddress()+"--"+InetAddress.getLocalHost().getHostName();;

//		host=InetAddress.getLocalHost().getHostName();
		host=direccion;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ok(llista_documents.render(l, p, reunio,host));
	}
	@Restrict({@Group("A"),@Group("P")})
	@Transactional(readOnly = true)
	public static Result detallDocument(Document document) {
		Form<Document> filledForm = documentForm.fill(document);
		return ok(detalls_document.render(filledForm, document.reunio, false));
	}
	@Restrict({@Group("A"),@Group("P")})
	@Transactional
	public static Result guardarDocument(Reunio reunio, boolean nou) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<Document> boundForm = documentForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			flash("error", Messages.get("constraint.formulari"));
			return badRequest(detalls_document.render(boundForm, reunio, nou));
		} else {
			Document documentForm = boundForm.get();
			MultipartFormData m = request().body().asMultipartFormData();
			MultipartFormData.FilePart part = m.getFile("document");
			if (part != null) {
				File document = part.getFile();
				String tipus = part.getContentType();
				if (!tipus.equals("application/pdf")) {
					flash("error", Messages.get("constraint.pdf"));
					return badRequest(detalls_document.render(boundForm, reunio, nou));
				}
				try {
					documentForm.document = Files.toByteArray(document);
				} catch (IOException e) {
					return internalServerError("Error reading file upload");
				}
			}
			documentForm.reunio = reunio;

			try {
				Document.guardarDocument(documentForm, nou);
				flash("success", String.format(Messages.get("success.guardar_document"), documentForm.descripcio));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				flash("error", String.format(Messages.get("error.guardar_document"), documentForm.descripcio));

			}

			return redirect(routes.Reunions.llistarDocuments(reunio, 1));
		}
	}
	@Restrict({@Group("A"),@Group("P")})
	@Transactional
	public static Result borrarDocument(Document document) {
		if (document == null) {
			return notFound(String.format("El document %s no existeix.", document.descripcio));
		}
		try {
			Document.borrarDocument(document);
			flash("success", String.format(Messages.get("success.borrar_document"), document.descripcio));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			flash("error",
					String.format(Messages.get("error.borrar_document") + " (" + e.getCause().getCause().toString()
							+ ")", document.descripcio));
			e.printStackTrace();
		}

		return redirect(routes.Reunions.llistarDocuments(document.reunio, 1));
	}
	@Restrict({@Group("A"),@Group("P")})
	@Transactional
	public static Result notificarReunio(Reunio reunio) {

		try {
			Reunio.notificarReunio(reunio);
			flash("success", String.format(
					String.format(Messages.get("ok_enviament_reunio"),reunio.descripcio)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			flash("error", String.format(
					String.format(Messages.get("no_enviament_reunio"),reunio.descripcio)));
			e.printStackTrace();
		}
		return redirect(routes.Reunions.llistarReunionsFiltrades(1));

	}
	@Restrict({@Group("A"),@Group("O")})
	@Transactional(readOnly = true)
	public static Result readFile(String codi) throws IOException {
		Document.borrarArchiuDirectori();
		
		Document d = null;
		try {
			d = Document.recercaPerCodi(Integer.parseInt(codi));
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		File tempdf = File.createTempFile("CU" + codi, ".pdf", new File("public\\javascripts\\web\\tmp"));
		tempdf.deleteOnExit();

		try {
			FileOutputStream fos = new FileOutputStream(tempdf);
			fos.write(d.document);
			fos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ok(tempdf.getName());

	}

}
