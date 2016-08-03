package planner;

import parser.Values;


public class ConfigurationPlanner {

	private Values vm;
	
	//Defining parameters to the stochastic-games model
			String md_probe = "CUR_PROBE";
			String md_maxCS = "MAX_CS";
			String md_maxRT = "MAX_RT";
			String md_maxFR = "MAX_FR";
			
			//String md_goalTQ = "GOAL_TQ";
			String md_goalTY = "GOAL_TY";
			String md_serviceType = "SV_TY";
			String md_serviceFailedId = "SV_FAIL_ID";
			String md_delay = "CUR_DELAY";
			String md_maxDelay = "MAX_DELAY";
			String md_minDelay = "MIN_DELAY";
			
			String md_retry = "RETRY";

			//utility-based decision making
			String md_wg_cs = "WG_CS"; 
			String md_wg_rt = "WG_RT";  
			String md_wg_fr = "WG_FR"; 
			
	public ConfigurationPlanner() {
		vm = new Values();
	}
	
	public void setAppRequirements(int id, double cpuCores, double cpuSpeed, double cpuLoads) {
		//set the following:
		
		String md_id = "APP"+id+"_ID";
		String md_cpuCores = "APP"+id+"_CPU_CORES";
		String md_cpuLoads = "APP"+id+"_CPU_LOADS";
		String md_cpuSpeed = "APP"+id+"_CPU_SPEED";
		
		System.out.println("the received requirements are, id:"+id+", cpu cores:"+cpuCores+", cpu loads:"+cpuLoads+", cpu speed:"+cpuSpeed);
		vm.setValue(md_id, id);
		vm.setValue(md_cpuCores, cpuCores);
		vm.setValue(md_cpuLoads, cpuLoads);
		vm.setValue(md_cpuSpeed, cpuSpeed);		
	}
	
	public void setNodeCapabilities(int id, double cpuCores, double cpuSpeed, double cpuLoads){
		//set the following:
		String md_id = "RS"+id+"_ID";
		String md_cpuCores = "RS"+id+"_CPU_CORES";
		String md_cpuLoads = "RS"+id+"_CPU_LOADS";
		String md_cpuSpeed = "RS"+id+"_CPU_SPEED";
		
		System.out.println("the received node capabilities are, id:"+id+", cpu cores:"+cpuCores+", cpu loads:"+cpuLoads+", cpu speed:"+cpuSpeed);
		vm.setValue(md_id, id);
		vm.setValue(md_cpuCores, cpuCores);
		vm.setValue(md_cpuLoads, cpuLoads);
		vm.setValue(md_cpuSpeed, cpuSpeed);
	}
	
	public void setAppRequirementsConstant(){
		
	}
	
	public void setContantsParameters(){
	
	}
	
	public void setConstantsGoalType(int goalType) {
		vm.setValue(md_goalTY, goalType);
	}
	
			
		
	public void setConstantsMaxCost(double maxCS) {
		vm.setValue(md_maxCS, maxCS);
	}
	
	public void setConstantsMaxResponseTime(int maxRT) {
		vm.setValue(md_maxRT, maxRT);
	}
	
	public void setConstantsMaxFailureRate(double maxFR) {
		vm.setValue(md_maxFR, maxFR);
	}
	
	public void setConstantsUtilWeight(double wgCS, double wgRT, double wgFR) {
		vm.setValue(md_wg_cs, wgCS);
		vm.setValue(md_wg_rt, wgRT);
		vm.setValue(md_wg_fr, wgFR);
	}
	
	public Values getDefinedValues() {
		return vm;
	}
	
	public static void main(String[] args){
		ConfigurationPlanner conf = new ConfigurationPlanner();
		conf.setAppRequirements(1, 0.7, 0.5, 0.3);
		conf.setNodeCapabilities(1, 0.3, 0.4, 0.5);
		
		System.out.println("Success");
	}
	
}
