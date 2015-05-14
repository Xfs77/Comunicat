package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
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
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
import play.mvc.PathBindable;
 
	@Entity
	@Table(name = "comunicat.reunio")
	public class Reunio implements Serializable, PathBindable<Reunio> {
		@Id
		@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reunio")
		@SequenceGenerator(name = "seq_reunio", sequenceName = "seq_reunio", allocationSize = 1, initialValue = 2)
		@Column(name = "codi")
		public int codi;
		@Column(name = "fecha")
		public Date fecha;
		@Column(name = "hora")
		public String hora;
		@Column(name = "lloc")
		public String lloc;
		@Column(name = "descripcio")
		public String descripcio;
		@ManyToOne
		@LazyCollection(LazyCollectionOption.FALSE)
		@JoinColumn(name = "comunitat")
		public Comunitat comunitat;
		@ManyToOne
		@LazyCollection(LazyCollectionOption.FALSE)
		@JoinColumn(name = "estat")
		public EstatReunio estat;
		@Column(name = "notificada")
		public boolean notificada;
		@OneToMany(mappedBy="reunio")
		public List<Document> documents = new ArrayList<Document>();
		
		
		public Reunio(){
		}



		public Reunio(int codi, Date fecha, String hora, String lloc, String descripcio, Comunitat comunitat,
				EstatReunio estat, boolean notificada, List<Document> documents) {
			super();
			this.codi = codi;
			this.fecha = fecha;
			this.hora = hora;
			this.lloc = lloc;
			this.descripcio = descripcio;
			this.comunitat = comunitat;
			this.estat = estat;
			this.notificada = notificada;
			this.documents = documents;
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
			Reunio other = (Reunio) obj;
			if (codi != other.codi)
				return false;
			return true;
		}
	

		public static Reunio recercaPerCodi(int codi) {
			Reunio result = null;
			Query query = JPA.em().createQuery("from Reunio r");
			List<Reunio> reunions = query.getResultList();

			for (Reunio candidate : reunions) {
				if (candidate.codi==codi) {
					result = candidate;

				}
			}
			return result;
		}
		
		public static void borrarReunio(Reunio reunio) throws Exception,PersistenceException {
			// TODO Auto-generated method stub
			EntityManager em = JPA.em();
			Reunio refReunio = obtenirRefReunio(reunio);
			try{
				em.remove(refReunio);
				em.flush();
			}catch (PersistenceException e){
				throw e;
				}
		}
		
		
		public static Reunio obtenirRefReunio(Reunio reunio) {
			EntityManager em = JPA.em();
			Reunio refReunio = em.find(Reunio.class, reunio.codi);
			return refReunio;
		}
		


		public static Page llistarReunions(int page) {
			Query query = null;
			query = JPA.em().createQuery("from Reunio");
		//	query = JPA.em().createQuery("select n from Nota n join n.comunitat c join c.accesComunitats a");
			List list = query.getResultList();
			Collections.sort(list, ReunioComparator);
			Page p = new Page(list, page);
			return p;

		}



		public static Page llistarReunionsFiltrades(int page, ReunionsFiltre reunioFiltreForm) {
			// TODO Auto-generated method stub
			Query query = null;
			query = JPA.em().createQuery("from Reunio r where r.fecha >?1 ");
			query.setParameter(1, reunioFiltreForm.fechaIni);
		//	query = JPA.em().createQuery("select n from Nota n join n.comunitat c join c.accesComunitats a");
			List list = query.getResultList();
			Collections.sort(list, ReunioComparator);
			Page p = new Page(list, page);
			return p;		
			}

		
		public static void guardarReunio(Reunio formReunio) {

			Reunio refReunio= obtenirRefReunio(formReunio);
			if (refReunio != null) {
				refReunio.fecha = formReunio.fecha;
				refReunio.hora = formReunio.hora;
				refReunio.descripcio = formReunio.descripcio;
				refReunio.lloc = formReunio.lloc;
				refReunio.estat = formReunio.estat;
				JPA.em().merge(refReunio);

			} else {
				
				JPA.em().persist(formReunio);
					}
		}
		
		public static void notificarReunio(Reunio reunio) {
			Query query = null;
			query = JPA.em().createQuery("select a from Reunio r join r.comunitat c join c.accesComunitats a where r=?1");
			query.setParameter(1, Reunio.obtenirRefReunio(reunio));
			List <Usuari>lu=query.getResultList();
			
			Iterator<Usuari> iterator = lu.iterator();
			while (iterator.hasNext()) {
				Usuari u=iterator.next();
				Reunio.correuReunio(u, reunio);
			}
			
			Reunio refReunio= Reunio.obtenirRefReunio(reunio);
			refReunio.notificada=true;
			JPA.em().merge(refReunio);
		}
		
		public static void correuReunio(Usuari usuari, Reunio reunio) {
			
		
			Email email = new Email();
		
			email.setSubject("Correu Reunio a www.ComunicatComunitat");
			email.setFrom("comunicatcomunitat@gmail.com");
			email.addTo(usuari.email);
			email.setBodyText("A text message");
		
			
			
			MailerPlugin.send(email);
		
			
		}
		
		public static Comparator<Reunio> ReunioComparator = new Comparator<Reunio>() {

			public int compare(Reunio r1, Reunio r2) {
				return 	r1.fecha.compareTo(r2.fecha)*-1;

			}
			};

		
		@Override
		public Reunio bind(String arg0, final String arg1) {
			// TODO Auto-generated method stub
			final Reunio c[] = { null };
			JPA.withTransaction(new F.Callback0() {
				@Override
				public void invoke() throws Throwable {
					c[0] = (Reunio.recercaPerCodi(Integer.parseInt(arg1)));
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

		
		
	}
