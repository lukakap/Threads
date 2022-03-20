// JCount.java

/*
 Basic GUI/Threading exercise.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JCount extends JPanel {
	private JTextField textField;
	private JLabel label;
	private JButton start;
	private JButton stop;
	private Worker worker;

	public JCount() {
		// Set the JCount to use Box layout
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		textField = new JTextField();

		label = new JLabel("0");

		start = new JButton("Start");
		stop = new JButton("Stop");

		add(textField);
		add(label);
		add(start);
		add(stop);
		add(Box.createRigidArea(new Dimension(0,40)));

		worker = null;

		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (worker != null) worker.interrupt();
				worker = new Worker(textField,label);
				worker.start();
			}
		});

		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(worker != null) worker.interrupt();
				worker = null;
			}
		});


	}

	public static void main(String[] args) {
		// Creates a frame with 4 JCounts in it.
		// (provided)
		JFrame frame = new JFrame("The Count");
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());


		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	class Worker extends Thread {
		private JTextField textField;
		private JLabel label;
		private final int updateInterval = 10000;

		public Worker(JTextField textField, JLabel label) {
			this.textField = textField;
			this.label = label;
		}

		@Override
		public void run() {
			int countTo = 0;
			if(!textField.getText().equals("")) countTo = Integer.parseInt(textField.getText());
			System.out.println(Thread.currentThread().getName() + "   OUT");


			for(int i = 1; i <= countTo; i++){
				if(isInterrupted()) break;

				if(i % updateInterval == 0){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						break;
					}

					int forUpdate = i;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							label.setText(String.valueOf(forUpdate));
						}
					});
				}
			}
		}
	};
}

