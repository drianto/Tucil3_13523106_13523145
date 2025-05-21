import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.gui.RushHourGUI;

public class RushHourMain {
	public static void main(String[] args) {
		runGUIMode();

	}
	private static void runGUIMode() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Gagal mengatur Look and Feel sistem.");
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new RushHourGUI();
			}
		});
	}
}
