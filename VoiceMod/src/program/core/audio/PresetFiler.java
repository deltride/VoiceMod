package program.core.audio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import program.core.gui.EffectMenu;
import program.core.gui.EffectMenu.EffectNode;

public class PresetFiler {
	public static void saveToFile(String name, EffectMenu instance){
		File f = new File("saves/"+name+".vm");
		BufferedWriter bw = null;
		try{
			if(f.exists()){
				f.delete();
			}else{
				new File(f.getParent()).mkdirs();
			}
			f.createNewFile();
			FileWriter fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			bw.write("FFTPRE\n");
			TreeModel tree = instance.getEffectPreFft().getModel();
			DefaultMutableTreeNode node = instance.getPreFftNode();
			for(int i = 0 ; i < tree.getChildCount(node); i++){
				Object n = tree.getChild(node, i);
				if(n instanceof EffectNode){
					AudioEffect e = ((EffectNode) n).getEffect();
					bw.write((e.getClass()+" :|: "+e.saveToString()+"\n").replaceAll("class ", ""));
				}
			}
			bw.write("FFT\n");
			tree = instance.getEffectFft().getModel();
			node = instance.getFftNode();
			for(int i = 0 ; i < tree.getChildCount(node); i++){
				Object n = tree.getChild(node, i);
				if(n instanceof EffectNode){
					AudioEffect e = ((EffectNode) n).getEffect();
					bw.write((e.getClass()+" :|: "+e.saveToString()+"\n").replaceAll("class ", ""));
				}
			}
			bw.write("FFTPOST\n");
			tree = instance.getEffectPostFft().getModel();
			node = instance.getPostFftNode();
			for(int i = 0 ; i < tree.getChildCount(node); i++){
				Object n = tree.getChild(node, i);
				if(n instanceof EffectNode){
					AudioEffect e = ((EffectNode) n).getEffect();
					bw.write((e.getClass()+" :|: "+e.saveToString()+"\n").replaceAll("class ", ""));
				}
			}
		}catch(Exception e){}finally{
			if(bw != null){
				try {
					bw.close();
				} catch (IOException e) {}
			}
		}
	}
	public static List<String> getPresets(){
		File dir = new File("saves");
		List<String> list = new ArrayList<String>();
		if(!dir.exists()){
			return list;
		}
		File[] c = dir.listFiles();
		for(File f : c){
			String s = f.getName();
			if(s.endsWith(".vm"))
				list.add(s.replaceAll(".vm", ""));
		}
		return list;
	}
	public static void loadFile(String name, EffectMenu instance){
		BufferedReader reader = null;
		try{
			File f = new File("saves/"+name+".vm");
			reader = new BufferedReader(new FileReader(f));
			String line;
			DefaultMutableTreeNode node = instance.getPostFftNode();
			DefaultTreeModel tree = (DefaultTreeModel) instance.getEffectPostFft().getModel();
			node.removeAllChildren();
			node = instance.getFftNode();
			tree.reload();
			tree = (DefaultTreeModel) instance.getEffectFft().getModel();
			node.removeAllChildren();
			tree.reload();
			node = instance.getPreFftNode();
			tree = (DefaultTreeModel) instance.getEffectPreFft().getModel();
			node.removeAllChildren();
			tree.reload();
			while((line = reader.readLine()) != null){
				if(line.equals("FFT")){
					node = instance.getFftNode();
					tree = (DefaultTreeModel) instance.getEffectFft().getModel();
				}else if(line.equals("FFTPOST")){
					node = instance.getPostFftNode();
					tree = (DefaultTreeModel) instance.getEffectPostFft().getModel();
				}else if(line.contains(":|:")){
					String[] val = line.split(" :|: ");
					try{
					@SuppressWarnings("unchecked")
					Constructor<? extends AudioEffect> inst = ((Class<? extends AudioEffect>)Class.forName(val[0])).getConstructor();
					AudioEffect effect = inst.newInstance().fromString(val[2]);
					node.add(new EffectNode(effect));
					tree.reload();
					}catch(Exception e){e.printStackTrace();}
				}
			}
		}catch(Exception e){e.printStackTrace();}finally{
			if(reader != null)
				try {
					reader.close();
				} catch (IOException e) {}
		}
	}
}
