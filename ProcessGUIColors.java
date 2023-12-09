package cpuScheduler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessGUIColors extends JFrame {

    private List<ProcessBox> processBoxes;
    private List<Pair<Process, Integer>> executionOrder;

    public ProcessGUIColors(List<Pair<Process, Integer>> executionOrder) {
        super("Processes Execution Order");

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
        setSize(xCoordinate + 50, boxHeight + 200); // Increased the window width
        setLocationRelativeTo(null);

        ProcessPanel processPanel = new ProcessPanel();
        setContentPane(processPanel);

        // Set background color
        getContentPane().setBackground(new Color(240, 240, 240));

        // Set custom font
        Font customFont = new Font("Arial", Font.PLAIN, 16);
        UIManager.put("Label.font", customFont);
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

            int currentTime = 0;
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

// ... (Remaining code)


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Pair<Process, Integer>> executionOrder = new ArrayList<>();
            executionOrder.add(new Pair<>(new Process("P1", "Red", 2, 6, 3), 1));
            executionOrder.add(new Pair<>(new Process("P2", "Blue", 5, 2, 1), 2));
            executionOrder.add(new Pair<>(new Process("P3", "Green", 1, 8, 7), 3));
            executionOrder.add(new Pair<>(new Process("P1", "Red", 2, 6, 3), 4));
            executionOrder.add(new Pair<>(new Process("P4", "purple", 0, 3, 8), 5));
            executionOrder.add(new Pair<>(new Process("P5", "orange", 4, 4, 2), 6));
            executionOrder.add(new Pair<>(new Process("P2", "Blue", 2, 6, 3), 7));
            executionOrder.add(new Pair<>(new Process("P2", "Blue", 2, 6, 3), 8));
            executionOrder.add(new Pair<>(new Process("P5", "Orange", 2, 6, 3), 9));

            ProcessGUIColors processGUI = new ProcessGUIColors(executionOrder); // Context time is set to 1 second
            processGUI.setVisible(true);
        });
    }
}
