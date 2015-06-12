package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.libs.F;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
import play.mvc.PathBindable;

@Entity
@Table(name = "comunicat.nota")
public class Nota implements Serializable, PathBindable<Nota> {
	private static final Object[] Address = null;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_nota")
	@SequenceGenerator(name = "seq_nota", sequenceName = "seq_nota", allocationSize = 1, initialValue = 1)
	@Column(name = "codi")
	public int codi;
	@Column(name = "fecha")
	public Date fecha;
	@MaxLength(150)
	@Required
	@Column(name = "descripcio")
	public String descripcio;
	@ManyToOne
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "estat")
	public EstatNota estat;
	@ManyToOne
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "comunitat")
	public Comunitat comunitat;
	@ManyToOne
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "usuari")
	public Usuari usuari;
	@OneToMany(mappedBy="nota",fetch=FetchType.EAGER)
	private List<MovimentNota> movimentsNota = new ArrayList<MovimentNota>();


	@Override
	public Nota bind(String arg0, final String arg1) {
		// TODO Auto-generated method stub
		final Nota c[] = { null };
		JPA.withTransaction(new F.Callback0() {
			@Override
			public void invoke() throws Throwable {
				c[0] = (Nota.recercaPerCodi(Integer.parseInt(arg1)));
			}
		});

		return (c[0]);
	}

	@Override
	public String javascriptUnbind() {
		// TODO Auto-generated method stub
		return Integer.toString(this.codi);
	}

	@Override
	public String unbind(String arg0) {
		// TODO Auto-generated method stub
		return Integer.toString(this.codi);
	}
	
	public Nota() {
	}

	public Nota(Usuari usuari) {
		this.usuari = usuari;
	}

	public Nota(int codi, Date fecha, String descripcio, EstatNota estat,
			Comunitat comunitat, Usuari usuari, List<MovimentNota> movimentsNota) {
		super();
		this.codi = codi;
		this.fecha = fecha;
		this.descripcio = descripcio;
		this.estat = estat;
		this.comunitat = comunitat;
		this.usuari = usuari;
		this.movimentsNota = movimentsNota;
	}

	public static Page llistarNotes(int page) throws Exception {
		Query query = null;
		query = JPA.em().createQuery("from Nota ");
		try{
		List list = query.getResultList();
		Collections.sort(list, NotaComparator);
		Page p = new Page(list, page);
		return p;
		}
		catch(Exception e){
			throw e;
		}
	}

	public static Page llistarNotesFiltrades(int page,
			NotesFiltre notaFiltreForm) throws Exception {
		Usuari usuari = Usuari.recercaPerDni(play.mvc.Controller.session().get(
				"dni"));
		Query query = null;
		String exp = null;
		if (usuari.administrador == true) {
			exp = "from Nota n where n.fecha >=?1 and n.fecha<=?2";
			query = JPA.em().createQuery(exp);
			query.setParameter(1, notaFiltreForm.fechaIni);
			query.setParameter(2, notaFiltreForm.fechaFi);

			if (notaFiltreForm.comunitat != null) {
				exp = exp + " and n.comunitat=?3";
				query = JPA.em().createQuery(exp);
				query.setParameter(1, notaFiltreForm.fechaIni);
				query.setParameter(2, notaFiltreForm.fechaFi);
				query.setParameter(3, notaFiltreForm.comunitat);
			}
			if (notaFiltreForm.estat != null) {
				exp = exp + " and n.estat=?4";
				query = JPA.em().createQuery(exp);
				query.setParameter(1, notaFiltreForm.fechaIni);
				query.setParameter(2, notaFiltreForm.fechaFi);
				if (exp.contains("?3")) {
					query.setParameter(3, notaFiltreForm.comunitat);
				}
				;
				query.setParameter(4, notaFiltreForm.estat);

			}
		}
		else {
			exp = "from Nota n where n.fecha >=?1 and n.fecha<=?2 and n.estat!=?6 and exists(from AccesComunitat a where a.vei=?5 and a.comunitat=n.comunitat)";

				query = JPA.em().createQuery(exp);
				query.setParameter(1, notaFiltreForm.fechaIni);
				query.setParameter(2, notaFiltreForm.fechaFi);
				query.setParameter(5,usuari);
				query.setParameter(6,EstatNota.recerca("P"));


				if (notaFiltreForm.comunitat != null) {
					exp = exp + " and n.comunitat=?3";
					query = JPA.em().createQuery(exp);
					query.setParameter(1, notaFiltreForm.fechaIni);
					query.setParameter(2, notaFiltreForm.fechaFi);
					query.setParameter(3, notaFiltreForm.comunitat);
					query.setParameter(5,usuari);

				}
				if (notaFiltreForm.estat != null) {
					exp = exp + " and n.estat=?4";
					query = JPA.em().createQuery(exp);
					query.setParameter(1, notaFiltreForm.fechaIni);
					query.setParameter(2, notaFiltreForm.fechaFi);
					query.setParameter(5,usuari);

					if (exp.contains("?3")) {
						query.setParameter(3, notaFiltreForm.comunitat);
					}
					;
					query.setParameter(4, notaFiltreForm.estat);

				}
			}
		
		try{
		List list = query.getResultList();
		Collections.sort(list, NotaComparator);
		Page p = new Page(list, page);
		return p;
		}
		catch(Exception e){
			throw e;
		}
	}

	public static void borrarNota(Nota nota) throws Exception,
			PersistenceException {
		// TODO Auto-generated method stub
		EntityManager em = JPA.em();
		Nota refNota = obtenirRefNota(nota);
		try {
			List<MovimentNota> lm=refNota.movimentsNota;
			for (Iterator<MovimentNota> it =lm.iterator();it.hasNext();){
				MovimentNota m=it.next();
				em.remove(m);
				em.flush();
			}

		} catch (Exception e) {
			throw e;
		}
	}

	public static Nota obtenirRefNota(Nota nota) {
		EntityManager em = JPA.em();
		Nota RefNota = em.find(Nota.class, nota.codi);
		return RefNota;
	}

	public static void notificarNota(Nota nota) throws Exception {
		Query query = null;
		query = JPA
				.em()
				.createQuery(
						"select a from Nota n join n.comunitat c join c.accesComunitats a where n=?1");
		query.setParameter(1, Nota.obtenirRefNota(nota));
		
		try{
		List<Usuari> lu = query.getResultList();
		List<MovimentNota> lm = nota.movimentsNota;
		Collections.sort(lm, MovimentNota.MovimentComparator);
		MovimentNota m = lm.get(0);
		List<Usuari> luf=new ArrayList<Usuari>();
		Iterator<Usuari> iterator = lu.iterator();
		while (iterator.hasNext()) {
			Usuari u = iterator.next();
			if(u.enviat==true) {
			luf.add(u);
			}
		}
		correuNota(luf,m);
		MovimentNota refMoviment = MovimentNota.obtenirRefMovimentNota(m);
		refMoviment.notificada = true;
		JPA.em().merge(refMoviment);
		}
		catch (Exception e){
			throw e;
		}
		
		
	}

	public static void correuNota(List<Usuari> lu, MovimentNota moviment) throws Exception{

		Email email = new Email();

		email.setSubject(Messages.get("notificacio.cap.alta_nota"));
		email.setFrom("comunicatcomunitat@gmail.com");
		email.addTo("comunicatcomunitat@gmail.com");

		Iterator<Usuari> iterator = lu.iterator();
		while (iterator.hasNext()) {
			Usuari u = iterator.next();
			email.addBcc(u.email);
		}
		email.setBodyText(String.format(
				 Messages.get("notificacio.detall.alta_nota"), Integer.toString(moviment.nota.codi),moviment.nota.descripcio,moviment.estat.descripcio));

		try{
		MailerPlugin.send(email);

		MovimentNota refMoviment = MovimentNota
				.obtenirRefMovimentNota(moviment);
		}
		catch(Exception e){
			throw e;
		}
	}

	public static Nota guardarNota(Nota formNota, String detall,boolean nou) throws Exception {

		Nota refNota = obtenirRefNota(formNota);
		if (nou ==false) {
			refNota.fecha = formNota.fecha;
			refNota.comunitat = formNota.comunitat;
			refNota.descripcio = formNota.descripcio;
			refNota.estat = formNota.estat;
			try{
			return (JPA.em().merge(refNota));
			}
			catch(Exception e){
				throw e;
			}

		} else {

			Nota n=null;
			try{
			 n=JPA.em().merge(formNota);
			}
			catch(Exception e){
				throw e;
			}
			 MovimentNota m = new MovimentNota();
			m.nota = n;
			m.fecha = new Date();
			m.detall = detall;
			m.estat = n.estat;
			m.notificada = false;
			m.usuari = n.usuari;
			m.codi = 1;
			try{
			JPA.em().persist(m);
			return (n);
			}
			catch(Exception e){
				throw e;
			}
		}
	}

	public static Comparator<Nota> NotaComparator = new Comparator<Nota>() {

		public int compare(Nota n1, Nota n2) {

			if(n1.codi<n2.codi)	{
				return 1;
			}
			else return -1;
		}
	};


	public static Nota recercaPerCodi(int codi) throws Exception {
		Nota result = null;
		Query query = JPA.em().createQuery("from Nota n");
		try{
		List<Nota> notes = query.getResultList();

		for (Nota candidate : notes) {
			if (candidate.codi == codi) {
				result = candidate;

			}
		}
		return result;
		}
		catch(Exception e){
			throw e;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + codi;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Nota other = (Nota) obj;
		if (codi != other.codi)
			return false;
		return true;
	}

}
