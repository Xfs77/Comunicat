package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import play.db.jpa.JPA;


@Entity
@Table(name = "public.Fill")
public class Fill implements Serializable {

	@Id
	@ManyToOne
	@JoinColumn(name = "pareid" , nullable=true)
	public Pare pare;
	@Id
	@Column(name = "codi")
	public String codi;
	@Column(name = "txt")
	public String txt;
	
	public Fill (){
		
	}

	public Fill(Pare pare, String codi, String txt) {
		super();
		this.pare = pare;
		this.codi = codi;
		this.txt = txt;
	}
	
	public static void Fills(){
		
		Query q=JPA.em().createQuery("FROM Fill");
		List l=q.getResultList();
		String s="";
	}
	
	
}
