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
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5)); // Layout dengan padding
        setBorder(BorderFactory.createEtchedBorder()); // Tambahkan border untuk visual

        // Inisialisasi JLabel dengan teks awal
        statusLabel = new JLabel("Status: Idle");
        statusLabel.setToolTipText("Status terkini dari aplikasi");

        timeLabel = new JLabel("Waktu Eksekusi: - ms");
        timeLabel.setToolTipText("Waktu yang dibutuhkan untuk mencari solusi");

        nodesLabel = new JLabel("Node Dikunjungi: -");
        nodesLabel.setToolTipText("Jumlah state/node yang dieksplorasi oleh solver");

        movesLabel = new JLabel("Langkah: -");
        movesLabel.setToolTipText("Jumlah langkah dalam solusi yang ditemukan");

        // Menambahkan JLabel ke panel
        add(statusLabel);
        add(createVerticalSeparator());
        add(movesLabel);
        add(createVerticalSeparator());
        add(nodesLabel);
        add(createVerticalSeparator());
        add(timeLabel);
    }

    private JSeparator createVerticalSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(2, 20));
        return separator;
    }

    /**
     * Memperbarui label status.
     * @param status Pesan status baru.
     */
    public void updateStatus(String status) {
        if (status != null && !status.isEmpty()) {
            statusLabel.setText("Status: " + status);
        } else {
            statusLabel.setText("Status: Idle");
        }
    }

    /**
     * Memperbarui label statistik berdasarkan SolutionResult.
     * @param result Hasil solusi dari solver.
     */
    public void updateStatistics(SolutionResult result) {
        if (result != null) {
            timeLabel.setText("Waktu Eksekusi: " + result.getExecutionTime() / 1_000_000 + " ms");
            nodesLabel.setText("Node Dikunjungi: " + result.getVisitedNodesCount());
            if (result.isSolved() && result.getMoves() != null) {
                movesLabel.setText("Langkah: " + result.getMoves().size());
            } else if (result.isSolved() && result.getMoves() == null) { // Solved tapi tidak ada langkah (misal, sudah di state akhir)
                 movesLabel.setText("Langkah: 0");
            }
            else {
                movesLabel.setText("Langkah: -");
            }
        } else {
            // Reset ke default jika result null
            clearStatistics();
        }
    }

    /**
     * Mengosongkan semua label statistik dan mengatur status ke Idle.
     */
    public void clearStatistics() {
        updateStatus("Idle"); // Atau status default lainnya
        timeLabel.setText("Waktu Eksekusi: - ms");
        nodesLabel.setText("Node Dikunjungi: -");
        movesLabel.setText("Langkah: -");
    }
}
