package cpuScheduler;

public class Process{
	private static int idCounter = 3280;
	private String name;
    private String color;
    private int executionOrder;
    private int PID;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    public int waitingTime;
    public int turnAroundTime;
    // the amount of private variables might seem excessive
    // but they will be important for the graphical representation

    public Process(String name, String color, int arrivalTime, int burstTime, int priority) {
        this.PID = idCounter++;
        this.name = name;
        this.color = color;
    	this.arrivalTime = arrivalTime;
    	this.burstTime = burstTime;
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

	public int getBurstTime() {
		return burstTime;
	}

	public int getPriority() {
		return priority;
	}
	public void setWaitTime(int myWaitTime) {
		this.waitingTime = myWaitTime;
	}
	public void setTurnAroundTime(int myTurnAroundTime) {
		this.turnAroundTime = myTurnAroundTime;
	}
	public void setExecutionOrder(int order) {
		this.executionOrder = order;
	}
}