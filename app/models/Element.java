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
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import controllers.Comunitats;
import play.libs.F;
import play.libs.F.Some;
import play.mvc.QueryStringBindable;
import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.mvc.PathBindable;
import play.db.jpa.Transactional;
import models.ElementPK;

@Entity
@Table(name = "comunicat.Element")
@IdClass(ElementPK.class)
public class Element implements Serializable, PathBindable<Element> {
	@Id
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "nif")
	public Comunitat comunitat;
	@Id
	@Required
	@MaxLength(50)
	@Column(name = "codi")
	public String codi;
	@Required
	@MaxLength(150)
	@Column(name = "descripcio")
	public String descripcio;
	@Required
	@Max(1)
	@Min(0)
	@Column(name = "coeficient")
	public float coeficient;
	@OneToMany(mappedBy = "element")
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<ElementVei> elementsVei = new ArrayList<ElementVei>();

	@Override
	public Element bind(String arg0, final String arg1) {
		// TODO Auto-generated method stub
		final Element e[] = { null };
		final Comunitat c[] = { null };
		final String comunitat = arg1.substring(0, arg1.indexOf('&'));
		final String element = arg1.substring(arg1.indexOf('&') + 1);
		JPA.withTransaction(new F.Callback0() {
			@Override
			public void invoke() throws Throwable {
				c[0] = (Comunitat.recercaPerNif(comunitat));
				e[0] = (Element.recercaPerCodi(c[0], element));
			}
		});

		return (e[0]);
	}

	@Override
	public String javascriptUnbind() {
		// TODO Auto-generated method stub
		return (this.comunitat.nif + "&" + this.codi);
	}

	@Override
	public String unbind(String arg0) {
		// TODO Auto-generated method stub
		return (this.comunitat.nif + "&" + this.codi);
	}

	public Element() {

	}

	public Element(Comunitat comunitat, String codi, String descripcio,
			float coeficient, List<ElementVei> elementsVei) {
		super();
		this.comunitat = comunitat;
		this.codi = codi;
		this.descripcio = descripcio;
		this.coeficient = coeficient;
		this.elementsVei = elementsVei;
	}

	public Element(Comunitat comunitat) {
		super();
		this.comunitat = comunitat;

	}

	public static Element obtenirRefElement(Element element) {
		EntityManager em = JPA.em();
		ElementPK epk = new ElementPK(element.comunitat.nif, element.codi);
		Element RefElement = em.find(Element.class, epk);
		return RefElement;
	}

	public static List<Element> obtenirElements() throws Exception {
		Query query = null;
		query = JPA.em().createQuery("from Element");
		try {
			List<Element> elements = query.getResultList();
			return elements;
		} catch (Exception e) {
			throw e;
		}
	}

	public static Page llistarElements(Comunitat comunitat, int page)
			throws Exception {
		Query query = null;
		query = JPA.em().createQuery("from Element e where e.comunitat=?1");
		query.setParameter(1, comunitat);
		try {
			List<Element> list = query.getResultList();
			Collections.sort(list, ElementComparator);
			Page p = new Page(list, page);
			return p;
		} catch (Exception e) {
			throw e;
		}

	}

	public static void guardarElement(Element element, boolean nou)
			throws Exception {
		Element refElement = obtenirRefElement(element);
		if (nou == false) {
			refElement.descripcio = element.descripcio;
			refElement.coeficient = element.coeficient;
			try {
				JPA.em().merge(refElement);
				JPA.em().flush();
			} catch (Exception e) {
				throw e;
			}
		} else {
			element.comunitat = Comunitat
					.obtenirRefComunitat(element.comunitat);
			try {
				JPA.em().persist(element);
				JPA.em().flush();
			} catch (Exception e) {
				throw e;
			}
		}

	}

	public static void borrarElement(Element element) throws Exception {
		// TODO Auto-generated method stub
		EntityManager em = JPA.em();
		ElementPK pk = new ElementPK();
		pk.setCodi(element.codi);
		pk.setComunitat(element.comunitat.nif);
		Element actorToBeRemoved = em.find(Element.class, pk);

		try {
			em.remove(actorToBeRemoved);
			em.flush();
		} catch (Exception e) {
			throw e;
		}
	}

	public static Element recercaPerCodi(Comunitat comunitat, String codi)
			throws Exception {
		Element result = null;
		Query query = JPA.em().createQuery(
				"from Element e where e.comunitat=?1 and e.codi=?2");
		query.setParameter(1, comunitat);
		query.setParameter(2, codi);

		try {
			List<Element> elements = query.getResultList();
			for (Element candidate : elements) {
				if (candidate.codi.toLowerCase().contains(codi.toLowerCase())
						&& candidate.comunitat.equals(comunitat)) {
					result = candidate;
				}
			}
			return result;
		} catch (Exception e) {
			throw e;
		}

	}

	public static Comparator<Element> ElementComparator = new Comparator<Element>() {

		public int compare(Element e1, Element e2) {

			if (e1.comunitat.nif.compareTo(e2.comunitat.nif) == 0) {
				return e1.codi.compareTo(e2.codi);
			} else {
				return e1.codi.compareTo(e2.codi);
			}

		}

	};

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result
				+ ((comunitat == null) ? 0 : comunitat.hashCode());
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
		Element other = (Element) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (comunitat == null) {
			if (other.comunitat != null)
				return false;
		} else if (!comunitat.equals(other.comunitat))
			return false;
		return true;
	}

}
