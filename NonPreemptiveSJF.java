// sort process with arrival time = 0, start with the first one
// each process's arrival time is <= the current time, add the process to the queue and sort it
// remove each executed process from the queue
// increment the current time by one each time a process is executed

//process queue
// current time = first process's arrival time
// for each process:
// add all processes with arrival time <= current time
// sort the queue
// execute the process
// remove the process from the queue
// increment the current time by the CONTEXT SWITCH time
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
	private static int totalTurnaround = 0;
	private static int totalWaiting = 0;
	
    public static void schedule(List<Process> inputProcesses, int contextSwitchTime) {
    	
    	SJP = new ArrayList<>();
    	processes = new ArrayList<>(inputProcesses);
        executionOrder = new ArrayList<>();
    	currentTime = processes.get(0).getArrivalTime();
    	
    	while (!processes.isEmpty()) {
    		addAndSort();
    		executeAndRemove(contextSwitchTime);
    	}
    	
    	//print the Average Waiting and Turnaround Time
    	System.out.println("Average Waiting Time for Processes was: " + totalWaiting/inputProcesses.size());
    	System.out.println("Average Turnaround Time for Processes was: " + totalTurnaround/inputProcesses.size());
    }
    
    private static void addAndSort() {
    	
    	int i = 0;
    	//add the arrived processes to the queue in order to be sorted
        while (i < processes.size() && processes.get(i).getArrivalTime() <= currentTime) {
            SJP.add(processes.get(i));
            processes.remove(i);
        }
        //sort the added processes
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
    
    private static void addProcessToGUI(Process p) {
    	for (int i = 0; i < p.getBurstTime(); i++) {
            executionOrder.add(new Pair<>(p, i + currentTime));
    	}
    }
    
    private static void printInfo(Process currentProcess) {
		System.out.println("Executing " + currentProcess);
		
		//turn around time
		int turnaroundTime = currentTime - currentProcess.getArrivalTime();
		System.out.println("Turnaround Time for " + currentProcess + " was " + turnaroundTime);
		
		//waiting time
		int waitingTime = turnaroundTime - currentProcess.getBurstTime();
		System.out.println("Waiting Time for " + currentProcess + " was " + waitingTime);
		
		// add to total waiting
		totalWaiting += waitingTime;
		// add to total turn around
		totalTurnaround += turnaroundTime;
		
    }
    
    
    
    
    
}