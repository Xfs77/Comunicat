package models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import play.db.jpa.JPA;
 
	@Entity
	@Table(name = "comunicat.elementvei")
	public class ElementVei implements Serializable{
		@Id
		@GeneratedValue
		@Column(name = "elementveiid")
		public int elementveiid;
		@ManyToOne
		@LazyCollection(LazyCollectionOption.FALSE)
		@JoinColumn(name = "vei")
		public Usuari usuari;
		@ManyToOne
		@LazyCollection(LazyCollectionOption.FALSE)
		@JoinColumns( {
	        @JoinColumn(name = "comunitat",referencedColumnName="nif"),
	        @JoinColumn(name = "element",referencedColumnName="codi")})
		public Element element;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + elementveiid;
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
			ElementVei other = (ElementVei) obj;
			if (elementveiid != other.elementveiid)
				return false;
			return true;
		}

		@ManyToOne
		@LazyCollection(LazyCollectionOption.FALSE)
		@JoinColumn(name = "tipus")
		public TipusVei tipus;
		
		public ElementVei() {
		
		}

		public ElementVei(Usuari usuari, Element element, TipusVei tipus) {
			super();
			this.usuari = usuari;
			this.element = element;
			this.tipus = tipus;
		}
			
		public static ElementVei recerca(Usuari usuari, Element element,TipusVei tipus) throws Exception {
			ElementVei result = null;
			Query query = JPA.em().createQuery("from ElementVei ev where ev.usuari=?1 and ev.element=?2 and ev.tipus=?3");
			Usuari u=null;
			u=Usuari.obtenirRefUsuari(usuari);
			query.setParameter(1,u);
			query.setParameter(2,Element.obtenirRefElement(element));
			query.setParameter(3,tipus);
			result=(ElementVei) query.getSingleResult();
			return result;
}
	}
