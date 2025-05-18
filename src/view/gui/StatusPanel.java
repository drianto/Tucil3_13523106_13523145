package view.gui;

import controller.SolutionResult;
import java.awt.*;
import javax.swing.*;

public class StatusPanel extends JPanel {
    private final JLabel statusLabel;
    private final JLabel timeLabel;
    private final JLabel nodesLabel;
    private final JLabel movesLabel;

    public StatusPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT)); // Contoh layout, bisa disesuaikan

        // Inisialisasi JLabel
        statusLabel = new JLabel("Status: Idle");
        timeLabel = new JLabel("Time: - ms");
        nodesLabel = new JLabel("Nodes Visited: -");
        movesLabel = new JLabel("Moves: -");

        // Menambahkan JLabel ke panel
        // Dengan menambahkan ke panel dan memodifikasinya di metode lain,
        // field ini sekarang "digunakan".
        add(statusLabel);
        add(new JSeparator(SwingConstants.VERTICAL)); // Pemisah visual opsional
        add(timeLabel);
        add(new JSeparator(SwingConstants.VERTICAL));
        add(nodesLabel);
        add(new JSeparator(SwingConstants.VERTICAL));
        add(movesLabel);
    }

    public void updateStatus(String status) {
        // Sekarang statusLabel digunakan
        if (status != null && !status.isEmpty()) {
            statusLabel.setText("Status: " + status);
        } else {
            statusLabel.setText("Status: Idle");
        }
    }

    public void updateStatistics(SolutionResult result) {
        // Sekarang field label lainnya digunakan
        if (result != null) {
            if (result.isSolved()) {
                updateStatus("Solved!"); // Update status juga jika ada hasil
                timeLabel.setText("Time: " + result.getExecutionTime() + " ms");
                nodesLabel.setText("Nodes Visited: " + result.getVisitedNodesCount());
                if (result.getMoves() != null) {
                    movesLabel.setText("Moves: " + result.getMoves().size());
                } else {
                    movesLabel.setText("Moves: -");
                }
            } else {
                updateStatus("Not Solved / No Solution Found.");
                timeLabel.setText("Time: " + result.getExecutionTime() + " ms");
                nodesLabel.setText("Nodes Visited: " + result.getVisitedNodesCount());
                movesLabel.setText("Moves: -");
            }
        } else {
            // Reset ke default jika result null
            clear();
        }
    }

    public void clear() {
        updateStatus("Idle"); // Atau status default lainnya
        timeLabel.setText("Time: - ms");
        nodesLabel.setText("Nodes Visited: -");
        movesLabel.setText("Moves: -");
    }
}