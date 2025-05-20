package view.gui;

import java.awt.*;
import java.util.HashMap;
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

	private int xOffset = 0;
	private int yOffset = 0;

	public static final int DEFAULT_CELL_SIZE = 50;

	public BoardPanel(int cellSize) {
		this.cellSize = cellSize > 0 ? cellSize : DEFAULT_CELL_SIZE;
		setBackground(new Color(220, 220, 220));
		initPieceColors();
		setPreferredSize(new Dimension(this.cellSize * 6, this.cellSize * 6));
	}

	private void initPieceColors() {
		pieceColorMap = new HashMap<>();
		pieceColorMap.put('P', new Color(220, 60, 60));  
		pieceColorMap.put('A', new Color(70, 130, 180)); 
		pieceColorMap.put('B', new Color(60, 179, 113)); 
		pieceColorMap.put('C', new Color(255, 165, 0));  
		pieceColorMap.put('D', new Color(147, 112, 219));
		pieceColorMap.put('E', new Color(240, 230, 140));
		pieceColorMap.put('F', new Color(0, 191, 255));  
		pieceColorMap.put('G', new Color(218, 112, 214));
		pieceColorMap.put('H', new Color(127, 255, 0));  
		pieceColorMap.put('I', new Color(255, 105, 180));
		pieceColorMap.put('J', new Color(32, 178, 170)); 
		pieceColorMap.put('L', new Color(139, 69, 19));  
		pieceColorMap.put('M', new Color(112, 128, 144));
		pieceColorMap.put('N', new Color(190, 128, 144));
		pieceColorMap.put('O', new Color(112, 255, 144));
		pieceColorMap.put('Q', new Color(112, 125, 144));
		pieceColorMap.put('R', new Color(112, 128, 144));
		pieceColorMap.put('S', new Color(239, 128, 123));
		pieceColorMap.put('T', new Color(233, 128, 32));
		pieceColorMap.put('U', new Color(178, 128, 189));
		pieceColorMap.put('V', new Color(234, 128, 1));
		pieceColorMap.put('W', new Color(112, 32, 43));
		pieceColorMap.put('X', new Color(1, 178, 144));
		pieceColorMap.put('Y', new Color(0, 128, 128));
		pieceColorMap.put('Z', new Color(135, 196, 182));
		pieceColorMap.put('K', new Color(0, 100, 0));    
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
		}
		calculateOffsets();
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
			drawExit(g2d, xOffset, yOffset);
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
				g2d.setColor(new Color(255, 255, 0, 100));
				g2d.fillRoundRect(piecePixelX + inset, piecePixelY + inset,
								  piecePixelWidth - 2 * inset, piecePixelHeight - 2 * inset,
								  15, 15);
				g2d.setColor(Color.ORANGE.darker());
				g2d.setStroke(new BasicStroke(2.5f));
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

	private void drawExit(Graphics2D g2d, int offsetX, int offsetY) {
		if (board == null || board.getExitPosition() == null) return;

		Position exitPos = board.getExitPosition();
		int exitX = exitPos.getX();
		int exitY = exitPos.getY();

		Piece primaryPiece = board.getPrimaryPiece();
		if (primaryPiece == null) return;

		g2d.setColor(getPieceColor('K'));
		Font exitFont = new Font("SansSerif", Font.BOLD, cellSize * 2 / 3);
		g2d.setFont(exitFont);
		FontMetrics fm = g2d.getFontMetrics();
		String exitChar = "K";
		int charWidth = fm.stringWidth(exitChar);
		int charHeight = fm.getAscent() - fm.getDescent();

		int drawKx;
		int drawKy;

		if (primaryPiece.getOrientation() == Orientation.HORIZONTAL) {
			int yPosCell = offsetY + exitY * cellSize;

			if (exitX == -1) {
				drawKx = offsetX - cellSize / 2 - charWidth / 2;
				drawKy = yPosCell + cellSize / 2 + charHeight / 2;
			} else if (exitX == board.getWidth()) {
				drawKx = offsetX + board.getWidth() * cellSize + cellSize / 2 - charWidth / 2;
				drawKy = yPosCell + cellSize / 2 + charHeight / 2;
			} else {
				return;
			}
		} else {
			int xPosCell = offsetX + exitX * cellSize;

			if (exitY == -1) {
							  
							  
							  
				drawKx = xPosCell + cellSize / 2 - charWidth / 2;
				drawKy = offsetY - cellSize / 2 + charHeight / 2;

			} else if (exitY == board.getHeight()) {
				drawKx = xPosCell + cellSize / 2 - charWidth / 2;
				drawKy = offsetY + board.getHeight() * cellSize + cellSize / 2 + charHeight / 2;
			}
			 else if (exitY == 0 && exitX >=0 && exitX < board.getWidth()) {
				drawKx = xPosCell + cellSize / 2 - charWidth / 2;
				drawKy = offsetY - cellSize / 2 + charHeight / 2;
			} else if (exitY == board.getHeight() - 1 && exitX >=0 && exitX < board.getWidth()) {
				drawKx = xPosCell + cellSize / 2 - charWidth / 2;
				drawKy = offsetY + board.getHeight() * cellSize + cellSize / 2 + charHeight / 2;
			}
			else {
				 return;
			}
		}

		if (drawKx != 0 || drawKy != 0) {
			g2d.drawString(exitChar, drawKx, drawKy);
		}
	}

	@Override
	public void doLayout() {
		super.doLayout();
		calculateOffsets();
	}
}