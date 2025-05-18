package view.gui;

import java.util.Map;
import javax.swing.*;
import java.awt.*;

import model.core.Board;

public class BoardPanel extends JPanel {
    private Board board;
    private int cellSize;
    private Map<Character, Color> PieceColor;

    public BoardPanel(int cellSize) {
        this.cellSize = cellSize;
        setPreferredSize(new Dimension(cellSize * 6, cellSize * 6)); // Asumsi papan 6x6
        setBackground(Color.LIGHT_GRAY); // Warna latar belakang awal
    }

    public void setBoard (Board board) {
        this.board = board;
        repaint();
    }

    public void highlightPiece(char pieceId) {

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // Logika untuk drawGrid(g2d) dan drawPieces(g2d) akan ditambahkan di sini
        // Contoh sederhana:
        if (board == null) {
            g2d.drawString("Papan belum dimuat.", 50, 50);
        } else {
            // Gambar grid dan bidak berdasarkan this.board dan this.cellSize
        }
    }

    public void drawBoard(Graphics2D g2d) {

    }

    public void drawPieces(Graphics2D g2d) {
        
    }

    public void drawGrid(Graphics2D g2d) {
        
    }
}
