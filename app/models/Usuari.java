package models;

import java.io.Serializable;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.Table;

import org.hibernate.JDBCException;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.postgresql.util.PSQLException;

import controllers.Comunitats;
import controllers.Usuaris;
import play.i18n.Messages;
import play.libs.F;
import play.libs.F.Some;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
import play.mvc.QueryStringBindable;
import play.mvc.Result;
import play.data.validation.Constraints.*;
import play.db.jpa.JPA;
import play.mvc.PathBindable;
import play.db.jpa.Transactional;

@Entity
@Table(name = "comunicat.Usuari")
public class Usuari implements Serializable, PathBindable<Usuari> {

	@Override
	public Usuari bind(String arg0, final String arg1) {
		// TODO Auto-generated method stub
		final Usuari u[] = { null };
		JPA.withTransaction(new F.Callback0() {
			@Override
			public void invoke() throws Throwable {
				u[0] = (Usuari.recercaPerDni(arg1));
			}
		});

		return (u[0]);
	}

	@Override
	public String javascriptUnbind() {
		// TODO Auto-generated method stub
		return this.dni;
	}

	@Override
	public String unbind(String arg0) {
		// TODO Auto-generated method stub
		return this.dni;
	}

	@Id
	@Required
	@MaxLength(50)
	@Column(name = "dni")
	public String dni;
	@Required
	@MaxLength(50)
	@Column(name = "nom")
	public String nom;
	@Required
	@MaxLength(50)
	@Column(name = "cognoms")
	public String cognoms;
	@Required
	@MaxLength(50)
	@Column(name = "tel1")
	public String tel1;
	@MaxLength(50)
	@Column(name = "tel2")
	public String tel2;
	@Required
	@play.data.validation.Constraints.Email
	@Column(name = "email")
	public String email;
	@MaxLength(50)
	@Column(name = "password")
	public String password;
	@Column(name = "baixa")
	public boolean baixa;
	@Column(name = "bloquejat")
	public boolean bloquejat;
	@Column(name = "president")
	public boolean president;
	@Column(name = "administrador")
	public boolean administrador;
	@Column(name = "enviat")
	public boolean enviat;
	@ManyToOne
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "tipus")
	@Required
	public TipusVei tipus;
//	@OneToMany(mappedBy = "usuari", cascade = CascadeType.ALL)
	@OneToMany(mappedBy = "usuari")
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<ElementVei> elementsVei = new ArrayList<ElementVei>();
	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "comunicat.accescomunitat", joinColumns = { @JoinColumn(name = "vei", referencedColumnName = "dni") }, inverseJoinColumns = { @JoinColumn(table = "comunitat", name = "comunitat", referencedColumnName = "nif") })
	public List<Comunitat> accesComunitats = new ArrayList<Comunitat>();

	public Usuari() {

	}

	public Usuari(String dni, String nom, String cognoms, String tel1,
			String tel2, String email, String password, boolean baixa,
			boolean bloquejat, boolean president, boolean administrador,
			boolean enviat, TipusVei tipus, List<ElementVei> elementsVei,
			List<Comunitat> accesComunitats) {
		super();
		this.dni = dni;
		this.nom = nom;
		this.cognoms = cognoms;
		this.tel1 = tel1;
		this.tel2 = tel2;
		this.email = email;
		this.password = password;
		this.baixa = baixa;
		this.bloquejat = bloquejat;
		this.president = president;
		this.administrador = administrador;
		this.enviat = enviat;
		this.tipus = tipus;
		this.elementsVei = elementsVei;
		this.accesComunitats = accesComunitats;
	}

	public static Usuari recercaPerDni(String dni) throws Exception {
		Usuari result = null;
		Query query = JPA.em().createQuery("from Usuari u where u.dni=?1");
		query.setParameter(1, dni);
		try {
			Usuari usuari = (Usuari) query.getSingleResult();
			return usuari;
		} catch (Exception e) {
			throw e;
		}
	}

	public static Page llistarUsuaris(int page) throws Exception {
		Query query = null;
		query = JPA.em().createQuery("from Usuari u");
		try {
			List list = query.getResultList();
			Collections.sort(list, UsuariComparator);
			Page p = new Page(list, page);
			return p;
		} catch (Exception e) {
			throw e;
		}
	}

	public static Page llistarUsuarisFiltrats(int page, UsuarisFiltre usuariFiltreForm) {
		// TODO Auto-generated method stub
		Query query = null;
		UsuarisFiltre u=usuariFiltreForm;
		String exp="select u from Usuari u";
		if(usuariFiltreForm.comunitat!=null){
			exp="select (a.vei) from AccesComunitat a join a.vei where a.comunitat=?1 order by a.vei asc ";
			query = JPA.em().createQuery(exp);
			query.setParameter(1, usuariFiltreForm.comunitat);
		}
		else{
			query = JPA.em().createQuery(exp);
		}
		
		List list = query.getResultList();
		
		Collections.sort(list, UsuariComparator);
		Page p = new Page(list, page);
		return p;		
		}
	
	public static List<Usuari> obtenirUsuaris() throws Exception {
		Query query = null;
		try {
			query = JPA.em().createQuery("from Usuari u");
			List list = query.getResultList();
			return list;
		} catch (Exception e) {
			throw e;
		}
	}

	public static Usuari obtenirRefUsuari(Usuari usuari) throws Exception {
		EntityManager em = JPA.em();
		try {
			Usuari RefUsuari = em.find(Usuari.class, usuari.dni);
			return RefUsuari;
		} catch (Exception e) {
			throw e;
		}
	}

	public static void guardarUsuari(Usuari usuari, boolean nou)
			throws Exception {

		try {
			Usuari refUsuari = obtenirRefUsuari(usuari);
			if (nou == false) {
				refUsuari.nom = usuari.nom;
				refUsuari.cognoms = usuari.cognoms;
				refUsuari.tel1 = usuari.tel1;
				refUsuari.tel2 = usuari.tel2;
				refUsuari.email = usuari.email;
				refUsuari.tipus = usuari.tipus;
				refUsuari.administrador = usuari.administrador;
				refUsuari.president = usuari.president;
				refUsuari.baixa = usuari.baixa;
				refUsuari.bloquejat = usuari.bloquejat;
				refUsuari.enviat = usuari.enviat;
				JPA.em().merge(refUsuari);

			} else {
				usuari.password = PasswordGenerator.getPassword(
						PasswordGenerator.MINUSCULAS
								+ PasswordGenerator.MAYUSCULAS
								+ PasswordGenerator.NUMEROS, 6);
				JPA.em().persist(usuari);
				Usuaris.correuAlta(usuari);
			}
		} catch (Exception e) {
			throw e;
		}

	}

	public static void correuAlta(Usuari usuari) throws Exception {

		usuari.password = PasswordGenerator.getPassword(
				PasswordGenerator.MINUSCULAS + PasswordGenerator.MAYUSCULAS
						+ PasswordGenerator.NUMEROS, 6);
		Email email = new Email();

		email.setSubject("Correu Alta a www.ComunicatComunitat");
		email.setFrom("comunicatcomunitat@gmail.com");
		email.addTo(usuari.email);
		email.setBodyText("A text message");
		try {
			MailerPlugin.send(email);
		} catch (Exception e) {
			throw e;
		}
		Usuari refUsuari = Usuari.obtenirRefUsuari(usuari);
		refUsuari.enviat = true;
		JPA.em().merge(refUsuari);
	}

	public static Usuari authenticate(String dni, String password)
			throws Exception {
		// TODO Auto-generated method stub

		Query query = JPA.em().createQuery(
				"SELECT u from  Usuari u where lower(u.dni)=?1");
		query.setParameter(1, dni.toLowerCase());
		try {
			Usuari usuari = (Usuari) query.getSingleResult();
			if (usuari.password.equalsIgnoreCase(password) && usuari.baixa == false
					&& usuari.bloquejat == false) {
				return usuari;
			} else {
				return null;
			}
		} catch (Exception e) {
			// Code for handling NoResultException
			throw e;
		}

	}

	public static Comparator<Usuari> UsuariComparator = new Comparator<Usuari>() {

		public int compare(Usuari u1, Usuari u2) {

			return u1.dni.compareTo(u2.dni);

		}

	};

	public static void assignarElement(Usuari usuari, Element element,
			TipusVei t) throws Exception {
		// TODO Auto-generated method stub
		Usuari refUsuari = obtenirRefUsuari(usuari);
		Element refElement = Element.obtenirRefElement(element);
		ElementVei elementVei = new ElementVei(refUsuari, refElement,
				TipusVei.recerca(t.tipus));
		try{
		refUsuari.elementsVei.add(elementVei);
		JPA.em().persist(elementVei);
		JPA.em().flush();
		}
		catch(Exception e){
			throw e;
		}
		Comunitat c = (refElement.comunitat);

		while (!c.nif.equals("arrel")) {
			if (refUsuari.accesComunitats.contains(c) == false) {
				refUsuari.accesComunitats.add(c);
			}
			c = (c.pare);

		}
	}

	public static void borrarUsuari(Usuari usuari) throws Exception {
		// TODO Auto-generated method stub
		try {
			EntityManager em = JPA.em();
			Usuari refUsuari = obtenirRefUsuari(usuari);
			em.remove(refUsuari);
			em.flush();
		} catch (Exception e) {
			throw e;
		}
	}

	public static void borrarElementAssignat(Usuari usuari, Element element,
			TipusVei tipus) throws Exception {
		// TODO Auto-generated method stub
		Usuari refUsuari = Usuari.obtenirRefUsuari(usuari);
		ElementVei elementVei = ElementVei.recerca(refUsuari, element, tipus);
		EntityManager em = JPA.em();
		ElementVei evr = elementVei;
		refUsuari.elementsVei.remove(evr);
		em.remove(evr);
		em.flush();

		refUsuari.accesComunitats.clear();
		
		for (Integer i = 0; i < refUsuari.elementsVei.size(); i = i + 1) {
			Comunitat c = refUsuari.elementsVei.get(i).element.comunitat;

			while (!c.nif.equals("arrel")) {
				if (refUsuari.accesComunitats.contains(c) == false) {
					refUsuari.accesComunitats.add(c);
				}
				c = (c.pare);
			}
		}
	}

}
