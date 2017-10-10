package program.core.gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import program.core.audio.Frame;

public class StereoAudioVisualizer extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private static final int freqInterval = 1000;
	private int height, width;
	Frame frame;
	
	public StereoAudioVisualizer(int x, int y, int width, int height){
		this.height = height;
		this.width = width;
		this.setBounds(x, y, width, height);
		this.setBackground(Color.BLACK);
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(frame != null){
			frame.decompose();
			g.setColor(Color.GRAY);
			int h = height/2;
			float freqSample = Frame.getBaseFrequency()*Frame.getSamples()/freqInterval;
			for(int i = 0 ; i < freqSample ; i++){
				int x = (int) (i*width/freqSample);
				g.drawLine(x, 0, x, height);
			}
			g.setColor(Color.GREEN);
			for(int i = 0 ; i < Frame.getSamples() ; i++){
				int x = (int) (((float)i)/(Frame.getSamples()) * width);
				g.drawLine(x, h, x, (int) (h - (frame.getMagnitude(0, i)/10 * h)));
				g.drawLine(x, h, x, (int) (h + (frame.getMagnitude(1, i)/10 * h)));
			}
		}
	}

	public Frame getFrame() {
		return frame;
	}

	public void setFrame(Frame frame) {
		this.frame = frame;
		repaint();
	}
}
