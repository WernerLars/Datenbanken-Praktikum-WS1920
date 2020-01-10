package de.unidue.inf.is.domain;

import java.math.BigDecimal;

public final class ViewProjectDonator {

	BigDecimal Spendenbetrag;
	String Name;
	
	public ViewProjectDonator() {}
	
	public ViewProjectDonator(BigDecimal sb,String n) {
		this.Spendenbetrag = sb;
		this.Name = n;
	}

	public BigDecimal getSpendenbetrag() {
		return Spendenbetrag;
	}

	public String getName() {
		return Name;
	}

	
	
	
	
	
	
	
	
	
	
	
}
