import controller.heuristic.Heuristic;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import model.GameState;
import model.core.Board;
import model.core.Piece;
import model.core.Position;
import utils.FileHandler;

public class RushHourMain {
    public static void main(String[] args) {
        FileHandler fileHandler = new FileHandler();
        String testInputFilePath = "test_board_input_for_read.txt"; // File input akan dibuat di direktori kerja

        System.out.println("--- Memulai Pengujian HANYA readBoardFromFile() ---");

        // 1. Buat file input pengujian (sama seperti sebelumnya)
        createTestInputFile(testInputFilePath);

        // 2. Uji metode readBoardFromFile dan cetak hasilnya secara detail
        System.out.println("\n--- Menguji readBoardFromFile ---");
        System.out.println("Mencoba membaca papan dari: " + testInputFilePath);
        Board loadedBoard = fileHandler.readBoardFromFile(testInputFilePath);

        if (loadedBoard != null) {
            System.out.println("\nPapan berhasil dibaca!");
            System.out.println("------------------------------------------");
            System.out.println("Detail Papan yang Dimuat:");
            System.out.println("------------------------------------------");
            System.out.println("Dimensi Papan: " + loadedBoard.getHeight() + " baris x " + loadedBoard.getWidth() + " kolom");

            if (loadedBoard.getExitPosition() != null) {
                System.out.println("Posisi Keluar (K): (" + loadedBoard.getExitPosition().getX() + "," + loadedBoard.getExitPosition().getY() + ")");
            } else {
                System.out.println("Posisi Keluar (K): Tidak ditemukan dalam konfigurasi.");
            }
            System.out.println("------------------------------------------");
            System.out.println("Daftar Pieces:");
            if (loadedBoard.getPieces() != null && !loadedBoard.getPieces().isEmpty()) {
                for (Piece piece : loadedBoard.getPieces()) {
                    System.out.println("  Piece ID: " + piece.getId());
                    System.out.println("    Primary: " + piece.isPrimary());
                    System.out.println("    Orientasi: " + piece.getOrientation());
                    System.out.println("    Posisi (kolom, baris):");
                    if (piece.getPositions() != null) {
                        for (Position pos : piece.getPositions()) {
                            System.out.println("      (" + pos.getX() + "," + pos.getY() + ")");
                        }
                    } else {
                        System.out.println("      (Posisi tidak tersedia)");
                    }
                }
            } else {
                System.out.println("  Tidak ada pieces yang dimuat atau daftar pieces kosong.");
            }
            System.out.println("------------------------------------------");
            System.out.println("Representasi Papan Aktual (dari board.toString()):");
            // PENTING: Ini bergantung pada implementasi yang benar dari Board.toString()
            // di kelas model.core.Board agar menghasilkan representasi grid.
            String boardRepresentation = loadedBoard.toString();
            if (boardRepresentation == null || boardRepresentation.equals("null") || boardRepresentation.trim().isEmpty()) {
                 System.out.println("CATATAN: Board.toString() belum mengembalikan representasi papan yang valid atau mengembalikan null/kosong.");
                 System.out.println("Harap implementasikan Board.toString() dengan benar di kelas Board.java untuk verifikasi visual.");
            } else {
                System.out.println(boardRepresentation);
            }
            System.out.println("------------------------------------------");

        } else {
            System.err.println("Gagal membaca papan dari file. Periksa pesan error di atas jika ada.");
        }

        System.out.println("\n--- Pengujian readBoardFromFile() Selesai ---");
        System.out.println("Silakan periksa file input yang dibuat: '" + testInputFilePath + "'");
    }

    private static void createTestInputFile(String filePath) {
        System.out.println("Membuat file input pengujian: " + filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("6 6\n");        // 6 baris, 6 kolom
            writer.write("11\n");         // Jumlah piece non-utama (sesuai contoh, jika ada 1 primary, maka ada 11 non-primary dari 12 total pieces di PDF)
            writer.write("AAB..F\n");
            writer.write("..BCDF\n");
            writer.write("GPPCDFK\n");    // P = Primary Piece, K = Exit
            writer.write("GH.III\n");
            writer.write("GHJ...\n");    // Pastikan panjangnya sesuai dengan jumlah kolom (6)
            writer.write("LLJMM.\n");
            System.out.println("File input pengujian berhasil dibuat.");
        } catch (IOException e) {
            System.err.println("Error saat membuat file input pengujian: " + e.getMessage());
        }
    }

    private void runConsoleMode() {

    }

    private void runGUIMode() {

    }

    private void createSolver(String algorithm, GameState initialState, String heuristic) {
        
    }

    private Heuristic createHeuristic(String heuristicName) {
        return null;
    }
}
