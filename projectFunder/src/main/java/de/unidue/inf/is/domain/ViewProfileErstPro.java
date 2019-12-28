package de.unidue.inf.is.domain;

import java.math.BigDecimal;

public class ViewProfileErstPro {

	private String Icon;
	private int Kennung;
	private String Titel;
	private BigDecimal Spendensumme;
	private String Status;
	
	public ViewProfileErstPro() {
		
	}
	
	public ViewProfileErstPro(String i ,int k, String t , BigDecimal sp , String st) {
		this.Icon = i;
		this.Kennung = k;
		this.Titel = t;
		this.Spendensumme = sp;
		this.Status = st;
	}

	public String getIcon() {
		return Icon;
	}
	
	public int getKennung() {
		return Kennung;
	}
	
	public String getTitel() {
		return Titel;
	}

	public BigDecimal getSpendensumme() {
		return Spendensumme;
	}

	public String getStatus() {
		return Status;
	}
	
	
	
	
	
	
	
	
	
	
	
}
