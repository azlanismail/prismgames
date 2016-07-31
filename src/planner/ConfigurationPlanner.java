package planner;

import parser.Values;


public class ConfigurationPlanner {

	Values vm;
	
	
	public ConfigurationPlanner() {
		vm = new Values();
	}
	
	public void setAppRequirements(int id, long cpuCores, long cpuSpeed, double cpuLoads) {
		//set the following:
		
		String md_id = "APP"+id+"_ID";
		String md_cpuCores = "APP"+id+"_CPU_CORES";
		String md_cpuLoads = "APP"+id+"_CPU_LOADS";
		String md_cpuSpeed = "APP"+id+"_CPU_SPEED";
		
		System.out.println("the received requirements are, id:"+id+", cpu cores:"+cpuCores+", cpu loads:"+cpuLoads+", cpu speed:"+cpuSpeed);
	//	vm.setValue(md_id, id);
	//	vm.setValue(md_cpuCores, cpuCores);
	//	vm.setValue(md_cpuLoads, cpuLoads);
	//	vm.setValue(md_cpuSpeed, cpuSpeed);		
	}
	
	public void setNodeCapabilities(int id, long cpuCores, long cpuSpeed, double cpuLoads){
		//set the following:
		String md_id = "RS"+id+"_ID";
		String md_cpuCores = "RS"+id+"_CPU_CORES";
		String md_cpuLoads = "RS"+id+"_CPU_LOADS";
		String md_cpuSpeed = "RS"+id+"_CPU_SPEED";
		
		System.out.println("the received node capabilities are, id:"+id+", cpu cores:"+cpuCores+", cpu loads:"+cpuLoads+", cpu speed:"+cpuSpeed);
	//	vm.setValue(md_id, id);
	//	vm.setValue(md_cpuCores, cpuCores);
	//	vm.setValue(md_cpuLoads, cpuLoads);
	//	vm.setValue(md_cpuSpeed, cpuSpeed);
	}
	
	public void setAppRequirementsConstant(){
		
	}
	
	public void setContantsParameters(){
	
	}
	
	public Values getDefinedValues() {
		return vm;
	}
	
}
