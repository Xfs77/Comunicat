package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class UsuarisFiltre  {

	
	public String dni;
	public Boolean president;
	public Boolean baixa;
	public Boolean administrador;
	public Boolean bloquejat;
	public Comunitat comunitat;
	
	
	
public UsuarisFiltre(){
	this.president=false;
	this.administrador=false;
	this.baixa=false;
	this.bloquejat=false;
	
	
}



public UsuarisFiltre(String dni, Boolean president, Boolean baixa,
		Boolean administrador, Boolean bloquejat, Comunitat comunitat) {
	super();
	this.dni = dni;
	this.president = president;
	this.baixa = baixa;
	this.administrador = administrador;
	this.bloquejat = bloquejat;
	this.comunitat = comunitat;
}



		

	









}
