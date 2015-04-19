package models;

import java.io.Serializable;

import javax.persistence.IdClass;

public class ElementPK implements Serializable{
	
	String comunitat;
	String codi;
	
	public ElementPK() {
		
	}

	public ElementPK(String comunitat, String codi) {
		super();
		this.comunitat = comunitat;
		this.codi = codi;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		 if(obj instanceof ElementPK){
	            ElementPK ElementPK = (ElementPK) obj;
	 
	            if(!ElementPK.getComunitat().equals(comunitat)){
	                return false;
	            }
	 
	            if(!ElementPK.getCodi().equals(codi)){
	                return false;
	            }
	 
	            return true;
	        }
	 
	        return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
        return comunitat.hashCode() + comunitat.hashCode();
	}

	public String getComunitat() {
		return comunitat;
	}

	public void setComunitat(String comunitat) {
		this.comunitat = comunitat;
	}

	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}
	
	

}