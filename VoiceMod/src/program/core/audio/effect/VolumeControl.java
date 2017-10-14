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

public class VolumeControl implements AudioEffect {
	private float scale;
	private String channel;
	private EffectPanel gui;
	public VolumeControl(float scale, String channel){
		this.scale = scale;
		this.channel = channel;
		genGui();
	}
	private void genGui(){
		gui = AudioEffect.super.getGUI();
		JSlider slider;
		gui.add(slider = EffectPanel.genSlider(1, 75, 300, 50, "Volume", 0, 200, (int)(scale*100), 10, 100, true));
		JLabel identifier = new JLabel("Volume Multiplier: "+slider.getValue()+"%");
		identifier.setHorizontalAlignment(JLabel.CENTER);
		identifier.setBounds(1,50,300,25);
		slider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				scale = slider.getValue()/100f;
				identifier.setText("Volume Multiplier: "+slider.getValue()+"%");
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
	public VolumeControl(){
		this(1,"main");
	}
	@Override
	public void pass(Frame f) {
		Channel c = f.getSubChannel(channel);
		if(c == null){
			return;
		}
		if(c.isDecomposed()){
			for(int i = 0 ; i < Frame.getSamples() ; i++){
				c.scaleMagnitude(0, i, scale);
				c.scaleMagnitude(1, i, scale);
			}
		}else{
			float[][] raw = c.getRaw();
			for(int i = 0 ; i < Frame.getSamples()*2 ; i++){
				raw[0][i] *= scale;
				raw[1][i] *= scale;
			}
		}
	}

	public double getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	@Override
	public EffectPanel getGUI() {
		return gui;
	}

	@Override
	public String getName() {
		return "Volume Controller/Amplifier";
	}
	@Override
	public boolean getDecomposedCompatible() {
		return true;
	}
	@Override
	public boolean getComposedCompatible() {
		return true;
	}
	@Override
	public String saveToString() {
		return channel.replaceAll(":", "")+":"+scale;
	}
	@Override
	public AudioEffect fromString(String s) {
		String[] val = s.split(":");
		return new VolumeControl(Float.parseFloat(val[1]),val[0]);
	}
}
