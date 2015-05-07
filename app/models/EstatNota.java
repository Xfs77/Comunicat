package models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;

import play.db.jpa.JPA;
import play.libs.F;
import play.mvc.PathBindable;


@Entity
@Table(name = "comunicat.EstatNota")
public class EstatNota implements Serializable, PathBindable<EstatNota> {

	
	public String getEstat() {
		return estat;
	}




	public void setEstat(String estat) {
		this.estat = estat;
	}




	public String getDescripcio() {
		return descripcio;
	}




	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	@Id
	@Column(name = "estat")
	public String estat;
	@Column(name = "descripcio")
	public String descripcio;
	


	public EstatNota() {

	}

	
	
	
	public static EstatNota recerca(String estat) {
		EstatNota result = null;
		Query query = JPA.em().createQuery("from EstatNota e");
		List<EstatNota> estatsNota = query.getResultList();

		for (EstatNota candidate : estatsNota) {
			if (candidate.estat.equals(estat)) {
				result = candidate;

			}
		}
		return result;
	}
		

	public static List<EstatNota> obtenirEstatsNota() {
		Query query = null;
		query = JPA.em().createQuery("from EstatNota e");
		List list = query.getResultList();
		return list;

	}
	
	public EstatNota(String estat) {
		super();
		this.estat = estat;
	}

	@Override
	public EstatNota bind(String arg0, final String arg1) {
		// TODO Auto-generated method stub
		final EstatNota t[] = { null };
		JPA.withTransaction(new F.Callback0() {
			@Override
			public void invoke() throws Throwable {
				t[0] = (EstatNota.recerca(arg1));
			}
		});

		return (t[0]);
		}

	@Override
	public String javascriptUnbind() {
		// TODO Auto-generated method stub
		return this.estat;
	}

	@Override
	public String unbind(String arg0) {
		// TODO Auto-generated method stub
		return this.estat;
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descripcio == null) ? 0 : descripcio.hashCode());
		result = prime * result + ((estat == null) ? 0 : estat.hashCode());
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
		EstatNota other = (EstatNota) obj;
		
		if (estat == null) {
			if (other.estat != null)
				return false;
		} else if (!estat.equals(other.estat))
			return false;
		return true;
	}

	
}