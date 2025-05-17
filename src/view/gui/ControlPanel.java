package view.gui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    private JComboBox algorithmSelector;
    private JComboBox heuristicSelector;
    private JButton loadButton;
    private JButton solveButton;
    private JButton animateButton;
    private JSlider animationSpeedSlider;
    private RushHourGUI parent;

    public ControlPanel(RushHourGUI parent) {
        this.parent = parent;
        setLayout(new FlowLayout()); // Contoh layout sederhana
        // Inisialisasi komponen (JButton, JComboBox, dll.) akan ditambahkan di sini
        add(new JLabel("Control Panel Area")); // Placeholder
    }

    public void initializeComponents() {

    }

    public void enableSolveControls(boolean enable) {

    }

    public void enableAnimationControls(boolean enable) {
        
    }

    public String getSelectedAlgorithm() {
        return null;
    }
    
    public String getSelectedHeuristic() {
        return null;
    }
    
    public int getAnimationSpeed() {
        return 1;
    }
}