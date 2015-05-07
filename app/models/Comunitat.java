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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.Table;

import org.hibernate.JDBCException;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.postgresql.util.PSQLException;

import controllers.Comunitats;
import play.libs.F;
import play.libs.F.Some;
import play.mvc.QueryStringBindable;
import play.db.jpa.JPA;
import play.mvc.PathBindable;
import play.db.jpa.Transactional;

import play.mvc.*;


@Entity
@Table(name = "comunicat.Comunitat")
public class Comunitat implements Serializable, QueryStringBindable<Comunitat>,
		PathBindable<Comunitat> {

	@Override
	public Comunitat bind(String arg0, final String arg1) {
		// TODO Auto-generated method stub
		final Comunitat c[] = { null };
		JPA.withTransaction(new F.Callback0() {
			@Override
			public void invoke() throws Throwable {
				c[0] = (Comunitat.recercaPerNif(arg1));
			}
		});

		return (c[0]);
	}

	@Override
	public play.libs.F.Option<Comunitat> bind(final String arg0,
			final Map<String, String[]> arg1) {
		// TODO Auto-generated method stub

		if (arg0 == "pare" && arg1.get("pare") != null) {
			final Comunitat c[] = { null };
			JPA.withTransaction(new F.Callback0() {
				@Override
				public void invoke() throws Throwable {
					c[0] = (Comunitat.recercaPerNif(arg1.get("pare")[0]));
				}
			});

			return play.libs.F.Option.Some(c[0]);
		}

		else
			return null;
	}

	@Override
	public String javascriptUnbind() {
		// TODO Auto-generated method stub
		return this.nif;
	}

	@Override
	public String unbind(String arg0) {
		// TODO Auto-generated method stub
		return this.nif;
	}

	@Id
	@Column(name = "nif")
	public String nif;
	@Column(name = "nom")
	public String nom;
	@Column(name = "adreca")
	public String adreca;
	@Column(name = "cp")
	public String cp;
	@Column(name = "poblacio")
	public String poblacio;
	@Column(name = "coeficient")
	public float coeficient;
	@OneToOne
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "pare")
	public Comunitat pare;
	@OneToOne
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "president")
	public Usuari president;
	@OneToMany(cascade=CascadeType.MERGE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "nif")
	private List<Element> elements = new ArrayList<Element>();
	@ManyToMany(mappedBy="accesComunitats")
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<Usuari> accesComunitats =new ArrayList<Usuari>();


	public Comunitat() {

	}

	public Comunitat(Comunitat pare) {
		super();
		this.pare = pare;
	}

	

	public Comunitat(String nif, String nom, String adreca, String cp, String poblacio, float coeficient,
			Comunitat pare, Usuari president, List<Element> elements, List<Usuari> accesComunitats) {
		super();
		this.nif = nif;
		this.nom = nom;
		this.adreca = adreca;
		this.cp = cp;
		this.poblacio = poblacio;
		this.coeficient = coeficient;
		this.pare = pare;
		this.president = president;
		this.elements = elements;
		this.accesComunitats = accesComunitats;
	}

	public static Page llistarComunitats(int page) {
		Query query = null;
		query = JPA.em().createQuery("from Comunitat");
		List list = query.getResultList();
		Collections.sort(list, ComunitatComparator);
		Page p = new Page(list, page);
		return p;

	}
	
	public static List<Comunitat> obtenirComunitats() {
		Query query = null;
		query = JPA.em().createQuery("from Comunitat c where c.nif!=?1");
		query.setParameter(1, "arrel");
		List<Comunitat> comunitats = query.getResultList();
		return comunitats;

	}
	
	
	public static List<Comunitat> llistarComunitatsElements() {
		Query query = null;
		query = JPA.em().createQuery("select c from Comunitat c  join c.elements group by c.nif");
	    List list = query.getResultList();
	    Collections.sort(list, ComunitatComparator);
		
		return list;

	}

	public static Page llistarSubComunitats(Comunitat pare, int page) {
		Query query = null;

		query = JPA.em().createQuery("select c from Comunitat c where c.pare=?1 ");
		//query = JPA.em().createQuery("select c from Comunitat c join c.accesComunitats where c.pare=?1 ");
		String dni=play.mvc.Controller.session().get("dni");
		query.setParameter(1, pare);
		//query.setParameter(2, dni);
		List list = query.getResultList();
		Collections.sort(list, ComunitatComparator);
		Page p = new Page(list, page);
		return p;

	}

	public static void guardarComunitat(Comunitat formComunitat, Comunitat pare) {

		Comunitat refComunitat = obtenirRefComunitat(formComunitat);
		if (refComunitat != null) {
			refComunitat.nom = formComunitat.nom;
			refComunitat.adreca = formComunitat.adreca;
			refComunitat.cp = formComunitat.cp;
			refComunitat.poblacio = formComunitat.poblacio;
			refComunitat.coeficient = formComunitat.coeficient;
			refComunitat.president = formComunitat.president;
			JPA.em().merge(refComunitat);

		} else {
			formComunitat.pare = pare;
			JPA.em().merge(formComunitat);
		}
	}

	public static void borrarComunitat(Comunitat comunitat) {
		// TODO Auto-generated method stub
		EntityManager em = JPA.em();
		Comunitat refComunitat = obtenirRefComunitat(comunitat);
		em.remove(refComunitat);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nif == null) ? 0 : nif.hashCode());
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
		Comunitat other = (Comunitat) obj;
		if (nif == null) {
			if (other.nif != null)
				return false;
		} else if (!nif.equals(other.nif))
			return false;
		return true;
	}

	public static Comunitat obtenirRefComunitat(Comunitat comunitat) {
		EntityManager em = JPA.em();
		Comunitat RefComunitat = em.find(Comunitat.class, comunitat.nif);
		return RefComunitat;
	}
	
	
	public static Comunitat recercaPerNif(String id) {
		Comunitat result = null;
		Query query = JPA.em().createQuery("from Comunitat c");
		List<Comunitat> comunitats = query.getResultList();

		for (Comunitat candidate : comunitats) {
			if (candidate.nif.toLowerCase().contains(id.toLowerCase())) {
				result = candidate;

			}
		}
		return result;
	}

	public static Comunitat recercaPerNifIni() {
		Comunitat result = null;
		Query query = JPA.em().createQuery("from Comunitat c");
		List<Comunitat> comunitats = query.getResultList();

		for (Comunitat candidate : comunitats) {
			if (candidate.pare == null) {
				result = candidate;

			}
		}
		return result;
	}

	public static Comparator<Comunitat> ComunitatComparator = new Comparator<Comunitat>() {

		public int compare(Comunitat c1, Comunitat c2) {

			return c1.nif.compareTo(c2.nif);

		}

	};


	public static Page llistarContactes(Comunitat comunitat, int page) {
		// TODO Auto-generated method stub
		Query query = JPA.em().createQuery("from Usuari u where u.administrador=true");
		List<Usuari> contactes=query.getResultList();
		contactes.add(comunitat.president);
		Page p = new Page(contactes, page);
		return p;
	}

}
