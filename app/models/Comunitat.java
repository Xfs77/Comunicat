package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import play.db.jpa.JPA;

@Entity
@Table(name = "comunicat.Comunitat")
public class Comunitat implements Serializable {

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
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "pare")
	public Comunitat pare;
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "president")
	public Comunitat president;

	public Comunitat() {

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
	
	public static Page llistarComunitats(String id, int page) {
		Query query = null;
		if (id==null) {
			query = JPA.em().createQuery("from Comunitat c where c.pare=null");

		}
		else{
			query = JPA.em().createQuery("from Comunitat c where c.pare.id=?");
			query.setParameter(1, id);
		}
		List list = query.getResultList();
		Collections.sort(list,ComunitatComparator);
		Page p= new Page(list,page);
		return p;
		
	}
	 public void guardarComunitat() {
		 JPA.em().merge(this);

	 }

	 public static Comparator<Comunitat> ComunitatComparator = new Comparator<Comunitat>() {
		    
	        public int compare(Comunitat c1, Comunitat c2) {
	        
	            return c1.nif.compareTo(c2.nif);
	        
	        }
	 
	    };

}
