package planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import parser.Values;

public class PropertiesGenerator {
	
	PrintWriter pp;
	Properties prop[];
	//for assigning constant parameters
	Values vm;
		
	//requirements
		int idR;
		int costR;
		int durR;
		double relR;

	public PropertiesGenerator() {}
	
	public void assignProperties(Properties p[]) {
		prop = p;
	}
	
	public void setPropPath(String path) {
		try {
				pp = new PrintWriter(new File(path));
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setReqParamswithValues() {
		
		if (vm == null) {
			vm = new Values();
		}
		
		for(int i=0; i < prop.length; i++) {
			vm.addValue(prop[i].name, prop[i].threshold); 
		}
	}
	
	public void encodeProperties() {
		
		//declare the parameters with threshold values
		for(int i=0; i < prop.length; i++) {
			pp.println("const "+prop[i].type+" "+prop[i].name+" = "+prop[i].threshold+";");
		}						
		//specify multi-objective properties
		//pp.println("<<p1>> (R{\"cost\"}<=MAXCS[C] & R{\"time\"}<=MAXDR[C] & R{\"reliability\"}>=MINRL[C])");
		
		String multiobj = "<<p1>> (R{\"rw_"+prop[0].name+"\"}"+prop[0].comparator+" "+prop[0].name+"[C] ";
		for(int i=1; i < prop.length; i++) {
			multiobj = multiobj + "& R{\"rw_"+prop[i].name+"\"}"+prop[i].comparator+" "+prop[i].name+"[C] ";	
		}
		pp.println(multiobj+")");
		pp.close();
	}
	
	public static void main(String args[]) {
		Properties pp[] = new Properties[4];
		PropertiesGenerator pg = new PropertiesGenerator();
		
		//create the empty properties
		pp[0] = new Properties();
		pp[1] = new Properties();
		pp[2] = new Properties();
		pp[3] = new Properties();
		
		//specify the parameters
		pp[0].setProperties(0, "cost", "int", 90, "<=");
		pp[1].setProperties(1, "time", "int", 1000, "<=");
		pp[2].setProperties(2, "reliability", "double", 0.9, ">=");
		pp[3].setProperties(3, "availability", "double", 0.9, ">=");
		
		//set the path
		String propPath = "/home/azlan/git/PrismGames/Prismfiles/propTest.props";
		pg.setPropPath(propPath);
		
		//assign properties to the properties generator
		pg.assignProperties(pp);
		
		pg.encodeProperties();
		System.out.println("done creating the properties specification...");
		
	}
}
