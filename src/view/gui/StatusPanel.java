package view.gui;

import controller.SolutionResult;
import java.awt.*;
import javax.swing.*;

public class StatusPanel  extends JPanel{
    private JLabel statusLabel;
    private JLabel timeLabel;
    private JLabel nodesLabel;
    private JLabel movesLabel;

    public StatusPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT)); // Contoh layout
        // Inisialisasi JLabel akan ditambahkan di sini
        add(new JLabel("Status: Idle")); // Placeholder
    }

    public void updateStatus(String status) {

    }

    public void updateStatistics(SolutionResult result) {

    }

    public void clear() {
        
    }
}
