package program.core.audio.effect;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

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

public class Delay implements AudioEffect{
	private LinkedList<Channel> dat;
	private float time;
	private int timeDom;
	private String channel;

	EffectPanel gui;

	public Delay(float time, String channel) {
		dat = new LinkedList<Channel>();
		this.channel = channel;
		setTime(time);
		genGui();
	}

	public Delay() {
		this(2, "main");
	}

	public void setTime(float time) {
		this.time = time;
		timeDom = (int) Math.floor(time * Frame.getBaseFrequency() * 2);
		// remove or add empty samples to simulate an absent echo until the
		// audio stream is initiated
		while (dat.size() < timeDom) {
			dat.addFirst(new Channel(new float[2][1]));
		}
		while (dat.size() > timeDom) {
			dat.removeFirst();
		}
	}

	private void genGui() {
		gui = AudioEffect.super.getGUI();
		
		JSlider sliderTime;
		gui.add(sliderTime = EffectPanel.genSlider(1, 155, 300, 50, "DelayTime", 0, 500, (int) (time * 100), 10, 100,
				false));
		JLabel identifierA = new JLabel("Delay Time: " + (sliderTime.getValue() / 100f) + "s");
		identifierA.setHorizontalAlignment(JLabel.CENTER);
		identifierA.setBounds(1, 130, 300, 25);
		sliderTime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setTime(sliderTime.getValue() / 100f);
				identifierA.setText("Delay Time: " + (sliderTime.getValue() / 100f) + "s");
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
		//
		// JSlider minScaleS;
		// gui.add(minScaleS = EffectPanel.genSlider(1, 235, 300, 50,
		// "MinScale", 0, 400, (int)(Math.max(0,Math.log10(scale)*100+400))
		// , 10, 100, false));
		// System.out.println(scale+"/"+minScaleS.getValue());
		// JLabel identifierB = new JLabel("Minimum Scale:
		// 10^"+((minScaleS.getValue()-400)/100f)+" or "+
		// Math.pow(10, (minScaleS.getValue()-400)/100f));
		// identifierB.setHorizontalAlignment(JLabel.CENTER);
		// identifierB.setBounds(1,210,300,25);
		// minScaleS.addChangeListener(new ChangeListener(){
		// @Override
		// public void stateChanged(ChangeEvent e) {
		// float val = (minScaleS.getValue()-400)/100f;
		// minScale = (float) Math.pow(10, val);
		// setTime(time);
		// identifierB.setText("Minimum Scale: 10^"+val+" or "+
		// (float) Math.pow(10, val));
		// }
		// });
		// gui.add(identifierB);
	}

	@Override
	public void pass(Frame f) {
		Channel c = f.getSubChannel(channel);
		if(c == null){
			return;
		}
		Channel thisFrame = dat.pollFirst();
		//swap channel data, if the frame lengths differ, just send an empty one
		float[][] val;
		int l;
		if(thisFrame.getRaw()[0].length != (l = c.getRaw()[0].length)){
			val = new float[2][l];
			thisFrame.setRaw(c.getRaw());
			c.setRaw(val);
			if(c.isDecomposed()){
				double[][] mag = new double[2][l];
				thisFrame.setMagnitudes(c.getMagnitudes());
				c.setMagnitudes(mag);
			}
			dat.addLast(thisFrame);
		}else{
			val = thisFrame.getRaw();
			thisFrame.setRaw(c.getRaw());
			c.setRaw(val);
			if(c.isDecomposed()){
				double[][] mag = thisFrame.getMagnitudes();
				thisFrame.setMagnitudes(c.getMagnitudes());
				c.setMagnitudes(mag);
			}
			dat.addLast(thisFrame);
		}
	}

	@Override
	public String getName() {
		return "Delay";
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
	public EffectPanel getGUI() {
		return gui;
	}

	@Override
	public String saveToString() {
		return channel.replaceAll(":", "")+":"+time;
	}

	@Override
	public AudioEffect fromString(String s) {
		String[] val = s.split(":");
		return new Delay(Float.parseFloat(val[1]),val[0]);
	}
}
