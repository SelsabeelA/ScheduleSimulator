package cpuScheduler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class NonPreemptivePS {
    static List<Pair<Process, Integer>> executionList = new ArrayList<>();
    public static float scheduleAvgWait;
    public static float scheduleAvgTurn;
    private static final int AGING_THRESHOLD = 7;

    public static void schedule(List<Process> processes, int contextSwitchTime) {
        // Sort processes by arrival time then after that by priority
        Collections.sort(processes, Comparator.comparingInt(Process::getArrivalTime).thenComparingInt(Process::getPriority));
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
            int processTimer = startTime;
            for(int i=0;i<process.getBurstTime();i++) {
                executionList.add(new Pair<>(process, processTimer));
                processTimer++;
            }
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
        float avgWaitTime = waitingTimeSum/processes.size();
        System.out.println("Average waiting time for processes was : " + avgWaitTime);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String roundedValue = decimalFormat.format(avgWaitTime);
        scheduleAvgWait = Float.parseFloat(roundedValue);


        float avgTurnAroundTime = turnAroundTimeSum/processes.size();
        System.out.println("Average turn around time for processes was : " + avgTurnAroundTime);
        roundedValue = decimalFormat.format(avgTurnAroundTime);
        scheduleAvgTurn = Float.parseFloat(roundedValue);

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

	public static List<Pair<Process, Integer>> getExecutionOrder() {
		return executionList;
	}

	public static float getAvgWaiting() {
		return scheduleAvgWait;
	}
	public static float getAvgTurnAround() {
		return scheduleAvgTurn;
	}
	
}
