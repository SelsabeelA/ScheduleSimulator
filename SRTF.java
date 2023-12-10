package cpuScheduler;

import java.util.*;
public class SRTF {
    private List<Process> processes;
    private PriorityQueue<Process> readyQueue;
    private int currentTime;
    private int totalWaitingTime;
    private int totalTurnaroundTime;
    private int contextTime;
    private static final int AGING_THRESHOLD = 7;
    private List<Pair<Process, Integer>> executionOrder;
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
                currentProcess = chooseProcess(currentProcess);

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

    public static void main(String[] args) {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", "Red", 2, 6, 3));
        processes.add(new Process("P2", "Blue", 5, 2, 1));
        processes.add(new Process("P3", "Green", 1, 8, 7));
        processes.add(new Process("P4", "purple", 0, 3, 8));
        processes.add(new Process("P5", "orange", 4, 4, 2));

        int contextTime = 1;

        SRTF srtfScheduler = new SRTF(processes, contextTime);
        srtfScheduler.runScheduler();
        ProcessGUIColors processGUI = new ProcessGUIColors(srtfScheduler.getExecutionOrder()); // Context time is set to 1 second
        processGUI.setVisible(true);
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
