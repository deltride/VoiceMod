package program.core;

import program.core.gui.ModulatorGui;

public class Main {
	public static ModulatorGui gui;
	public static void main(String args[]){
		gui = new ModulatorGui(1000,(int) (1000 * .61f));
		while(!gui.closeRequested){
			gui.update();
		}
		System.exit(0);
	}
}
