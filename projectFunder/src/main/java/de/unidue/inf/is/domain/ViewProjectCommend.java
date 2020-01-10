package de.unidue.inf.is.domain;

public class ViewProjectCommend {

	String Name;
	String Text;
	
	public ViewProjectCommend() {}
	
	public ViewProjectCommend(String name,String text) {	
		this.Name = name;
		this.Text = text;
	}

	public String getText() {
		return Text;
	}

	public String getName() {
		return Name;
	}
	
}
