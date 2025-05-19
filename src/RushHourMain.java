// import controller.SolutionResult;
// import controller.heuristic.BlockingPiecesHeuristic;
// import controller.heuristic.Heuristic;
// import controller.heuristic.ManhattanDistance;
// import controller.solver.AStarSolver;
// import controller.solver.GreedyBestFirstSearchSolver;
// import controller.solver.RushHourSolver;
// import controller.solver.UCSolver;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
// import model.GameState;
// import model.core.Board;
// import utils.FileHandler;
// import view.console.ConsoleView;
import view.gui.RushHourGUI;
// import java.util.ArrayList;
// import java.util.Scanner;

public class RushHourMain {
    // private FileHandler fileHandler;
    // private ConsoleView consoleView;
    // private Board currentBoard;
    // private GameState initialState;

    // public RushHourMain() {
    //     fileHandler = new FileHandler();
    //     // consoleView = new ConsoleView();
    // }

    public static void main(String[] args) {
        // Menjalankan GUI
        runGUIMode();

        // RushHourMain mainApp = new RushHourMain();
        // mainApp.runConsoleMode();
    }

    private static void runGUIMode() {
        // Atur Look and Feel untuk tampilan yang lebih modern (opsional)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Gagal mengatur Look and Feel sistem.");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RushHourGUI(); // Membuat instance dan menjalankan GUI
            }
        });
    }
    
    // private void runConsoleMode() {

    // }

    // private void createSolver(String algorithm, GameState initialState, String heuristic) {
        
    // }

    // private Heuristic createHeuristic(String heuristicName) {
    //     return null;
    // }
}
