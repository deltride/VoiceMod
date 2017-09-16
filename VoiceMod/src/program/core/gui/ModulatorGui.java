package program.core.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class ModulatorGui {
	private int width;
	private int height;
	private JFrame frame;
	public ModulatorGui(int width, int height){
		frame = new JFrame();
		this.width = width;
		this.height = height;
		initialize();
		frame.setSize(width, height);
		frame.setLayout(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	/**
	 * sets up the Gui
	 */
	private void initialize(){
		JButton testB = new JButton("Play");
		setComponentBounds(testB, .5f, 0, .5f, .5f);
		testB.addActionListener(new PlayListener());
		frame.add(testB);
	}
	public void setComponentBounds(Component c, float x, float y, float width, float height){
		c.setBounds((int)(x * this.width), (int)(y * this.height), (int)(width * this.width), (int)(height * this.height));
	}
	static class PlayListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent event) {
			System.out.println("play");
		}
	}
}
