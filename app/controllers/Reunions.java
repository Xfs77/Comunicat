package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

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

	
	@Transactional(readOnly = true)
	public static Result novaReunio() {
		Reunio reunio = new Reunio();
		EstatReunio er= EstatReunio.recerca("Pendent");
		reunio.estat=er;
		Form<Reunio> filledForm =reunioForm.fill(reunio);
		 String dni=session("dni");
	        Usuari usuari=Usuari.recercaPerDni(dni);	
			List<Comunitat> lc=null;
			if (usuari.administrador==true){
				lc=Comunitat.obtenirComunitats();
			}
			else {
				lc=usuari.accesComunitats;
			}	
		return ok(detalls_reunio.render(filledForm, lc, EstatReunio.obtenirEstatsReunio()));
	}
	
	
	@Transactional(readOnly = true)
	public static Result llistarReunions(int page) {
		Page p = Reunio.llistarReunions(page);
		List<Reunio> l = p.getList();
		Form<ReunionsFiltre> filtre=reunioFiltreForm.fill(new ReunionsFiltre());
		return ok(llista_reunions.render(l, p, filtre, Comunitat.accesComunitats(),EstatReunio.obtenirEstatsReunio()));
	}
	
	@Transactional(readOnly = true)
	public static Result llistarReunionsFiltrades(int page) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<ReunionsFiltre> boundForm = reunioFiltreForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			 return badRequest();
		} else {
			ReunionsFiltre filtre = boundForm.get();
			Page p = Reunio.llistarReunionsFiltrades(1,filtre);
			List<Reunio> l = p.getList();
			return ok(llista_reunions.render(l, p, boundForm, Comunitat.accesComunitats(),EstatReunio.obtenirEstatsReunio()));
		}
	}
	
	
	@Transactional(readOnly = true)
	public static Result detallReunio(Reunio reunio ) {
		Form<Reunio> filledForm = reunioForm.fill(reunio);
		 String dni=session("dni");
	        Usuari usuari=Usuari.recercaPerDni(dni);	
		List<EstatReunio> ler=EstatReunio.obtenirEstatsReunio();
			List<Comunitat> lc=null;
			if (usuari.administrador==true){
				lc=Comunitat.obtenirComunitats();
			}
			else {
				lc=usuari.accesComunitats;
			}		return ok(detalls_reunio.render(filledForm,lc, ler));
	}

	
	@Transactional
	public static Result guardarReunio() {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<Reunio> boundForm = reunioForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			 String dni=session("dni");
		        Usuari usuari=Usuari.recercaPerDni(dni);	
				List<Comunitat> lc=null;
				if (usuari.administrador==true){
					lc=Comunitat.obtenirComunitats();
				}
				else {
					lc=usuari.accesComunitats;
				}	
			flash("error", Messages.get("constraint.formulari"));
			return badRequest(detalls_reunio.render(boundForm, lc,EstatReunio.obtenirEstatsReunio()	));
		} else {
			Reunio reunioForm = boundForm.get();
			Reunio.guardarReunio(reunioForm);
			flash("success", String.format(
					"La reunio %s s'ha registrat correctament",
					reunioForm.codi));

			return redirect(routes.Reunions.llistarReunions(1));
		}
	}

	
	@Transactional
	public static Result borrarReunio(Reunio reunio) throws Exception {
		if (reunio == null) {
			return notFound(String.format("La Reunio %s no existeix.",
					reunio.codi));
		}
		Reunio.borrarReunio(reunio);
		flash("success", String.format(
				"La reunio %s s'ha borrat correctament", reunio.codi));

		return redirect(routes.Reunions.llistarReunions(1));
	}
	
	
	@Transactional(readOnly = true)
	public static Result nouDocument(Reunio reunio) {
		Document document = new Document();
		document.reunio=reunio;
		Form<Document> filledForm =documentForm.fill(document);
		
		return ok(detalls_document.render(filledForm, document.reunio));
	}
	
	
	@Transactional(readOnly = true)
	public static Result llistarDocuments(Reunio reunio,int page) {
		Page p = Document.llistarDocuments(reunio,page);
		List<Document> l = p.getList();
		return ok(llista_documents.render(l, p,reunio));
	}
	
	@Transactional(readOnly = true)
	public static Result detallDocument(Document document ) {
		Form<Document> filledForm = documentForm.fill(document);
		return ok(detalls_document.render(filledForm,document.reunio));
	}

	
	@Transactional
	public static Result guardarDocument(Reunio reunio) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<Document> boundForm = documentForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			 flash("error", Messages.get("constraint.formulari"));
			return badRequest(detalls_document.render(boundForm,reunio));
		} else {
			Document documentForm = boundForm.get();
			MultipartFormData m = request().body().asMultipartFormData();
			MultipartFormData.FilePart part = m.getFile("document");
		    if(part != null) {
			      File document = part.getFile();
		    
			 try {
			        documentForm.document = Files.toByteArray(document);
			        String s="";
			      } catch (IOException e) {
			        return internalServerError("Error reading file upload");
			      }
		    }
			documentForm.reunio=reunio;
			Document.guardarDocument(documentForm);
			flash("success", String.format(
					"El document %s s'ha registrat correctament",
					documentForm.codi));

			return redirect(routes.Reunions.llistarDocuments(reunio,1));
		}
	}

	
	@Transactional
	public static Result borrarDocument(Document document) {
		if (document == null) {
			return notFound(String.format("El document %s no existeix.",
					document.codi));
		}
		Document.borrarDocument(document);
		flash("success", String.format(
				"El document %s s'ha borrat correctament", document.codi));

		return redirect(routes.Reunions.llistarDocuments(document.reunio,1));
	}
	
	@Transactional
	public static Result notificarReunio(Reunio reunio) {
		
		Reunio.notificarReunio(reunio);
		return redirect(routes.Reunions.llistarReunions(1));
		
	}

	@Transactional(readOnly=true)
	public static Result readFile(String codi) throws IOException {
		Document.borrarArchiuDirectori();
		Document d=Document.recercaPerCodi(Integer.parseInt(codi));

		File tempdf = File.createTempFile("CU"+codi , ".pdf",new File("public\\javascripts\\web\\tmp"));
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

