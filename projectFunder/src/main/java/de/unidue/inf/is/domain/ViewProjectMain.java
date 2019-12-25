package de.unidue.inf.is.domain;

import java.math.BigDecimal;

public final class ViewProjectMain {
	
	private int Kennung;
	private String Titel;
	private String Icon;
	private String Ersteller;
	private BigDecimal Spendensumme;
	
	public ViewProjectMain() {
		
	}
	
	public ViewProjectMain(int kennung,String titel,String icon,String ersteller, BigDecimal spendensumme) {
		this.Kennung = kennung;
		this.Titel = titel;
		this.Icon = icon;
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

	public String getErsteller() {
		return Ersteller;
	}

	public BigDecimal getSpendensumme() {
		return Spendensumme;
	}

	
	

}
