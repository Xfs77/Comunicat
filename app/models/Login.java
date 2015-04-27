package models;

public  class Login {
	public String dni;
	public String password;
	
	public Login(){
		
	}
	
	public Login(String dni, String password){
		this.dni=dni;
		this.password=password;
	}
	
	public String validate() {
	    if (Usuari.authenticate(dni, password) == null) {
	      return "invalid_user_password";
	    }
	    return null;
	}
}

