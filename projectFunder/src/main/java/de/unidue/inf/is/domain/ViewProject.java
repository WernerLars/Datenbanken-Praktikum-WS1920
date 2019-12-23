package de.unidue.inf.is.domain;

public final class ViewProject {
	
	private String Titel;
	private String Icon;
	private String Ersteller;
	private int Spendensumme;
	
	public ViewProject() {
		
	}
	
	public ViewProject(String titel,String icon,String ersteller, int spendensumme) {
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

	public int getSpendensumme() {
		return Spendensumme;
	}

	@Override
	public String toString() {
		return "ViewProject [Titel=" + Titel + ", Icon=" + Icon + ", Ersteller=" + Ersteller + ", Spendensumme="
				+ Spendensumme + "]";
	}

	
	
	

}
