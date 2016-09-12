package code.bean.test;

public class BanJi implements TestInterface{
	private String name;
	private String classes;

	public void setName(String name){
		this.name=name;
	}

	public String getName(){
		return name;
	}

	public void setClasses(String classes){
		this.classes=classes;
	}

	public String getClasses(){
		return classes;
	}
}
