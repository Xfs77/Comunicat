package models;

import java.io.File;
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
	@Table(name = "comunicat.document")
	public class Document implements Serializable, PathBindable<Document> {
		@Id
		@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_document")
		@SequenceGenerator(name = "seq_document", sequenceName = "seq_document", allocationSize = 1, initialValue = 1)
		@Column(name = "codi")
		public int codi;
		@Column(name = "descripcio")
		public String descripcio;
		@ManyToOne
		@LazyCollection(LazyCollectionOption.FALSE)
		@JoinColumn(name = "reunio")
		public Reunio reunio;
		@Column(name = "document")
		public byte[] document;
		
		public Document(){
		}

		public Document(int codi, String descripcio, Reunio reunio, byte[] document) {
			super();
			this.codi = codi;
			this.descripcio = descripcio;
			this.reunio = reunio;
			this.document = document;
		}

		public static Document recercaPerCodi(int codi) {
			Document result = null;
			Query query = JPA.em().createQuery("from Document d");
			List<Document> documents = query.getResultList();

			for (Document candidate : documents) {
				if (candidate.codi==codi) {
					result = candidate;

				}
			}
			return result;
		}
		
		public static void borrarDocument(Document document)  {
			// TODO Auto-generated method stub
			EntityManager em = JPA.em();
			Document refDocument = obtenirRefDocument(document);
				em.remove(refDocument);
				em.flush();
			
		}
		
		
		public static Document obtenirRefDocument(Document document) {
			EntityManager em = JPA.em();
			Document refDocument = em.find(Document.class, document.codi);
			return refDocument;
		}
		


		public static Page llistarDocuments(Reunio reunio,int page) {
			Query query = null;
			query = JPA.em().createQuery("from Document");
			query = JPA.em().createQuery("from Document d where d.reunio=?1");
			query.setParameter(1,Reunio.obtenirRefReunio(reunio));
		//	query = JPA.em().createQuery("select n from Nota n join n.comunitat c join c.accesComunitats a");
			List list = query.getResultList();
			Collections.sort(list, DocumentComparator);
			Page p = new Page(list, page);
			return p;

		}
		
		public static void guardarDocument(Document formDocument) {

			Document refDocument= obtenirRefDocument(formDocument);
			if (refDocument != null) {
				refDocument.descripcio=formDocument.descripcio;
				if(formDocument.document!=null) refDocument.document=formDocument.document;
				JPA.em().merge(refDocument);

			} else {
				Reunio refReunio=Reunio.obtenirRefReunio(formDocument.reunio);
				refReunio.documents.add(formDocument);
				JPA.em().persist(formDocument);
					}
		}
		

		 public static void borrarArchiuDirectori() {

			  String direccion = "c:\\Usuarios\\Xavi\\git\\Comunicat\\public\\javascripts\\web\\tmp";
		        File directorio = new File(direccion);
		        File f;
		        boolean t =directorio.isDirectory();
		        if (directorio.isDirectory()) {
		            String[] files = directorio.list();
		            if (files.length > 0) {
		                System.out.println(" Directorio vacio: " + direccion);
		                for (String archivo : files) {
		                    System.out.println(archivo);
		                    f = new File(direccion + File.separator + archivo);
		                        f.delete();
		                        f.deleteOnExit();
		                    }

		                }
		            }
		        }
		 
		
		public static Comparator<Document> DocumentComparator = new Comparator<Document>() {

			public int compare(Document d1, Document d2) {
				return 	d1.descripcio.compareTo(d2.descripcio);

			}
			};

		
		@Override
		public Document bind(String arg0, final String arg1) {
			// TODO Auto-generated method stub
			final Document c[] = { null };
			JPA.withTransaction(new F.Callback0() {
				@Override
				public void invoke() throws Throwable {
					c[0] = (Document.recercaPerCodi(Integer.parseInt(arg1)));
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
			Document other = (Document) obj;
			if (codi != other.codi)
				return false;
			return true;
		}
		
		
	}
