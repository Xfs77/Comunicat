package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import controllers.Comunitats;
import play.libs.F;
import play.libs.F.Some;
import play.mvc.QueryStringBindable;
import play.db.jpa.JPA;
import play.mvc.PathBindable;
import play.db.jpa.Transactional;

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
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pare")
	public Comunitat pare;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "president")
	public Comunitat president;

	public Comunitat() {

	}

	public Comunitat(Comunitat pare) {
		this.pare = pare;
	}

	public Comunitat(String nif, String nom, String adreca, String cp,
			String poblacio, float coeficient, Comunitat pare,
			Comunitat president) {
		super();
		this.nif = nif;
		this.nom = nom;
		this.adreca = adreca;
		this.cp = cp;
		this.poblacio = poblacio;
		this.coeficient = coeficient;
		this.pare = pare;
		this.president = president;
	}

	public static Page llistarComunitats(int page) {
		Query query = null;
		query = JPA.em().createQuery("from Comunitat c where c.pare is null");
		List list = query.getResultList();
		Collections.sort(list, ComunitatComparator);
		Page p = new Page(list, page);
		return p;

	}
	public static Page llistarSubComunitats(Comunitat pare, int page) {
		Query query = null;

		query = JPA.em().createQuery("from Comunitat c where c.pare=?");
		query.setParameter(1, pare);
		List list = query.getResultList();
		Collections.sort(list, ComunitatComparator);
		Page p = new Page(list, page);
		return p;

	}

	public void guardarComunitat() {
		Comunitat j = this;
		JPA.em().merge(this);

	}

	public static void borrarComunitat(Comunitat comunitat) {
		// TODO Auto-generated method stub
		EntityManager em = JPA.em();
		Comunitat actorToBeRemoved = em.getReference(Comunitat.class,
				comunitat.nif);
		em.remove(actorToBeRemoved);

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

}
