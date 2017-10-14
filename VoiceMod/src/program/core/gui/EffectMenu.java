package program.core.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import program.core.Main;
import program.core.audio.AudioEffect;
import program.core.audio.AudioEffect.EffectPanel;
import program.core.audio.PresetFiler;
import program.core.audio.effect.ChannelListener;
import program.core.audio.effect.Delay;
import program.core.audio.effect.Echo;
import program.core.audio.effect.Harmonizer;
import program.core.audio.effect.LowPass;
import program.core.audio.effect.ReverseEcho;
import program.core.audio.effect.Shift;
import program.core.audio.effect.VolumeControl;
import program.core.audio.effect.WaveformSynthesizer;

public class EffectMenu {
	private static List<Class<? extends AudioEffect>> audioClasses = genClassList();
	
	private DefaultMutableTreeNode fftNode, preFftNode, postFftNode;
	private JTree effectPreFft, effectPostFft, effectFft;
	private TreeSelectionModel selectPreFft, selectPostFft, selectFft;
	private JScrollPane pipelinePreFft, pipelinePostFft, pipelineFft;
	private JTabbedPane pipeline;
	private EffectPanel activePanel;
	private EffectNode activeNode;
	private JTextField saveName;
	private JMenu addEffectMenu;
	private JPanel pipelineHandlerMenu;
	
	public EffectMenu(){
		fftNode = new DefaultMutableTreeNode("Within FFT");
		preFftNode = new DefaultMutableTreeNode("Before FFT");
		postFftNode = new DefaultMutableTreeNode("After FFT");
		activeNode = null;
		effectPreFft = new JTree(preFftNode);
		selectPreFft = effectPreFft.getSelectionModel();
		selectPreFft.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		effectPreFft.setRootVisible(false);
		effectPreFft.addTreeSelectionListener(new TreeSelectionListener(){
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) effectPreFft.getLastSelectedPathComponent();
				EffectPanel panel;
				if(node instanceof EffectNode && activePanel != (panel = ((EffectNode)node).getEffect().getGUI())){
					Main.gui.getFrame().remove(activePanel);
					activePanel.invalidate();
					Main.gui.getFrame().add(panel);
					panel.revalidate();
					Main.gui.getFrame().repaint();
					activeNode = (EffectNode) node;
					activePanel = panel;
				}
			}
		});
		pipelinePreFft = new JScrollPane(effectPreFft);
		
		effectFft = new JTree(fftNode);
		selectFft = effectFft.getSelectionModel();
		selectFft.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		effectFft.setRootVisible(false);
		effectFft.addTreeSelectionListener(new TreeSelectionListener(){
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) effectFft.getLastSelectedPathComponent();
				EffectPanel panel;
				if(node instanceof EffectNode && activePanel != (panel = ((EffectNode)node).getEffect().getGUI())){
					Main.gui.getFrame().remove(activePanel);
					activePanel.invalidate();
					Main.gui.getFrame().add(panel);
					panel.revalidate();
					Main.gui.getFrame().repaint();
					activeNode = (EffectNode) node;
					activePanel = panel;
				}
			}
		});
		pipelineFft = new JScrollPane(effectFft);
		
		effectPostFft = new JTree(postFftNode);
		selectPostFft = effectPostFft.getSelectionModel();
		selectPostFft.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		effectPostFft.setRootVisible(false);
		effectPostFft.addTreeSelectionListener(new TreeSelectionListener(){
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) effectPostFft.getLastSelectedPathComponent();
				EffectPanel panel;
				if(node instanceof EffectNode && activePanel != (panel = ((EffectNode)node).getEffect().getGUI())){
					Main.gui.getFrame().remove(activePanel);
					activePanel.invalidate();
					Main.gui.getFrame().add(panel);
					panel.revalidate();
					Main.gui.getFrame().repaint();
					activeNode = (EffectNode) node;
					activePanel = panel;
				}
			}
		});
		pipelinePostFft = new JScrollPane(effectPostFft);
		
		pipeline = new JTabbedPane();
		pipeline.setBounds(300,6,182,500);
		pipeline.addTab("Pre-FFT", pipelinePreFft);
		pipeline.addTab("FFT", pipelineFft);
		pipeline.addTab("Post-FFT", pipelinePostFft);
		pipeline.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				updateAddMenu();
				int sel = pipeline.getSelectedIndex();
				if(sel != -1){
					DefaultMutableTreeNode node;
					switch(sel){
					case 0:
						node = (DefaultMutableTreeNode) effectPreFft.getLastSelectedPathComponent();
						break;
					case 1:
						node = (DefaultMutableTreeNode) effectFft.getLastSelectedPathComponent();
						break;
					case 2:
						node = (DefaultMutableTreeNode) effectPostFft.getLastSelectedPathComponent();
						break;
					default:
						return;
				}
					EffectPanel panel;
					if(node instanceof EffectNode && activePanel != (panel = ((EffectNode)node).getEffect().getGUI())){
						Main.gui.getFrame().remove(activePanel);
						activePanel.invalidate();
						Main.gui.getFrame().add(panel);
						panel.revalidate();
						Main.gui.getFrame().repaint();
						activeNode = (EffectNode) node;
						activePanel = panel;
					}
				}
			}});
		activePanel = new EffectPanel();
		
		pipelineHandlerMenu = new JPanel(null);
		pipelineHandlerMenu.setBounds(482,300,538,209);
		pipelineHandlerMenu.setBorder(BorderFactory.createTitledBorder(null, "Pipeline", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
		final JMenu load = new JMenu("Load Preset");
		{
			saveName = new JTextField();
			saveName.setBounds(10, 160, 300, 25);
			pipelineHandlerMenu.add(saveName);
			JButton save = new JButton("Save");
			save.setBounds(310,160,100,25);
			save.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					PresetFiler.saveToFile(saveName.getText(),Main.gui.getAudioCoder().getEffectMenu());
				}});
			pipelineHandlerMenu.add(save);
			JButton reload = new JButton("Reload Directory");
			reload.setBounds(210,50,120,25);
			reload.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					load.removeAll();
					for(String s : PresetFiler.getPresets()){
						JMenuItem item = new JMenuItem(s);
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e) {
								PresetFiler.loadFile(s, Main.gui.getAudioCoder().getEffectMenu());
							}});
						load.add(item);
					}
				}});
			pipelineHandlerMenu.add(reload);
		}
		
		
		JMenuBar effectBar = new JMenuBar();
		addEffectMenu = new JMenu("Add Effect");
		effectBar.add(addEffectMenu);
		effectBar.setBounds(0, 20, 300, 30);
		pipelineHandlerMenu.add(effectBar);
		updateAddMenu();
		
		JMenu menu = new JMenu("Move");
		{
			JMenuItem item = new JMenuItem("Up");
			item.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					DefaultMutableTreeNode pNode = (DefaultMutableTreeNode) activeNode.getParent();
					if(activeNode == null || pNode == null){
						return;
					}
					int index = pNode.getIndex(activeNode);
					if(index > 0){
						DefaultTreeModel model;
						switch(pipeline.getSelectedIndex()){
							case 0:
								model = (DefaultTreeModel) effectPreFft.getModel();
								break;
							case 1:
								model = (DefaultTreeModel) effectFft.getModel();
								break;
							case 2:
								model = (DefaultTreeModel) effectPostFft.getModel();
								break;
							default:
								return;
						}
						DefaultMutableTreeNode rep = (DefaultMutableTreeNode)pNode.getChildAt(index-1);
						model.removeNodeFromParent(rep);
						model.insertNodeInto(rep, pNode, index);
						Main.gui.getFrame().repaint();
					}
					
				}});
			menu.add(item);
			item = new JMenuItem("Down");
			item.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					DefaultMutableTreeNode pNode = (DefaultMutableTreeNode) activeNode.getParent();
					if(activeNode == null || pNode == null){
						return;
					}
					int index = pNode.getIndex(activeNode);
					if(index < pNode.getChildCount()-1){
						DefaultTreeModel model;
						switch(pipeline.getSelectedIndex()){
							case 0:
								model = (DefaultTreeModel) effectPreFft.getModel();
								break;
							case 1:
								model = (DefaultTreeModel) effectFft.getModel();
								break;
							case 2:
								model = (DefaultTreeModel) effectPostFft.getModel();
								break;
							default:
								return;
						}
						model.removeNodeFromParent(activeNode);
						model.insertNodeInto(activeNode, pNode, index+1);
						Main.gui.getFrame().repaint();
					}
					
				}});
			menu.add(item);
		}
		effectBar.add(menu);
		menu = new JMenu("Remove");
		{
			JMenuItem item = new JMenuItem("Remove Selected Item");
			item.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(activeNode == null || activeNode.getParent() == null){
						return;
					}
					DefaultTreeModel model;
					switch(pipeline.getSelectedIndex()){
						case 0:
							model = (DefaultTreeModel) effectPreFft.getModel();
							break;
						case 1:
							model = (DefaultTreeModel) effectFft.getModel();
							break;
						case 2:
							model = (DefaultTreeModel) effectPostFft.getModel();
							break;
						default:
							return;
					}
					Main.gui.getFrame().remove(activePanel);
					activePanel.invalidate();
					activePanel = new EffectPanel();
					Main.gui.getFrame().add(activePanel);
					activePanel.revalidate();
					model.removeNodeFromParent(activeNode);
					Main.gui.getFrame().repaint();
				}});
			menu.add(item);
		}
		effectBar.add(menu);
		{
			for(String s : PresetFiler.getPresets()){
				JMenuItem item = new JMenuItem(s);
				item.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						PresetFiler.loadFile(s, Main.gui.getAudioCoder().getEffectMenu());
					}});
				load.add(item);
			}
		}
		effectBar.add(load);
	}
	public void updateAddMenu(){
		boolean decomposed = pipeline.getSelectedIndex() == 1;
		addEffectMenu.removeAll();
		for(Class<? extends AudioEffect> c : audioClasses){
			try {
				Constructor<? extends AudioEffect> inst = c.getConstructor();
				AudioEffect effect = inst.newInstance();
				if((decomposed && effect.getDecomposedCompatible()) || (effect.getComposedCompatible() && !decomposed)){
					JMenuItem item = new JMenuItem(effect.getName());
					item.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								EffectNode n = new EffectNode(inst.newInstance());
								switch(pipeline.getSelectedIndex()){
									case 0:
										preFftNode.add(n);
										((DefaultTreeModel)effectPreFft.getModel()).reload();
										break;
									case 1:
										fftNode.add(n);
										((DefaultTreeModel)effectFft.getModel()).reload();
										break;
									case 2:
										postFftNode.add(n);
										((DefaultTreeModel)effectPostFft.getModel()).reload();
										break;
									default:
										break;
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					});
					addEffectMenu.add(item);
				}
			} catch (Exception e1) {
				System.err.println("Invalid effect class: "+c.getName()+" Make sure there is a constructor with no inputs.\nError:");
				e1.printStackTrace();
			}
		}
	}
	/**
	 * phase 0: pre-fft
	 * phase 1: during fft
	 * phase 2: post-fft
	 */
	public List<AudioEffect> getEffects(int phase){
		DefaultTreeModel model;
		DefaultMutableTreeNode node;
		switch(phase){
			case 0:
				model = (DefaultTreeModel) effectPreFft.getModel();
				node = preFftNode;
				break;
			case 1:
				model = (DefaultTreeModel) effectFft.getModel();
				node = fftNode;
				break;
			case 2:
				model = (DefaultTreeModel) effectPostFft.getModel();
				node = postFftNode;
				break;
			default:
				return null;
		}
		List<AudioEffect> l = new ArrayList<AudioEffect>();
		for(int i = 0; i < model.getChildCount(node); i++){
			Object child = model.getChild(node, i);
			if(child instanceof EffectNode){
				l.add(((EffectNode) child).getEffect());
			}
		}
		return l;
	}
	public JComponent getPipelineDisplay() {
		return pipeline;
	}
	public EffectPanel getEffectDisplay() {
		return activePanel;
	}
	public JPanel getPipelineHandlerMenu() {
		return pipelineHandlerMenu;
	}
	public DefaultMutableTreeNode getFftNode() {
		return fftNode;
	}
	public DefaultMutableTreeNode getPreFftNode() {
		return preFftNode;
	}
	public DefaultMutableTreeNode getPostFftNode() {
		return postFftNode;
	}
	public JTree getEffectPreFft() {
		return effectPreFft;
	}
	public JTree getEffectPostFft() {
		return effectPostFft;
	}
	public JTree getEffectFft() {
		return effectFft;
	}
	public static class EffectNode extends DefaultMutableTreeNode{
		private static final long serialVersionUID = 1L;
		AudioEffect effect;
		public EffectNode(AudioEffect e){
			super(e.getName());
			effect = e;
		}
		public AudioEffect getEffect() {
			return effect;
		}
	}
	private static List<Class<? extends AudioEffect>> genClassList(){
		List<Class<? extends AudioEffect>> l = new ArrayList<Class<? extends AudioEffect>>();
		l.add(Echo.class);
		l.add(ReverseEcho.class);
		l.add(LowPass.class);
		l.add(Shift.class);
		l.add(VolumeControl.class);
		l.add(WaveformSynthesizer.class);
		l.add(ChannelListener.class);
		l.add(Delay.class);
		l.add(Harmonizer.class);
		return l;
	}
}
