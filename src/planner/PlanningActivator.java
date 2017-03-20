package planner;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import planner.CompositionalStochasticPlanner;
import prism.Prism;
import prism.PrismFileLog;
import prism.PrismLog;

public class PlanningActivator implements BundleActivator {

	CompositionalStochasticPlanner plan;
	Prism prism;
	PrismLog mainLog;
	String logPath = "./myLog.txt";
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		
		System.out.println("Executing Compositional Planner");
		try {
			//System.loadLibrary("libppl_java.so");
		}catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load"+e);
			System.exit(1);
		}
		//mainLog = new PrismFileLog(logPath);
        //prism = new Prism(mainLog);
		//plan = new CompositionalMultiPlanner(1);
		
		//parameters: id, cpuCores, cpuSpeed, cpuLoadas
 		//plan.setApplicationRequirements(0, 0.6, 0.6, 0.6);
 		//plan.setApplicationRequirements(1, 0.6, 0.6, 0.6);
 		
 		//plan.setNodeCapabilities(0, "NODE0", 4, 4, 0.4);
 		//plan.setNodeCapabilities(1, "NODE1", 4, 4, 0.4);
 		//plan.setNodeCapabilities(2, "NODE2", 8, 8, 0.8);
 		//plan.setNodeCapabilities(3, "NODE3", 8, 8, 0.8);
 		//plan.setNodeCapabilities(4, "NODE4", 8, 8, 0.8);
 		
 		
 		//plan.generate();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("Goodbye Compositional Planner!!");
		
	}

}
