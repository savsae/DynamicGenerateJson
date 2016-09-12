package code.bean.test;

   /**
    * Student 实体类
    * Fri Sep 09 17:29:16 CST 2016 ss
    */ 


public class Student{
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

