package view.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ControlPanel extends JPanel {
    private JComboBox<String> algorithmSelector;
    private JComboBox<String> heuristicSelector;
    private JButton loadButton;
    private JButton solveButton;
    private JButton animateButton;
    private JSlider animationSpeedSlider;
    private final RushHourGUI parent;

    public ControlPanel(RushHourGUI parent) {
        this.parent = parent;
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Layout dengan sedikit padding
        initializeComponents();
        addListeners();
    }

    private void initializeComponents() {
        // Inisialisasi JComboBox untuk Algoritma
        String[] algorithms = {"UCS", "A*", "Greedy BFS", "Beam Search (10 beam width)"}; // Sesuaikan dengan solver yang ada
        algorithmSelector = new JComboBox<>(algorithms);
        algorithmSelector.setToolTipText("Pilih algoritma pencarian");

        // Inisialisasi JComboBox untuk Heuristik
        String[] heuristics = {"Manhattan Distance", "Blocking Pieces"}; // Sesuaikan dengan heuristik yang ada
        heuristicSelector = new JComboBox<>(heuristics);
        heuristicSelector.setToolTipText("Pilih heuristik (jika diperlukan oleh algoritma)");
        heuristicSelector.setEnabled(false); // Awalnya nonaktif

        // Inisialisasi JButton
        loadButton = new JButton("Muat Papan");
        loadButton.setToolTipText("Muat konfigurasi papan dari file .txt");

        solveButton = new JButton("Selesaikan");
        solveButton.setToolTipText("Cari solusi untuk papan yang dimuat");
        solveButton.setEnabled(false); // Awalnya nonaktif sampai papan dimuat

        animateButton = new JButton("Animasi");
        animateButton.setToolTipText("Animaskan langkah-langkah solusi");
        animateButton.setEnabled(false); // Awalnya nonaktif sampai solusi ditemukan

        // Inisialisasi JSlider untuk Kecepatan Animasi
        animationSpeedSlider = new JSlider(JSlider.HORIZONTAL, 100, 2000, 500); // min, max (ms), initial (ms)
        animationSpeedSlider.setMajorTickSpacing(500);
        animationSpeedSlider.setMinorTickSpacing(100);
        animationSpeedSlider.setPaintTicks(true);
        animationSpeedSlider.setPaintLabels(true);
        animationSpeedSlider.setToolTipText("Kecepatan animasi (jeda dalam milidetik)");
        animationSpeedSlider.setEnabled(false); // Awalnya nonaktif

        // Menambahkan komponen ke panel
        add(new JLabel("Algoritma:"));
        add(algorithmSelector);
        add(new JLabel("Heuristik:"));
        add(heuristicSelector);
        add(loadButton);
        add(solveButton);
        add(animateButton);
        add(new JLabel("Kecepatan Animasi (ms):"));
        add(animationSpeedSlider);
    }

    private void addListeners() {
        // Listener untuk tombol Muat Papan
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // Atur direktori awal ke direktori kerja proyek atau folder tes spesifik
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                fileChooser.setDialogTitle("Pilih File Papan Rush Hour (.txt)");
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("File Teks (*.txt)", "txt"));
                int returnValue = fileChooser.showOpenDialog(parent.getFrame()); // Gunakan frame utama sebagai parent
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (parent != null) {
                        parent.loadBoard(selectedFile.getAbsolutePath());
                    }
                }
            }
        });

        // Listener untuk tombol Selesaikan
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parent != null) {
                    parent.initiateSolveProcess(); // Panggil metode di RushHourGUI untuk memulai penyelesaian
                }
            }
        });

        // Listener untuk tombol Animasi
        animateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parent != null) {
                    parent.animateSolution(); // Panggil metode di RushHourGUI untuk memulai animasi
                }
            }
        });

        // Listener untuk pemilihan Algoritma
        algorithmSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAlgorithm = getSelectedAlgorithm();
                // Aktifkan/Nonaktifkan pemilihan heuristik berdasarkan algoritma
                if ("A*".equals(selectedAlgorithm) || "Greedy BFS".equals(selectedAlgorithm) || "Beam Search (10 beam width)".equals(selectedAlgorithm)) {
                    heuristicSelector.setEnabled(true);
                } else {
                    heuristicSelector.setEnabled(false);
                    heuristicSelector.setSelectedIndex(-1); // Kosongkan pilihan jika nonaktif
                }
            }
        });

        // Listener untuk JSlider Kecepatan Animasi
        animationSpeedSlider.addChangeListener(e -> {
            // Hanya perbarui kecepatan jika pengguna selesai menyesuaikan slider
            if (!animationSpeedSlider.getValueIsAdjusting() && parent != null) {
                parent.setAnimationSpeed(getAnimationSpeed());
            }
        });
    }

    // Metode untuk mengaktifkan/menonaktifkan tombol Muat Papan
    public void enableLoadButton(boolean enable) {
        loadButton.setEnabled(enable);
    }

    // Metode untuk mengaktifkan/menonaktifkan tombol Selesaikan
    public void enableSolveButton(boolean enable) {
        solveButton.setEnabled(enable);
    }

    // Metode untuk mengaktifkan/menonaktifkan tombol Animasi dan slider kecepatan
    public void enableAnimateButton(boolean enable) {
        animateButton.setEnabled(enable);
        animationSpeedSlider.setEnabled(enable);
    }

    // Mendapatkan algoritma yang dipilih
    public String getSelectedAlgorithm() {
        if (algorithmSelector.getSelectedItem() != null) {
            return (String) algorithmSelector.getSelectedItem();
        }
        return null;
    }

    // Mendapatkan heuristik yang dipilih
    public String getSelectedHeuristic() {
        if (heuristicSelector.getSelectedItem() != null && heuristicSelector.isEnabled()) {
            return (String) heuristicSelector.getSelectedItem();
        }
        return null;
    }

    // Mendapatkan kecepatan animasi dari slider
    public int getAnimationSpeed() {
        return animationSpeedSlider.getValue();
    }

    // Memeriksa apakah pemilihan heuristik diaktifkan
    public boolean isHeuristicEnabled() {
        return heuristicSelector.isEnabled();
    }
}