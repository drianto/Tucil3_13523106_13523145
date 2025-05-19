package controller.heuristic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.GameState;
import model.core.Board;
import model.core.Orientation;
import model.core.Piece;
import model.core.Position;

public class BlockingPiecesHeuristic implements Heuristic {

    public BlockingPiecesHeuristic() {
        // Constructor bisa tetap kosong jika tidak ada inisialisasi khusus
    }

    @Override
    public int calculate(GameState gameState) {
        Board board = gameState.getBoard();
        if (board == null) {
            return Integer.MAX_VALUE; // Keadaan tidak valid
        }

        Piece primaryPiece = board.getPrimaryPiece();
        if (primaryPiece == null) {
            return Integer.MAX_VALUE; // Tidak ada bidak utama, keadaan tidak valid
        }

        Position exitPos = board.getExitPosition();
        if (exitPos == null) {
            return Integer.MAX_VALUE; // Tidak ada posisi keluar, keadaan tidak valid
        }

        Set<Character> blockingPieceIds = new HashSet<>();
        List<Piece> allPieces = board.getPieces();

        if (primaryPiece.getOrientation() == Orientation.HORIZONTAL) {
            // Bidak utama bergerak secara horizontal
            int pieceRow = primaryPiece.getPositions().get(0).getY();
            int primaryPieceMinX = Integer.MAX_VALUE;
            int primaryPieceMaxX = Integer.MIN_VALUE;

            for (Position pos : primaryPiece.getPositions()) {
                if (pos.getX() < primaryPieceMinX) {
                    primaryPieceMinX = pos.getX();
                }
                if (pos.getX() > primaryPieceMaxX) {
                    primaryPieceMaxX = pos.getX();
                }
            }

            // Tentukan rentang X untuk jalur
            int pathStartX, pathEndX;
            if (exitPos.getX() > primaryPieceMaxX) { // Pintu keluar di sebelah kanan bidak utama
                pathStartX = primaryPieceMaxX + 1;
                pathEndX = board.getWidth() - 1; // Periksa hingga tepi kanan papan
            } else if (exitPos.getX() < primaryPieceMinX) { // Pintu keluar di sebelah kiri bidak utama
                pathStartX = primaryPieceMinX - 1;
                pathEndX = 0; // Periksa hingga tepi kiri papan
            } else {
                return 0; // Bidak utama sudah sejajar atau melewati kolom pintu keluar (atau tidak ada jalur)
            }

            // Iterasi sel-sel di jalur horizontal
            for (int x = Math.min(pathStartX, pathEndX); x <= Math.max(pathStartX, pathEndX); x++) {
                Position currentPathPos = new Position(x, pieceRow);
                for (Piece otherPiece : allPieces) {
                    if (otherPiece.isPrimary()) {
                        continue; // Lewati bidak utama itu sendiri
                    }
                    if (otherPiece.getPositions().contains(currentPathPos)) {
                        blockingPieceIds.add(otherPiece.getId());
                        // Tidak perlu break di sini jika satu bidak penghalang bisa memblokir beberapa sel jalur
                        // Namun, karena kita menggunakan Set, duplikat ID akan diabaikan.
                        // Break di sini akan sedikit lebih efisien jika hanya ingin tahu apakah sel ini diblokir.
                        // Untuk menghitung bidak unik, kita biarkan loop otherPiece berlanjut (atau break, tidak masalah karena Set)
                    }
                }
            }

        } else { // Orientation.VERTICAL
            // Bidak utama bergerak secara vertikal
            int pieceCol = primaryPiece.getPositions().get(0).getX();
            int primaryPieceMinY = Integer.MAX_VALUE;
            int primaryPieceMaxY = Integer.MIN_VALUE;

            for (Position pos : primaryPiece.getPositions()) {
                if (pos.getY() < primaryPieceMinY) {
                    primaryPieceMinY = pos.getY();
                }
                if (pos.getY() > primaryPieceMaxY) {
                    primaryPieceMaxY = pos.getY();
                }
            }

            // Tentukan rentang Y untuk jalur
            int pathStartY, pathEndY;
            if (exitPos.getY() > primaryPieceMaxY) { // Pintu keluar di bawah bidak utama
                pathStartY = primaryPieceMaxY + 1;
                pathEndY = board.getHeight() - 1; // Periksa hingga tepi bawah papan
            } else if (exitPos.getY() < primaryPieceMinY) { // Pintu keluar di atas bidak utama
                pathStartY = primaryPieceMinY - 1;
                pathEndY = 0; // Periksa hingga tepi atas papan
            } else {
                return 0; // Bidak utama sudah sejajar atau melewati baris pintu keluar (atau tidak ada jalur)
            }
            
            // Iterasi sel-sel di jalur vertikal
            for (int y = Math.min(pathStartY, pathEndY); y <= Math.max(pathStartY, pathEndY); y++) {
                Position currentPathPos = new Position(pieceCol, y);
                for (Piece otherPiece : allPieces) {
                    if (otherPiece.isPrimary()) {
                        continue; // Lewati bidak utama itu sendiri
                    }
                    if (otherPiece.getPositions().contains(currentPathPos)) {
                        blockingPieceIds.add(otherPiece.getId());
                    }
                }
            }
        }
        return blockingPieceIds.size();
    }

    @Override
    public String getName() {
        return "Blocking Pieces Heuristic";
    }
}