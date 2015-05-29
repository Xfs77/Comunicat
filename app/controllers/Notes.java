package controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.Comunitat;
import models.Nota;
import models.EstatNota;
import models.MovimentNota;
import models.Nota;
import models.Nota;
import models.NotesFiltre;
import models.Page;
import models.Usuari;
import models.UsuarisFiltre;
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
	private static Form<MovimentNota> movimentForm = Form
			.form(MovimentNota.class);
	private static Form<NotesFiltre> notaFiltreForm = Form
			.form(NotesFiltre.class);

	/*
	 * @Transactional(readOnly = true) public static Result llistarNotes(int
	 * page) { Page p; try { p = Nota.llistarNotes(page); } catch (Exception e)
	 * { // TODO Auto-generated catch block e.printStackTrace(); } List<Nota> l
	 * = p.getList(); return ok(); }
	 */
	@Transactional(readOnly = true)
	public static Result llistarNotesFiltrades(int page) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<NotesFiltre> boundForm = notaFiltreForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			return badRequest();
		} else {
			NotesFiltre filtre = boundForm.get();
			Page p = null;
			List<Nota> l = null;

			try {
				p = Nota.llistarNotesFiltrades(page, filtre);
				l = p.getList();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				l = null;
			}

			List<Comunitat> lc = null;
			try {
				lc = Comunitat.accesComunitats();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				lc = null;
				e.printStackTrace();
			}
			List<EstatNota> le = null;
			try {
				le = EstatNota.accesEstatsNota();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				le = null;
				e.printStackTrace();
			}
			return ok(llista_notes.render(l, p, boundForm, lc, le));
		}
	}
	@Restrict({@Group("A"),@Group("O")})
	@Transactional(readOnly = true)
	public static Result novaNota() {
		String dni = session("dni");
		Usuari usuari = null;
		try {
			usuari = Usuari.recercaPerDni(dni);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Nota nota = new Nota(usuari);
		try {
			nota.estat = EstatNota.recerca("P");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		nota.fecha = new Date();
		Form<Nota> filledForm = notaForm.fill(nota);
		List<EstatNota> len;
		try {
			len = EstatNota.obtenirEstatsNota();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			len = null;
			e1.printStackTrace();
		}
		List<Comunitat> lc = null;
		if (usuari.administrador == true) {
			try {
				lc = Comunitat.obtenirComunitats();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				lc = null;
				e.printStackTrace();
			}
		} else {
			lc = usuari.accesComunitats;
		}
		return ok(detalls_nota.render(filledForm, usuari, len, lc, "", true));
	}
	@Restrict({@Group("A"),@Group("O")})
	@Transactional(readOnly = true)
	public static Result nouMovimentNota(Nota nota) {
		String dni = session("dni");
		Usuari usuari = null;
		try {
			usuari = Usuari.recercaPerDni(dni);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MovimentNota moviment = new MovimentNota();
		moviment.nota = nota;
		moviment.fecha = new Date();
		moviment.estat = nota.estat;
		Form<MovimentNota> filledForm = movimentForm.fill(moviment);
		List<EstatNota> len;
		try {
			len = EstatNota.obtenirEstatsNota();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			len = null;
			e.printStackTrace();
		}

		return ok(detalls_movimentnota.render(filledForm, nota, usuari, len,
				true));
	}
	@Restrict({@Group("A")})
	@Transactional(readOnly = true)
	public static Result detallNota(Nota nota) {
		Form<Nota> filledForm = notaForm.fill(nota);
		String dni = session("dni");
		Usuari usuari = null;
		try {
			usuari = Usuari.recercaPerDni(dni);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<EstatNota> len;
		try {
			len = EstatNota.obtenirEstatsNota();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			len = null;
			e1.printStackTrace();
		}
		List<Comunitat> lc = null;
		if (usuari.administrador == true) {
			try {
				lc = Comunitat.obtenirComunitats();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				lc = null;
				e.printStackTrace();
			}
		} else {
			lc = usuari.accesComunitats;
		}
		return ok(detalls_nota.render(filledForm, usuari, len, lc, "", false));
	}

	@Restrict({@Group("A")})
	@Transactional(readOnly = true)
	public static Result detallMovimentNota(MovimentNota moviment) {
		Form<MovimentNota> filledForm = movimentForm.fill(moviment);

		List<EstatNota> len;
		try {
			len = EstatNota.obtenirEstatsNota();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			len = null;
			e.printStackTrace();
		}
		return ok(detalls_movimentnota.render(filledForm, moviment.nota,
				moviment.usuari, len, false));
	}

	@Transactional(readOnly = true)
	public static Result llistarMoviments(Nota nota, int page) {
		Page p = null;
		List l = null;
		try {
			p = MovimentNota.llistarMoviments(nota, page);
			l = p.getList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok(llista_movimentsnotes.render(l, nota, p));
	}
	@Restrict({@Group("A")})
	@Transactional
	public static Result borrarNota(Nota nota) {
		if (nota == null) {
			return notFound();
		}
		try {
			Nota.borrarNota(nota);
			flash("success", String.format(Messages.get("success.borrar_nota"),
					nota.codi));
			return redirect(routes.Notes.llistarNotesFiltrades(1));
		} catch (Exception e) {
			flash("error", String.format(Messages.get("error.borrar_nota")
					+ " (" + e.getCause().getCause().toString() + ")",
					nota.codi));

			return redirect(routes.Notes.llistarNotesFiltrades(1));
		}
	}
	@Restrict({@Group("A")})
	@Transactional
	public static Result borrarMovimentNota(MovimentNota moviment) {
		if (moviment == null) {
			return notFound();
		}
		try {
			MovimentNota.borrarMovimentNota(moviment);
			flash("success", String.format(Messages.get("success.borrar_movnota"),
					moviment.codi));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			flash("error", String.format(Messages.get("error.borrar_movnota"),
					moviment.codi));
			e.printStackTrace();
		}
		

		return redirect(routes.Notes.llistarNotesFiltrades(1));
	}
	@Restrict({@Group("A"),@Group("O")})
	@Transactional
	public static Result guardarNota(boolean nou) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		Form<Nota> boundForm = notaForm.bindFromRequest();
		String detall = body.asMultipartFormData().asFormUrlEncoded()
				.get("detall")[0];

		if (boundForm.hasErrors()) {
			flash("error", Messages.get("constraint.formulari"));
			String dni = session("dni");
			Usuari usuari;
			try {
				usuari = Usuari.recercaPerDni(dni);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				usuari = null;
				e.printStackTrace();
			}
			List<EstatNota> len;
			try {
				len = EstatNota.obtenirEstatsNota();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				len = null;
				e.printStackTrace();
			}
			List<Comunitat> lc = null;
			if (usuari.administrador == true) {
				try {
					lc = Comunitat.obtenirComunitats();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					lc = null;
					e.printStackTrace();
				}
			} else {
				lc = usuari.accesComunitats;
			}
			return badRequest(detalls_nota.render(boundForm, usuari, len, lc,
					detall, nou));

		} else {
			Nota NotaForm = boundForm.get();
			Nota nf;
			try {
				nf = Nota.guardarNota(NotaForm, detall, nou);
				flash("success", String.format(
						Messages.get("success.guardar_nota"), nf.codi));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				flash("error",
						String.format(Messages.get("error.guardar_nota")));

			}

			return redirect(routes.Notes.llistarNotesFiltrades(1));
		}
	}
	@Restrict({@Group("A"),@Group("P")})
	@Transactional
	public static Result guardarMovimentNota(Boolean nou) {
		play.mvc.Http.Request request = request();
		RequestBody body = request().body();
		String dni = body.asMultipartFormData().asFormUrlEncoded()
				.get("usuari")[0];
		Usuari u = null;
		try {
			u = Usuari.recercaPerDni(dni);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String numNota = body.asMultipartFormData().asFormUrlEncoded()
				.get("nota")[0];
		Nota n;
		try {
			n = Nota.recercaPerCodi(Integer.parseInt(numNota));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			n = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			n = null;
			e.printStackTrace();
		}

		Form<MovimentNota> boundForm = movimentForm.bindFromRequest();

		if (boundForm.hasErrors()) {
			flash("error", Messages.get("constraint.formulari"));
			List<EstatNota> len;
			try {
				len = EstatNota.obtenirEstatsNota();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				len = null;
				e.printStackTrace();
			}

			return badRequest(detalls_movimentnota.render(movimentForm, n, u,
					len, nou));

		} else {
			MovimentNota movimentNotaForm = boundForm.get();
			MovimentNota mf=null;
			try {
				mf = MovimentNota.guardarMovimentNota(
						movimentNotaForm, nou);
				flash("success", String.format(
						Messages.get("success.guardar_movnota"), mf.codi));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				flash("error", String.format(
						Messages.get("error.guardar_movnota"), mf.codi));
				e.printStackTrace();
			}
			

			return redirect(routes.Notes.llistarMoviments(
					movimentNotaForm.nota, 1));
		}
	}
	@Restrict({@Group("A")})
	@Transactional
	public static Result notificarNota(Nota nota) {

		try {
			Nota.notificarNota(nota);
			flash("success", String.format(
					Messages.get("success.notificar_nota"), nota.codi));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			flash("error", String.format(Messages.get("error.notificar_nota"),
					nota.codi));
		}
		return redirect(routes.Notes.llistarNotesFiltrades(1));

	}

}