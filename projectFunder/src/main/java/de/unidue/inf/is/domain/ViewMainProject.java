package de.unidue.inf.is.domain;

import java.math.BigDecimal;

public final class ViewMainProject {
	
	private int Kennung;
	private String Titel;
	private String Icon;
	private String Email;
	private String Ersteller;
	private BigDecimal Spendensumme;
	
	public ViewMainProject() {}
	
	public ViewMainProject(int kennung,String titel,String icon,String email,String ersteller, BigDecimal spendensumme) {
		this.Kennung = kennung;
		this.Titel = titel;
		this.Icon = icon;
		this.Email = email;
		this.Ersteller = ersteller;
		this.Spendensumme = spendensumme;
		
	}
	
	public int getKennung() {
		return Kennung;
	}
	
	public String getTitel() {
		return Titel;
	}

	public String getIcon() {
		return Icon;
	}
	
	public String getEmail() {
		return Email;
	}
	
	public String getErsteller() {
		return Ersteller;
	}

	public BigDecimal getSpendensumme() {
		return Spendensumme;
	}



}
