// sort process with arrival time = 0, start with the first one
// each process's arrival time is <= the current time, add the process to the queue and sort it
// remove each executed process from the queue
// increment the current time by one each time a process is executed

package cpuScheduler;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NonPreemptiveSJF {

	private static List<Process> SJP;
	private static List<Process> processes;
    	private static List<Pair<Process, Integer>> executionOrder;
	private static int currentTime;
	private static float totalTurnaround = 0;
	private static float totalWaiting = 0;
	
    public static void schedule(List<Process> inputProcesses, int contextSwitchTime) {
    	SJP = new ArrayList<>();
    	//get the processes queue
	processes = new ArrayList<>(inputProcesses);
        executionOrder = new ArrayList<>();
	// current time = first process's arrival time
    	currentTime = processes.get(0).getArrivalTime();

	// for each process:
    	while (!processes.isEmpty()) {
    		addAndSort();
    		executeAndRemove(contextSwitchTime);
    	}

    	totalWaiting /= inputProcesses.size();
	totalTurnaround /= inputProcesses.size();

    	//print the Average Waiting and Turnaround Time
        System.out.println("================================");
    	System.out.println("Average Waiting Time for Processes was: " + totalWaiting);
    	System.out.println("Average Turnaround Time for Processes was: " + totalTurnaround);
        System.out.println("================================");
    }
    
    private static void addAndSort() {
    	
    	int i = 0;
	// Add only the arrived processes [with arrival time <= current time], to be sorted
	// And remove it from the original processes queue
        while (i < processes.size() && processes.get(i).getArrivalTime() <= currentTime) {
            SJP.add(processes.get(i));
            processes.remove(i);
        }

        //sort the added processes according to their burst time
    	Collections.sort(SJP, Comparator.comparingInt(Process::getBurstTime));
    	
    }
    
    private static void executeAndRemove(int contextSwitchTime) {
    	
    	while (!SJP.isEmpty()) {		//for each process
    		final Process currentProcess = SJP.get(0);
    		
    		//include the process in the execution order to be visualized later
    		addProcessToGUI(currentProcess);
    		    		
    		//update current time
    		currentTime += currentProcess.getBurstTime();
    		currentTime += contextSwitchTime;
    		
    		//print the process info
    		printInfo(currentProcess);

    		//remove the process
    		SJP.remove(0);
    	}

    }
    
    private static void addProcessToGUI(Process p) { // Add each process and the time it was executed
    	for (int i = 0; i < p.getBurstTime(); i++) {
            executionOrder.add(new Pair<>(p, i + currentTime));
    	}
    }
    
    public static List<Pair<Process, Integer>> getExecutionOrder(){
        return executionOrder;
    }
    
	public static float getAvgWaiting() {
		return totalWaiting;
	}
	public static float getAvgTurnAround() {
		return totalTurnaround;
	}
    
    private static void printInfo(Process currentProcess) {
        System.out.println("================================");
		System.out.println("Executing " + currentProcess);
		
		// Turnaround time
		int turnaroundTime = currentTime - currentProcess.getArrivalTime();
		System.out.println("Turnaround Time: " + turnaroundTime);
		
		// Waiting time
		int waitingTime = turnaroundTime - currentProcess.getBurstTime();
		System.out.println("Waiting Time: " + waitingTime);
		
		// Add to total waiting
		totalWaiting += waitingTime;
		// Add to total turnaround
		totalTurnaround += turnaroundTime;
		
    }
    
    
    
    
    
}
