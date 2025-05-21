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
		setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		initializeComponents();
		addListeners();
	}

	private void initializeComponents() {
		String[] algorithms = {"UCS", "A*", "Greedy BFS", "Beam Search (10 beam width)"};
		algorithmSelector = new JComboBox<>(algorithms);
		algorithmSelector.setToolTipText("Pilih algoritma pencarian");

		String[] heuristics = {"Manhattan Distance", "Blocking Pieces"};
		heuristicSelector = new JComboBox<>(heuristics);
		heuristicSelector.setToolTipText("Pilih heuristik (jika diperlukan oleh algoritma)");
		heuristicSelector.setEnabled(false);

		loadButton = new JButton("Muat Papan");
		loadButton.setToolTipText("Muat konfigurasi papan dari file .txt");

		solveButton = new JButton("Selesaikan");
		solveButton.setToolTipText("Cari solusi untuk papan yang dimuat");
		solveButton.setEnabled(false);

		animateButton = new JButton("Animasi");
		animateButton.setToolTipText("Animaskan langkah-langkah solusi");
		animateButton.setEnabled(false);

		animationSpeedSlider = new JSlider(JSlider.HORIZONTAL, 100, 2000, 500);
		animationSpeedSlider.setMajorTickSpacing(500);
		animationSpeedSlider.setMinorTickSpacing(100);
		animationSpeedSlider.setPaintTicks(true);
		animationSpeedSlider.setPaintLabels(true);
		animationSpeedSlider.setToolTipText("Kecepatan animasi (jeda dalam milidetik)");
		animationSpeedSlider.setEnabled(false);

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
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
				fileChooser.setDialogTitle("Pilih File Papan Rush Hour (.txt)");
				fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("File Teks (*.txt)", "txt"));
				int returnValue = fileChooser.showOpenDialog(parent.getFrame());
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					if (parent != null) {
						parent.loadBoard(selectedFile.getAbsolutePath());
					}
				}
			}
		});

		solveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (parent != null) {
					parent.initiateSolveProcess();
				}
			}
		});

		animateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (parent != null) {
					parent.animateSolution();
				}
			}
		});

		algorithmSelector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedAlgorithm = getSelectedAlgorithm();
				if ("A*".equals(selectedAlgorithm) || "Greedy BFS".equals(selectedAlgorithm) || "Beam Search (10 beam width)".equals(selectedAlgorithm)) {
					heuristicSelector.setEnabled(true);
				} else {
					heuristicSelector.setEnabled(false);
					heuristicSelector.setSelectedIndex(-1);
				}
			}
		});

		animationSpeedSlider.addChangeListener(e -> {
			if (!animationSpeedSlider.getValueIsAdjusting() && parent != null) {
				parent.setAnimationSpeed(getAnimationSpeed());
			}
		});
	}

	public void enableLoadButton(boolean enable) {
		loadButton.setEnabled(enable);
	}

	public void enableSolveButton(boolean enable) {
		solveButton.setEnabled(enable);
	}

	public void enableAnimateButton(boolean enable) {
		animateButton.setEnabled(enable);
		animationSpeedSlider.setEnabled(enable);
	}

	public String getSelectedAlgorithm() {
		if (algorithmSelector.getSelectedItem() != null) {
			return (String) algorithmSelector.getSelectedItem();
		}
		return null;
	}

	public String getSelectedHeuristic() {
		if (heuristicSelector.getSelectedItem() != null && heuristicSelector.isEnabled()) {
			return (String) heuristicSelector.getSelectedItem();
		}
		return null;
	}

	public int getAnimationSpeed() {
		return animationSpeedSlider.getValue();
	}

	public boolean isHeuristicEnabled() {
		return heuristicSelector.isEnabled();
	}
}