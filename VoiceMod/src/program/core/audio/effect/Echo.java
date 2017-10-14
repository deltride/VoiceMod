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

public class Echo implements AudioEffect{
	private double[][] amplitudes;
	private double decay;
	private float peak;
	private EffectPanel gui;
	private String channel;
	public Echo(float time, float peak, String channel){
		amplitudes = new double[2][1];
		//calculate per sample decay to achieve .1 original amplitude after [time] seconds
		//10^(log(.1)/samplesAcrossPeriod)
		decay = Math.pow(10, -1/(time * Frame.getBaseFrequency() * 2));// *2 base because overlapping samples
		this.peak = peak;
		this.channel = channel;
		genGui(time);
	}
	public void setTime(float time){
		decay = Math.pow(10, -1/(time * Frame.getBaseFrequency() * 2));
	}
	public Echo(){
		this(2,5,"main");
	}
	private void genGui(float time){
		gui = AudioEffect.super.getGUI();
		JSlider sliderPeak;
		gui.add(sliderPeak = EffectPanel.genSlider(1, 75, 300, 50, "Peak", 0, 500, (int)(peak*10), 10, 50, false));
		JLabel identifier = new JLabel("Peak: "+(sliderPeak.getValue()/10f));
		identifier.setHorizontalAlignment(JLabel.CENTER);
		identifier.setBounds(1,50,300,25);
		sliderPeak.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				peak = sliderPeak.getValue()/10f;
				identifier.setText("Peak: "+(sliderPeak.getValue()/10f));
			}
		});
		gui.add(identifier);

		JSlider sliderTime;
		gui.add(sliderTime = EffectPanel.genSlider(1, 155, 300, 50, "Decay Time", 0, 500, (int)(time*100), 10, 100, false));
		JLabel identifierA = new JLabel("Decay Time: "+(sliderTime.getValue()/100f)+"s");
		identifierA.setHorizontalAlignment(JLabel.CENTER);
		identifierA.setBounds(1,130,300,25);
		sliderTime.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				setTime(sliderTime.getValue()/100f);
				identifierA.setText("Decay Time: "+(sliderTime.getValue()/100f)+"s");
			}
		});
		gui.add(identifierA);
		
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
		if(c == null){
			return;
		}
		c.decompose();
		if(Frame.getSamples() != amplitudes[0].length){
			amplitudes[0] = new double[Frame.getSamples()];
			amplitudes[1] = new double[Frame.getSamples()];
		}
		for(int i = 0 ; i < Frame.getSamples() ; i++){
			double m1 = c.getMagnitude(0, i);
			double m2 = c.getMagnitude(1, i);
			double dAmpA = Math.min(m1,peak);
			double dAmpB = Math.min(m2,peak);
			c.setMagnitude(0, i, m1+amplitudes[0][i]);
			c.setMagnitude(1, i, m2+amplitudes[1][i]);
			amplitudes[0][i] = Math.max(dAmpA, amplitudes[0][i]) * decay;
			amplitudes[1][i] = Math.max(dAmpB, amplitudes[1][i]) * decay;
		}
//		f.setMagnitude(0, (int) (880 / f.getBaseFrequency()), 20+f.getMagnitude(0, 41));
//		f.setMagnitude(1, (int) (880 / f.getBaseFrequency()), 20+f.getMagnitude(1, 41));
//		
//		f.setMagnitude(0, (int) (1109 / f.getBaseFrequency()), 20+f.getMagnitude(0, 51));
//		f.setMagnitude(1, (int) (1109 / f.getBaseFrequency()), 20+f.getMagnitude(1, 51));
//		
//		f.setMagnitude(0, (int) (1319 / f.getBaseFrequency()), 20+f.getMagnitude(0, 51));
//		f.setMagnitude(1, (int) (1319 / f.getBaseFrequency()), 20+f.getMagnitude(1, 51));
//		
//		f.setMagnitude(0, (int) (1760 / f.getBaseFrequency()), 20+f.getMagnitude(0, 41));
//		f.setMagnitude(1, (int) (1760 / f.getBaseFrequency()), 20+f.getMagnitude(1, 41));
	}
	@Override
	public String getName() {
		return "Echo";
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
		return channel.replaceAll(":", "")+":"+peak+":"+decay;
	}
	@Override
	public AudioEffect fromString(String s) {
		String[] val = s.split(":");
		Echo e = new Echo(1,Float.parseFloat(val[1]),val[0]);
		e.decay = Double.parseDouble(val[2]);
		return e;
	}

}
