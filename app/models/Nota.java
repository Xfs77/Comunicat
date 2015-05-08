package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import play.db.jpa.JPA;
import play.libs.F;
import play.mvc.PathBindable;
 
	@Entity
	@Table(name = "comunicat.nota")
	public class Nota implements Serializable, PathBindable<Nota> {
		@Id
		@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_nota")
		@SequenceGenerator(name = "seq_nota", sequenceName = "seq_nota", allocationSize = 1, initialValue = 7)
		@Column(name = "codi")
		public int codi;
		@Column(name = "fecha")
		public Date fecha;
		@Column(name = "descripcio")
		public String descripcio;
		@ManyToOne
		@LazyCollection(LazyCollectionOption.FALSE)
		@JoinColumn(name = "estat")
		public EstatNota estat;
		@ManyToOne
		@LazyCollection(LazyCollectionOption.FALSE)
		@JoinColumn(name = "comunitat")
		public Comunitat comunitat;
		@ManyToOne
		@LazyCollection(LazyCollectionOption.FALSE)
		@JoinColumn(name = "usuari")
		public Usuari usuari;
		@OneToMany(mappedBy="nota", cascade=CascadeType.ALL)
		private List<MovimentNota> movimentsNota = new ArrayList<MovimentNota>();
		
		public Nota(){
		}
		
		public Nota (Usuari usuari){
			this.usuari=usuari;
		}

		public Nota(int codi, Date fecha, String descripcio, EstatNota estat, Comunitat comunitat, Usuari usuari,
				List<MovimentNota> movimentsNota) {
			super();
			this.codi = codi;
			this.fecha = fecha;
			this.descripcio = descripcio;
			this.estat = estat;
			this.comunitat = comunitat;
			this.usuari = usuari;
			this.movimentsNota = movimentsNota;
		}








		public static Page llistarNotes(int page) {
			Query query = null;
			query = JPA.em().createQuery("from Nota ");
		//	query = JPA.em().createQuery("select n from Nota n join n.comunitat c join c.accesComunitats a");
			List list = query.getResultList();
			Collections.sort(list, NotaComparator);
			Page p = new Page(list, page);
			return p;

		}
		
		public static void borrarNota(Nota nota) {
			// TODO Auto-generated method stub
			EntityManager em = JPA.em();
			Nota refNota = obtenirRefNota(nota);
			refNota.movimentsNota.clear();
			try{
				em.remove(refNota);
			}catch (Exception e){
				
			}
		}
		
		
		public static Nota obtenirRefNota(Nota nota) {
			EntityManager em = JPA.em();
			Nota RefNota = em.find(Nota.class, nota.codi);
			return RefNota;
		}
		
		public static Nota guardarNota(Nota formNota,String detall) {

			Nota refNota= obtenirRefNota(formNota);
			if (refNota != null) {
				refNota.fecha = formNota.fecha;
				refNota.comunitat = formNota.comunitat;
				refNota.descripcio = formNota.descripcio;
				refNota.estat=formNota.estat;
				
				return(JPA.em().merge(refNota));

			} else {
				
				Nota n=(JPA.em().merge(formNota));
				MovimentNota m=new MovimentNota();
				m.nota=n;
				m.fecha=new Date();
				m.detall=detall;
				m.estat=n.estat;
				m.notificada=false;
				m.usuari=n.usuari;
				m.codi=1;
				JPA.em().persist(m);

				return(n);
			}
		}
		
		
		public static Comparator<Nota> NotaComparator = new Comparator<Nota>() {

			public int compare(Nota n1, Nota n2) {

				return Integer.compare(n1.codi, n2.codi)*-1;

			}
			};

			@Override
			public Nota bind(String arg0, final String arg1) {
				// TODO Auto-generated method stub
				final Nota c[] = { null };
				JPA.withTransaction(new F.Callback0() {
					@Override
					public void invoke() throws Throwable {
						c[0] = (Nota.recercaPerCodi(Integer.parseInt(arg1)));
					}
				});

				return (c[0]);
			}

			@Override
			public String javascriptUnbind() {
				// TODO Auto-generated method stub
				return Integer.toString(this.codi);
			}

			@Override
			public String unbind(String arg0) {
				// TODO Auto-generated method stub
				return Integer.toString(this.codi);
			}
			
			public static Nota recercaPerCodi(int codi) {
				Nota result = null;
				Query query = JPA.em().createQuery("from Nota n");
				List<Nota> notes = query.getResultList();

				for (Nota candidate : notes) {
					if (candidate.codi==codi) {
						result = candidate;

					}
				}
				return result;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + codi;
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
				Nota other = (Nota) obj;
				if (codi != other.codi)
					return false;
				return true;
			}

		
		}
		
	
		