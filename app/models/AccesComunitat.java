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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import play.db.jpa.JPA;
 
	@Entity
	@Table(name = "comunicat.accescomunitat")
	public class AccesComunitat implements Serializable{
		
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		public int id;
		@ManyToOne
		@LazyCollection(LazyCollectionOption.FALSE)
		@JoinColumn(name = "comunitat")
		public Comunitat comunitat;
		@ManyToOne
		@LazyCollection(LazyCollectionOption.FALSE)
		@JoinColumn(name = "vei")
		public Usuari vei;

	AccesComunitat (){
		
	}

	public AccesComunitat(int id, Comunitat comunitat, Usuari vei) {
		super();
		this.id = id;
		this.comunitat = comunitat;
		this.vei = vei;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		AccesComunitat other = (AccesComunitat) obj;
		if (id != other.id)
			return false;
		return true;
	}

	}
