package models;

public class CanviPassword {

	
		public String nou1;
		public String nou2;
		
		public CanviPassword(){
			
		}
		
	
		
		public CanviPassword(String nou1, String nou2) {
			super();
			this.nou1 = nou1;
			this.nou2 = nou2;
		}

		public String validate() {
		    if ((nou1.equals(nou2)==false)) {
		      return "contrasenyes_no_coincideixen";
		    }
		    return null;
		}
	}

	
	
	
	
	
	

