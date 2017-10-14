package program.core.audio.effect;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import program.core.audio.AudioEffect;
import program.core.audio.Channel;
import program.core.audio.Frame;

public class Shift implements AudioEffect{
	private float pitchShift;
	private String channel;
	private EffectPanel gui;
	public Shift(float shift, String channel){
		pitchShift = shift;
		this.channel = channel;
		genGui();
	}
	public Shift(){
		this(1,"main");
	}
	private void genGui(){
		gui = AudioEffect.super.getGUI();
		JSlider slider;
		gui.add(slider = EffectPanel.genSlider(1, 75, 300, 50, "Pitch", 50, 200,(int) (pitchShift*100), 5, 50, false));
		JLabel identifier = new JLabel("Pitch: "+pitchShift+" : 1");
		identifier.setHorizontalAlignment(JLabel.CENTER);
		identifier.setBounds(1,50,300,25);
		slider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				pitchShift = slider.getValue()/100f;
				identifier.setText("Pitch: "+pitchShift+" : 1");
			}
		});
		gui.add(identifier);
		JTextField channelField;
		gui.add(channelField = new JTextField(channel));
		channelField.setBounds(1, 235, 300, 25);
		JLabel identifierChannel = new JLabel("Channel: "+channelField.getText());
		identifierChannel.setHorizontalAlignment(JLabel.CENTER);
		identifierChannel.setBounds(1,210,300,25);
		channelField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				change();
			}
			public void removeUpdate(DocumentEvent e) {
				change();
			}
			public void insertUpdate(DocumentEvent e) {
				change();
			}
			public void change() {
				channel = channelField.getText();
				identifierChannel.setText("Channel: "+channel);
			}
		});
		channelField.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				channel = channelField.getText();
				identifierChannel.setText("Channel: "+channel);
			}
		});
		gui.add(identifierChannel);
	}
	@Override
	public void pass(Frame f) {
		Channel c = f.getSubChannel(channel);
		if(c == null)
			return;
		c.decompose();
		double[][] mag = c.getMagnitudes();
		int period = mag[0].length/2;
		double[][] newMag = new double[2][mag[0].length];
		for(int i = 1 ; i < period ; i++){
			double src = i/pitchShift;
			int srcA = (int) Math.floor(src);
			int srcB = srcA+1;
			src-=srcA;
			if(srcB < period){
				newMag[0][i] = mag[0][srcA]*(1-src) + mag[0][srcB]*src;
				newMag[1][i] = mag[1][srcA]*(1-src) + mag[1][srcB]*src;
			}
		}
		for(int i = 0 ; i < period ; i++){
			c.setMagnitude(0, i, newMag[0][i]);
			c.setMagnitude(1, i, newMag[1][i]);
		}
	}

	@Override
	public String getName() {
		return "Pitch Shifter";
	}
	@Override
	public boolean getDecomposedCompatible() {
		return true;
	}
	@Override
	public boolean getComposedCompatible() {
		return false;
	}
	@Override
	public EffectPanel getGUI() {
		return gui;
	}
	@Override
	public String saveToString() {
		return channel.replaceAll(":", "")+":"+pitchShift;
	}
	@Override
	public AudioEffect fromString(String s) {
		String[] val = s.split(":");
		return new Shift(Float.parseFloat(val[1]),val[0]);
	}
}
