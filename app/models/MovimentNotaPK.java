package models;

import java.io.Serializable;

public class MovimentNotaPK implements Serializable {

	int nota;
	int codi;
	
	public MovimentNotaPK(){
		
	}

	public MovimentNotaPK(int nota, int codi) {
		super();
		this.nota = nota;
		this.codi = codi;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + codi;
		result = prime * result + nota;
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
		MovimentNotaPK other = (MovimentNotaPK) obj;
		if (codi != other.codi)
			return false;
		if (nota != other.nota)
			return false;
		return true;
	}

	public int getNota() {
		return nota;
	}

	public void setNota(int nota) {
		this.nota = nota;
	}

	public int getCodi() {
		return codi;
	}

	public void setCodi(int codi) {
		this.codi = codi;
	}
	

	
	
}
