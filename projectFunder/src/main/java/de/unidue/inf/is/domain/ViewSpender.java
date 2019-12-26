package de.unidue.inf.is.domain;

import java.math.BigDecimal;

public final class ViewSpender {

	BigDecimal Spendenbetrag;
	String Name;
	
	public ViewSpender() {
		
	}
	
	public ViewSpender(BigDecimal sb,String n) {
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
