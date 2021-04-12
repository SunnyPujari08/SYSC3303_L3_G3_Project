package elevatorsim;

import javax.swing.*;
import java.awt.*;

public class FloorGui {
	//22 floors
	public static void main(String[] args) {
		//Creating the Frame
        JFrame frame = new JFrame("Floor Frame");
        JPanel ePanel = new JPanel();
        JLabel timeLabel = new JLabel("Time: ");
        timeLabel.setFont(new Font("Ariel", Font.PLAIN, 15));
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 250);
        frame.setResizable(false);

        GridLayout layout = new GridLayout(2, 11);
        ePanel.setLayout(layout);
        
        for (int i=0; i<2; i++) {
        	for (int j=0; j<11; j++) {
        		JPanel ep = elevatorPanel("Floor" + ((i*10)+j));
        		ePanel.add(ep);
        	}
        }
        frame.getContentPane().add(BorderLayout.NORTH, timeLabel);
        frame.getContentPane().add(BorderLayout.CENTER, ePanel);
        frame.setVisible(true);
	}
	// label.setText(now);
	
	//Creating the panel for elevator
	public static JPanel elevatorPanel(String name) {
		JPanel panel = new JPanel(); // the panel is not visible in output
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(new Dimension(30, 10));
		
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Serif", Font.BOLD, 20));
        JLabel dirLabel = new JLabel("Button: ");
        dirLabel.setFont(new Font("Ariel", Font.PLAIN, 15));
        JLabel locLabel = new JLabel("Elevator?: ");
        locLabel.setFont(new Font("Ariel", Font.PLAIN, 15));

        panel.add(nameLabel);
        panel.add(dirLabel);
        panel.add(locLabel);
        
        return panel;
	}
}
