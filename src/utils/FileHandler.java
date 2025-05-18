package utils;

import controller.SolutionResult;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.core.Board;
import model.core.Direction;
import model.core.Move;
import model.core.Orientation;
import model.core.Piece;
import model.core.Position;

public class FileHandler {

    private static final Map<Direction, String> directionToIndonesian = new HashMap<>();

    static {
        directionToIndonesian.put(Direction.UP, "atas");
        directionToIndonesian.put(Direction.DOWN, "bawah");
        directionToIndonesian.put(Direction.LEFT, "kiri");
        directionToIndonesian.put(Direction.RIGHT, "kanan");
    }
    /**
     * Membaca konfigurasi papan dari file sesuai spesifikasi Tucil 3 yang detail.
     *
     * @param path Path ke file konfigurasi papan.
     * @return Objek Board yang dikonfigurasi, atau null jika terjadi error atau
     * konfigurasi tidak valid.
     */
    public Board readBoardFromFile(String path) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error membaca file papan: " + e.getMessage());
            return null;
        }

        if (lines.size() < 2) {
            System.err.println("Format file papan tidak valid: Jumlah baris kurang untuk dimensi dan N.");
            return null;
        }

        try {
            // Baris 1: Dimensi Papan (A B)
            String[] dimensions = lines.get(0).trim().split("\\s+");
            if (dimensions.length != 2) {
                // Coba parsing jika formatnya "66" (tanpa spasi untuk single digit)
                // Meskipun spesifikasi meminta spasi, ini untuk robustnes berdasarkan catatan Anda.
                String dimLine = lines.get(0).trim();
                if (dimLine.length() == 2 && Character.isDigit(dimLine.charAt(0)) && Character.isDigit(dimLine.charAt(1))) {
                    dimensions = new String[]{String.valueOf(dimLine.charAt(0)), String.valueOf(dimLine.charAt(1))};
                     System.out.println("Peringatan: Dimensi papan diparsing sebagai digit tunggal yang digabungkan (misalnya, '66'). Format yang lebih disukai adalah dengan spasi (misalnya, '6 6').");
                } else {
                    System.err.println("Format dimensi papan tidak valid. Diharapkan 'Baris Kolom' (misalnya, '6 6'). Ditemukan: " + lines.get(0));
                    return null;
                }
            }
            int numRowsA = Integer.parseInt(dimensions[0]); // A
            int numColsB = Integer.parseInt(dimensions[1]); // B

            if (numRowsA <= 0 || numColsB <= 0) {
                System.err.println("Dimensi papan tidak valid: Baris dan kolom harus positif.");
                return null;
            }

            // Baris 2: N (jumlah piece BUKAN primary piece)
            // int numNonPrimaryPieces = Integer.parseInt(lines.get(1).trim()); // Saat ini tidak digunakan secara langsung dalam validasi di sini

            if (lines.size() < 2 + numRowsA) {
                System.err.println("Format file papan tidak valid: Jumlah baris konfigurasi papan kurang. Diharapkan " + numRowsA + " baris setelah dimensi dan N.");
                return null;
            }

            // Persiapan untuk memproses piece
            ArrayList<Piece> pieces = new ArrayList<>();
            Position exitPosition = null; // Akan ditentukan berdasarkan 'K'
            Piece primaryPieceObj = null; // Untuk validasi orientasi P vs K

            // Grid sementara untuk validasi tumpang tindih dan identifikasi piece
            char[][] gridForValidation = new char[numRowsA][numColsB];
            for (int r = 0; r < numRowsA; r++) {
                for (int c = 0; c < numColsB; c++) {
                    gridForValidation[r][c] = '.'; // Inisialisasi dengan sel kosong
                }
            }

            Map<Character, ArrayList<Position>> pieceCandidatePositions = new HashMap<>();
            int exitRow = -1, exitCol = -1; // Koordinat konseptual K
            boolean exitIsHorizontal = false; // Apakah K di dinding kiri/kanan (horizontal exit)

            // Memproses setiap baris konfigurasi papan (A baris)
            for (int r = 0; r < numRowsA; r++) {
                String currentBoardLine = lines.get(r + 2); // Baris papan dimulai dari index 2
                int expectedLength = numColsB;
                boolean currentRowHasHorizontalExit = false;

                // Cek apakah baris ini memiliki 'K' di akhir (menandakan pintu keluar horizontal)
                if (currentBoardLine.length() == numColsB + 1 && currentBoardLine.endsWith("K")) {
                    if (exitPosition != null) { // Sudah ada K lain ditemukan
                        System.err.println("Error: Lebih dari satu Pintu Keluar 'K' ditemukan.");
                        return null;
                    }
                    exitPosition = new Position(numColsB, r); // K secara konseptual di kolom B (setelah B-1)
                    exitRow = r;
                    exitCol = numColsB; // Sebelah kanan
                    exitIsHorizontal = true;
                    currentBoardLine = currentBoardLine.substring(0, numColsB); // Ambil bagian grid A x B saja
                    currentRowHasHorizontalExit = true;
                } else if (currentBoardLine.length() == numColsB + 1 && currentBoardLine.startsWith("K")) {
                     if (exitPosition != null) {
                        System.err.println("Error: Lebih dari satu Pintu Keluar 'K' ditemukan.");
                        return null;
                    }
                    exitPosition = new Position(-1, r); // K secara konseptual di kolom -1 (sebelum 0)
                    exitRow = r;
                    exitCol = -1; // Sebelah kiri
                    exitIsHorizontal = true;
                    currentBoardLine = currentBoardLine.substring(1); // Ambil bagian grid A x B saja
                    currentRowHasHorizontalExit = true;
                }


                if (currentBoardLine.length() != numColsB) {
                    System.err.println("Error: Baris " + (r + 1) + " (\"" + lines.get(r+2) + "\") memiliki panjang " + currentBoardLine.length() + ", diharapkan " + numColsB + (currentRowHasHorizontalExit ? " (setelah menghapus K)" : "") + ".");
                    return null;
                }


                for (int c = 0; c < numColsB; c++) {
                    char cellChar = currentBoardLine.charAt(c);
                    if (cellChar == 'K') { // K di dalam grid A x B (pintu keluar vertikal)
                        if (exitPosition != null) {
                            System.err.println("Error: Lebih dari satu Pintu Keluar 'K' ditemukan.");
                            return null;
                        }
                        // Validasi K di dinding untuk pintu keluar vertikal
                        if (r != 0 && r != numRowsA - 1) {
                            System.err.println("Error: Pintu Keluar 'K' vertikal di (" + c + "," + r + ") tidak berada di dinding atas atau bawah.");
                            return null;
                        }
                        exitPosition = new Position(c, r); // Posisi K adalah sel itu sendiri
                        exitRow = r;
                        exitCol = c;
                        exitIsHorizontal = false; // Ini pintu keluar vertikal
                        gridForValidation[r][c] = '.'; // Sel K dianggap kosong di grid permainan
                    } else if (cellChar != '.') {
                        pieceCandidatePositions.putIfAbsent(cellChar, new ArrayList<>());
                        pieceCandidatePositions.get(cellChar).add(new Position(c, r));
                        // Validasi tumpang tindih secara langsung
                        if (gridForValidation[r][c] != '.') {
                            System.err.println("Error: Piece tumpang tindih di (" + c + "," + r + "). Sel ditempati oleh '" + gridForValidation[r][c] + "' dan '" + cellChar + "'.");
                            return null;
                        }
                        gridForValidation[r][c] = cellChar;
                    } else {
                        // gridForValidation[r][c] sudah '.'
                    }
                }
            }

            if (exitPosition == null) {
                System.err.println("Error: Tidak ada Pintu Keluar 'K' yang ditemukan dalam konfigurasi.");
                return null;
            }

            // Identifikasi dan buat objek Piece dari pieceCandidatePositions
            for (Map.Entry<Character, ArrayList<Position>> entry : pieceCandidatePositions.entrySet()) {
                char id = entry.getKey();
                ArrayList<Position> positions = entry.getValue(); // Posisi (col, row) atau (x, y)
                if (positions.isEmpty()) continue;

                // Urutkan posisi untuk memudahkan identifikasi orientasi dan kontinuitas
                // Urutkan berdasarkan y (baris) dulu, baru x (kolom)
                Collections.sort(positions, Comparator.comparingInt(Position::getY).thenComparingInt(Position::getX));

                Orientation orientation;
                int pieceLength = positions.size();

                if (pieceLength < 2 || pieceLength > 3) {
                    System.err.println("Error: Piece '" + id + "' memiliki panjang " + pieceLength + ". Panjang yang valid adalah 2 atau 3.");
                    return null;
                }

                boolean allYSame = true;
                boolean allXSame = true;
                int firstY = positions.get(0).getY();
                int firstX = positions.get(0).getX();

                for (int i = 1; i < pieceLength; i++) {
                    if (positions.get(i).getY() != firstY) allYSame = false;
                    if (positions.get(i).getX() != firstX) allXSame = false;
                }

                if (allYSame && !allXSame) { // Horizontal
                    orientation = Orientation.HORIZONTAL;
                    // Cek kontinuitas horizontal
                    for (int i = 0; i < pieceLength - 1; i++) {
                        if (positions.get(i+1).getX() - positions.get(i).getX() != 1 || positions.get(i+1).getY() != positions.get(i).getY()) {
                            System.err.println("Error: Piece horizontal '" + id + "' tidak kontinu. Posisi: " + positions);
                            return null;
                        }
                    }
                } else if (allXSame && !allYSame) { // Vertikal
                    orientation = Orientation.VERTICAL;
                    // Cek kontinuitas vertikal
                    for (int i = 0; i < pieceLength - 1; i++) {
                        if (positions.get(i+1).getY() - positions.get(i).getY() != 1 || positions.get(i+1).getX() != positions.get(i).getX()) {
                            System.err.println("Error: Piece vertikal '" + id + "' tidak kontinu. Posisi: " + positions);
                            return null;
                        }
                    }
                } else {
                     System.err.println("Error: Piece '" + id + "' memiliki tata letak yang tidak valid (tidak lurus horizontal atau vertikal, atau hanya 1 sel jika lolos cek panjang). Posisi: " + positions);
                    return null;
                }

                // Cek apakah piece berada sepenuhnya dalam batas grid A x B
                for (Position pos : positions) {
                    if (pos.getX() < 0 || pos.getX() >= numColsB || pos.getY() < 0 || pos.getY() >= numRowsA) {
                        System.err.println("Error: Piece '" + id + "' sebagian atau seluruhnya berada di luar batas papan " + numRowsA + "x" + numColsB + ". Posisi: " + pos.getX() + "," + pos.getY());
                        return null;
                    }
                }


                boolean isPrimary = (id == 'P');
                Piece currentPiece = new Piece(id, positions, orientation, isPrimary);
                pieces.add(currentPiece);
                if (isPrimary) {
                    if (primaryPieceObj != null) {
                        System.err.println("Error: Lebih dari satu Primary Piece 'P' ditemukan.");
                        return null;
                    }
                    primaryPieceObj = currentPiece;
                }
            }

            if (primaryPieceObj == null) {
                System.err.println("Error: Tidak ada Primary Piece 'P' yang ditemukan.");
                return null;
            }

            // Validasi Kunci: "Pintu keluar pasti berada di dinding papan dan sejajar dengan orientasi primary piece."
            Orientation primaryOrientation = primaryPieceObj.getOrientation();
            if (exitIsHorizontal) { // K di dinding kiri/kanan
                if (primaryOrientation != Orientation.HORIZONTAL) {
                    System.err.println("Error: Pintu Keluar 'K' berada di dinding horizontal, tetapi Primary Piece 'P' berorientasi vertikal.");
                    return null;
                }
                // Cek apakah baris K sama dengan salah satu baris P
                boolean rowMatch = false;
                for(Position pPos : primaryPieceObj.getPositions()){
                    if(pPos.getY() == exitRow){
                        rowMatch = true;
                        break;
                    }
                }
                if(!rowMatch){
                     System.err.println("Error: Pintu Keluar 'K' horizontal di baris " + exitRow + " tidak sejajar dengan baris Primary Piece 'P'.");
                     return null;
                }

            } else { // K di dinding atas/bawah (exitPosition adalah sel K itu sendiri)
                if (primaryOrientation != Orientation.VERTICAL) {
                    System.err.println("Error: Pintu Keluar 'K' berada di dinding vertikal, tetapi Primary Piece 'P' berorientasi horizontal.");
                    return null;
                }
                 // Cek apakah kolom K sama dengan salah satu kolom P
                boolean colMatch = false;
                for(Position pPos : primaryPieceObj.getPositions()){
                    if(pPos.getX() == exitCol){ // exitCol adalah kolom dari K
                        colMatch = true;
                        break;
                    }
                }
                 if(!colMatch){
                     System.err.println("Error: Pintu Keluar 'K' vertikal di kolom " + exitCol + " tidak sejajar dengan kolom Primary Piece 'P'.");
                     return null;
                }
            }


            // Validasi karakter tidak dikenal (jika ada karakter di pieceCandidatePositions yang tidak menjadi piece valid)
            // Ini sebagian besar sudah ditangani oleh validasi panjang piece dan tata letak.

            return new Board(numColsB, numRowsA, pieces, exitPosition); // Ingat Board(width, height, ...)

        } catch (NumberFormatException e) {
            System.err.println("Error memparsing angka untuk dimensi atau N: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error memproses baris file (kemungkinan terlalu sedikit baris atau format salah untuk dimensi/N): " + e.getMessage());
        } catch (Exception e) { // Tangkap error tak terduga lainnya
            System.err.println("Terjadi error tak terduga saat parsing papan: " + e.getMessage());
            e.printStackTrace(); // Cetak stack trace untuk debug
        }
        return null; // Gagal parsing atau validasi
    }


    /**
     * Menulis hasil solusi ke file sesuai format output Tucil 3.
     *
     * @param result         Objek SolutionResult yang berisi detail solusi.
     * @param initialBoard   Papan awal.
     * @param solverName     Nama solver yang digunakan.
     * @param heuristicName  Nama heuristik yang digunakan (bisa null atau kosong jika tidak ada).
     * @param path           Path untuk menyimpan file solusi.
     */
    public void writeSolutionToFile(SolutionResult result, Board initialBoard, String solverName, String heuristicName, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            // Informasi tambahan di awal file (opsional, tapi baik untuk kelengkapan)
            writer.write("Solver: " + (solverName != null ? solverName : "N/A") + "\n");
            if (heuristicName != null && !heuristicName.isEmpty()) {
                writer.write("Heuristic: " + heuristicName + "\n");
            }
            if (result != null) {
                writer.write("Waktu Eksekusi (ms): " + result.getExecutionTime() + "\n");
                writer.write("Node Dikunjungi: " + result.getVisitedNodesCount() + "\n");
            } else {
                 writer.write("Waktu Eksekusi (ms): N/A\n");
                 writer.write("Node Dikunjungi: N/A\n");
            }
            writer.write("\n");

            writer.write("Papan Awal\n");
            // Membutuhkan implementasi Board.toString() yang menghasilkan representasi grid papan
            writer.write(initialBoard != null ? initialBoard.toString() : "N/A\n");
            writer.write("\n");

            if (result == null) {
                writer.write("Tidak ada hasil solusi (SolutionResult is null).\n");
                System.out.println("Solution (null result) written to: " + path);
                return;
            }
            
            List<Move> moves = result.getMoves();
            List<Board> boardStates = result.getBoardStates(); // Asumsi boardStates[i] adalah keadaan SETELAH moves[i]

            if (result.isSolved()) {
                if (moves != null && !moves.isEmpty()) {
                    // Jika boardStates disediakan dan ukurannya sesuai dengan moves
                    if (boardStates != null && boardStates.size() == moves.size()) {
                        for (int i = 0; i < moves.size(); i++) {
                            Move move = moves.get(i);
                            Board currentBoardState = boardStates.get(i);
                            String directionStr = directionToIndonesian.getOrDefault(move.getDirection(), move.getDirection().toString());

                            writer.write(String.format("Gerakan %d: %c-%s\n",
                                    (i + 1),
                                    move.getPieceId(),
                                    directionStr));
                            writer.write(currentBoardState.toString()); // Membutuhkan Board.toString()
                            writer.write("\n");
                        }
                    } else {
                        // Fallback jika boardStates tidak sesuai, hanya tampilkan gerakan
                         for (int i = 0; i < moves.size(); i++) {
                            Move move = moves.get(i);
                            String directionStr = directionToIndonesian.getOrDefault(move.getDirection(), move.getDirection().toString());
                            writer.write(String.format("Gerakan %d: %c-%s\n",
                                    (i + 1),
                                    move.getPieceId(),
                                    directionStr));
                            writer.write("Konfigurasi papan setelah gerakan ini tidak tersedia.\n\n");
                        }
                        if (boardStates == null) System.err.println("Peringatan: boardStates adalah null dalam SolutionResult.");
                        else System.err.println("Peringatan: Ukuran boardStates ("+boardStates.size()+") tidak cocok dengan ukuran moves ("+moves.size()+").");
                    }
                } else { // Solved tapi tidak ada langkah (misalnya, sudah di posisi akhir)
                    writer.write("Puzzle sudah terselesaikan pada konfigurasi awal atau tidak memerlukan langkah.\n");
                }
            } else {
                writer.write("Solusi tidak ditemukan.\n");
            }

            System.out.println("Hasil solusi berhasil ditulis ke: " + path);

        } catch (IOException e) {
            System.err.println("Error menulis solusi ke file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /*
     * Metode parseBoardConfig yang lama dari file asli tidak sesuai dengan spesifikasi Tucil 3.
     * Jika masih dibutuhkan untuk keperluan lain, bisa di-uncomment atau dipindahkan.
     * Untuk saat ini, saya akan comment out karena fokus pada implementasi Tucil 3.
     */
    /*
    private Board parseBoardConfig(List<String> lines) {
        try {
            // Baris 1: Dimensi Papan
            String[] dimensions = lines.get(0).split("\\s+");
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);

            // Baris 2: Posisi Exit
            String[] exitCoords = lines.get(1).split("\\s+");
            Position exitPosition = new Position(Integer.parseInt(exitCoords[1]), Integer.parseInt(exitCoords[0])); // col, row

            ArrayList<Piece> pieces = new ArrayList<>();
            // Baris 3 dst.: Definisi Bidak
            for (int i = 2; i < lines.size(); i++) {
                String[] pieceData = lines.get(i).split("\\s+");
                if (pieceData.length < 6) {
                    System.err.println("Skipping invalid piece data (not enough fields): " + lines.get(i));
                    continue;
                }
                char id = pieceData[0].charAt(0);
                boolean isPrimary = Boolean.parseBoolean(pieceData[1]);
                Orientation orientation = pieceData[2].equalsIgnoreCase("H") ? Orientation.HORIZONTAL : Orientation.VERTICAL;
                int startRow = Integer.parseInt(pieceData[3]);
                int startCol = Integer.parseInt(pieceData[4]);
                int length = Integer.parseInt(pieceData[5]);

                ArrayList<Position> piecePositions = new ArrayList<>();
                for (int j = 0; j < length; j++) {
                    if (orientation == Orientation.HORIZONTAL) {
                        piecePositions.add(new Position(startCol + j, startRow));
                    } else { // VERTICAL
                        piecePositions.add(new Position(startCol, startRow + j));
                    }
                }
                pieces.add(new Piece(id, piecePositions, orientation, isPrimary));
            }

            return new Board(width, height, pieces, exitPosition);

        } catch (NumberFormatException e) {
            System.err.println("Error parsing number in board file: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error parsing line in board file (not enough data): " + e.getMessage());
        } catch (Exception e) { // Catch other potential errors during parsing
            System.err.println("An unexpected error occurred during board parsing: " + e.getMessage());
        }
        return null;
    }
    */

    /*
     * Metode parseBoard yang lama dari file asli, sekarang logikanya sudah diintegrasikan
     * ke dalam readBoardFromFile. Jika dibutuhkan sebagai helper terpisah,
     * bisa dipertahankan dan dimodifikasi. Untuk saat ini, saya comment out.
     */
    /*
    public ArrayList<Piece> parseBoard(int boardWidth, int boardHeight, String[] boardLines) {
        ArrayList<Piece> pieces = new ArrayList<>();
        Map<Character, ArrayList<Position>> pieceCandidatePositions = new HashMap<>();
        char[][] grid = new char[boardHeight][boardWidth];

        for (int r = 0; r < boardHeight; r++) {
            for (int c = 0; c < boardWidth; c++) {
                if (r < boardLines.length && c < boardLines[r].length()) {
                    grid[r][c] = boardLines[r].charAt(c);
                    char pieceChar = grid[r][c];
                    if (pieceChar != '.' && pieceChar != ' ') { // Asumsikan '.' atau ' ' adalah kosong
                        pieceCandidatePositions.putIfAbsent(pieceChar, new ArrayList<>());
                        pieceCandidatePositions.get(pieceChar).add(new Position(c, r));
                    }
                } else {
                    grid[r][c] = '.'; // Padding jika boardLines lebih pendek
                }
            }
        }

        for (Map.Entry<Character, ArrayList<Position>> entry : pieceCandidatePositions.entrySet()) {
            char id = entry.getKey();
            ArrayList<Position> positions = entry.getValue();
            if (positions.isEmpty()) continue;

            // Tentukan orientasi dan validasi panjang/kontinuitas (implementasi sederhana)
            boolean isHorizontal = false;
            boolean isVertical = false;

            if (positions.size() > 1) {
                // Semua Y sama -> horizontal, Semua X sama -> vertical
                boolean allYSame = true;
                boolean allXSame = true;
                int firstY = positions.get(0).getY();
                int firstX = positions.get(0).getX();

                for (int i = 1; i < positions.size(); i++) {
                    if (positions.get(i).getY() != firstY) allYSame = false;
                    if (positions.get(i).getX() != firstX) allXSame = false;
                }

                if (allYSame && !allXSame) isHorizontal = true;
                if (allXSame && !allYSame) isVertical = true;
                if (allXSame && allYSame && positions.size() == 1) { // Bidak 1x1
                    isHorizontal = true; // Default ke horizontal untuk 1x1
                } else if (!isHorizontal && !isVertical) {
                     System.err.println("Piece " + id + " has non-contiguous or diagonal layout, skipping.");
                     continue; // Skip bidak yang tidak lurus
                }
            } else { // Bidak 1x1
                 isHorizontal = true; // Default ke horizontal untuk 1x1
            }


            Orientation orientation = isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            // Asumsi: bidak 'X' adalah bidak utama. Ini bisa diubah.
            boolean isPrimary = (id == 'X'); // INI PERLU DISESUAIKAN DENGAN 'P'

            // Urutkan posisi untuk konsistensi (opsional, tapi bisa membantu)
            positions.sort((p1, p2) -> {
                if (p1.getY() != p2.getY()) return Integer.compare(p1.getY(), p2.getY());
                return Integer.compare(p1.getX(), p2.getX());
            });
            
            pieces.add(new Piece(id, positions, orientation, isPrimary));
        }
        return pieces;
    }
    */
}