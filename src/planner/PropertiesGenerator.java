package planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import parser.Values;

public class PropertiesGenerator extends ModelGenerator{
	
	PrintWriter pw;
	Properties prop[];
	//for assigning constant parameters
	Values vp;

	public PropertiesGenerator() {}
	
	public void assignProperties(Properties p[]) {
		prop = p;
	}
	
	public void setPropPath(String path) {
		try {
				pw = new PrintWriter(new File(path));
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setThresholdParamswithValues() {
		
		if (vm == null) {
			vm = new Values();
		}
		
		for(int i=0; i < prop.length; i++) {
			vm.addValue(prop[i].name, prop[i].values); 
		}
	}
	
	public Properties[] getProperties() {
		return prop;
	}
	
	public void encodeProperties() {
		
	
		if (super.setValuesStatus) {
			//declare the parameters with threshold values
			for(int i=0; i < prop.length; i++) {
				pw.println("const "+prop[i].type+" "+prop[i].name+" = "+prop[i].values+";");
			}
		}
		else {
			//declare the parameters without threshold values
			for(int i=0; i < prop.length; i++) {
				pw.println("const "+prop[i].type+" "+prop[i].name+";");
			}
		}
		//specify multi-objective properties
		//pp.println("<<p1>> (R{\"cost\"}<=MAXCS[C] & R{\"time\"}<=MAXDR[C] & R{\"reliability\"}>=MINRL[C])");
		
		String multiobj = "<<p1>> (R{\"rw_"+prop[0].name+"\"}"+prop[0].comparator+" "+prop[0].name+"[C] ";
		for(int i=1; i < prop.length; i++) {
			multiobj = multiobj + "& R{\"rw_"+prop[i].name+"\"}"+prop[i].comparator+" "+prop[i].name+"[C] ";	
		}
		pw.println(multiobj+")");
		pw.close();
	}
	
	public static void main(String args[]) {
		
		boolean assignValue = true;
		
		Properties pp[] = new Properties[3];
		PropertiesGenerator pg = new PropertiesGenerator();
		
		//create the empty properties
		pp[0] = new Properties();
		pp[1] = new Properties();
		pp[2] = new Properties();
		//pp[3] = new Properties();
		
		//specify the parameters
		pp[0].setProperties(0, "cost", "double", 90, 10, "<");
		pp[1].setProperties(1, "time", "int", 1000, 100,"<");
		pp[2].setProperties(2, "reliability", "double", 0.9, 0.1, ">");
		//pp[3].setProperties(3, "availability", "double", 0.9, 0.1, ">=");
		
		//set the path
		String propPath = "/home/azlan/git/prismgames/Prismfiles/propTest.props";
		pg.setPropPath(propPath);
		
		//assign properties to the properties generator
		pg.assignProperties(pp);
		pg.setValuesStatus(assignValue); //true-encode properties with threshold, false-without threshold (later stage)
		pg.encodeProperties();
		System.out.println("done creating the properties specification...");
		
	}
}
