//package cpuScheduler;
import java.util.*;
import java.util.List;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ScheduleSimulator {    
	private java.util.List<Process> processes;
	public float avgWaitingTime;
	public float avgTurnAroundTime;

	public ScheduleSimulator() {
    processes = new ArrayList<>();
	float avgWaitingTime = 0;
	float avgTurnAroundTime = 0;
	}
    
    public static void main(String[] args) {
        ScheduleSimulator scheduleSimulator = new ScheduleSimulator();
        scheduleSimulator.run();
    }
    
    
    public List<Process> getProcesses() {
        return processes;
    }

	public void run() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter the number of processes.");
		int noOfProcesses = scanner.nextInt();
		System.out.println("Please enter the Round Robin Time Quantum.");
		int rrQuantumTime = scanner.nextInt();		
		System.out.println("Please enter the context switching time.");
		int contextSwitchTime = scanner.nextInt();
		List<Process> processes = new ArrayList<>();

		for(int i=0;i<noOfProcesses;i++) {
            System.out.println("Please enter the details of process number " + (i + 1));
            System.out.print("Process Name: ");
            String name = scanner.next();

            System.out.print("Process Color (Graphical Representation): ");
            String color = scanner.next();

            System.out.print("Process Arrival Time: ");
            int arrivalTime = scanner.nextInt();

            System.out.print("Process Burst Time: ");
            int burstTime = scanner.nextInt();

            System.out.print("Process Priority Number: ");
            int priority = scanner.nextInt();
            processes.add(new Process(name, color, arrivalTime, burstTime,priority));
		}
		// Select scheduler type and simulate
        System.out.println("Select Scheduler Type:");
        System.out.println("1. Non-Preemptive Shortest- Job First (SJF)");
        System.out.println("2. Shortest- Remaining Time First (SRTF) Scheduling");
        System.out.println("3. Non-preemptive Priority Scheduling");
        System.out.println("4. AG Scheduling");

        int schedulerType = scanner.nextInt();
        List<Pair<Process, Integer>> execOrder = new ArrayList<>();
        
        switch (schedulerType) {
            
        case 1:
            // Non-Preemptive SJF Scheduler Function
        	//NonPreemptiveSJF scheduler = new NonPreemptiveSJF();
        	//scheduler.schedule(processes, contextSwitchTime);
        	NonPreemptiveSJF.schedule(processes, contextSwitchTime);
        	execOrder = NonPreemptiveSJF.getExecutionOrder();
        	avgWaitingTime = NonPreemptiveSJF.getAvgWaiting(); 
        	avgTurnAroundTime = NonPreemptiveSJF.getAvgTurnAround();
            break;

        case 2:
            // Call Shortest- Remaining Time First Scheduler Function
            SRTF srtfScheduler = new SRTF(processes, contextSwitchTime);
            srtfScheduler.runScheduler();
            execOrder = srtfScheduler.getExecutionOrder();
            avgWaitingTime = srtfScheduler.getAvgWaiting(); 
        	avgTurnAroundTime = srtfScheduler.getAvgTurnAround();
            break;

        case 3:
            // Call Non-preemptive Priority Scheduler Function
        	NonPreemptivePS.schedule(processes, contextSwitchTime);
        	execOrder = NonPreemptivePS.getExecutionOrder();
        	avgWaitingTime = NonPreemptivePS.getAvgWaiting(); 
        	avgTurnAroundTime = NonPreemptivePS.getAvgTurnAround();
        	
        	// I'm implementing the printing inside the function itself since the printing process 
        	// will differ from scheduler to scheduler
            break;

        case 4:
        AGScheduling.schedule(processes, rrQuantumTime);
        execOrder = AGScheduling.getExecutionOrder();
        avgWaitingTime = AGScheduling.getAverageWaitingTime(); 
        avgTurnAroundTime = AGScheduling.getAverageTurnaroundTime();
            
        break;

        default:
            System.out.println("Please enter a type number, i.e 1 or 2 or 3 or 4.");
        }
        
    	ProcessGUIColors processGUI = new ProcessGUIColors(execOrder);
        // Update the GUI with the calculated statistics
        processGUI.setAvgWaitingTime(avgWaitingTime);
        processGUI.setAvgTurnAroundTime(avgTurnAroundTime);
        processGUI.setSchedulerType(schedulerType);
    	processGUI.setVisible(true);
	}

}

////////////////////////////////////////////////////////////////////////////////SRTF SCHEDULER/////////////////////////////////////////////////////////////////////////////////////////////////////

class SRTF {
    private java.util.List<Process> processes;
    private PriorityQueue<Process> readyQueue;
    private int currentTime;
    private int totalWaitingTime;
    private int totalTurnaroundTime;
    private int contextTime;
    private static final int AGING_THRESHOLD = 7;
    private java.util.List<Pair<Process, Integer>> executionOrder;
    private Set<Process> processesAddedToQueue;

    public SRTF(List<Process> processes, int contextTime) {
        this.processes = processes;
        this.readyQueue = new PriorityQueue<>(new ShortestRemainingTimeComparator());
        this.executionOrder = new ArrayList<>();
        this.processesAddedToQueue = new HashSet<>();
        this.contextTime = contextTime;
        this.currentTime = 0;
        this.totalTurnaroundTime = 0;
        this.totalWaitingTime = 0;
    }

    private void updateReadyQueue() {
        for (Process process : processes) {
            if (process.getArrivalTime() <= currentTime && !process.isCompleted() && !processesAddedToQueue.contains(process)) {
                readyQueue.add(process);
            }
        }
//        printReady(readyQueue);
    }

    public void runScheduler() {
        int totalProcesses = processes.size();
        int completedProcesses = 0;
        Process previousProcess = null;

        while (completedProcesses < totalProcesses) {
            updateReadyQueue();

            if (!readyQueue.isEmpty()) {
                Process currentProcess = readyQueue.poll();
                currentProcess = handleContextSwitch(previousProcess, currentProcess);
//                currentProcess = chooseProcess(currentProcess);

                executionOrder.add(new Pair<>(currentProcess, currentTime));

                int increaseCompleted = executeProcess(currentProcess);
                if (increaseCompleted == 1) {
                    completedProcesses++;
                }

                previousProcess = currentProcess;
            }
            currentTime++;
        }
        printResults();
    }

    private Process handleContextSwitch(Process previousProcess, Process currentProcess) {
        if (previousProcess != null && !currentProcess.getName().equals(previousProcess.getName())) {
            // Add context time only if there is a current process, and it's not the same as the previous one
//            System.out.println("current process was " + currentProcess.getName());
//            System.out.println("current time before context: " + currentTime);
            currentTime += contextTime;
//            System.out.println("current time after context: " + currentTime);            }

            readyQueue.clear();
            processesAddedToQueue.clear();
            updateReadyQueue();

            return readyQueue.poll();
        }
        return currentProcess;
    }
    private int executeProcess(Process currentProcess){
        currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);

        if (currentProcess.isCompleted()) {
            calculateTurnaround(currentProcess);
            calculateWaitingTime(currentProcess);

//            System.out.println("the ended process turnaround is " + currentProcess.getTurnAroundTime());
//            System.out.println("the ended process waiting is " + currentProcess.getWaitingTime());

            totalWaitingTime += currentProcess.getWaitingTime();
            totalTurnaroundTime += currentProcess.getTurnAroundTime();

//            processes.removeIf(Process::isCompleted);
            readyQueue.removeIf(Process::isCompleted);
            processesAddedToQueue.removeIf(Process::isCompleted);
            return 1;
        }
        else {
            readyQueue.add(currentProcess);
        }
        return 0;
    }

    private void handleAging(Process chosenProcess) {
        for (Process process : readyQueue) {
            if (process != chosenProcess) {
                int processPriority = process.getPriority();
                process.setPriority(processPriority + 1);
            }
            else{
                chosenProcess.setPriority(chosenProcess.getPriority() - 1);
            }
        }
        if (chosenProcess != null) {
            chosenProcess.setPriority(chosenProcess.getPriority() - 1);
        }
    }
    public Process chooseProcess(Process currentProcess) {
        if (currentProcess.getPriority() >= AGING_THRESHOLD){
            return currentProcess;
        }
        if (!readyQueue.isEmpty()) {
            Process highestPriorityProcess = currentProcess;

            for (Process process : readyQueue) {
                if (process.getPriority() > highestPriorityProcess.getPriority() && process.getPriority() >= AGING_THRESHOLD) {
                    highestPriorityProcess = process;
                }
            }
            // Otherwise, return the process with the highest priority in the ready queue
            currentProcess = highestPriorityProcess;
        }
        handleAging(currentProcess);
        return currentProcess;
    }

    private void calculateTurnaround(Process process) {
        process.setTurnAroundTime(currentTime - process.getArrivalTime() + 1 + contextTime);
    }

    private void calculateWaitingTime(Process process) {
        process.setWaitTime(process.getTurnAroundTime() - process.getBurstTime());
    }

    private void printResults() {
        double averageWaitingTime = (double) totalWaitingTime / processes.size();
        double averageTurnaroundTime = (double) totalTurnaroundTime / processes.size();

        System.out.println("================================");

        System.out.println("Execution Order:");
        for (Pair<Process, Integer> pair : executionOrder) {
            System.out.println("Process " + pair.getFirst().getName() +
                    " entered execution at time " + pair.getSecond());
        }
        System.out.println("================================");

        for (Process process : processes) {
            int turnaroundTime = process.getTurnAroundTime();
            int waitingTime = process.getWaitingTime();

            System.out.println("Process " + process.getName() + ":");
            System.out.println("  Turnaround Time: " + turnaroundTime);
            System.out.println("  Waiting Time: " + waitingTime);

            System.out.println("================================");
        }

        System.out.println("Average Waiting Time: " + averageWaitingTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
    }
    public List<Pair<Process, Integer>> getExecutionOrder(){
        return executionOrder;
    }

    public void printPro(List<Process> processes) {
        for (Process p : processes){
            System.out.print(p + " ");
        }
        System.out.println(" ");
    }

    public void print(Set<Process> set) {
        for (Process s : set){
            System.out.print(s + " ");
        }
        System.out.println(" ");
    }
    public void printReady(PriorityQueue<Process> readyQueue) {
        System.out.println("the ready queue is: ");
        for (Process r : readyQueue){
            System.out.print(r + " ");
        }
        System.out.println(" ");

    }

    public void setTotalTurnaroundTime(int totalTurnaroundTime) {
        this.totalTurnaroundTime = totalTurnaroundTime;
    }
    public void setTotalWaitingTime(int totalWaitingTime) {
        this.totalWaitingTime = totalWaitingTime;
    }
    public float getAvgWaiting(){
        return (float) totalWaitingTime / processes.size();
    }
    public float getAvgTurnAround(){
        return (float) totalTurnaroundTime / processes.size();
    }
}

class Pair<F, S> {
    private final F first;
    private final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}

class ShortestRemainingTimeComparator implements Comparator<Process> {
    @Override
    public int compare(Process p1, Process p2) {
        int result = Integer.compare(p1.getRemainingTime(), p2.getRemainingTime());

        if (result == 0) {
            result = Integer.compare(p1.getArrivalTime(), p2.getArrivalTime());
        }

        return result;
    }
}

////////////////////////////////////////////////////////////////////////////////Non Preemptive SJF SCHEDULER/////////////////////////////////////////////////////////////////////////////////////////////////////

class NonPreemptiveSJF {

	private static List<Process> SJP;
	private static List<Process> processes;
 	private static List<Pair<Process, Integer>> executionOrder;
	private static int currentTime;
	private static float totalTurnaround = 0;
	private static float totalWaiting = 0;
	
 public static void schedule(List<Process> inputProcesses, int contextSwitchTime) {
 	SJP = new ArrayList<>();
 	// Get the processes queue
	processes = new ArrayList<>(inputProcesses);
     executionOrder = new ArrayList<>();
	// current time = first process's arrival time
 	currentTime = processes.get(0).getArrivalTime();

	// For each process:
 	while (!processes.isEmpty()) {
 		addAndSort();
 		executeAndRemove(contextSwitchTime);
 	}

	// Get the Average Waiting and Turnaround Time
 	totalWaiting /= inputProcesses.size();
	totalTurnaround /= inputProcesses.size();

 	// Print the Average Waiting and Turnaround Time
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

     // Sort the added processes according to their burst time
 	Collections.sort(SJP, Comparator.comparingInt(Process::getBurstTime));
 	
 }
 
 private static void executeAndRemove(int contextSwitchTime) {
 	
 	while (!SJP.isEmpty()) {	// For each process
 		final Process currentProcess = SJP.get(0);
 		
 		// Include the process in the execution order to be visualized later
 		addProcessToGUI(currentProcess);
 		    		
 		// Update current time
 		currentTime += currentProcess.getBurstTime();
 		currentTime += contextSwitchTime;
 		
 		// Print the process info
 		printInfo(currentProcess);

 		// Remove the process
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

////////////////////////////////////////////////////////////////////////////////Non Preemptive PS SCHEDULER/////////////////////////////////////////////////////////////////////////////////////////////////////

class NonPreemptivePS {
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

////////////////////////////////////////////////////////////////////////////////AG SCHEDULER/////////////////////////////////////////////////////////////////////////////////////////////////////


class AGScheduling {
public static float averageWaitingTime;
    public static float averageTurnaroundTime;
    static List<Pair<Process, Integer>> executionList = new ArrayList<>();

    public static void schedule(List<Process> processes, int QuantumTime) {

        Queue<Process> readyQueue = new LinkedList<>(processes); 
        Queue<Process> inQueue = new LinkedList<>();
        List<Process> dieList = new LinkedList<>();

        int time = 0;

        // Initializing processes with quantum time and AG factor
        for (Process process : processes) {
            process.setQuantumTime(QuantumTime);
            process.setAgFactor(calculateAGFactor(process));
        }


        List<Process> ArrivalList = new LinkedList<>(readyQueue);
        Collections.sort(ArrivalList, Comparator.comparingInt(Process::getArrivalTime));

        List<Process> AgFactorList = new LinkedList<>(readyQueue);
        Collections.sort(AgFactorList, Comparator.comparingInt(Process::getAGFactor));

        boolean first = true;
        boolean cases = false;
        boolean firstoccur = true;
        Process runningProcess = null;

        inQueue.add(ArrivalList.get(0));
        
        while (!readyQueue.isEmpty()) {
            boolean breakloop = true;
            StringBuilder quantumPrint;
            int counter;
            int halfQuantum;

            if (runningProcess != null && runningProcess.getRemainingTime() == 0) {
                // Remove the completed process from queues and lists
                inQueue.remove(runningProcess);
                readyQueue.remove(runningProcess);
                AgFactorList.remove(runningProcess);
        
                // Fetch the next process to execute if the queue is not empty
                if (!AgFactorList.isEmpty()) {
                    runningProcess = inQueue.poll();
                } else {
                    break; // Break the loop if no more processes are in the queue
                }
            }
            //printing the ouput
            quantumPrint = new StringBuilder("Quantum (");
            for (Process process : processes) {
                quantumPrint.append(process.getQuantumTime()).append(", ");
            }

            quantumPrint.delete(quantumPrint.length() - 2, quantumPrint.length());
            quantumPrint.append(")");
            System.out.println(quantumPrint);

            if(first){
                runningProcess = inQueue.poll();
                first = false;
            }
            if(cases){
                runningProcess = inQueue.poll();
                cases = false;
            }
            if(runningProcess.getRemainingTime() == 0){
                inQueue.remove(runningProcess);
                readyQueue.remove(runningProcess);
                AgFactorList.remove(runningProcess);
                runningProcess = inQueue.poll();
                continue;
            }
            counter=0;
            halfQuantum = (int) Math.ceil(0.5 * runningProcess.getQuantumTime());
// add the pairs
            if(runningProcess.getRemainingTime() > halfQuantum){
                for(int i = time ; i < time+halfQuantum; i++)
                {
                    executionList.add(new Pair<>(runningProcess, i));
                }
            }
            // Scenario 3.1: Process completes within its Quantum time
            if (runningProcess.getRemainingTime() <= halfQuantum) {
                for(int i = time ; i < time+runningProcess.getRemainingTime(); i++)
                    {
                        executionList.add(new Pair<>(runningProcess, i));
                    }
                    time += runningProcess.getRemainingTime();
                    for (Process readyProcess : readyQueue) {
                        if (readyProcess == runningProcess) {
                                                    readyProcess.setRemainingTime(0);
                            readyProcess.setQuantumTime(0);
                            readyProcess.setKilledTime(time);
                            dieList.add(readyProcess);
                            runningProcess = inQueue.poll();
                            breakloop = false;
                            break;
                        }
                    }

            }

            else{
            time += halfQuantum; // Increment time by Quantum time
            counter = halfQuantum;
            }

            while(breakloop){
                for (Process process : processes)
                {
                    if(process == runningProcess){
                        continue;
                    }
                    if(firstoccur){
                        if(time >= process.getArrivalTime() && process.getAGFactor() < runningProcess.getAGFactor()){
                            for (Process readyProcess : readyQueue) {
                                if (readyProcess == runningProcess) {
                                    // Update quantum time and remaining time
                                    readyProcess.setQuantumTime(readyProcess.getQuantumTime() + (readyProcess.getQuantumTime() - counter));
                                    readyProcess.setRemainingTime(readyProcess.getRemainingTime() - counter);
                                    break;
                                }
                            }
                            inQueue.add(runningProcess);
                            runningProcess = process;
                            breakloop = false;
                            firstoccur = false;
                            break;
                        }
                    }

                    else if (!firstoccur){
                        // Secanrio 2
                        if(time >= process.getArrivalTime() && process.getAGFactor() < runningProcess.getAGFactor() && isMininumagFactor(process,AgFactorList)){
                            for (Process readyProcess : readyQueue) {
                                if (readyProcess == runningProcess) {
                                    // Update quantum time and remaining time
                                    readyProcess.setQuantumTime(readyProcess.getQuantumTime() + (readyProcess.getQuantumTime() - counter));
                                    readyProcess.setRemainingTime(readyProcess.getRemainingTime() - counter);
                                    break;
                                }
                            }
                            inQueue.add(runningProcess);
                            runningProcess = process;
                            breakloop = false;
                            break;
                        }
                    }

                }
                if(breakloop){
                    executionList.add(new Pair<>(runningProcess, time));
                    time++;
                    counter++;
                }
                // Senario 3.2:
                if (runningProcess.getRemainingTime() == counter) {
                    for (Process readyProcess : readyQueue) {
                        if (readyProcess == runningProcess) {
                            readyProcess.setRemainingTime(0);
                            readyProcess.setQuantumTime(0);
                            readyProcess.setKilledTime(time);
                            dieList.add(readyProcess);
                            readyQueue.remove(runningProcess);
                            AgFactorList.remove(runningProcess);
                            if(inQueue.isEmpty() && !AgFactorList.isEmpty()){
                                inQueue.add(AgFactorList.get(0));
                                runningProcess = inQueue.poll();
                            }
                            breakloop = false;
                            break;
                        }
                    }

                }
                // Senario 1:
                if(counter == runningProcess.getQuantumTime() && runningProcess.getRemainingTime() != 0){
                    int mean = (int) Math.ceil(0.1 * calculateMeanOfQuantum(readyQueue));
                    for (Process readyProcess : readyQueue) {
                        if (readyProcess == runningProcess) {
                            // Update quantum time and remaining time
                            readyProcess.setQuantumTime(readyProcess.getQuantumTime() + mean);
                            readyProcess.setRemainingTime(readyProcess.getRemainingTime() - counter);
                            break;
                        }
                    }
                    cases = true;
                    inQueue.add(runningProcess);
                    break;
                }


            }
        }



        //print the Final Quantum
        StringBuilder finalQuantumPrint = new StringBuilder("Quantum (");
        for (Process process : processes) {
            finalQuantumPrint.append(process.getQuantumTime()).append(", ");
        }
        finalQuantumPrint.delete(finalQuantumPrint.length() - 2, finalQuantumPrint.length());
        finalQuantumPrint.append(")");
        System.out.println(finalQuantumPrint);

        calculateWaitingTime(processes);
        printWaitingTime(processes);
        calculateTurnaroundTime(processes);
        printTurnaroundTime(processes);

        calculateAverageWaitingTime(processes);
        calculateAverageTurnaroundTime(processes);
        System.out.println(averageTurnaroundTime);
        System.out.println(averageWaitingTime);

        for (Pair<Process, Integer> pair : executionList) {
            Process process = pair.getFirst();
            Integer value = pair.getSecond();
            System.out.println("Process: " + process + ", Value: " + value);
        }



        }


    private static boolean isMininumagFactor(Process process,List<Process> AgFactorList){
        return process == AgFactorList.get(0);
    }


    //هذه الفنكشن بتحسب ال AGFactor يا يوسف
private static int calculateAGFactor(Process process) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter the random value for test: ");
    int randomValue = scanner.nextInt();
    int AGFactor;

    if (randomValue < 10) {
        AGFactor = randomValue + process.getArrivalTime() + process.getBurstTime();
    } else if (randomValue > 10) {
        AGFactor = 10 + process.getArrivalTime() + process.getBurstTime();
    } else {
        AGFactor = process.getPriority() + process.getArrivalTime() + process.getBurstTime();
    }

    return AGFactor;
}


    //هذه الفنكشن بتحسب ال mean يا يوسف
    private static int calculateMeanOfQuantum(Queue<Process> processes) {
        int totalQuantum = 0;
        for (Process process : processes) {
            totalQuantum += process.getQuantumTime();
        }

        if (processes.size() > 0) {
            return totalQuantum / processes.size();
        } else {
            return 0;
        }
    }

// Function to calculate waiting time for each process
    private static void calculateWaitingTime(List<Process> processes) {
        for (Process process : processes) {
            int waitingTime = process.getKilledTime() - process.getArrivalTime() - process.getBurstTime();
            process.setWaitTime(waitingTime);
        }
    }

    // Function to calculate waiting time for each process
    private static void calculateTurnaroundTime(List<Process> processes) {
        for (Process process : processes) {
            int turnAroundTime = process.getWaitingTime() + process.getBurstTime();
            process.setTurnAroundTime(turnAroundTime);
        }
    }

    // Function to print waiting time for each process
    private static void printWaitingTime(List<Process> processes) {
        System.out.println("\nWaiting Time for each process:");
        for (Process process : processes) {
            System.out.println("Process " + process.getName() + ": " + process.getWaitingTime());
        }
    }
    // Function to print turnaround time for each process
    private static void printTurnaroundTime(List<Process> processes) {
        System.out.println("\nTurnaround Time for each process:");
        for (Process process : processes) {
            System.out.println("Process " + process.getName() + ": " + process.getTurnAroundTime());
        }
    }

    // Function to calculate average waiting time
    private static void calculateAverageWaitingTime(List<Process> processes) {
        int totalWaitingTime = 0;
        for (Process process : processes) {
            totalWaitingTime += process.getWaitingTime();
        }
        averageWaitingTime = (float) ((double) totalWaitingTime / processes.size());
    }

    // Function to calculate average turnaround time
    private static void calculateAverageTurnaroundTime(List<Process> processes) {
        int totalTurnaroundTime = 0;
        for (Process process : processes) {
            totalTurnaroundTime += process.getTurnAroundTime();
        }
        averageTurnaroundTime = (float) ((double) totalTurnaroundTime / processes.size());
    }

    public static float getAverageTurnaroundTime()
    {
        return averageTurnaroundTime;
    }

    public static float getAverageWaitingTime()
    {
        return averageWaitingTime;
    }

    public static List<Pair<Process, Integer>> getExecutionOrder() {
    return executionList;
}

}

////////////////////////////////////////////////////////////////////////////////Process////////////////////////////////////////////////////////////////////////////////////////////////////////

class Process{
	private static int idCounter = 3280;
	private String name;
    private String color;
    private int executionOrder;
    private int PID;
    private int arrivalTime;
    private int burstTime;
	private int remainingTime;
    private int priority;
    public int waitingTime;
    public int turnAroundTime;
	public int AGFactor;
	public int quantum;
	public int KilledTime;

    // the amount of private variables might seem excessive
    // but they will be important for the graphical representation


    public Process(String name, String color, int arrivalTime, int burstTime, int priority) {
        this.PID = idCounter++;
        this.name = name;
        this.color = color;
    	this.arrivalTime = arrivalTime;
    	this.burstTime = burstTime;
		this.remainingTime = this.burstTime;
    	this.priority = priority;
     }	     
	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public int getPID() {
		return PID;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setBurstTime(int NewBurstTime)
	{
		this.burstTime = NewBurstTime;
	}

	public int getBurstTime() {
		return burstTime;
	}

	public int getPriority() {
		return priority;
	}
	public int getExecutionOrder() {
		return executionOrder;
	}
	public int getWaitingTime(){
		return waitingTime;
	}
	public int getTurnAroundTime() {
		return turnAroundTime;
	}
	public int getRemainingTime() {
		return remainingTime;
	}
	public void setWaitTime(int myWaitTime) {
		this.waitingTime = myWaitTime;
	}
	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}

	public void setTurnAroundTime(int myTurnAroundTime) {
		this.turnAroundTime = myTurnAroundTime;
	}
	public void setExecutionOrder(int order) {
		this.executionOrder = order;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public boolean isCompleted(){
		return remainingTime == 0;
	}
	@Override
	public String toString() {
	    return "Process " + name;
	}

	public void setAgFactor(int calculateAGFactor) {
		this.AGFactor = calculateAGFactor;
	}

    public int getQuantumTime() {
		return quantum;
	}
	
    public void setQuantumTime(int newQuantum) {
		this.quantum = newQuantum;
    }
    public int getAGFactor() {
        return AGFactor;
    }
	public void setKilledTime(int time) {
		this.KilledTime = time;

	}
	public int getKilledTime() {
	return KilledTime;
 	}



}

////////////////////////////////////////////////////////////////////////////////Process GUI Colors/////////////////////////////////////////////////////////////////////////////////////////////////////

class ProcessGUIColors extends JFrame {

    private java.util.List<ProcessBox> processBoxes;
    private java.util.List<Pair<Process, Integer>> executionOrder;

    private JLabel avgWaitingLabel;
    private JLabel avgTurnAroundLabel;
    private JLabel schedulerTypeLabel;
    private JLabel statisticsLabel;

    public ProcessGUIColors(List<Pair<Process, Integer>> executionOrder) {
        super("CPU Scheduling Graph");

        this.executionOrder = executionOrder;
        processBoxes = new ArrayList<>();
        int xCoordinate = 50;
        int yCoordinate = 50;
        int boxWidth = 30;
        int boxHeight = 70;

        for (Pair<Process, Integer> pair : executionOrder) {
            Process process = pair.getFirst();
            Color processColor = getColorFromString(process.getColor());
            processBoxes.add(new ProcessBox(process.getName(), processColor, xCoordinate, yCoordinate, boxWidth, boxHeight, pair.getSecond()));
            xCoordinate += boxWidth + 15;
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);

        JTable processTable = new JTable(getTableModel());

        getContentPane().setBackground(new Color(240, 240, 240));

        Font customFont = new Font("Arial", Font.PLAIN, 16);
        UIManager.put("Label.font", customFont);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Process Information"));
        tablePanel.add(new JScrollPane(processTable), BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.EAST);

        ProcessPanel processPanel = new ProcessPanel();
        JScrollPane processScrollPane = new JScrollPane(processPanel);
        mainPanel.add(processScrollPane, BorderLayout.CENTER);

        JPanel statisticsPanel = new JPanel();
        statisticsPanel.setLayout(new BoxLayout(statisticsPanel, BoxLayout.Y_AXIS));

        statisticsLabel = new JLabel("<html><u><font color='red'>Statistics</font></u></html>");
        statisticsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statisticsPanel.add(statisticsLabel);

        avgWaitingLabel = new JLabel("Average Waiting Time: ");
        avgTurnAroundLabel = new JLabel("Average Turn Around Time: ");
        schedulerTypeLabel = new JLabel("Scheduler Name: ");

        statisticsPanel.add(schedulerTypeLabel);
        statisticsPanel.add(avgWaitingLabel);
        statisticsPanel.add(avgTurnAroundLabel);

        mainPanel.add(statisticsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private Color getColorFromString(String colorString) {
        switch (colorString.toLowerCase()) {
            case "red":
                return Color.RED;
            case "blue":
                return Color.BLUE;
            case "green":
                return Color.GREEN;
            case "purple":
                return new Color(128, 0, 128);
            case "orange":
                return Color.ORANGE;
            case "yellow":
                return Color.YELLOW;
            default:
                return Color.BLACK;
        }
    }

    private DefaultTableModel getTableModel() {
        String[] columnNames = {"Process", "Color", "Name", "PID", "Priority"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Pair<Process, Integer> pair : executionOrder) {
            Process process = pair.getFirst();
            Object[] rowData = {process, process.getColor(), process.getName(), process.getPID(), process.getPriority()};
            model.addRow(rowData);
        }

        return model;
    }

    private class ProcessBox {
        private String name;
        private Color color;
        private int x, y, width, height, executionOrder;

        public ProcessBox(String name, Color color, int x, int y, int width, int height, int executionOrder) {
            this.name = name;
            this.color = color;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.executionOrder = executionOrder;
        }
    }

    private class ProcessPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int currentTime = -1;
            for (ProcessBox box : processBoxes) {
                int remainingTime = box.executionOrder - currentTime;
                int boxWidth = Math.min(remainingTime, 1) * box.width;

                if (boxWidth > 0) {
                    g.setColor(box.color);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.fillRoundRect(box.x, box.y, boxWidth, box.height, 10, 10);

                    g.setColor(Color.BLACK);
                    g2d.drawRoundRect(box.x, box.y, box.width, box.height, 10, 10);

                    g.setColor(Color.BLACK);
                    g.drawString(box.name, box.x + box.width / 2 - 10, box.y + box.height / 2 + 5);

                    g.drawString(Integer.toString(box.executionOrder), box.x, box.y + box.height + 15);
                }

                currentTime += Math.min(remainingTime, 1);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            int totalWidth = processBoxes.isEmpty() ? 0 : processBoxes.get(processBoxes.size() - 1).x
                    + processBoxes.get(processBoxes.size() - 1).width;
            return new Dimension(totalWidth, super.getPreferredSize().height);
        }
    }

    public void setAvgWaitingTime(float avgWaitingTime) {
        avgWaitingLabel.setText("Average Waiting Time: " + avgWaitingTime);
    }

    public void setAvgTurnAroundTime(float avgTurnAroundTime) {
        avgTurnAroundLabel.setText("Average Turn Around Time: " + avgTurnAroundTime);
    }

    public void setSchedulerType(int schedulerType) {
        if (schedulerType == 1) {
            schedulerTypeLabel.setText("Scheduler: Non-Preemptive Shortest Job First Scheduler");
        } else if (schedulerType == 2) {
            schedulerTypeLabel.setText("Scheduler: Shortest-Remaining Time First Scheduler");
        } else if (schedulerType == 3) {
            schedulerTypeLabel.setText("Scheduler: Non-preemptive Priority Scheduler");
        } else if (schedulerType == 4) {
            schedulerTypeLabel.setText("Scheduler: AG Scheduler");
        }
    }
}

