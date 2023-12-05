package cpuScheduler;

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

waiting time = start time – arrival time
Turnaround time (latency): total time taken to execute a particular process =
waiting time + processing time (Min.)
*/


public class NonPreemptivePS {
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
        	process.setExecutionOrder(executionCount++);
            System.out.println("Executing Process " + process.getName() + " with Priority " + process.getPriority());
            process.setWaitTime(startTime - process.getArrivalTime());
            waitingTimeSum += process.waitingTime;
            System.out.println("Waiting Time for Process " + process.getName() + " was " + process.waitingTime);
            process.setTurnAroundTime(process.waitingTime + process.getBurstTime());
            turnAroundTimeSum += process.turnAroundTime;
            System.out.println("Turnaround Time for Process " + process.getName() + " was " + process.turnAroundTime);
            currentTime += process.getBurstTime() + contextSwitchTime;
            
        }
        int avgWaitTime = waitingTimeSum/processes.size();
        int avgTurnAroundTime = turnAroundTimeSum/processes.size();
        System.out.println("Average waiting time for processes was : " + avgWaitTime);
        System.out.println("Average turn around time for processes was : " + avgTurnAroundTime);
    }
    
    // NOTE FOR MYSELF:
    /* Implementation incomplete. I think there is something wrong in the way I'm calculating the starttime,
     and also problem starvation has to be solved perhaps by increasing the priority as time passes.
     */
}
