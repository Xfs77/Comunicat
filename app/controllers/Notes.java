package controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import models.Comunitat;
import models.Nota;
import models.EstatNota;
import models.MovimentNota;
import models.Nota;
import models.Nota;
import models.Page;
import models.Usuari;
import play.data.Form;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.RequestBody;
import views.html.*;


@Security.Authenticated(Secured.class)
public class Notes extends Controller {
	
	private static Form<Nota> notaForm = Form.form(Nota.class);
	private static Form<MovimentNota> movimentForm = Form.form(MovimentNota.class);

	
	@Transactional(readOnly = true)
	public static Result llistarNotes(int page) {
		Page p = Nota.llistarNotes(page);
		List<Nota> l = p.getList();
		return ok(llista_notes.render(l, p));
	}
	
	@Transactional(readOnly = true)
	public static Result novaNota() {
        String dni=session("dni");
        Usuari usuari=Usuari.recercaPerDni(dni);
		Nota nota = new Nota(usuari);
		nota.estat=EstatNota.recerca("P");
		nota.fecha=new Date();
		Form<Nota> filledForm =notaForm.fill(nota);
		List<EstatNota> len=EstatNota.obtenirEstatsNota();
		List<Comunitat> lc=null;
		if (usuari.administrador==true){
			try {
				lc=Comunitat.obtenirComunitats();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			lc=usuari.accesComunitats;
		}
		return ok(detalls_nota.render(filledForm, usuari,len, lc, ""));
	}
	
	
	@Transactional(readOnly = true)
	public static Result nouMovimentNota(Nota nota) {
        String dni=session("dni");
        Usuari usuari=Usuari.recercaPerDni(dni);
		MovimentNota moviment=new MovimentNota();
		moviment.nota=nota;
		moviment.fecha=new Date();
		moviment.estat=nota.estat;
		Form<MovimentNota> filledForm =movimentForm.fill(moviment);
		List<EstatNota> len=EstatNota.obtenirEstatsNota();
	
		return ok(detalls_movimentnota.render(filledForm, nota,usuari,len));
	}
	
	@Transactional(readOnly = true)
	public static Result detallNota(Nota nota) {
		Form<Nota> filledForm = notaForm.fill(nota);
		 String dni=session("dni");
	        Usuari usuari=Usuari.recercaPerDni(dni);	
		List<EstatNota> len=EstatNota.obtenirEstatsNota();
			List<Comunitat> lc=null;
			if (usuari.administrador==true){
				try {
					lc=Comunitat.obtenirComunitats();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				lc=usuari.accesComunitats;
			}		return ok(detalls_nota.render(filledForm, usuari,len,lc,""));
	}


	@Transactional(readOnly = true)
	public static Result detallMovimentNota(MovimentNota moviment) {
		Form<MovimentNota> filledForm = movimentForm.fill(moviment);
	
		List<EstatNota> len=EstatNota.obtenirEstatsNota();
	return ok(detalls_movimentnota.render(filledForm, moviment.nota,moviment.usuari,len));
	}

	
	@Transactional(readOnly = true)
	public static Result llistarMoviments(Nota nota, int page) {
		Page p = MovimentNota.llistarMoviments(nota,page);
		List l = p.getList();
		return ok(llista_movimentsnotes.render(l,nota, p));
	}
	
	
	
	@Transactional
	public static Result borrarNota(Nota nota)  {
		if (nota == null) {
			return notFound(String.format("La Nota %s no existeix.",
					nota.codi));
		}
		try {
			Nota.borrarNota(nota);
			flash("success", String.format(
					"La Nota %s s'ha borrat correctament", nota.codi));
			return redirect(routes.Notes.llistarNotes(1));
		} catch (PersistenceException e) {
			flash("error", String.format(
					"La Nota %s s'ha borrat correctament", nota.codi));
			return redirect(routes.Notes.llistarNotes(1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flash("success", String.format(
					"La Nota %s s'ha borrat correctament", nota.codi));
			return redirect(routes.Notes.llistarNotes(1));

		}
	

		
	}
	
	@Transactional
	public static Result borrarMovimentNota(MovimentNota moviment)  {
		if (moviment == null) {
			return notFound(String.format("La Nota %s no existeix.",
					moviment.codi));
		}
		MovimentNota.borrarNota(moviment);
		flash("success", String.format(
				"La Nota %s s'ha registrat correctament", moviment.codi));

		return redirect(routes.Notes.llistarNotes(1));
	}
	
		

	
	@Transactional
	public static Result guardarNota() throws Exception {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<Nota> boundForm = notaForm.bindFromRequest();
		String detall=body.asMultipartFormData().asFormUrlEncoded().get("detall")[0];
		
		if (boundForm.hasErrors()) {
			flash("error", Messages.get("constraint.formulari"));
			String dni=session("dni");
		        Usuari usuari=Usuari.recercaPerDni(dni);
				List<EstatNota> len=EstatNota.obtenirEstatsNota();
				List<Comunitat> lc=null;
				if (usuari.administrador==true){
					lc=Comunitat.obtenirComunitats();
				}
				else {
					lc=usuari.accesComunitats;
				}
				return badRequest(detalls_nota.render(notaForm, usuari, len, lc,detall));
			
		} else {
			Nota NotaForm = boundForm.get();
			Nota nf=Nota.guardarNota(NotaForm,detall);
			flash("success", String.format(
					"La Nota %d s'ha registrat correctament",
					nf.codi));

			return redirect(routes.Notes.llistarNotes(1));
		}
	}

	@Transactional
	public static Result guardarMovimentNota() {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		String dni=body.asMultipartFormData().asFormUrlEncoded().get("usuari")[0];
		Usuari u=Usuari.recercaPerDni(dni);
		String numNota=body.asMultipartFormData().asFormUrlEncoded().get("nota")[0];
		Nota n=Nota.recercaPerCodi(Integer.parseInt(numNota));
		
		Form<MovimentNota> boundForm = movimentForm.bindFromRequest();
		
		if (boundForm.hasErrors()) {
			flash("error", Messages.get("constraint.formulari"));
				List<EstatNota> len=EstatNota.obtenirEstatsNota();
				
				
				return badRequest(detalls_movimentnota.render(movimentForm, n,u, len));

				} else {
			MovimentNota movimentNotaForm = boundForm.get();
			MovimentNota mf=MovimentNota.guardarMovimentNota(movimentNotaForm);
			flash("success", String.format(
					"El moviment %d s'ha registrat correctament",
					mf.codi));

			return redirect(routes.Notes.llistarMoviments(movimentNotaForm.nota,1));
		}
	}

	@Transactional
	public static Result notificarNota(Nota nota) {
		
			Nota.notificarNota(nota);
			return redirect(routes.Notes.llistarNotes(1));
		
	}

	
}