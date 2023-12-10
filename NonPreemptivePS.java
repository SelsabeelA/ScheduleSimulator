

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
NOTES FROM LAB:
● Preemptive scheduling: The preemptive scheduling is prioritized. The highest
priority process should always be the process that is currently running.
● Non-Preemptive scheduling: When a process enters the state of running, it
cannot be preempted (interrupted) until completes finishes its service time.
3. Priority Scheduling :
o A priority number (integer) is associated with each process
o CPU is allocated to the process with the highest priority (smallest
integer≡ highest priority)

o Priority can be decided based on memory requirements, time
requirements or any other resource requirement.
o Problem Starvation – low priority processes may never execute Solution
Aging – as time progresses increase the priority of the process

waiting time = turnaroundtime - processing time
Turnaround time (latency): total time taken to execute a particular process =
waiting time + processing time (Min.)
*/


public class NonPreemptivePS {
    private static final int AGING_THRESHOLD = 7;

    public static void schedule(List<Process> processes, int contextSwitchTime) {
        Collections.sort(processes, Comparator.comparingInt(Process::getPriority));
        printInformation(processes, contextSwitchTime);
    }

    private static void printInformation(List<Process> processes, int contextSwitchTime) {
        int waitingTimeSum = 0;
        int turnAroundTimeSum = 0;
        int startTime = processes.get(0).getArrivalTime();
        int currentTime = startTime;
        System.out.println("Process Execution Order:");
        int executionCount = 0;
        for (Process process : processes) {
            startTime = Math.max(currentTime, process.getArrivalTime());

        	process.setExecutionOrder(executionCount++);
            System.out.println("Executing Process " + process.getName() + " with Priority " + process.getPriority());
            
            currentTime += process.getBurstTime() + contextSwitchTime;
            
            process.setTurnAroundTime(currentTime - process.getArrivalTime());
            // the first process arrival time is the original start time for all processes
            turnAroundTimeSum += process.turnAroundTime;
            System.out.println("Turnaround Time for Process " + process.getName() + " was " + process.turnAroundTime);
            

            process.setWaitTime(process.turnAroundTime - process.getBurstTime());
            waitingTimeSum += process.waitingTime;
            System.out.println("Waiting Time for Process " + process.getName() + " was " + process.waitingTime);
            ageProcesses(processes, currentTime);
            
            
        }
        int avgWaitTime = waitingTimeSum/processes.size();
        System.out.println("Average waiting time for processes was : " + avgWaitTime);

        int avgTurnAroundTime = turnAroundTimeSum/processes.size();
        System.out.println("Average turn around time for processes was : " + avgTurnAroundTime);
    }

	private static void ageProcesses(List<Process> processes, int currentTime) {
		for (Process process : processes) {
            if (currentTime - process.getArrivalTime() > AGING_THRESHOLD) { // Adjust the aging threshold as needed
            	if(process.getPriority()>0) {
                    int newPriority = process.getPriority() - 1;
                    process.setPriority(newPriority);
            	}
                System.out.println("Aging: Increased Priority for Process " + process.getName() + " to " + process.getPriority());
            }
        }
	}
}
