package elevatorsim;

import javax.swing.*;
import java.awt.*;

public class ElevatorGui {
	public static void main(String[] args) {
		//Creating the Frame
        JFrame frame = new JFrame("Elevator Frame");
        JPanel ePanel = new JPanel();
        JLabel timeLabel = new JLabel("Time: ");
        timeLabel.setFont(new Font("Ariel", Font.PLAIN, 15));
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 200);
        frame.setResizable(false);

        FlowLayout layout = new FlowLayout();
        frame.setLayout(layout);
        
        for (int i=0; i<5; i++) {
        	JPanel ep = elevatorPanel("Elevator" + (i+1));
        	ePanel.add(BorderLayout.CENTER, ep);
        }
        frame.getContentPane().add(BorderLayout.NORTH, timeLabel);
        frame.getContentPane().add(BorderLayout.SOUTH, ePanel);
        frame.setVisible(true);
	}
	// 5 elevators
	// label.setText(now);
	
	//Creating the panel for elevator
	public static JPanel elevatorPanel(String name) {
		JPanel panel = new JPanel(); // the panel is not visible in output
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(new Dimension(150, 120));
		
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Serif", Font.BOLD, 28));
        JLabel dirLabel = new JLabel("Direction: ");
        dirLabel.setFont(new Font("Ariel", Font.PLAIN, 15));
        JLabel locLabel = new JLabel("Location: ");
        locLabel.setFont(new Font("Ariel", Font.PLAIN, 15));
        JLabel doorLabel = new JLabel("Door: ");
        doorLabel.setFont(new Font("Ariel", Font.PLAIN, 15));
        
        panel.add(nameLabel);
        panel.add(dirLabel);
        panel.add(locLabel);
        panel.add(doorLabel);
        
        return panel;
	}
}