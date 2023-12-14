//package cpuScheduler;

import java.util.*;

public class AGScheduling {
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
