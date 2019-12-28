package de.unidue.inf.is.domain;

import java.math.BigDecimal;

public class ViewProfileUnPro {
	
	private String Icon;
	private int Kennung;
	private String Titel;
	private BigDecimal Finanzierungslimit;
	private String Status;
	private BigDecimal Spendenbetrag;
	
	public ViewProfileUnPro() {
		
	}
	
	public ViewProfileUnPro(String i,int k, String t,BigDecimal f,String st,BigDecimal sp) {
		this.Icon = i;
		this.Kennung = k;
		this.Titel = t;
		this.Finanzierungslimit = f;
		this.Status = st;
		this.Spendenbetrag = sp;
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

	public BigDecimal getFinanzierungslimit() {
		return Finanzierungslimit;
	}

	public String getStatus() {
		return Status;
	}

	public BigDecimal getSpendenbetrag() {
		return Spendenbetrag;
	}
	
	
	
	
	
	
}
