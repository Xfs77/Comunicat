package models;

import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import play.db.jpa.JPA;
import play.libs.F;
import play.mvc.PathBindable;

@Entity
@Table(name = "comunicat.movimentnota")
@IdClass(MovimentNotaPK.class)
public class MovimentNota implements Serializable, PathBindable<MovimentNota> {

	@Id
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "nota")
	public Nota nota;
	@Id
	@Column(name = "codi", columnDefinition = "serial")
	public int codi;
	@Column(name = "fecha")
	public Date fecha;
	@Column(name = "detall")
	public String detall;
	@Column(name = "previsio")
	public Date previsio;
	@ManyToOne
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "estat")
	public EstatNota estat;
	@ManyToOne
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "usuari")
	public Usuari usuari;
	@Column(name = "notificada")
	public boolean notificada;

	@Override
	public MovimentNota bind(String arg0, String arg1) {
		// TODO Auto-generated method stub
		final MovimentNota m[] = { null };
		final Nota n[] = { null };
		final String nota = arg1.substring(0, arg1.indexOf('&'));
		final String moviment = arg1.substring(arg1.indexOf('&') + 1);
		JPA.withTransaction(new F.Callback0() {
			@Override
			public void invoke() throws Throwable {
				n[0] = (Nota.recercaPerCodi(Integer.parseInt(nota)));
				m[0] = (MovimentNota.recercaPerCodi(n[0],
						Integer.parseInt(moviment)));
			}
		});

		return (m[0]);
	}

	@Override
	public String javascriptUnbind() {
		// TODO Auto-generated method stub
		return (Integer.toString(this.nota.codi) + "&" + Integer
				.toString(this.codi));
	}

	@Override
	public String unbind(String arg0) {
		// TODO Auto-generated method stub
		return (Integer.toString(this.nota.codi) + "&" + Integer
				.toString(this.codi));
	}

	public MovimentNota() {
	}

	public MovimentNota(Nota nota) {
		this.nota = nota;
	}

	public MovimentNota(Nota nota, int codi, Date fecha, String detall,
			Date previsio, EstatNota estat, Usuari usuari, boolean notificada) {
		super();
		this.nota = nota;
		this.codi = codi;
		this.fecha = fecha;
		this.detall = detall;
		this.previsio = previsio;
		this.estat = estat;
		this.usuari = usuari;
		this.notificada = notificada;
	}

	public static MovimentNota recercaPerCodi(models.Nota nota, int codi)
			throws Exception {
		// TODO Auto-generated method stub
		MovimentNota result = null;
		Query query = JPA.em().createQuery(
				"from MovimentNota m where m.nota=?1 and m.codi=?2");
		query.setParameter(1, nota);
		query.setParameter(2, codi);

		try {
			List<MovimentNota> moviments = query.getResultList();

			for (MovimentNota candidate : moviments) {
				if (candidate.codi == codi && candidate.nota.equals(nota)) {
					result = candidate;
				}
			}
			return result;
		} catch (Exception e) {
			throw e;
		}
	}

	public static MovimentNota obtenirRefMovimentNota(MovimentNota movimentNota) {
		EntityManager em = JPA.em();
		MovimentNotaPK mpk = new MovimentNotaPK(movimentNota.nota.codi,
				movimentNota.codi);
		MovimentNota refMovimentNota = em.find(MovimentNota.class, mpk);
		return refMovimentNota;
	}

	public static Page llistarMoviments(Nota nota, int page) throws Exception {
		// TODO Auto-generated method stub
		Query query = null;
		query = JPA.em().createQuery("from MovimentNota m where m.nota=?1");
		query.setParameter(1, nota);
		try {
			List list = query.getResultList();
			Collections.sort(list, MovimentComparator);
			Page p = new Page(list, page);
			return p;
		} catch (Exception e) {
			throw e;
		}
	}

	public static Comparator<MovimentNota> MovimentComparator = new Comparator<MovimentNota>() {

		public int compare(MovimentNota m1, MovimentNota m2) {

			if (m1.nota.codi == m2.nota.codi) {
				if (m1.codi < m2.codi) {
					return 1;
				} else
					return -1;
			} else {
				if (m1.nota.codi < m2.nota.codi) {
					return 1;
				} else
					return -1;
			}

		}

	};

	public static void borrarMovimentNota(MovimentNota moviment)
			throws Exception {
		// TODO Auto-generated method stub
		EntityManager em = JPA.em();
		MovimentNota refMovimentNota = obtenirRefMovimentNota(moviment);
		try {
			em.remove(refMovimentNota);
			em.flush();
		} catch (Exception e) {
			throw e;
		}
		Query query = null;
		query = JPA.em().createQuery(
				"SELECT MAX(m.codi) from MovimentNota m WHERE m.nota=?1");
		query.setParameter(1, moviment.nota);
		Integer m = 0;
		MovimentNota ultimMov = null;
		try {
			m = (Integer) query.getSingleResult();
		} catch (Exception e) {
			throw e;
		}
		try {
			ultimMov = MovimentNota.recercaPerCodi(moviment.nota, m);
		} catch (Exception e) {
			throw e;
		}
		try {
			ultimMov.nota.estat = ultimMov.estat;
			JPA.em().merge(ultimMov);
			JPA.em().flush();
		} catch (Exception e) {
			throw e;
		}
	}

	public static MovimentNota guardarMovimentNota(
			MovimentNota movimentNotaForm, Boolean nou) throws Exception {
		// TODO Auto-generated method stub
		MovimentNota refMovimentNota = obtenirRefMovimentNota(movimentNotaForm);
		Nota refNota = Nota.obtenirRefNota(movimentNotaForm.nota);

		if (nou == false) {

			refMovimentNota.detall = movimentNotaForm.detall;
			refMovimentNota.fecha = movimentNotaForm.fecha;
			refMovimentNota.previsio = movimentNotaForm.previsio;
			refMovimentNota.estat = movimentNotaForm.estat;
			try {
				MovimentNota m = JPA.em().merge(refMovimentNota);
				JPA.em().flush();
				return m;
			} catch (Exception e) {
				throw e;
			}

		} else {
			Query query = null;
			query = JPA.em().createQuery(
					"SELECT MAX(m.codi) from MovimentNota m WHERE m.nota=?1");
			query.setParameter(1, movimentNotaForm.nota);
			try {
				Integer i = (Integer) query.getSingleResult();
				movimentNotaForm.codi = i + 1;
			} catch (Exception e) {
				movimentNotaForm.codi = 1;
			}
			MovimentNota m = JPA.em().merge(movimentNotaForm);
			refNota.estat = m.estat;
			try {
				JPA.em().merge(refNota);
				JPA.em().flush();
				return (m);
			} catch (Exception e) {
				throw e;
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + codi;
		result = prime * result + ((nota == null) ? 0 : nota.hashCode());
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
		MovimentNota other = (MovimentNota) obj;
		if (codi != other.codi)
			return false;
		if (nota == null) {
			if (other.nota != null)
				return false;
		} else if (!nota.equals(other.nota))
			return false;
		return true;
	}

}
