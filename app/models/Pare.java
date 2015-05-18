package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


@Entity
@Table(name = "public.Pare")
public class Pare {

	
	@Id
	@Column(name = "id")
	public String id;
	@Column(name = "txt")
	public String txt;
	
	//@OneToMany(mappedBy="pare", cascade=CascadeType.ALL)
	@OneToMany(mappedBy="pare")
	@LazyCollection( LazyCollectionOption.FALSE)
	public List<Fill> fills = new ArrayList<Fill>();
	
	public Pare( )
	{
		
	}

	public Pare(String id, String txt) {
		super();
		this.id = id;
		this.txt = txt;
	}

	public static void borrarPare() throws Exception{
		
		
	String id="1";
		try{
	
		
			EntityManager em = JPA.em();
		
		Pare refPare = em.find(Pare.class, id);
		refPare.fills.clear();
		//Pare p=new Pare("4","4");
		//Fill f=new Fill(p,"2","hola");
	//	refPare.fills.add(f);
	//	em.persist(refPare);
//em.getTransaction().		
		em.remove(refPare);
		em.flush();
		}
		catch(Exception e){
			throw e;
			
		}
		}
	
}
