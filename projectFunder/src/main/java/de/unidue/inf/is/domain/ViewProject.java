package de.unidue.inf.is.domain;

import java.math.BigDecimal;

public final class ViewProject {
	
	private String Titel;
	private String Icon;
	private String Ersteller;
	private BigDecimal Spendensumme;
	
	public ViewProject() {
		
	}
	
	public ViewProject(String titel,String icon,String ersteller, BigDecimal spendensumme) {
		this.Titel = titel;
		this.Icon = icon;
		this.Ersteller = ersteller;
		this.Spendensumme = spendensumme;
		
	}
	public String getTitel() {
		return Titel;
	}

	public String getIcon() {
		return Icon;
	}

	public String getErsteller() {
		return Ersteller;
	}

	public BigDecimal getSpendensumme() {
		return Spendensumme;
	}

	
	

}
