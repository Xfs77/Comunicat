package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ReunionsFiltre {

	
	public Date fechaIni;
	public Date fechaFi;
	public EstatReunio estat;
	public Comunitat comunitat;
	
	
	
public ReunionsFiltre(){
	this.fechaIni=sumarRestarDiasFecha(new Date(),-3650);
	this.fechaFi=sumarRestarDiasFecha(new Date(),365);
	
}
		

	public ReunionsFiltre(Date fechaIni, Date fechaFi, EstatReunio estat, Comunitat comunitat) {
	super();
	this.fechaIni = fechaIni;
	this.fechaFi = fechaFi;
	this.estat = estat;
	this.comunitat = comunitat;
}

	public Date sumarRestarDiasFecha(Date fecha, int dias){
		       Calendar calendar = Calendar.getInstance();
		       calendar.setTime(fecha); // Configuramos la fecha que se recibe
		       calendar.add(Calendar.DAY_OF_YEAR, dias);  // numero de días a añadir, o restar en caso de días<0		 07	
		       return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
	
		  }
}
