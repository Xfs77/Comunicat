package models;

import java.io.Serializable;

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
@Table(name = "comunicat.TipusVei")
public class TipusVei implements Serializable, PathBindable<TipusVei> {

	
	@Id
	@Column(name = "tipus")
	public String tipus;
	


	public TipusVei() {

	}

	public static TipusVei recerca(String txt) {
		TipusVei result = null;
		Query query = JPA.em().createQuery("from TipusVei t where t.tipus=?1");
		query.setParameter(1,txt);
		result = (TipusVei) query.getSingleResult();

		return result;
	}

	
	public TipusVei(String tipus) {
		super();
		this.tipus = tipus;
	}

	@Override
	public TipusVei bind(String arg0, final String arg1) {
		// TODO Auto-generated method stub
		final TipusVei t[] = { null };
		JPA.withTransaction(new F.Callback0() {
			@Override
			public void invoke() throws Throwable {
				t[0] = (TipusVei.recerca(arg1));
			}
		});

		return (t[0]);
		}

	@Override
	public String javascriptUnbind() {
		// TODO Auto-generated method stub
		return this.tipus;
	}

	@Override
	public String unbind(String arg0) {
		// TODO Auto-generated method stub
		return this.tipus;
	}

	
}