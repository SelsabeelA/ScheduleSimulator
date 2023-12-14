//package cpuScheduler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessGUIColors extends JFrame {

    private List<ProcessBox> processBoxes;
    private List<Pair<Process, Integer>> executionOrder;

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

