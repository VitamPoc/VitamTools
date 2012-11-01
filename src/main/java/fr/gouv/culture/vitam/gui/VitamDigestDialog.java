/**
 * This file is part of Vitam Project.
 * 
 * Copyright 2010, Frederic Bregier, and individual contributors by the @author tags. See the
 * COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 * 
 * All Vitam Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Vitam is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Vitam. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fr.gouv.culture.vitam.gui;

import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JLabel;

import fr.gouv.culture.vitam.digest.DigestCompute;
import fr.gouv.culture.vitam.utils.FileExtensionFilter;
import fr.gouv.culture.vitam.utils.StaticValues;

import javax.swing.JTextField;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.border.CompoundBorder;

/**
 * Dialog to handle configuration change in the GUI
 * 
 * @author "Frederic Bregier"
 * 
 */
public class VitamDigestDialog extends JPanel {
	private static final long serialVersionUID = 5129887729538501977L;
	JFrame frame;
	private VitamGui vitamGui;
	private static boolean fromMain = false;
	private JTextField source;
	private JTextField destination;
	private JTextField globaldir;
	private JTextField tarzip;
	private JCheckBox chckbxTarzipAssoci;
	private JCheckBox chckbxFichierGlobal;
	private JCheckBox chckbxUnFichierPar;
	private JButton btnTar;
	private JButton btnGlobal;
	private JTextField prefix;

	/**
	 * @param frame
	 *            the parent frame
	 * @param vitamGui
	 *            the VitamGui associated
	 */
	public VitamDigestDialog(JFrame frame, VitamGui vitamGui) {
		super(new BorderLayout());
		this.vitamGui = vitamGui;
		this.frame = frame;
		setBorder(new CompoundBorder());

		JPanel buttonPanel = new JPanel();
		GridBagLayout buttons = new GridBagLayout();
		buttons.columnWidths = new int[] { 194, 124, 0, 0, 0 };
		buttons.rowHeights = new int[] { 0, 0 };
		buttons.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		buttons.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		buttonPanel.setLayout(buttons);
		add(buttonPanel, BorderLayout.SOUTH);

		JButton btnRunDigest = new JButton(StaticValues.LBL.tools_dir_digest.get());
		btnRunDigest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runDigest();
			}
		});
		GridBagConstraints gbc_btnRunDigest = new GridBagConstraints();
		gbc_btnRunDigest.insets = new Insets(0, 0, 0, 5);
		gbc_btnRunDigest.gridx = 0;
		gbc_btnRunDigest.gridy = 0;
		buttonPanel.add(btnRunDigest, gbc_btnRunDigest);

		String text = StaticValues.LBL.button_cancel.get();
		if (fromMain) {
			text += " " + StaticValues.LBL.button_exit.get();
		}
		JButton btnCancel = new JButton(text);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 1;
		gbc_btnCancel.gridy = 0;
		buttonPanel.add(btnCancel, gbc_btnCancel);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);

		JPanel xmlFilePanel = new JPanel();
		tabbedPane.addTab("Digest Context", null, xmlFilePanel, null);
		GridBagLayout gbl_xmlFilePanel = new GridBagLayout();
		gbl_xmlFilePanel.columnWidths = new int[] { 21, 38, 86, 0, 45, 86, 72, 34, 0 };
		gbl_xmlFilePanel.rowHeights = new int[] { 0, 20, 0, 0, 0, 0, 0, 0, 0 };
		gbl_xmlFilePanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0,
				Double.MIN_VALUE };
		gbl_xmlFilePanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		xmlFilePanel.setLayout(gbl_xmlFilePanel);

		JLabel lblXsd = new JLabel("Source");
		GridBagConstraints gbc_lblXsd = new GridBagConstraints();
		gbc_lblXsd.anchor = GridBagConstraints.EAST;
		gbc_lblXsd.insets = new Insets(0, 0, 5, 5);
		gbc_lblXsd.gridx = 1;
		gbc_lblXsd.gridy = 0;
		xmlFilePanel.add(lblXsd, gbc_lblXsd);

		source = new JTextField();
		GridBagConstraints gbc_source = new GridBagConstraints();
		gbc_source.gridwidth = 3;
		gbc_source.insets = new Insets(0, 0, 5, 5);
		gbc_source.fill = GridBagConstraints.HORIZONTAL;
		gbc_source.gridx = 2;
		gbc_source.gridy = 0;
		xmlFilePanel.add(source, gbc_source);
		source.setColumns(10);

		JButton btnSource = new JButton();
		btnSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File file = openFile(source.getText(), StaticValues.LBL.tools_dirfile.get(), null);
				if (file != null) {
					source.setText(file.getAbsolutePath());
				}
			}
		});
		btnSource.setMargin(new Insets(2, 2, 2, 2));
		btnSource.setIcon(new ImageIcon(VitamDigestDialog.class
				.getResource(VitamGui.RESOURCES_IMG_CHECKFILES_PNG)));
		GridBagConstraints gbc_btnSource = new GridBagConstraints();
		gbc_btnSource.insets = new Insets(0, 0, 5, 5);
		gbc_btnSource.gridx = 5;
		gbc_btnSource.gridy = 0;
		xmlFilePanel.add(btnSource, gbc_btnSource);

		JLabel lblXsdRoot = new JLabel("Destination");
		GridBagConstraints gbc_lblXsdRoot = new GridBagConstraints();
		gbc_lblXsdRoot.anchor = GridBagConstraints.EAST;
		gbc_lblXsdRoot.insets = new Insets(0, 0, 5, 5);
		gbc_lblXsdRoot.gridx = 1;
		gbc_lblXsdRoot.gridy = 1;
		xmlFilePanel.add(lblXsdRoot, gbc_lblXsdRoot);

		destination = new JTextField();
		GridBagConstraints gbc_destination = new GridBagConstraints();
		gbc_destination.gridwidth = 3;
		gbc_destination.insets = new Insets(0, 0, 5, 5);
		gbc_destination.fill = GridBagConstraints.HORIZONTAL;
		gbc_destination.gridx = 2;
		gbc_destination.gridy = 1;
		xmlFilePanel.add(destination, gbc_destination);
		destination.setColumns(10);

		JButton btnDestination = new JButton();
		btnDestination.setMargin(new Insets(2, 2, 2, 2));
		btnDestination.setIcon(new ImageIcon(VitamDigestDialog.class
				.getResource(VitamGui.RESOURCES_IMG_CHECKFILES_PNG)));
		btnDestination.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File file = openDir(destination.getText(), StaticValues.LBL.tools_dir.get());
				if (file != null) {
					destination.setText(file.getAbsolutePath());
				}
			}
		});
		GridBagConstraints gbc_btnDestination = new GridBagConstraints();
		gbc_btnDestination.insets = new Insets(0, 0, 5, 5);
		gbc_btnDestination.gridx = 5;
		gbc_btnDestination.gridy = 1;
		xmlFilePanel.add(btnDestination, gbc_btnDestination);

		chckbxTarzipAssoci = new JCheckBox("Tar/Zip associé");
		chckbxTarzipAssoci.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tarzip.setEnabled(chckbxTarzipAssoci.isSelected());
				btnTar.setEnabled(chckbxTarzipAssoci.isSelected());
			}
		});
		GridBagConstraints gbc_chckbxTarzipAssoci = new GridBagConstraints();
		gbc_chckbxTarzipAssoci.anchor = GridBagConstraints.WEST;
		gbc_chckbxTarzipAssoci.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxTarzipAssoci.gridx = 1;
		gbc_chckbxTarzipAssoci.gridy = 2;
		xmlFilePanel.add(chckbxTarzipAssoci, gbc_chckbxTarzipAssoci);

		JLabel lblDocumentField = new JLabel("Tar/Zip");
		GridBagConstraints gbc_lblDocumentField = new GridBagConstraints();
		gbc_lblDocumentField.anchor = GridBagConstraints.EAST;
		gbc_lblDocumentField.insets = new Insets(0, 0, 5, 5);
		gbc_lblDocumentField.gridx = 2;
		gbc_lblDocumentField.gridy = 2;
		xmlFilePanel.add(lblDocumentField, gbc_lblDocumentField);

		tarzip = new JTextField();
		GridBagConstraints gbc_tarzip = new GridBagConstraints();
		gbc_tarzip.gridwidth = 3;
		gbc_tarzip.insets = new Insets(0, 0, 5, 5);
		gbc_tarzip.fill = GridBagConstraints.HORIZONTAL;
		gbc_tarzip.gridx = 3;
		gbc_tarzip.gridy = 2;
		xmlFilePanel.add(tarzip, gbc_tarzip);
		tarzip.setColumns(10);

		btnTar = new JButton();
		btnTar.setMargin(new Insets(2, 2, 2, 2));
		btnTar.setIcon(new ImageIcon(VitamDigestDialog.class
				.getResource(VitamGui.RESOURCES_IMG_CHECKFILES_PNG)));
		btnTar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = openFile(tarzip.getText(), StaticValues.LBL.tools_file.get(), "");
				if (file != null) {
					tarzip.setText(file.getAbsolutePath());
				}
			}
		});
		GridBagConstraints gbc_btnTar = new GridBagConstraints();
		gbc_btnTar.insets = new Insets(0, 0, 5, 5);
		gbc_btnTar.gridx = 6;
		gbc_btnTar.gridy = 2;
		xmlFilePanel.add(btnTar, gbc_btnTar);

		chckbxFichierGlobal = new JCheckBox("Fichier Global");
		chckbxFichierGlobal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				globaldir.setEnabled(chckbxFichierGlobal.isSelected());
				btnGlobal.setEnabled(chckbxFichierGlobal.isSelected());
				prefix.setEnabled(chckbxFichierGlobal.isSelected());
			}
		});
		GridBagConstraints gbc_chckbxFichierGlobal = new GridBagConstraints();
		gbc_chckbxFichierGlobal.anchor = GridBagConstraints.WEST;
		gbc_chckbxFichierGlobal.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxFichierGlobal.gridx = 1;
		gbc_chckbxFichierGlobal.gridy = 3;
		xmlFilePanel.add(chckbxFichierGlobal, gbc_chckbxFichierGlobal);

		JLabel lblFileAttribut = new JLabel("Répertoire");
		GridBagConstraints gbc_lblFileAttribut = new GridBagConstraints();
		gbc_lblFileAttribut.anchor = GridBagConstraints.EAST;
		gbc_lblFileAttribut.insets = new Insets(0, 0, 5, 5);
		gbc_lblFileAttribut.gridx = 2;
		gbc_lblFileAttribut.gridy = 3;
		xmlFilePanel.add(lblFileAttribut, gbc_lblFileAttribut);

		globaldir = new JTextField();
		GridBagConstraints gbc_globaldir = new GridBagConstraints();
		gbc_globaldir.gridwidth = 3;
		gbc_globaldir.insets = new Insets(0, 0, 5, 5);
		gbc_globaldir.fill = GridBagConstraints.HORIZONTAL;
		gbc_globaldir.gridx = 3;
		gbc_globaldir.gridy = 3;
		xmlFilePanel.add(globaldir, gbc_globaldir);
		globaldir.setColumns(10);

		btnGlobal = new JButton();
		btnGlobal.setMargin(new Insets(2, 2, 2, 2));
		btnGlobal.setIcon(new ImageIcon(VitamDigestDialog.class
				.getResource(VitamGui.RESOURCES_IMG_CHECKFILES_PNG)));
		btnGlobal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = openDir(tarzip.getText(), StaticValues.LBL.tools_dir.get());
				if (file != null) {
					globaldir.setText(file.getAbsolutePath());
				}
			}
		});
		GridBagConstraints gbc_btnGlobal = new GridBagConstraints();
		gbc_btnGlobal.insets = new Insets(0, 0, 5, 5);
		gbc_btnGlobal.gridx = 6;
		gbc_btnGlobal.gridy = 3;
		xmlFilePanel.add(btnGlobal, gbc_btnGlobal);

		JLabel lblPrefixe = new JLabel("Prefixe");
		GridBagConstraints gbc_lblPrefixe = new GridBagConstraints();
		gbc_lblPrefixe.insets = new Insets(0, 0, 5, 5);
		gbc_lblPrefixe.anchor = GridBagConstraints.EAST;
		gbc_lblPrefixe.gridx = 2;
		gbc_lblPrefixe.gridy = 4;
		xmlFilePanel.add(lblPrefixe, gbc_lblPrefixe);

		prefix = new JTextField();
		GridBagConstraints gbc_prefix = new GridBagConstraints();
		gbc_prefix.gridwidth = 3;
		gbc_prefix.insets = new Insets(0, 0, 5, 5);
		gbc_prefix.fill = GridBagConstraints.HORIZONTAL;
		gbc_prefix.gridx = 3;
		gbc_prefix.gridy = 4;
		xmlFilePanel.add(prefix, gbc_prefix);
		prefix.setColumns(10);

		chckbxUnFichierPar = new JCheckBox("Un fichier par source");
		GridBagConstraints gbc_chckbxUnFichierPar = new GridBagConstraints();
		gbc_chckbxUnFichierPar.anchor = GridBagConstraints.WEST;
		gbc_chckbxUnFichierPar.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxUnFichierPar.gridx = 1;
		gbc_chckbxUnFichierPar.gridy = 5;
		xmlFilePanel.add(chckbxUnFichierPar, gbc_chckbxUnFichierPar);

		JLabel lblTitle = new JLabel("Vitam Configuration");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblTitle, BorderLayout.NORTH);
		prefix.setText("allinone");
		tarzip.setEnabled(false);
		btnTar.setEnabled(false);
		globaldir.setEnabled(false);
		btnGlobal.setEnabled(false);
		prefix.setEnabled(false);
		initValue();
	}

	public void initValue() {
	}

	public void runDigest() {
		try {
			String ssrc = source.getText();
			String sdst = destination.getText();
			if (ssrc == null || sdst == null) {
				JOptionPane.showMessageDialog(frame,
						"Source & Destination invalides",
						"Digest Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			File src = new File(ssrc);
			File dst = new File(sdst);
			if (!src.exists() || !dst.exists()) {
				JOptionPane.showMessageDialog(frame,
						"Source & Destination invalides",
						"Digest Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			boolean oneDigestPerFile = chckbxUnFichierPar.isSelected();
			boolean globalFile = chckbxFichierGlobal.isSelected();
			File fglobal = null;
			if (globalFile && globaldir.getText() == null) {
				JOptionPane.showMessageDialog(frame,
						"Global invalide",
						"Digest Warning",
						JOptionPane.WARNING_MESSAGE);
				globalFile = false;
			} else if (globalFile) {
				fglobal = new File(globaldir.getText());
				if (!fglobal.exists()) {
					JOptionPane.showMessageDialog(frame,
							"Global invalide",
							"Digest Warning",
							JOptionPane.WARNING_MESSAGE);
					fglobal = null;
					globalFile = false;
				} else {
					File fout = new File(fglobal, prefix.getText() + "_all_digests.xml");
					fglobal = fout;
				}
			}
			boolean tarFile = chckbxTarzipAssoci.isSelected();
			File ftar = null;
			if (tarFile && tarzip.getText() == null) {
				JOptionPane.showMessageDialog(frame,
						"TAR/ZIP invalide",
						"Digest Warning",
						JOptionPane.WARNING_MESSAGE);
				tarFile = false;
			} else if (tarFile) {
				ftar = new File(tarzip.getText());
				if (!ftar.exists()) {
					JOptionPane.showMessageDialog(frame,
							"TAR/ZIP invalide",
							"Digest Warning",
							JOptionPane.WARNING_MESSAGE);
					ftar = null;
					tarFile = false;
				}
			}
			int currank = DigestCompute.createDigest(src, dst, ftar, fglobal, oneDigestPerFile, null);
			if (currank > 0) {
				vitamGui.texteOut.insertIcon(new ImageIcon(getClass().getResource(
						VitamGui.RESOURCES_IMG_VALID_PNG)));
				System.out
						.println(StaticValues.LBL.action_digest.get() +
								" [ " + currank  + " ]");
			}
		} finally {
			if (fromMain) {

			} else {
				this.vitamGui.setEnabled(true);
				this.vitamGui.requestFocus();
				this.frame.setVisible(false);
			}
		}
	}

	public void cancel() {
		if (fromMain) {
			this.frame.dispose();
			System.exit(0);
		} else {
			this.vitamGui.setEnabled(true);
			this.vitamGui.requestFocus();
			this.frame.setVisible(false);
		}
	}

	public File openFile(String currentValue, String text, String extension) {
		JFileChooser chooser = null;
		if (currentValue != null) {
			String file = StaticValues.resourceToFile(currentValue);
			if (file != null) {
				File ffile = new File(file).getParentFile();
				chooser = new JFileChooser(ffile);
			}
		}
		if (chooser == null) {
			chooser = new JFileChooser(System.getProperty("user.dir"));
		}
		if (extension == null) {
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		} else if (extension.length() > 0) {
			FileExtensionFilter filter = new FileExtensionFilter(extension, text);
			chooser.setFileFilter(filter);
		} else {
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
		chooser.setDialogTitle(text);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	public File openDir(String currentValue, String text) {
		JFileChooser chooser = null;
		if (currentValue != null) {
			String file = StaticValues.resourceToFile(currentValue);
			if (file != null) {
				File ffile = new File(file).getParentFile();
				chooser = new JFileChooser(ffile);
			}
		}
		if (chooser == null) {
			chooser = new JFileChooser(System.getProperty("user.dir"));
		}
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle(text);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("Vitam Digest");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Create and set up the content pane.
		fromMain = true;
		VitamDigestDialog newContentPane = new VitamDigestDialog(frame, null);
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		StaticValues.initialize();
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
