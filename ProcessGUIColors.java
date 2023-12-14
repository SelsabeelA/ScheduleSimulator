package cpuScheduler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class ProcessGUIColors extends JFrame {

    private List<ProcessBox> processBoxes;
    private List<Pair<Process, Integer>> executionOrder;

    // Add JLabels to display statistics
    private JLabel avgWaitingLabel;
    private JLabel avgTurnAroundLabel;
    private JLabel schedulerTypeLabel;

    // Add JLabels to display statistics
    private JLabel statisticsLabel;


    public ProcessGUIColors(List<Pair<Process, Integer>> executionOrder) {
        super("CPU Scheduling Graph");

        this.executionOrder = executionOrder;
        processBoxes = new ArrayList<>();
        int xCoordinate = 50; // Initial x-coordinate for all processes
        int yCoordinate = 50; // Fixed y-coordinate for all processes
        int boxWidth = 30; // Increased box width
        int boxHeight = 70; // Increased box height

        for (Pair<Process, Integer> pair : executionOrder) {
            Process process = pair.getFirst();
            Color processColor = getColorFromString(process.getColor()); // Get color from string
            processBoxes.add(new ProcessBox(process.getName(), processColor, xCoordinate, yCoordinate, boxWidth, boxHeight, pair.getSecond()));
            xCoordinate += boxWidth + 15; // Add some spacing between processes
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600); // Set the window size
        setLocationRelativeTo(null);

        // Create a JTable to display process information
        JTable processTable = new JTable(getTableModel());

        // Set background color
        getContentPane().setBackground(new Color(240, 240, 240));

        // Set custom font
        Font customFont = new Font("Arial", Font.PLAIN, 16);
        UIManager.put("Label.font", customFont);

        // Create a mainPanel to hold the processPanel, processTable, and statisticsLabel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create a panel to hold the processTable and add it to the mainPanel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Process Information"));
        tablePanel.add(new JScrollPane(processTable), BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.EAST);

        // Create a panel to hold the ProcessPanel and add it to the mainPanel
        ProcessPanel processPanel = new ProcessPanel();
        mainPanel.add(processPanel, BorderLayout.CENTER);

        // Create a panel to hold the statistics and add it to the mainPanel
        JPanel statisticsPanel = new JPanel();
        statisticsPanel.setLayout(new BoxLayout(statisticsPanel, BoxLayout.Y_AXIS));

        // Add the "Statistics" label
        statisticsLabel = new JLabel("<html><u><font color='red'>Statistics</font></u></html>");
        statisticsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statisticsPanel.add(statisticsLabel);

        // Add the labels for average waiting time and average turn around time
        avgWaitingLabel = new JLabel("Average Waiting Time: ");
        avgTurnAroundLabel = new JLabel("Average Turn Around Time: ");
        schedulerTypeLabel = new JLabel("Scheduler Name: ");

        statisticsPanel.add(schedulerTypeLabel);

        statisticsPanel.add(avgWaitingLabel);
        statisticsPanel.add(avgTurnAroundLabel);
        statisticsPanel.add(avgTurnAroundLabel);


        mainPanel.add(statisticsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    
    // Utility method to convert a string to Color
    private Color getColorFromString(String colorString) {
        switch (colorString.toLowerCase()) {
            case "red":
                return Color.RED;
            case "blue":
                return Color.BLUE;
            case "green":
                return Color.GREEN;
            case "purple":
                return new Color(128, 0, 128); // Example for custom color (purple)
            case "orange":
                return Color.ORANGE;
            case "yellow":
                return Color.YELLOW;
            default:
                return Color.BLACK;
        }
    }

    // Method to create a DefaultTableModel for the JTable
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
                int boxWidth = Math.min(remainingTime, 1) * box.width; // Draw for one second

                if (boxWidth > 0) {
                    // Draw rounded rectangle for process box
                    g.setColor(box.color);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.fillRoundRect(box.x, box.y, boxWidth, box.height, 10, 10);

                    // Draw border
                    g.setColor(Color.BLACK);
                    g2d.drawRoundRect(box.x, box.y, box.width, box.height, 10, 10);

                    // Draw text (name of the process) inside the box in black
                    g.setColor(Color.BLACK);
                    g.drawString(box.name, box.x + box.width / 2 - 10, box.y + box.height / 2 + 5);

                    // Draw starting time below the box in black
                    g.drawString(Integer.toString(box.executionOrder), box.x, box.y + box.height + 15);
                }

                currentTime += Math.min(remainingTime, 1);
            }
        }
    }
    

    public void setAvgWaitingTime(float avgWaitingTime) {
        avgWaitingLabel.setText("Average Waiting Time: " + avgWaitingTime);
    }

    public void setAvgTurnAroundTime(float avgTurnAroundTime) {
        avgTurnAroundLabel.setText("Average Turn Around Time: " + avgTurnAroundTime);
    }

	public void setSchedulerType(int schedulerType) {
		if(schedulerType == 1) {
			//Non-Preemptive SJF Scheduler
			schedulerTypeLabel.setText("Scheduler: Non-Preemptive Shortest Job First Scheduler");
		}
		else if(schedulerType == 2) {
			//Shortest-Remaining Time First Scheduler
			schedulerTypeLabel.setText("Scheduler: Shortest-Remaining Time First Scheduler");
		}
		else if(schedulerType == 3) {
			// Non-preemptive Priority Scheduler
			schedulerTypeLabel.setText("Scheduler: Non-preemptive Priority Scheduler");
		}
		else if(schedulerType == 4) {
			//AGScheduling Scheduler
			schedulerTypeLabel.setText("Scheduler: AG Scheduler");
		}
		
	}
}
