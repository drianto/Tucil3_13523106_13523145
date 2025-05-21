package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.core.Board;
import model.core.Direction;
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

	public Board readBoardFromFile(String path) {
		List<String> allFileLinesOriginal = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = reader.readLine()) != null) {
				allFileLinesOriginal.add(line);
			}
		} catch (IOException e) {
			System.err.println("Error membaca file papan: " + e.getMessage());
			return null;
		}

		if (allFileLinesOriginal.isEmpty()) {
			System.err.println("Format file papan tidak valid: File kosong.");
			return null;
		}
		
		List<String> allFileLines = new ArrayList<>();
		for (String line : allFileLinesOriginal) {
			allFileLines.add(line.trim());
		}
		allFileLines.removeIf(String::isEmpty);

		if (allFileLines.size() < 2) { 
			System.err.println("Format file papan tidak valid: Jumlah baris kurang untuk dimensi dan N setelah trim.");
			return null;
		}

		try {
			String[] dimensions = allFileLines.get(0).split("\\s+");
			if (dimensions.length != 2) {
				String dimLine = allFileLines.get(0);
				if (dimLine.length() == 2 && Character.isDigit(dimLine.charAt(0)) && Character.isDigit(dimLine.charAt(1))) {
					dimensions = new String[]{String.valueOf(dimLine.charAt(0)), String.valueOf(dimLine.charAt(1))};
				} else {
					System.err.println("Format dimensi papan tidak valid. Diharapkan 'Baris Kolom' (misalnya, '6 6'). Ditemukan: " + allFileLines.get(0));
					return null;
				}
			}
			int numRowsA = Integer.parseInt(dimensions[0]);
			int numColsB = Integer.parseInt(dimensions[1]);

			if (numRowsA <= 0 || numColsB <= 0) {
				System.err.println("Dimensi papan tidak valid: Baris dan kolom harus positif.");
				return null;
			}

			Position exitPosition = null;
			int exitRow = -1, exitCol = -1; 
			boolean exitIsHorizontal = false; 

			List<String> gridContentLines = new ArrayList<>();
			int fileContentStartIndex = 2;

			if (allFileLinesOriginal.size() > fileContentStartIndex) {
				String lineToTest = allFileLinesOriginal.get(fileContentStartIndex);
				int kVisualIndex = -1;
				int kCount = 0;

				for(int i=0; i < lineToTest.length(); i++) {
					if(lineToTest.charAt(i) == 'K') {
						kVisualIndex = i;
						kCount++;
					}
				}

				if (kCount == 1) {
					boolean isDedicatedKLine = true;
					for (int i = 0; i < lineToTest.length(); i++) {
						if (i != kVisualIndex && lineToTest.charAt(i) != ' ' && lineToTest.charAt(i) != '.') {
							isDedicatedKLine = false;
							break;
						}
					}
					if (isDedicatedKLine) {
						if (kVisualIndex >= numColsB) {
							 System.err.println("Error: Posisi 'K' ("+ kVisualIndex +") di luar batas kolom papan ("+numColsB+") pada baris K di atas grid.");
							 return null;
						}
						exitPosition = new Position(kVisualIndex, -1); 
						exitCol = kVisualIndex;
						exitRow = -1;
						exitIsHorizontal = false;
						fileContentStartIndex++;
					}
				}
			}

			if (allFileLinesOriginal.size() < fileContentStartIndex + numRowsA) {
				System.err.println("Format file papan tidak valid: Jumlah baris kurang untuk konfigurasi papan inti. Tersedia (setelah N dan K atas): " + (allFileLinesOriginal.size() - fileContentStartIndex) + ", Butuh: " + numRowsA);
				return null;
			}
			for (int i = 0; i < numRowsA; i++) {
				gridContentLines.add(allFileLinesOriginal.get(fileContentStartIndex + i));
			}
			int nextLineAfterGridIndex = fileContentStartIndex + numRowsA;


			if (exitPosition == null && allFileLinesOriginal.size() > nextLineAfterGridIndex) {
				 String lineToTest = allFileLinesOriginal.get(nextLineAfterGridIndex);
				 int kVisualIndex = -1;
				 int kCount = 0;
				 for(int i=0; i < lineToTest.length(); i++) {
					 if(lineToTest.charAt(i) == 'K') {
						 kVisualIndex = i;
						 kCount++;
					 }
				 }
				 if (kCount == 1) { 
					boolean isDedicatedKLine = true;
					for (int i = 0; i < lineToTest.length(); i++) {
						if (i != kVisualIndex && lineToTest.charAt(i) != ' ' && lineToTest.charAt(i) != '.') {
							isDedicatedKLine = false;
							break;
						}
					}
					if (isDedicatedKLine) {
						if (kVisualIndex >= numColsB) {
							 System.err.println("Error: Posisi 'K' ("+ kVisualIndex +") di luar batas kolom papan ("+numColsB+") pada baris K di bawah grid.");
							 return null;
						}
						exitPosition = new Position(kVisualIndex, numRowsA); 
						exitCol = kVisualIndex;
						exitRow = numRowsA;
						exitIsHorizontal = false;
					}
				 }
			}

			ArrayList<Piece> pieces = new ArrayList<>();
			Piece primaryPieceObj = null;
			char[][] gridForValidation = new char[numRowsA][numColsB];
			for (int r_grid = 0; r_grid < numRowsA; r_grid++) {
				for (int c_grid = 0; c_grid < numColsB; c_grid++) {
					gridForValidation[r_grid][c_grid] = '.';
				}
			}
			Map<Character, ArrayList<Position>> pieceCandidatePositions = new HashMap<>();

			for (int r = 0; r < numRowsA; r++) {
				String currentBoardLineOriginal = gridContentLines.get(r);
				String lineForPieceParsing = currentBoardLineOriginal;

				boolean currentRowHasHorizontalExit = false;
				if (exitPosition == null) {
					if (lineForPieceParsing.length() == numColsB + 1 && lineForPieceParsing.endsWith("K")) {
						exitPosition = new Position(numColsB, r);
						exitRow = r; 
						exitCol = numColsB; 
						exitIsHorizontal = true;
						lineForPieceParsing = lineForPieceParsing.substring(0, numColsB); 
						currentRowHasHorizontalExit = true;
					} else if (lineForPieceParsing.length() == numColsB + 1 && lineForPieceParsing.startsWith("K")) {
						exitPosition = new Position(-1, r);
						exitRow = r; 
						exitCol = -1; 
						exitIsHorizontal = true; 
						lineForPieceParsing = lineForPieceParsing.substring(1); 
						currentRowHasHorizontalExit = true;
					}
				} else if ((lineForPieceParsing.length() == numColsB + 1 && (lineForPieceParsing.endsWith("K") || lineForPieceParsing.startsWith("K")))) {
					System.err.println("Error: Lebih dari satu Pintu Keluar 'K' ditemukan (K atas/bawah/sebelumnya dan K samping).");
					return null;
				}
				
				if (!currentRowHasHorizontalExit && lineForPieceParsing.length() != numColsB) {
					if (lineForPieceParsing.startsWith(" ") && lineForPieceParsing.length() == numColsB + 1) {
						lineForPieceParsing = lineForPieceParsing.substring(1);
					} else {
						String trimmedLine = lineForPieceParsing.trim();
						if (trimmedLine.length() == numColsB) {
							lineForPieceParsing = trimmedLine;
						}
					}
				}

				if (lineForPieceParsing.length() != numColsB) {
					System.err.println("Error: Baris grid " + (r + 1) + " (\"" + currentBoardLineOriginal + "\") setelah proses K samping dan penyesuaian padding memiliki panjang " + lineForPieceParsing.length() + ", diharapkan " + numColsB + ".");
					return null;
				}

				for (int c = 0; c < numColsB; c++) {
					char cellChar = lineForPieceParsing.charAt(c);
					if (cellChar == 'K') {
						if (exitPosition == null) {
							System.err.println("Error: Karakter 'K' ditemukan di dalam sel grid (" + c + "," + r + ") yang tidak diproses sebagai pintu keluar tepi.");
							return null;
						} else {
							System.err.println("Error: Lebih dari satu Pintu Keluar 'K' ditemukan (K sebelumnya dan K dalam sel grid).");
							return null;
						}
					} else if (cellChar != '.' && cellChar != ' ') {
						pieceCandidatePositions.putIfAbsent(cellChar, new ArrayList<>()); 
						pieceCandidatePositions.get(cellChar).add(new Position(c, r)); 
						if (gridForValidation[r][c] != '.') { 
							System.err.println("Error: Piece tumpang tindih di (" + c + "," + r + "). Sel ditempati oleh '" + gridForValidation[r][c] + "' dan '" + cellChar + "'.");
							return null;
						}
						gridForValidation[r][c] = cellChar; 
					}
				}
			}

			if (exitPosition == null) {
				System.err.println("Error: Tidak ada Pintu Keluar 'K' yang ditemukan dalam konfigurasi.");
				return null;
			}

			for (Map.Entry<Character, ArrayList<Position>> entry : pieceCandidatePositions.entrySet()) {
				char id = entry.getKey();
				ArrayList<Position> positions = entry.getValue();
				if (positions.isEmpty()) continue;

				Collections.sort(positions, Comparator.comparingInt(Position::getY).thenComparingInt(Position::getX));

				Orientation orientation;
				int pieceLength = positions.size();
				
				boolean allYSame = true;
				boolean allXSame = true;
				if (pieceLength > 0) { 
					int firstY = positions.get(0).getY();
					int firstX = positions.get(0).getX();

					for (int i = 1; i < pieceLength; i++) {
						if (positions.get(i).getY() != firstY) allYSame = false;
						if (positions.get(i).getX() != firstX) allXSame = false;
					}

					if (pieceLength == 1) {
						orientation = exitIsHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
					} else if (allYSame && !allXSame) { 
						orientation = Orientation.HORIZONTAL;
						for (int i = 0; i < pieceLength - 1; i++) { 
							if (positions.get(i+1).getX() - positions.get(i).getX() != 1 || positions.get(i+1).getY() != positions.get(i).getY()) {
								System.err.println("Error: Piece horizontal '" + id + "' tidak kontinu. Posisi: " + positions);
								return null;
							}
						}
					} else if (allXSame && !allYSame) { 
						orientation = Orientation.VERTICAL;
						for (int i = 0; i < pieceLength - 1; i++) { 
							if (positions.get(i+1).getY() - positions.get(i).getY() != 1 || positions.get(i+1).getX() != positions.get(i).getX()) {
								System.err.println("Error: Piece vertikal '" + id + "' tidak kontinu. Posisi: " + positions);
								return null;
							}
						}
					} else {
						System.err.println("Error: Piece '" + id + "' memiliki tata letak yang tidak valid (bukan garis lurus atau panjang tidak sesuai). Posisi: " + positions);
						return null;
					}
				} else { continue; } 

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

			Orientation primaryOrientation = primaryPieceObj.getOrientation();

			if (exitIsHorizontal) {
				if (primaryOrientation != Orientation.HORIZONTAL) {
					System.err.println("Error: Pintu Keluar 'K' berada di dinding horizontal (kiri/kanan), tetapi Primary Piece 'P' ("+primaryPieceObj.getPositions()+") berorientasi vertikal.");
					return null;
				}
				boolean rowMatch = false;
				for (Position pPos : primaryPieceObj.getPositions()) {
					if (pPos.getY() == exitRow) {
						rowMatch = true;
						break;
					}
				}
				if (!rowMatch) {
					System.err.println("Error: Pintu Keluar 'K' horizontal di baris grid " + exitRow + " tidak sejajar dengan baris Primary Piece 'P'.");
					return null;
				}
			} else {
				if (primaryOrientation != Orientation.VERTICAL) {
					System.err.println("Error: Pintu Keluar 'K' berada di jalur vertikal (atas/bawah), tetapi Primary Piece 'P' ("+primaryPieceObj.getPositions()+") berorientasi horizontal.");
					return null;
				}
				boolean colMatch = false;
				for (Position pPos : primaryPieceObj.getPositions()) {
					if (pPos.getX() == exitCol) {
						colMatch = true;
						break;
					}
				}
				if (!colMatch) {
					System.err.println("Error: Pintu Keluar 'K' vertikal di kolom grid " + exitCol + " tidak sejajar dengan kolom Primary Piece 'P'.");
					return null;
				}
			}
			return new Board(numColsB, numRowsA, pieces, exitPosition);

		} catch (NumberFormatException e) {
			System.err.println("Error memparsing angka untuk dimensi atau N: " + e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			System.err.println("Error memproses baris file (kemungkinan terlalu sedikit baris atau format salah): " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Terjadi error tak terduga saat parsing papan: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}