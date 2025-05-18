package view.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {
    // Parameterisasi JComboBox dengan String, asumsikan itemnya berupa String
    private JComboBox<String> algorithmSelector;
    private JComboBox<String> heuristicSelector;
    private JButton loadButton;
    private JButton solveButton;
    private JButton animateButton;
    private JSlider animationSpeedSlider;
    private final RushHourGUI parent; // Jadikan parent final

    public ControlPanel(RushHourGUI parent) {
        this.parent = parent; // parent sekarang digunakan
        setLayout(new FlowLayout()); // Contoh layout sederhana
        initializeComponents();
        addListeners(); // Tambahkan pemanggil untuk listener
    }

    public void initializeComponents() {
        // Inisialisasi JComboBox
        // Contoh item, sesuaikan dengan kebutuhan aplikasi Anda
        String[] algorithms = {"UCS", "A*", "Greedy BFS", "Beam Search"};
        algorithmSelector = new JComboBox<>(algorithms);

        String[] heuristics = {"Manhattan Distance", "Blocking Pieces", "Combined"};
        heuristicSelector = new JComboBox<>(heuristics);
        // Awalnya, heuristicSelector mungkin dinonaktifkan sampai algoritma yang memerlukan heuristik dipilih
        heuristicSelector.setEnabled(false);

        // Inisialisasi JButton
        loadButton = new JButton("Load Board");
        solveButton = new JButton("Solve");
        animateButton = new JButton("Animate");
        animateButton.setEnabled(false); // Awalnya dinonaktifkan sampai ada solusi

        // Inisialisasi JSlider
        animationSpeedSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50); // min, max, initial value
        animationSpeedSlider.setMajorTickSpacing(25);
        animationSpeedSlider.setMinorTickSpacing(5);
        animationSpeedSlider.setPaintTicks(true);
        animationSpeedSlider.setPaintLabels(true);
        animationSpeedSlider.setEnabled(false); // Awalnya dinonaktifkan

        // Menambahkan komponen ke panel
        add(new JLabel("Algorithm:"));
        add(algorithmSelector);
        add(new JLabel("Heuristic:"));
        add(heuristicSelector);
        add(loadButton);
        add(solveButton);
        add(animateButton);
        add(new JLabel("Speed:"));
        add(animationSpeedSlider);
    }

    private void addListeners() {
        // Contoh sederhana bagaimana listener bisa ditambahkan
        // Ini akan membuat field-field tersebut "digunakan"
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Panggil metode di parent (RushHourGUI) untuk memuat board
                // Contoh: parent.loadBoardFromFile();
                // Karena parent sudah digunakan di konstruktor, warning "never read" untuk parent hilang
                if (ControlPanel.this.parent != null) {
                    // Untuk menghilangkan warning "parent is never read" jika belum ada pemanggilan lain
                    // Anda akan mengganti ini dengan logika sebenarnya
                    System.out.println("Load button clicked, parent: " + ControlPanel.this.parent.getClass().getSimpleName());
                    // parent.handleLoadBoard(); // Metode contoh di RushHourGUI
                }
            }
        });

        solveButton.addActionListener(e -> {
            // Logika untuk tombol solve
            // Contoh: parent.startSolving(getSelectedAlgorithm(), getSelectedHeuristic());
            System.out.println("Solve button clicked");
            // enableAnimationControls(true); // Setelah solusi ditemukan
        });

        animateButton.addActionListener(e -> {
            // Logika untuk tombol animate
            // Contoh: parent.startAnimation(getAnimationSpeed());
            System.out.println("Animate button clicked");
        });

        algorithmSelector.addActionListener(e -> {
            String selectedAlgorithm = getSelectedAlgorithm();
            // Aktifkan/Nonaktifkan heuristicSelector berdasarkan algoritma yang dipilih
            if ("A*".equals(selectedAlgorithm) || "Greedy BFS".equals(selectedAlgorithm) || "Beam Search".equals(selectedAlgorithm)) {
                heuristicSelector.setEnabled(true);
            } else {
                heuristicSelector.setEnabled(false);
            }
            System.out.println("Algorithm selected: " + selectedAlgorithm);
        });

        heuristicSelector.addActionListener(e -> {
            System.out.println("Heuristic selected: " + getSelectedHeuristic());
        });

        animationSpeedSlider.addChangeListener(e -> {
            // Jika tidak dalam proses penyesuaian oleh user (user sudah melepas slider)
            if (!animationSpeedSlider.getValueIsAdjusting()) {
                System.out.println("Animation speed: " + getAnimationSpeed());
                // Contoh: if (parent.isAnimating()) { parent.setAnimationSpeed(getAnimationSpeed()); }
            }
        });
    }

    public void enableSolveControls(boolean enable) {
        solveButton.setEnabled(enable);
        algorithmSelector.setEnabled(enable);
        // Hanya aktifkan heuristic selector jika algoritma yang dipilih membutuhkannya
        if (enable && ("A*".equals(getSelectedAlgorithm()) || "Greedy BFS".equals(getSelectedAlgorithm()) || "Beam Search".equals(getSelectedAlgorithm()))) {
            heuristicSelector.setEnabled(true);
        } else if (!enable) {
            heuristicSelector.setEnabled(false);
        }
    }

    public void enableAnimationControls(boolean enable) {
        animateButton.setEnabled(enable);
        animationSpeedSlider.setEnabled(enable);
    }

    public String getSelectedAlgorithm() {
        if (algorithmSelector.getSelectedItem() != null) {
            return (String) algorithmSelector.getSelectedItem();
        }
        return null; // atau algoritma default
    }

    public String getSelectedHeuristic() {
        if (heuristicSelector.getSelectedItem() != null && heuristicSelector.isEnabled()) {
            return (String) heuristicSelector.getSelectedItem();
        }
        return null; // atau heuristik default jika diperlukan
    }

    public int getAnimationSpeed() {
        return animationSpeedSlider.getValue();
    }
}