package view.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List; // Diperlukan untuk List<Integer> di process()
import java.util.function.Consumer;

import model.core.Board;
import model.GameState;
import model.core.Move; // Diperlukan untuk SolutionResult.getMoves()
import controller.SolutionResult;
import controller.solver.*; // Import semua solver
import controller.heuristic.*; // Import semua heuristic
import utils.FileHandler;

public class RushHourGUI {
    private JFrame frame;
    private BoardPanel boardPanel;
    private ControlPanel controlPanel;
    private StatusPanel statusPanel;

    private Board currentBoard; // Menyimpan papan yang sedang dimuat
    private SolutionResult currentSolution; // Menyimpan solusi saat ini
    private Timer animationTimer;
    private int animationStepIndex;

    private final FileHandler fileHandler; // Instansi dari FileHandler

    public RushHourGUI() {
        fileHandler = new FileHandler(); // Inisialisasi FileHandler
        initialize();
    }

    // Getter untuk frame, berguna untuk dialog di ControlPanel
    public JFrame getFrame() {
        return frame;
    }

    public void initialize() {
        // Membuat frame utama
        frame = new JFrame("Rush Hour Puzzle Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10)); // Menambahkan sedikit jarak antar komponen

        // Inisialisasi Panel-panel
        boardPanel = new BoardPanel(50); // Ukuran sel bisa disesuaikan
        controlPanel = new ControlPanel(this); // ControlPanel memerlukan referensi ke RushHourGUI
        statusPanel = new StatusPanel();

        // Menambahkan padding ke panel untuk estetika
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Menambahkan panel-panel ke frame
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.SOUTH);

        frame.setMinimumSize(new Dimension(650, 600)); // Ukuran minimum frame
        frame.pack(); // Menyesuaikan ukuran frame dengan kontennya
        frame.setLocationRelativeTo(null); // Menampilkan frame di tengah layar
        frame.setVisible(true); // Menampilkan frame
    }

    /**
     * Memuat papan dari path file yang diberikan dan memperbarui GUI.
     * Metode ini dipanggil dari ControlPanel.
     * @param filePath Path absolut ke file konfigurasi papan.
     * @return true jika papan berhasil dimuat, false jika gagal.
     */
    public boolean loadBoard(String filePath) {
        System.out.println("Mencoba memuat papan dari: " + filePath);
        Board loadedBoard = fileHandler.readBoardFromFile(filePath);

        if (loadedBoard != null) {
            this.currentBoard = loadedBoard;
            boardPanel.setBoard(this.currentBoard);
            statusPanel.updateStatus("Papan berhasil dimuat dari: " + new File(filePath).getName());
            statusPanel.clearStatistics(); // Bersihkan statistik solusi sebelumnya
            controlPanel.enableSolveButton(true); // Aktifkan tombol solve
            controlPanel.enableAnimateButton(false); // Nonaktifkan animasi sampai ada solusi
            this.currentSolution = null; // Reset solusi saat ini
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop(); // Hentikan animasi yang sedang berjalan
            }
            System.out.println("Papan dimuat dan GUI diperbarui.");
            return true;
        } else {
            this.currentBoard = null;
            boardPanel.setBoard(null); // Kosongkan panel papan
            statusPanel.updateStatus("Gagal memuat papan. Periksa konsol untuk error.");
            JOptionPane.showMessageDialog(frame,
                    "Tidak dapat memuat papan dari file: " + new File(filePath).getName() + "\nSilakan periksa format file dan konsol untuk error.",
                    "Error Memuat Papan", JOptionPane.ERROR_MESSAGE);
            controlPanel.enableSolveButton(false);
            controlPanel.enableAnimateButton(false);
            System.err.println("Gagal memuat papan dari: " + filePath);
            return false;
        }
    }

    /**
     * Memulai proses penyelesaian puzzle.
     * Metode ini dipanggil dari ControlPanel.
     * Akan membuat solver berdasarkan pilihan pengguna dan memanggil solvePuzzle.
     */
    public void initiateSolveProcess() {
        if (currentBoard == null) {
            JOptionPane.showMessageDialog(frame, "Silakan muat papan terlebih dahulu.", "Papan Belum Dimuat", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String algorithmName = controlPanel.getSelectedAlgorithm();
        String heuristicName = controlPanel.getSelectedHeuristic();

        if (algorithmName == null) {
            JOptionPane.showMessageDialog(frame, "Silakan pilih algoritma.", "Algoritma Belum Dipilih", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Membuat GameState dari currentBoard
        GameState initialState = new GameState(0, currentBoard.clone(), new ArrayList<>()); // Biaya awal 0, tanpa langkah sebelumnya

        Heuristic heuristic = null;
        if (heuristicName != null && controlPanel.isHeuristicEnabled()) {
            heuristic = createHeuristic(heuristicName);
             if (heuristic == null && (algorithmName.equals("A*") || algorithmName.equals("Greedy BFS") || algorithmName.equals("Beam Search (10 beam width)"))) {
                JOptionPane.showMessageDialog(frame, "Heuristik '" + heuristicName + "' tidak dikenal.", "Error Heuristik", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (heuristic == null && (algorithmName.equals("A*") || algorithmName.equals("Greedy BFS") || algorithmName.equals("Beam Search (10 beam width)"))) {
            JOptionPane.showMessageDialog(frame, "Algoritma " + algorithmName + " memerlukan heuristik. Silakan pilih satu.", "Heuristik Diperlukan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        RushHourSolver solver = createSolver(algorithmName, initialState, heuristic);

        if (solver != null) {
            solvePuzzle(solver);
        } else {
            JOptionPane.showMessageDialog(frame, "Gagal membuat solver untuk algoritma: " + algorithmName, "Error Solver", JOptionPane.ERROR_MESSAGE);
            statusPanel.updateStatus("Gagal membuat solver.");
        }
    }

    /**
     * Membuat instance Heuristic berdasarkan nama.
     * @param heuristicName Nama heuristik.
     * @return Instance Heuristic atau null jika nama tidak dikenal.
     */
    private Heuristic createHeuristic(String heuristicName) {
        if (heuristicName == null) return null;
        switch (heuristicName) {
            case "Manhattan Distance" -> {
                return new ManhattanDistance();
            }
            case "Blocking Pieces" -> {
                return new BlockingPiecesHeuristic();
            }
            case "Combined" -> {
                // Untuk CombinedHeuristic, mungkin perlu setup lebih lanjut atau default
                CombinedHeuristic ch = new CombinedHeuristic();
                // Contoh: ch.addHeuristic(new ManhattanDistance(), 0.5); // Ini perlu implementasi di CombinedHeuristic
                // ch.addHeuristic(new BlockingPiecesHeuristic(), 0.5);
                // JOptionPane.showMessageDialog(frame, "CombinedHeuristic dipilih, komponennya belum dapat dikonfigurasi via GUI.", "Info Heuristik", JOptionPane.INFORMATION_MESSAGE);
                return ch; // Akan menggunakan implementasi default/kosong dari CombinedHeuristic jika tidak ada komponen
            }
            default -> {
                System.err.println("Heuristik tidak dikenal: " + heuristicName);
                return null;
            }
        }
    }

    /**
     * Membuat instance RushHourSolver berdasarkan nama algoritma, keadaan awal, dan heuristik.
     * @param algorithmName Nama algoritma.
     * @param initialState Keadaan awal permainan.
     * @param heuristic Heuristik yang akan digunakan (bisa null).
     * @return Instance RushHourSolver atau null jika nama tidak dikenal.
     */
    private RushHourSolver createSolver(String algorithmName, GameState initialState, Heuristic heuristic) {
        if (algorithmName == null) return null;
        switch (algorithmName) {
            case "UCS" -> {
                return new UCSolver(initialState);
            }
            case "A*" -> {
                return new AStarSolver(initialState, heuristic);
            }
            case "Greedy BFS" -> {
                return new GreedyBestFirstSearchSolver(initialState, heuristic);
            }
            case "Beam Search (10 beam width)" -> {
                return new BeamSearchSolver(initialState, heuristic);
            }
            default -> {
                System.err.println("Algoritma tidak dikenal: " + algorithmName);
                return null;
            }
        }
    }

    public void solvePuzzle(RushHourSolver solver) {
        if (solver == null) {
            statusPanel.updateStatus("Solver tidak valid.");
            return;
        }

        statusPanel.updateStatus("Menyelesaikan (" + solver.getName() + ")... Node: 0");
        controlPanel.enableSolveButton(false);
        controlPanel.enableAnimateButton(false);
        controlPanel.enableLoadButton(false);

        SwingWorker<SolutionResult, Integer> worker = new SwingWorker<SolutionResult, Integer>() {
            @Override
            protected SolutionResult doInBackground() throws Exception {
                // --- AWAL Perubahan untuk Progress Reporting (FIXED) ---
                // Membuat lambda yang akan memanggil metode publish() dari instance SwingWorker ini
                Consumer<Integer> progressCallback = data -> publish(data);
                // Mengatur callback ini pada solver
                solver.setProgressEventConsumer(progressCallback);
                // --- AKHIR Perubahan untuk Progress Reporting (FIXED) ---
                
                return solver.solve();
            }

            @Override
            protected void process(List<Integer> chunks) {
                if (chunks != null && !chunks.isEmpty()) {
                    Integer latestVisitedNodesCount = chunks.get(chunks.size() - 1);
                    statusPanel.updateStatus("Menyelesaikan (" + solver.getName() + ")... Node: " + latestVisitedNodesCount);
                }
            }

            @Override
            protected void done() {
                try {
                    currentSolution = get();
                    if (currentSolution != null) {
                        displaySolution(currentSolution);
                    } else {
                        statusPanel.updateStatus("Solver tidak mengembalikan hasil solusi.");
                         JOptionPane.showMessageDialog(frame, "Solver tidak mengembalikan hasil solusi.", "Hasil Tidak Valid", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    statusPanel.updateStatus("Proses penyelesaian (" + solver.getName() + ") dibatalkan.");
                    System.err.println("Penyelesaian (" + solver.getName() + ") dibatalkan: " + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    statusPanel.updateStatus("Error saat menyelesaikan (" + solver.getName() + "): " + e.getClass().getSimpleName());
                    JOptionPane.showMessageDialog(frame, "Terjadi error saat menyelesaikan (" + solver.getName() + "):\n" + e.getMessage(), "Error Penyelesaian", JOptionPane.ERROR_MESSAGE);
                } finally {
                    controlPanel.enableSolveButton(true);
                    controlPanel.enableLoadButton(true);
                    if (solver != null) {
                        // Bersihkan consumer untuk menghindari referensi yang tidak perlu
                        solver.setProgressEventConsumer(null);
                    }
                    if (currentSolution != null && currentSolution.isSolved() && currentSolution.getMoves() != null && !currentSolution.getMoves().isEmpty()) {
                        controlPanel.enableAnimateButton(true);
                    } else {
                        controlPanel.enableAnimateButton(false);
                    }
                }
            }
        };
        worker.execute();
    }

    /**
     * Menampilkan hasil solusi di GUI.
     * @param result Hasil solusi dari solver.
     */
    public void displaySolution(SolutionResult result) {
        this.currentSolution = result;
        if (result != null) {
            statusPanel.updateStatus(result.isSolved() ? "Solusi ditemukan!" : "Solusi tidak ditemukan.");
            showStatistics(result); // Tampilkan statistik menggunakan metode yang ada
            boardPanel.setBoard(currentBoard); // Reset ke tampilan papan awal sebelum animasi
            if (result.isSolved() && result.getMoves() != null && !result.getMoves().isEmpty()) {
                controlPanel.enableAnimateButton(true);
            } else {
                controlPanel.enableAnimateButton(false);
            }
        } else {
            statusPanel.updateStatus("Solver gagal atau tidak mengembalikan hasil.");
            // JOptionPane.showMessageDialog(frame, "Solver tidak mengembalikan hasil.", "Error Solver", JOptionPane.ERROR_MESSAGE);
             // Pesan ini mungkin terlalu sering muncul jika solver belum diimplementasikan, jadi bisa dikomentari sementara
        }
    }

    /**
     * Memulai animasi solusi jika ada.
     * Metode ini dipanggil dari ControlPanel.
     */
    public void animateSolution() {
        if (currentSolution == null || !currentSolution.isSolved() ||
            currentSolution.getBoardStates() == null || currentSolution.getBoardStates().isEmpty() ||
            currentSolution.getMoves() == null || currentSolution.getMoves().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Tidak ada solusi untuk dianimasikan atau solusi kosong.", "Error Animasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationStepIndex = 0;
        boardPanel.setBoard(currentBoard); // Tampilkan keadaan papan paling awal (sebelum langkah solusi pertama)
        boardPanel.highlightPiece(' ', false); // Hapus highlight sebelumnya

        int delay = controlPanel.getAnimationSpeed();

        animationTimer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // getBoardStates() harusnya berisi urutan papan SETELAH setiap langkah.
                // Ukuran getBoardStates() harus sama dengan getMoves().
                if (animationStepIndex < currentSolution.getBoardStates().size()) {
                    Board nextBoardState = currentSolution.getBoardStates().get(animationStepIndex);
                    Move lastMove = currentSolution.getMoves().get(animationStepIndex);

                    boardPanel.setBoard(nextBoardState);
                    boardPanel.highlightPiece(lastMove.getPieceId(), true);

                    statusPanel.updateStatus("Animasi langkah: " + (animationStepIndex + 1) + "/" + currentSolution.getMoves().size());
                    animationStepIndex++;
                } else {
                    animationTimer.stop();
                    statusPanel.updateStatus("Animasi selesai.");
                    // Pastikan state terakhir ditampilkan tanpa highlight
                    if (!currentSolution.getBoardStates().isEmpty()){
                        boardPanel.setBoard(currentSolution.getBoardStates().get(currentSolution.getBoardStates().size() - 1));
                    }
                    boardPanel.highlightPiece(' ', false); // Hapus highlight di akhir
                    controlPanel.enableAnimateButton(true);
                    controlPanel.enableSolveButton(true);
                }
            }
        });
        animationTimer.setInitialDelay(Math.max(0, delay/2)); // Mulai setelah jeda singkat
        animationTimer.start();
        controlPanel.enableAnimateButton(false); // Nonaktifkan kontrol selama animasi
        controlPanel.enableSolveButton(false);
    }

    /**
     * Menampilkan statistik solusi di StatusPanel.
     * @param result Hasil solusi.
     */
    public void showStatistics(SolutionResult result) {
        if (result != null) {
            statusPanel.updateStatistics(result);
        } else {
            statusPanel.clearStatistics();
        }
    }

    /**
     * Mengatur kecepatan animasi.
     * Dipanggil dari ControlPanel saat slider diubah.
     * @param speed Jeda dalam milidetik antar langkah animasi.
     */
    public void setAnimationSpeed(int speed) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.setDelay(speed);
        }
        // Jika tidak berjalan, kecepatan akan diambil saat animasi dimulai berikutnya
        System.out.println("Kecepatan animasi diatur ke: " + speed + "ms");
    }
}
