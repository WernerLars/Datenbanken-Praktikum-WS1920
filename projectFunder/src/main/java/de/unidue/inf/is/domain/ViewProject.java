package de.unidue.inf.is.domain;


import java.math.BigDecimal;

public final class ViewProject {
	
	private String Status;
	private String Beschreibung;
	private String Titel;
	private BigDecimal Finanzierungslimit;
	private String Icon;
	private String Email;
	private String Ersteller;
	private BigDecimal Spendensumme;
	
	public ViewProject() {
		
	}

	public ViewProject(String status,String beschreibung,String titel,BigDecimal fl,String icon,String email,String ersteller,BigDecimal ss) {
		this.Status = status;
		this.Beschreibung = beschreibung;
		this.Titel = titel;
		this.Finanzierungslimit = fl;
		this.Icon = icon;
		this.Email = email;
		this.Ersteller = ersteller;
		this.Spendensumme = ss;
		
		
	}

	public String getStatus() {
		return Status;
	}

	public String getBeschreibung() {
		return Beschreibung;
	}

	public String getTitel() {
		return Titel;
	}

	public BigDecimal getFinanzierungslimit() {
		return Finanzierungslimit;
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