
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class SRTF {
    private List<Process> processes;
    private PriorityQueue<Process> readyQueue;
    private int currentTime;
    private int totalWaitingTime;
    private int totalTurnaroundTime;
    private int contextTime;
    private List<Pair<Process, Integer>> executionOrder; // New list to track execution order

    public SRTF(List<Process> processes, int contextTime) {
        this.processes = processes;
        this.readyQueue = new PriorityQueue<>(new ShortestRemainingTimeComparator());
        executionOrder = new ArrayList<>();
        this.contextTime = contextTime;
        this.currentTime = 0;
        totalTurnaroundTime = 0;
        totalWaitingTime = 0;
    }

    public void runScheduler() {
        int totalProcesses = processes.size();
        int completedProcesses = 0;

        while (completedProcesses < totalProcesses) {
            // Check for new arrivals and add them to the ready queue
            for (Process process : processes) {
                if (process.getArrivalTime() == currentTime && !process.isCompleted()) {
                    readyQueue.add(process);
                }
            }

            // If the ready queue is not empty, execute the process with the shortest remaining time
            if (!readyQueue.isEmpty()) {
                Process currentProcess = readyQueue.poll();

//                System.out.println("current is " + currentProcess.getName());

                // Add the current process and the time it entered execution to the execution order
                executionOrder.add(new Pair<>(currentProcess, currentTime));

                // Execute the process for one time unit
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
                System.out.println("current process is " + currentProcess.getName() + " entered at time " + currentTime + " and its remaining time is " + currentProcess.getRemainingTime());

                // Check if the process is completed
                if (currentProcess.isCompleted()) {
                    currentProcess.setTurnAroundTime(currentTime + contextTime - currentProcess.getArrivalTime() + 1);
                    currentProcess.setWaitTime(currentProcess.getTurnAroundTime() - currentProcess.getBurstTime());
                    System.out.println("process " + currentProcess.getName() + " has turnaround equals " + currentProcess.getTurnAroundTime() + " and waiting " + currentProcess.getWaitingTime() );
                    totalWaitingTime += currentProcess.getWaitingTime();
                    totalTurnaroundTime += currentProcess.getTurnAroundTime();
                    readyQueue.remove(currentProcess);
                    completedProcesses++;
                }
                else {
                    // Process is not completed, add it back to the ready queue
                    readyQueue.add(currentProcess);
                }
            }

            currentTime++;
        }

        // Calculate average waiting time and turnaround time
        double averageWaitingTime = (double) totalWaitingTime / totalProcesses;
        double averageTurnaroundTime = (double) totalTurnaroundTime / totalProcesses;

        System.out.println("Average Waiting Time: " + averageWaitingTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);

        // Print the execution order
//        System.out.println("Execution Order:");
//        for (Pair<Process, Integer> pair : executionOrder) {
//            System.out.println("Process " + pair.getFirst().getName() +
//                    " entered execution at time " + pair.getSecond());
//        }
    }

    public static void main(String[] args) {
        // Example usage
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", "Red", 2, 6, 3));
        processes.add(new Process("P2", "Blue", 5, 2, 1));
        processes.add(new Process("P3", "Green", 1, 8, 7));
        processes.add(new Process("P4", "Green", 0, 3, 8));
        processes.add(new Process("P5", "Green", 4, 4, 2));

        int contextTime = 0;

        SRTF srtfScheduler = new SRTF(processes, contextTime);
        srtfScheduler.runScheduler();
    }
}

// Define Pair class to store pairs
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
        return Integer.compare(p1.getRemainingTime(), p2.getRemainingTime());
    }
}
