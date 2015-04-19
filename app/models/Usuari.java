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
import play.libs.F;
import play.libs.F.Some;
import play.mvc.QueryStringBindable;
import play.db.jpa.JPA;
import play.mvc.PathBindable;
import play.db.jpa.Transactional;

@Entity
@Table(name = "comunicat.Usuari")
public class Usuari {

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
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "nif")
	public Set<Comunitat> administradorsComunitats;

	public Usuari() {

	}

	

	public Usuari(String dni, String nom, String cognoms, String tel1,
			String tel2, String email, String password, boolean baixa,
			boolean bloquejat, boolean president, boolean administrador,
			boolean enviat, String tipus,
			Set<Comunitat> administradorsComunitats) {
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
		this.administradorsComunitats = administradorsComunitats;
	}



	public static Page llistarUsuaris(int page) {
		Query query = null;
		query = JPA.em().createQuery("from Usuari u");
		List list = query.getResultList();
		Collections.sort(list, UsuariComparator);
		Page p = new Page(list, page);
		return p;

	}

	public static Comparator<Usuari> UsuariComparator = new Comparator<Usuari>() {

		public int compare(Usuari u1, Usuari u2) {

			return u1.dni.compareTo(u2.dni);

		}

	};
}
