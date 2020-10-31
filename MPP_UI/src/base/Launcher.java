package base;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
* Launcher
* <p>
* The launcher contains the main routine, which starts the MainWindow
*/
public class Launcher {
	/**
	* this method is the main and starts the MainWindow
	* @param args arguments
	*/
	public static void main(String[] args) {
		try {
			 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {	e.printStackTrace(); }
		
		MainWindow window = new MainWindow();
	}

}
