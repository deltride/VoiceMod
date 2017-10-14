package program.core.audio.effect;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import program.core.audio.AudioEffect;
import program.core.audio.Channel;
import program.core.audio.Frame;
import program.core.gui.ToggleButton;

public class Harmonizer implements AudioEffect{
	private static final float[] RATIOS = {16f/15, 9f/8, 6f/5, 5f/4, 4f/3, 45f/32, 3f/2, 8f/5, 5f/3, 16f/9, 15f/8, 2};
	private boolean[] harmonies;
	private ToggleButton[] b;
	private String channel;
	private EffectPanel gui;
	public Harmonizer(boolean[] harmonies, String channel){
		this.harmonies = harmonies;
		this.channel = channel;
		genGui();
	}
	public Harmonizer(){
		this(new boolean[12],"main");
	}
	private void genGui(){
		b = new ToggleButton[12];
		gui = AudioEffect.super.getGUI();
		b[0] = new ToggleButton("m2", "m2", Color.GRAY, Color.DARK_GRAY){
			private static final long serialVersionUID = 1L;
			@Override
			protected void toggleOn() {
				harmonies[0] = true;
			}
			@Override
			protected void toggleOff() {
				harmonies[0] = false;
			}};
		b[0].setBounds(1,50,60,40);
		b[0].setToggleState(harmonies[0]);
		gui.add(b[0]);
		
		b[1] = new ToggleButton("M2", "M2", Color.GRAY, Color.DARK_GRAY){
			private static final long serialVersionUID = 1L;
			@Override
			protected void toggleOn() {
				harmonies[1] = true;
			}
			@Override
			protected void toggleOff() {
				harmonies[1] = false;
			}};
		b[1].setBounds(61,50,60,40);
		b[1].setToggleState(harmonies[1]);
		gui.add(b[1]);
		
		b[2] = new ToggleButton("m3", "m3", Color.GRAY, Color.DARK_GRAY){
			private static final long serialVersionUID = 1L;
			@Override
			protected void toggleOn() {
				harmonies[2] = true;
			}
			@Override
			protected void toggleOff() {
				harmonies[2] = false;
			}};
		b[2].setBounds(121,50,60,40);
		b[2].setToggleState(harmonies[2]);
		gui.add(b[2]);
		
		b[3] = new ToggleButton("M3", "M3", Color.GRAY, Color.DARK_GRAY){
			private static final long serialVersionUID = 1L;
			@Override
			protected void toggleOn() {
				harmonies[3] = true;
			}
			@Override
			protected void toggleOff() {
				harmonies[3] = false;
			}};
		b[3].setBounds(181,50,60,40);
		b[3].setToggleState(harmonies[3]);
		gui.add(b[3]);
		
		b[4] = new ToggleButton("P4", "P4", Color.GRAY, Color.DARK_GRAY){
			private static final long serialVersionUID = 1L;
			@Override
			protected void toggleOn() {
				harmonies[4] = true;
			}
			@Override
			protected void toggleOff() {
				harmonies[4] = false;
			}};
		b[4].setBounds(1,90,60,40);
		b[4].setToggleState(harmonies[4]);
		gui.add(b[4]);
		
		b[5] = new ToggleButton("TT", "TT", Color.GRAY, Color.DARK_GRAY){
			private static final long serialVersionUID = 1L;
			@Override
			protected void toggleOn() {
				harmonies[5] = true;
			}
			@Override
			protected void toggleOff() {
				harmonies[5] = false;
			}};
		b[5].setBounds(61,90,60,40);
		b[5].setToggleState(harmonies[5]);
		gui.add(b[5]);
		
		b[6] = new ToggleButton("P5", "P5", Color.GRAY, Color.DARK_GRAY){
			private static final long serialVersionUID = 1L;
			@Override
			protected void toggleOn() {
				harmonies[6] = true;
			}
			@Override
			protected void toggleOff() {
				harmonies[6] = false;
			}};
		b[6].setBounds(121,90,60,40);
		b[6].setToggleState(harmonies[6]);
		gui.add(b[6]);
		
		b[7] = new ToggleButton("m6", "m6", Color.GRAY, Color.DARK_GRAY){
			private static final long serialVersionUID = 1L;
			@Override
			protected void toggleOn() {
				harmonies[7] = true;
			}
			@Override
			protected void toggleOff() {
				harmonies[7] = false;
			}};
		b[7].setBounds(181,90,60,40);
		b[7].setToggleState(harmonies[7]);
		gui.add(b[7]);
		
		b[8] = new ToggleButton("M6", "M6", Color.GRAY, Color.DARK_GRAY){
			private static final long serialVersionUID = 1L;
			@Override
			protected void toggleOn() {
				harmonies[8] = true;
			}
			@Override
			protected void toggleOff() {
				harmonies[8] = false;
			}};
		b[8].setBounds(1,130,60,40);
		b[8].setToggleState(harmonies[8]);
		gui.add(b[8]);
		
		b[9] = new ToggleButton("m7", "m7", Color.GRAY, Color.DARK_GRAY){
			private static final long serialVersionUID = 1L;
			@Override
			protected void toggleOn() {
				harmonies[9] = true;
			}
			@Override
			protected void toggleOff() {
				harmonies[9] = false;
			}};
		b[9].setBounds(61,130,60,40);
		b[9].setToggleState(harmonies[9]);
		gui.add(b[9]);
		
		b[10] = new ToggleButton("M7", "M7", Color.GRAY, Color.DARK_GRAY){
			private static final long serialVersionUID = 1L;
			@Override
			protected void toggleOn() {
				harmonies[10] = true;
			}
			@Override
			protected void toggleOff() {
				harmonies[10] = false;
			}};
		b[10].setBounds(121,130,60,40);
		b[10].setToggleState(harmonies[10]);
		gui.add(b[10]);
		
		b[11] = new ToggleButton("8ve", "8ve", Color.GRAY, Color.DARK_GRAY){
			private static final long serialVersionUID = 1L;
			@Override
			protected void toggleOn() {
				harmonies[11] = true;
			}
			@Override
			protected void toggleOff() {
				harmonies[11] = false;
			}};
		b[11].setBounds(181,130,60,40);
		b[11].setToggleState(harmonies[11]);
		gui.add(b[11]);
		
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
		JButton preset = new JButton("Preset chord");
		preset.setBounds(241, 50, 60, 120);
		JPopupMenu menu = new JPopupMenu("Chord");
		{
			menu.add(genChordItem(CHORD[0],"Major"));
			menu.add(genChordItem(CHORD[1],"Minor"));
			menu.add(genChordItem(CHORD[2],"Diminished"));
			menu.add(genChordItem(CHORD[3],"Augmented"));
			menu.add(genChordItem(CHORD[4],"Sus4"));
			menu.add(genChordItem(CHORD[5],"Sus2"));
			menu.add(genChordItem(CHORD[6],"Major Add4"));
			menu.add(genChordItem(CHORD[7],"Major Add2"));
			
			menu.add(genChordItem(CHORD[8],"Major 1st inversion"));
			menu.add(genChordItem(CHORD[9],"Minor 1st inversion"));
			menu.add(genChordItem(CHORD[10],"Major 2nd inversion"));
			menu.add(genChordItem(CHORD[11],"Minor 2nd inversion"));
			
			menu.add(genChordItem(CHORD[12],"Major 7th"));
			menu.add(genChordItem(CHORD[13],"Dominant 7th"));
			menu.add(genChordItem(CHORD[14],"Minor 7th"));
			menu.add(genChordItem(CHORD[15],"Half-Diminished 7th"));
			menu.add(genChordItem(CHORD[16],"Fully-Diminished 7th"));
		}
		preset.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				menu.show(gui, e.getX(), e.getY());
			}});
		gui.add(preset);
	}
	@Override
	public void pass(Frame f) {
		Channel c = f.getSubChannel(channel);
		if(c == null)
			return;
		c.decompose();
		int scale = 1;
		for(boolean b : harmonies)
			if(b)
				scale++;
		double[][] mag = c.getMagnitudes();
		int period = mag[0].length/2;
		double[][] newMag = new double[2][mag[0].length];
		System.arraycopy(mag[0], 0, newMag[0], 0, period);
		System.arraycopy(mag[1], 0, newMag[1], 0, period);
		for(int i = 1 ; i < period ; i++){
			double src;
			for(int j = 0 ; j < 12 ; j++){
				if(harmonies[j]){
					src = i/RATIOS[j];
					int srcA = (int) Math.floor(src);
					int srcB = srcA+1;
					src-=srcA;
					if(srcB < period){
						newMag[0][i] += mag[0][srcA]*(1-src) + mag[0][srcB]*src;
						newMag[1][i] += mag[1][srcA]*(1-src) + mag[1][srcB]*src;
					}
				}
			}
		}
		for(int i = 0 ; i < period ; i++){
			c.setMagnitude(0, i, newMag[0][i]/scale);
			c.setMagnitude(1, i, newMag[1][i]/scale);
		}
	}

	@Override
	public String getName() {
		return "Harmonizer";
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
		String v = "";
		for(int i = 0 ; i < 12 ; i++){
			if(harmonies[i]){
				v+="1";
			}else{
				v+="0";
			}
		}
		return channel.replaceAll(":", "")+":"+v;
	}
	@Override
	public AudioEffect fromString(String s) {
		String[] val = s.split(":");
		char[] c = val[1].toCharArray();
		boolean[] h = new boolean[12];
		for(int i = 0 ; i < 12 ; i++){
			if(c[i] == ('1')){
				h[i] = true;
			}
		}
		return new Harmonizer(h,val[0]);
	}
	private static final boolean[][] CHORD = {
			{false,false,false,true,false,false,true,false,false,false,false,false},//M
			{false,false,true,false,false,false,true,false,false,false,false,false},//m
			{false,false,true,false,false,true,false,false,false,false,false,false},//dim
			{false,false,false,true,false,false,false,true,false,false,false,false},//aug
			{false,false,false,false,true,false,true,false,false,false,false,false},//sus4
			{false,true,false,false,false,false,true,false,false,false,false,false},//sus2
			{false,false,false,true,true,false,true,false,false,false,false,false},//add4
			{false,true,false,true,false,false,true,false,false,false,false,false},//add2

			{false,false,true,false,false,false,false,true,false,false,false,false},//M6
			{false,false,false,true,false,false,false,false,true,false,false,false},//m6
			{false,false,false,false,true,false,false,false,true,false,false,false},//M64
			{false,false,false,false,true,false,false,true,false,false,false,false},//m64
			
			{false,false,false,true,false,false,true,false,false,false,true,false},//M7
			{false,false,false,true,false,false,true,false,false,true,false,false},//Mm7
			{false,false,true,false,false,false,true,false,false,true,false,false},//m7
			{false,false,true,false,false,true,false,false,false,true,false,false},//halfdim7
			{false,false,true,false,false,true,false,false,true,false,false,false}//fulldim7
			};
	//		  m2    M2    m3     M3   P4    TT    P5    m6    M6    m7    M7   8ve
	private JMenuItem genChordItem(final boolean[] preset, String name){
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i = 0 ; i < 12 ; i++){
					b[i].setToggleState(preset[i]);
				}
			}});
		return item;
	}
}
