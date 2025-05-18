package view.gui;

import javax.swing.*;
import java.awt.*;

import model.core.Board;
import controller.SolutionResult;
import controller.solver.RushHourSolver;

public class RushHourGUI {
    private JFrame frame;
    private BoardPanel boardPanel;
    private ControlPanel controlPanel;
    private StatusPanel statusPanel;
    private Board currentBoard;
    private SolutionResult currentSolution;
    private Timer animationTimer;

    public RushHourGUI() {
        initialize();
    }

    public void initialize() {
        // Membuat frame utama
        frame = new JFrame("Rush Hour Puzzle Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout()); // Menggunakan BorderLayout

        // Inisialisasi Panel-panel
        // Untuk saat ini, kita asumsikan BoardPanel, ControlPanel, dan StatusPanel
        // memiliki konstruktor default atau konstruktor yang sesuai.
        // Parameter cellSize untuk BoardPanel bisa disesuaikan.
        boardPanel = new BoardPanel(50); // Misal cellSize = 50
        controlPanel = new ControlPanel(this); // ControlPanel mungkin memerlukan referensi ke RushHourGUI
        statusPanel = new StatusPanel();

        // Menambahkan panel-panel ke frame dengan posisi yang ditentukan
        // ControlPanel di bagian atas (NORTH)
        frame.add(controlPanel, BorderLayout.NORTH);
        // BoardPanel di bagian tengah (CENTER)
        frame.add(boardPanel, BorderLayout.CENTER);
        // StatusPanel di bagian bawah (SOUTH)
        frame.add(statusPanel, BorderLayout.SOUTH);

        // Mengatur ukuran preferensi frame berdasarkan komponen di dalamnya
        frame.pack(); // Ukuran frame akan disesuaikan dengan kontennya
        frame.setMinimumSize(new Dimension(600, 400)); // Atur ukuran minimum jika perlu
        frame.setLocationRelativeTo(null); // Menampilkan frame di tengah layar
        frame.setVisible(true); // Menampilkan frame
    }

    public boolean loadBoard(String filePath) {
        return true;
    }

    public void solvePuzzle(RushHourSolver solver) {

    }

    public void displaySolution(SolutionResult result) {

    }

    public void animateSolution() {
        
    }

    public void showStatistics(SolutionResult result) {

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RushHourGUI();
            }
        });
    }
}
