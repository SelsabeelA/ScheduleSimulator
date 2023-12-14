package cpuScheduler;

public class Process{
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
