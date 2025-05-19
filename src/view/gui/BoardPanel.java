package view.gui;

import java.awt.*;
import java.util.HashMap; // Diperlukan jika menggunakan AffineTransform, tapi tidak dalam solusi ini
import java.util.Map;
import javax.swing.*;
import model.core.Board;
import model.core.Orientation;
import model.core.Piece;
import model.core.Position;

public class BoardPanel extends JPanel {
    private Board board;
    private final int cellSize;
    private Map<Character, Color> pieceColorMap;
    private char highlightedPieceId = ' ';
    private boolean shouldHighlight = false;

    // Offset untuk menengahkan papan di dalam panel
    private int xOffset = 0;
    private int yOffset = 0;

    public static final int DEFAULT_CELL_SIZE = 50; // Contoh konstanta

    public BoardPanel(int cellSize) {
        this.cellSize = cellSize > 0 ? cellSize : DEFAULT_CELL_SIZE;
        setBackground(new Color(220, 220, 220));
        initPieceColors();
        // Ukuran awal, akan disesuaikan, dan digunakan untuk kalkulasi penengahan
        setPreferredSize(new Dimension(this.cellSize * 6, this.cellSize * 6)); // Ukuran default
    }

    private void initPieceColors() {
        pieceColorMap = new HashMap<>();
        pieceColorMap.put('P', new Color(220, 60, 60));   // Merah untuk Utama (Primary)
        pieceColorMap.put('A', new Color(70, 130, 180));  // Steel Blue
        pieceColorMap.put('B', new Color(60, 179, 113));  // Medium Sea Green
        pieceColorMap.put('C', new Color(255, 165, 0));   // Orange
        pieceColorMap.put('D', new Color(147, 112, 219)); // Medium Purple
        pieceColorMap.put('E', new Color(240, 230, 140)); // Khaki
        pieceColorMap.put('F', new Color(0, 191, 255));   // Deep Sky Blue
        pieceColorMap.put('G', new Color(218, 112, 214)); // Orchid
        pieceColorMap.put('H', new Color(127, 255, 0));   // Chartreuse
        pieceColorMap.put('I', new Color(255, 105, 180)); // Hot Pink
        pieceColorMap.put('J', new Color(32, 178, 170));  // Light Sea Green
        pieceColorMap.put('L', new Color(139, 69, 19));   // Saddle Brown
        pieceColorMap.put('M', new Color(112, 128, 144)); // Slate Gray
        // Tambahkan warna untuk 'K' jika diperlukan, atau gunakan warna default
        pieceColorMap.put('K', new Color(0, 100, 0));     // Hijau tua untuk 'K'
    }

    private Color getPieceColor(char pieceId) {
        if (pieceId == 'P' && pieceColorMap.containsKey('P')) {
            return pieceColorMap.get('P');
        }
        return pieceColorMap.computeIfAbsent(pieceId, k ->
            new Color(Math.abs(k * 30 + 50) % 206 + 50, Math.abs(k * 50 + 30) % 206 + 50, Math.abs(k * 70 + 20) % 206 + 50)
        );
    }

    public void setBoard(Board board) {
        this.board = board;
        if (board != null) {
            // PreferredSize panel tetap, atau bisa diatur sesuai kebutuhan UI keseluruhan.
        }
        calculateOffsets(); // Hitung offset setiap kali papan baru di-set atau panel di-resize
        revalidate();
        repaint();
    }

    public void highlightPiece(char pieceId, boolean highlight) {
        this.highlightedPieceId = pieceId;
        this.shouldHighlight = highlight;
        repaint();
    }

    private void calculateOffsets() {
        if (board == null) {
            xOffset = 0;
            yOffset = 0;
            return;
        }
        int boardPixelWidth = board.getWidth() * cellSize;
        int boardPixelHeight = board.getHeight() * cellSize;

        xOffset = Math.max(0, (getWidth() - boardPixelWidth) / 2);
        yOffset = Math.max(0, (getHeight() - boardPixelHeight) / 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        calculateOffsets();

        if (board == null) {
            String msg = "Papan belum dimuat.";
            FontMetrics fm = g2d.getFontMetrics();
            int msgWidth = fm.stringWidth(msg);
            g2d.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2);
        } else {
            drawGrid(g2d, xOffset, yOffset);
            drawPieces(g2d, xOffset, yOffset);
            drawExit(g2d, xOffset, yOffset); // Panggil metode drawExit yang sudah dimodifikasi
        }
    }

    private void drawGrid(Graphics2D g2d, int offsetX, int offsetY) {
        g2d.setColor(Color.GRAY.brighter());
        int boardPixelWidth = board.getWidth() * cellSize;
        int boardPixelHeight = board.getHeight() * cellSize;

        for (int i = 0; i <= board.getWidth(); i++) {
            g2d.drawLine(offsetX + i * cellSize, offsetY, offsetX + i * cellSize, offsetY + boardPixelHeight);
        }
        for (int i = 0; i <= board.getHeight(); i++) {
            g2d.drawLine(offsetX, offsetY + i * cellSize, offsetX + boardPixelWidth, offsetY + i * cellSize);
        }
    }

    private void drawPieces(Graphics2D g2d, int offsetX, int offsetY) {
        for (Piece piece : board.getPieces()) {
            if (piece.getPositions() == null || piece.getPositions().isEmpty()) continue;

            Color pieceColor = getPieceColor(piece.getId());
            g2d.setColor(pieceColor);

            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

            for (Position pos : piece.getPositions()) {
                minX = Math.min(minX, pos.getX());
                minY = Math.min(minY, pos.getY());
                maxX = Math.max(maxX, pos.getX());
                maxY = Math.max(maxY, pos.getY());
            }

            int piecePixelX = offsetX + minX * cellSize;
            int piecePixelY = offsetY + minY * cellSize;
            int piecePixelWidth = (maxX - minX + 1) * cellSize;
            int piecePixelHeight = (maxY - minY + 1) * cellSize;

            int inset = 3;
            g2d.fillRoundRect(piecePixelX + inset, piecePixelY + inset,
                              piecePixelWidth - 2 * inset, piecePixelHeight - 2 * inset,
                              15, 15);

            g2d.setColor(pieceColor.darker());
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(piecePixelX + inset, piecePixelY + inset,
                              piecePixelWidth - 2 * inset, piecePixelHeight - 2 * inset,
                              15, 15);

            if (shouldHighlight && piece.getId() == highlightedPieceId) {
                g2d.setColor(new Color(255, 255, 0, 100)); // Warna highlight kuning transparan
                g2d.fillRoundRect(piecePixelX + inset, piecePixelY + inset,
                                  piecePixelWidth - 2 * inset, piecePixelHeight - 2 * inset,
                                  15, 15);
                g2d.setColor(Color.ORANGE.darker());
                g2d.setStroke(new BasicStroke(2.5f)); // Stroke lebih tebal untuk highlight
                g2d.drawRoundRect(piecePixelX + inset, piecePixelY + inset,
                                  piecePixelWidth - 2 * inset, piecePixelHeight - 2 * inset,
                                  15, 15);
            }

            g2d.setColor(Color.BLACK);
            String pieceIdStr = String.valueOf(piece.getId());
            Font font = new Font("SansSerif", Font.BOLD, Math.max(10, cellSize / 3));
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            int stringX = piecePixelX + (piecePixelWidth - fm.stringWidth(pieceIdStr)) / 2;
            int stringY = piecePixelY + (piecePixelHeight - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(pieceIdStr, stringX, stringY);
        }
    }

    /**
     * Menggambar karakter 'K' yang menunjukkan pintu keluar.
     * Karakter 'K' digambar di tepi grid, di luar sel terakhir tempat mobil 'P' bisa keluar.
     */
    private void drawExit(Graphics2D g2d, int offsetX, int offsetY) {
        if (board == null || board.getExitPosition() == null) return;

        Position exitPos = board.getExitPosition(); // Ini adalah posisi konseptual 'K'
        int exitX = exitPos.getX(); // Kolom konseptual tempat 'K' berada atau yang dituju
        int exitY = exitPos.getY(); // Baris konseptual tempat 'K' berada atau yang dituju

        Piece primaryPiece = board.getPrimaryPiece();
        if (primaryPiece == null) return; // Tidak ada mobil utama, tidak ada exit yang relevan

        g2d.setColor(getPieceColor('K')); // Gunakan warna yang didefinisikan untuk 'K'
        Font exitFont = new Font("SansSerif", Font.BOLD, cellSize * 2 / 3); // Font yang lebih besar untuk 'K'
        g2d.setFont(exitFont);
        FontMetrics fm = g2d.getFontMetrics();
        String exitChar = "K";
        int charWidth = fm.stringWidth(exitChar);
        int charHeight = fm.getAscent() - fm.getDescent(); // Perkiraan tinggi karakter yang terlihat

        // Posisi untuk menggambar 'K'
        int drawKx;
        int drawKy;

        if (primaryPiece.getOrientation() == Orientation.HORIZONTAL) {
            // Pintu keluar di dinding kiri atau kanan, sejajar dengan baris mobil utama
            // exitY adalah baris di mana mobil utama akan berada saat keluar
            int yPosCell = offsetY + exitY * cellSize; // Y atas dari sel tempat keluar

            if (exitX == -1) { // Dinding kiri (Mobil keluar ke kiri)
                // 'K' digambar di sebelah kiri sel (0, exitY)
                drawKx = offsetX - cellSize / 2 - charWidth / 2; // Tengahkan 'K' di "sel" virtual di kiri
                drawKy = yPosCell + cellSize / 2 + charHeight / 2;
            } else if (exitX == board.getWidth()) { // Dinding kanan (Mobil keluar ke kanan)
                // 'K' digambar di sebelah kanan sel (board.getWidth()-1, exitY)
                drawKx = offsetX + board.getWidth() * cellSize + cellSize / 2 - charWidth / 2; // Tengahkan 'K' di "sel" virtual di kanan
                drawKy = yPosCell + cellSize / 2 + charHeight / 2;
            } else {
                return; // Jika exitPosition tidak berada di tepi, jangan gambar 'K' dengan logika ini
            }
        } else { // Orientation.VERTICAL
            // Pintu keluar di dinding atas atau bawah, sejajar dengan kolom mobil utama
            // exitX adalah kolom di mana mobil utama akan berada saat keluar
            int xPosCell = offsetX + exitX * cellSize; // X kiri dari sel tempat keluar

            if (exitY == -1) { // Dinding atas (Mobil keluar ke atas dari baris 0)
                               // Sebenarnya, jika Y = 0, mobil keluar ke atas dari sel (exitX, 0)
                               // maka exitPosition.getY() seharusnya 0, dan kita menggambar 'K' di atasnya.
                               // Jika exitPosition.getY() adalah -1, itu lebih eksplisit.
                drawKx = xPosCell + cellSize / 2 - charWidth / 2;
                drawKy = offsetY - cellSize / 2 + charHeight / 2; // Tengahkan 'K' di "sel" virtual di atas

            } else if (exitY == board.getHeight()) { // Dinding bawah (Mobil keluar ke bawah dari baris terakhir)
                                                    // Mirip dengan di atas, jika Y = board.getHeight()-1 adalah baris terakhir,
                                                    // maka exitPosition.getY() seharusnya board.getHeight().
                drawKx = xPosCell + cellSize / 2 - charWidth / 2;
                drawKy = offsetY + board.getHeight() * cellSize + cellSize / 2 + charHeight / 2; // Tengahkan 'K' di "sel" virtual di bawah
            }
            // Kasus di mana exitY adalah baris 0 atau board.getHeight()-1 dan K ada di dalam grid
            // (misalnya, K adalah sel tujuan di baris 0 atau baris terakhir) juga bisa ditangani di sini
            // jika model datanya mendukung itu.
            // Namun, berdasarkan kode drawExit sebelumnya, K adalah konseptual di luar grid.
             else if (exitY == 0 && exitX >=0 && exitX < board.getWidth()) { // Dinding atas (K di dalam grid pada baris 0, keluar ke atas)
                // Ini berarti mobil 'P' harus mencapai sel (exitX, 0) dan kemudian keluar ke atas.
                // 'K' ditempatkan di atas sel (exitX, 0).
                drawKx = xPosCell + cellSize / 2 - charWidth / 2;
                drawKy = offsetY - cellSize / 2 + charHeight / 2; // Sesuaikan jika ingin 'K' lebih dekat ke garis
            } else if (exitY == board.getHeight() - 1 && exitX >=0 && exitX < board.getWidth()) { // Dinding bawah (K di dalam grid pada baris terakhir, keluar ke bawah)
                // Mobil 'P' harus mencapai sel (exitX, board.getHeight() - 1) dan kemudian keluar ke bawah.
                // 'K' ditempatkan di bawah sel (exitX, board.getHeight() - 1).
                drawKx = xPosCell + cellSize / 2 - charWidth / 2;
                drawKy = offsetY + board.getHeight() * cellSize + cellSize / 2 + charHeight / 2; // Sesuaikan
            }
            else {
                 return; // Jika exitPosition tidak berada di tepi, jangan gambar 'K'
            }
        }

        if (drawKx != 0 || drawKy != 0) { // Hanya gambar jika posisi K telah ditentukan
            g2d.drawString(exitChar, drawKx, drawKy);
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();
        calculateOffsets();
    }
}