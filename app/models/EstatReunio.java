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
@Table(name = "comunicat.EstatReunio")
public class EstatReunio implements Serializable, PathBindable<EstatReunio> {

	

	@Id
	@Column(name = "estat")
	public String estat;
	@Column(name = "descripcio")
	public String descripcio;
	


	public EstatReunio() {

	}

	
	
	
	public static EstatReunio recerca(String estat) {
		EstatReunio result = null;
		Query query = JPA.em().createQuery("from EstatReunio e");
		List<EstatReunio> estatsReunio = query.getResultList();

		for (EstatReunio candidate : estatsReunio) {
			if (candidate.estat.equals(estat)) {
				result = candidate;

			}
		}
		return result;
	}
		

	public static List<EstatReunio> obtenirEstatsReunio() {
		Query query = null;
		query = JPA.em().createQuery("from EstatReunio e");
		List list = query.getResultList();
		return list;

	}
	
	public EstatReunio(String estat) {
		super();
		this.estat = estat;
	}

	@Override
	public EstatReunio bind(String arg0, final String arg1) {
		// TODO Auto-generated method stub
		final EstatReunio t[] = { null };
		JPA.withTransaction(new F.Callback0() {
			@Override
			public void invoke() throws Throwable {
				t[0] = (EstatReunio.recerca(arg1));
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


	
}