package models;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.Table;

import org.hibernate.JDBCException;
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
import play.db.jpa.JPA;
import play.mvc.PathBindable;
import play.db.jpa.Transactional;

@Entity
@Table(name = "comunicat.Usuari")
public class Usuari implements Serializable,
PathBindable<Usuari> {

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
	@Column(name = "dni")
	public String dni;
	@Column(name = "nom")
	public String nom;
	@Column(name = "cognoms")
	public String cognoms;
	@Column(name = "tel1")
	public String tel1;
	@Column(name = "tel2")
	public String tel2;
	@Column(name = "email")
	public String email;
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
	@Column(name = "tipus")
	public String tipus;
	 @ManyToMany(cascade = {CascadeType.ALL}, fetch=FetchType.EAGER)
	 @JoinTable(name="comunicat.elementvei", 
	                joinColumns={@JoinColumn(name="vei", referencedColumnName="dni")}, 
	                inverseJoinColumns={
	    							@JoinColumn(table="element",name="comunitat",referencedColumnName="nif"),
	    							@JoinColumn(table="element",name="element",referencedColumnName="codi")

	   })
	public List<Element> elements_vei=new ArrayList<Element>();
	


	public Usuari() {

	}

	public Usuari(String dni, String nom, String cognoms, String tel1,
			String tel2, String email, String password, boolean baixa,
			boolean bloquejat, boolean president, boolean administrador,
			boolean enviat, String tipus, List<Element> elements_vei) {
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
		this.elements_vei = elements_vei;
	}



	public static Usuari recercaPerDni(String dni) {
		Usuari result = null;
		Query query = JPA.em().createQuery("from Usuari u where u.dni=?1");
		query.setParameter(1,dni);
		Usuari usuari = (Usuari) query.getSingleResult();

		return usuari;
	}

		
	public static Page llistarUsuaris(int page) {
		Query query = null;
		query = JPA.em().createQuery("from Usuari u");
		List list = query.getResultList();
		Collections.sort(list, UsuariComparator);
		Page p = new Page(list, page);
		return p;

	}
	

	public static List<Usuari> obtenirUsuaris() {
		Query query = null;
		query = JPA.em().createQuery("from Usuari u");
		List list = query.getResultList();
		return list;

	}
	
	public static Usuari obtenirRefUsuari(Usuari usuari) {
		EntityManager em = JPA.em();
		Usuari RefUsuari = em.find(Usuari.class, usuari.dni);
		return RefUsuari;
	}
	
	public static void guardarUsuari(Usuari usuari) {

		Usuari refUsuari = obtenirRefUsuari(usuari);
		if (refUsuari != null) {
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
			Usuaris.correuAlta(usuari);
			}

		
	}

	public static void correuAlta(Usuari usuari) {
		
		usuari.password=PasswordGenerator.getPassword(
			PasswordGenerator.MINUSCULAS+
			PasswordGenerator.MAYUSCULAS+
			PasswordGenerator.NUMEROS,6);
		Email email = new Email();
	
		email.setSubject("Correu Alta a www.ComunicatComunitat");
		email.setFrom("comunicatcomunitat@gmail.com");
		email.addTo(usuari.email);
		email.setBodyText("A text message");
	
		MailerPlugin.send(email);
	
		usuari.enviat=true;
		JPA.em().merge(usuari);
	}

	public static Usuari authenticate(String dni, String password) {
		// TODO Auto-generated method stub

		 Query query =JPA.em().createQuery("SELECT u from  Usuari u where u.dni=?1");
		 query.setParameter(1, dni);
		 try {
			 Usuari usuari=(Usuari) query.getSingleResult();
			 if (usuari.password.equals(password) && usuari.baixa==false && usuari.bloquejat==false) {
				 return usuari;
			 }
			 else{
				 return null;
			 }
		 }
		 catch (NoResultException nre) {
			 // Code for handling NoResultException
			 return null;
			 
		 } 
	
	}
	public static Comparator<Usuari> UsuariComparator = new Comparator<Usuari>() {

		public int compare(Usuari u1, Usuari u2) {

			return u1.dni.compareTo(u2.dni);

		}

	};

	
	public static void assignarElement(Usuari usuari, Element element) {
		// TODO Auto-generated method stub
		Usuari refUsuari = obtenirRefUsuari(usuari);
		Element refElement=Element.obtenirRefElement(element);
		refUsuari.elements_vei.add(refElement);
		
	}



	public static void borrarUsuari(Usuari usuari) {
		// TODO Auto-generated method stub		
			EntityManager em = JPA.em();
			Usuari refUsuari = obtenirRefUsuari(usuari);
			em.remove(refUsuari);
		}
	



	public static void borrarElementAssignat(Usuari usuari, Element element) {
		// TODO Auto-generated method stub
		Element RefElement=Element.obtenirRefElement(element);
		Usuari RefUsuari=Usuari.obtenirRefUsuari(usuari);
		boolean b=RefUsuari.elements_vei.contains(element);
		RefUsuari.elements_vei.remove(element);
		EntityManager em = JPA.em();
		em.merge(RefUsuari);
			

	}
	

}
