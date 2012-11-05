/**
 * This file is part of Vitam Project.
 * 
 * Copyright 2010, Frederic Bregier, and individual contributors by the @author
 * tags. See the COPYRIGHT.txt in the distribution for a full listing of individual contributors.
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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.transform.TransformerException;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSModel;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.jdom.JDOMException;

import de.schlichtherle.io.FileInputStream;
import de.schlichtherle.io.FileOutputStream;

import uk.gov.nationalarchives.droid.command.action.CommandExecutionException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import fr.gouv.culture.vitam.digest.DigestCompute;
import fr.gouv.culture.vitam.droid.DroidFileFormat;
import fr.gouv.culture.vitam.droid.DroidHandler;
import fr.gouv.culture.vitam.pdfa.PdfaConverter;
import fr.gouv.culture.vitam.utils.Commands;
import fr.gouv.culture.vitam.utils.ConfigLoader;
import fr.gouv.culture.vitam.utils.FileExtensionFilter;
import fr.gouv.culture.vitam.utils.StaticValues;
import fr.gouv.culture.vitam.utils.Version;
import fr.gouv.culture.vitam.utils.VitamArgument;
import fr.gouv.culture.vitam.utils.XmlDom;
import fr.gouv.culture.vitam.utils.VitamArgument.VitamOutputModel;
import fr.gouv.culture.vitam.utils.XmlDom.AllTestsItems;
import fr.gouv.culture.vitam.utils.VitamResult;
import static fr.gouv.culture.vitam.utils.XmlTools.*;
import static fr.gouv.culture.vitam.utils.XmlDom.*;
import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.UIManager;

/**
 * Main class for the GUI tool
 * 
 * @author "Frederic Bregier"
 * 
 */
public class VitamGui extends JFrame implements WindowListener, PropertyChangeListener {
	protected static final String VITAM_PNG = "/resources/img/vitam.png";
	private static final String VITAM64_PNG = "/resources/img/vitam64.png";
	private static final String VITAM32_PNG = "/resources/img/vitam32.png";
	private static final String SEDA_V1_0_FILETYPE_CODE_XSD = "seda_v1-0_filetype_code.xsd";
	protected static final String RESOURCES_IMG_VALID_PNG = "/resources/img/valid.png";
	private static final String RESOURCES_IMG_CLEAR_PNG = "/resources/img/edit-clear.png";
	private static final String RESOURCES_IMG_COPY_PNG = "/resources/img/copy.png";
	private static final String RESOURCES_IMG_XML_PNG = "/resources/img/openxml.png";
	protected static final String RESOURCES_IMG_XSL_PNG = "/resources/img/xsl-open.png";
	protected static final String RESOURCES_IMG_SCH_PNG = "/resources/img/schematron-open.png";
	protected static final String RESOURCES_IMG_XSD_PNG = "/resources/img/xsd.png";
	private static final String RESOURCES_IMG_DIGEST_PNG = "/resources/img/empreinte.png";
	private static final String RESOURCES_IMG_EXIT_PNG = "/resources/img/exit.png";
	private static final String RESOURCES_IMG_HELP_PNG = "/resources/img/help.png";
	private static final String RESOURCES_IMG_CONFIG_PNG = "/resources/img/settings.png";
	private static final String RESOURCES_IMG_CHECKFORMAT_PNG = "/resources/img/find-format.png";
	protected static final String RESOURCES_IMG_CHECKFILES_PNG = "/resources/img/find-files.png";
	private static final String RESOURCES_IMG_SCHEMATRON_PNG = "/resources/img/schematron.png";
	private static final String RESOURCES_IMG_VALIDATE_PNG = "/resources/img/rng.png";
	private static final String RESOURCES_IMG_VALIDATEXSD_PNG = "/resources/img/validation-xml.png";
	private static final String RESOURCES_IMG_VALIDATEXML_PNG = "/resources/img/xml.png";
	private static final String RESOURCES_IMG_TRANSFORMXSL_PNG = "/resources/img/xsl.png";
	private static final String RESOURCES_IMG_FORMATS_PNG = "/resources/img/format-output.png";
	private static final String RESOURCES_IMG_HDR_LOGO_GIF = "/resources/img/hdr-logo.gif";
	private static final String RESOURCES_IMG_XSLSEDA_PNG = "/resources/img/xslseda.png";
	private static final String RESOURCES_IMG_PDFA_GIF = "/resources/img/pdf.gif";
	private static final long serialVersionUID = -8010724089786663345L;

	protected File current_file;
	protected File current_xsl;
	protected File current_sch;
	protected File tempFile;
	protected JTextPane texteOut;
	protected JTextPane texteErr;
	protected JToolBar toolBar;
	protected JToolBar toolBarWest;
	protected JMenuBar mb;
	protected List<JMenuItem> listMenuItem;
	protected List<JButton> listButton;
	protected List<JMenuItem> listAllMenuItem;
	protected List<JButton> listAllButton;
	protected StringList tempList;
	protected static VitamGui vitamGui;
	protected static JProgressBar progressBar;
	public ConfigLoader config;
	protected JFrame frameDialog;
	protected VitamConfigDialog configDialog;
	protected JFrame frameDigestDialog;
	protected VitamDigestDialog vitamDigestDialog;
	protected VitamResult vitamResult;
	protected XMLWriter writer;
	protected List<File> filesToScan;
	protected File fileToApplyXsl;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				vitamGui = new VitamGui();
				vitamGui.setVisible(true);
			}
		});
	}

	/**
	 * Main GUI constructor
	 */
	public VitamGui() {
		StaticValues.initialize();
		List<Image> images = new ArrayList<Image>();
		images.add(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(VITAM64_PNG)));
		images.add(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(VITAM32_PNG)));
		setIconImages(images);
		try {
			writer = new XMLWriter(System.out, StaticValues.defaultOutputFormat);
		} catch (UnsupportedEncodingException e1) {
			System.err.println(StaticValues.LBL.error_writer.get() + e1.toString());
			quit();
			return;
		}
		listMenuItem = new ArrayList<JMenuItem>();
		listButton = new ArrayList<JButton>();
		listAllMenuItem = new ArrayList<JMenuItem>();
		listAllButton = new ArrayList<JButton>();
		getContentPane().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.current_file = null;
		this.config = StaticValues.config;

		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setVgap(5);
		getContentPane().setLayout(borderLayout);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		Hashtable<String, String> listMenuItems = new Hashtable<String, String>();
		listMenuItems.put("menu", "menu.file/menu.edit/menu.tools/menu.droid/menu.help");
		listMenuItems.put("menu.file", "file.xsd/file.open/file.sch/file.xsl/-/file.quit");
		listMenuItems.put("menu.edit", "edit.copy/edit.clear");
		listMenuItems
				.put("menu.tools",
						"tools.xml_validation/tools.xsd_validation/tools.profil_validation/tools.schematron_validation/"
								+
								"+/tools.attachment_test/tools.hashcode_test/tools.attachment_format_test/tools.attachment_format_output/"
								+
								"-/tools.transform/" +
								"+/tools.dir_digest/-/tools.digest/tools.dir_format_test/tools.dir_format_output");
		listMenuItems.put("menu.droid", "tools.droidtransform/tools.vitam2seda/tools.pdfa");
		listMenuItems.put("menu.help", "help.about/help.config");

		setTitle(StaticValues.LBL.appName.get());
		setBackground(Color.white);

		/*
		 * Toolbars
		 */
		toolBar = new JToolBar("Toolbar");
		toolBar.setFloatable(false);
		toolBarWest = new JToolBar("Toolbar");
		toolBarWest.setFloatable(false);
		toolBarWest.setOrientation(JToolBar.VERTICAL);
		mb = new JMenuBar();
		createMenuBar(listMenuItems);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(toolBarWest, BorderLayout.WEST);
		setJMenuBar(mb);
		changeButtonMenu(false);

		Dimension screenSize = new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
		int width = screenSize.width / 2;
		if (width < 700) {
			width = 700;
		}
		int height = (int) (screenSize.getHeight() / 2);
		if (height < 640) {
			height = 640;
		}
		screenSize.setSize(width, height);
		setSize(screenSize);

		texteOut = new JTextPane();
		texteOut.setEditable(false);
		texteErr = new JTextPane();
		texteErr.setEditable(false);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		splitPane.setDividerSize(2);
		splitPane.setAutoscrolls(true);
		JScrollPane outPane = new JScrollPane(texteOut);
		outPane.setViewportBorder(UIManager.getBorder("TextPane.border"));
		JScrollPane errPane = new JScrollPane(texteErr);

		splitPane.setLeftComponent(outPane);
		splitPane.setRightComponent(errPane);
		splitPane.setDividerLocation((screenSize.height - 100) / 2);

		getContentPane().add(splitPane, BorderLayout.CENTER);

		// Redirection of System.out and System.err
		ConsoleOutputStream cos = new ConsoleOutputStream(texteOut, null);
		System.setOut(new PrintStream(cos, true));
		ConsoleOutputStream coserr = new ConsoleOutputStream(texteErr, Color.RED);
		System.setErr(new PrintStream(coserr, true));

		addWindowListener(this);
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		getContentPane().add(progressBar, BorderLayout.PAGE_END);
		endProgressBar();

		frameDialog = new JFrame("Vitam Configuration");
		frameDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				vitamGui.setEnabled(true);
				vitamGui.requestFocus();
				frameDialog.setVisible(false);
			}
		});
		configDialog = new VitamConfigDialog(frameDialog, this);
		configDialog.setOpaque(true); // content panes must be opaque
		frameDialog.setContentPane(configDialog);
		frameDialog.pack();
		frameDialog.setVisible(false);

		frameDigestDialog = new JFrame("Vitam Digest");
		frameDigestDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				vitamGui.setEnabled(true);
				vitamGui.requestFocus();
				frameDigestDialog.setVisible(false);
			}
		});
		vitamDigestDialog = new VitamDigestDialog(frameDigestDialog, this);
		vitamDigestDialog.setOpaque(true); // content panes must be opaque
		frameDigestDialog.setContentPane(vitamDigestDialog);
		frameDigestDialog.pack();
		frameDigestDialog.setVisible(false);
	}

	/**
	 * Quit application
	 */
	private void quit() {
		dispose();
		System.exit(0);
	}

	/**
	 * This method allows or disallows some button/menu items
	 * 
	 * @param valid
	 */
	protected final void changeButtonMenu(boolean valid) {
		for (JButton button : listButton) {
			button.setEnabled(valid);
		}
		for (JMenuItem menu : listMenuItem) {
			menu.setEnabled(valid);
		}
	}

	/**
	 * This method allows or disallows all button/menu items
	 * 
	 * @param valid
	 */
	protected final void changeAllButtonMenu(boolean valid) {
		for (JButton button : listAllButton) {
			button.setEnabled(valid);
		}
		for (JMenuItem menu : listAllMenuItem) {
			menu.setEnabled(valid);
		}
	}

	/**
	 * create MenuBar
	 * 
	 * @param hash
	 */
	protected final void createMenuBar(Hashtable<String, String> hash) {
		String liste = hash.get("menu");
		StringTokenizer menuKeys = new StringTokenizer(liste, "/");
		while (menuKeys.hasMoreTokens()) {
			String name = menuKeys.nextToken();
			JMenu m = createMenu(name, hash);
			if (m != null) {
				mb.add(m);
				if (name.equals("+")) {
					toolBarWest.addSeparator();
				} else {
					toolBar.addSeparator();
				}
			}
		}
	}

	/**
	 * Create one MenuItem
	 * 
	 * @param cmd
	 * @return the MenuItem
	 */
	protected final JMenuItem createMenuItem(String cmd) {
		JMenuItem mi = new JMenuItem(StaticValues.LABELS.get(cmd));
		return mi;
	}

	/**
	 * Create one MenuItem with an icon
	 * 
	 * @param cmd
	 * @param icon
	 * @return the MenuItem
	 */
	protected final JMenuItem createMenuItem(String cmd, Icon icon) {
		JMenuItem mi = createMenuItem(cmd);
		mi.setIcon(icon);
		return mi;
	}

	/**
	 * Create sub Menu
	 * 
	 * @param key
	 * @param hash
	 * @return the sub Menu
	 */
	private final JMenu createMenu(String key, Hashtable<String, String> hash) {
		final JMenu menu = new JMenu(StaticValues.LABELS.get(key));
		String s = (String) hash.get(key);
		StringTokenizer menuItems = new StringTokenizer(s, "/");

		while (menuItems.hasMoreTokens()) {
			String name = menuItems.nextToken();
			String newname = name.replaceFirst("\\.", "_");
			try {
				final StaticValues.LBL label = StaticValues.LBL.valueOf(newname);
				JMenuItem mi = null;
				JButton button = null;
				ImageIcon img = null;
				ActionListener actionListener = null;
				switch (label) {
					case edit_clear:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_CLEAR_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								texteOut.setText("");
								texteErr.setText("");
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						break;
					case edit_copy:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_COPY_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								texteOut.copy();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						break;
					case file_open:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_XML_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								openXmlMessage();
								runCommand(label);
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case file_xsl:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_XSL_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								File file = openXslt();
								if (file != null) {
									config.CURRENT_XSL = file.getAbsolutePath();
								} else {
									config.CURRENT_XSL = null;
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case file_sch:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_SCH_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								File file = openSchematron();
								if (file != null) {
									config.CURRENT_SCHEMATRON = file.getAbsolutePath();
								} else {
									config.CURRENT_SCHEMATRON = null;
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case file_quit:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_EXIT_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								quit();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						break;
					case file_xsd:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_XSD_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								tempFile = openXsd();
								if (tempFile != null) {
									initIndeterminateProgressBar();
									runCommand(label);
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case help_config:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_CONFIG_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								config();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case help_about:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_HELP_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								about();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_attachment_test:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_CHECKFILES_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								initIndeterminateProgressBar();
								runCommand(label);
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBarWest.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_hashcode_test:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_DIGEST_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								initProgressBar();
								runCommand(label);
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBarWest.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_profil_validation:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_VALIDATE_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								tempFile = openProfil();
								if (tempFile != null) {
									initIndeterminateProgressBar();
									runCommand(label);
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_schematron_validation:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_SCHEMATRON_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								initIndeterminateProgressBar();
								runCommand(label);
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_xsd_validation:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_VALIDATEXSD_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								initIndeterminateProgressBar();
								runCommand(label);
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_transform:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_TRANSFORMXSL_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								initIndeterminateProgressBar();
								runCommand(label);
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_droidtransform:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_HDR_LOGO_GIF));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								tempFile = openFile(null, "Droid Signature", "xml", false);
								if (tempFile != null) {
									initIndeterminateProgressBar();
									runCommand(label);
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBarWest.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_vitam2seda:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_XSLSEDA_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								String previous = config.lastScannedDirectory == null ? null : config.lastScannedDirectory.getAbsolutePath();
								tempFile = openFile(previous, "Vitam Result Unique XML file", "xml", false);
								if (tempFile != null) {
									previous = (fileToApplyXsl == null ? null : fileToApplyXsl.getAbsolutePath());
									File temp = openFile(previous, "Vitam XSLT transform file", "xsl", false);
									if (temp != null) {
										fileToApplyXsl = temp;
										initIndeterminateProgressBar();
										runCommand(label);
									}
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBarWest.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_xml_validation:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_VALIDATEXML_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								runCommand(label);
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_attachment_format_test:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_CHECKFORMAT_PNG));
						button = new JButton(img);
						button.setToolTipText(StaticValues.LBL.tools_attachment_format_test.get()
								+ ". " + StaticValues.LBL.action_pending.get());
						mi = createMenuItem(name, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								initProgressBar();
								runCommand(label);
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBarWest.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_attachment_format_output:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_FORMATS_PNG));
						button = new JButton(img);
						button.setToolTipText(StaticValues.LBL.tools_attachment_format_output.get()
								+ ". " + StaticValues.LBL.action_pending.get());
						mi = createMenuItem(name, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								initProgressBar();
								runCommand(label);
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBarWest.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_dir_format_test:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_CHECKFORMAT_PNG));
						button = new JButton(img);
						button.setBackground(Color.CYAN);
						button.setToolTipText(StaticValues.LBL.tools_dir_format_test.get()
								+ ". " + StaticValues.LBL.action_pending.get());
						mi = createMenuItem(name, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								File directory = choixDirectory("(check)");
								if (directory != null) {
									int nb = initDirectoryListing(directory);
									if (nb > 0) {
										config.nbDocument = nb;
										config.lastScannedDirectory = directory;
										initProgressBar();
										runCommand(label);
									} else {
										filesToScan = null;
									}
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBarWest.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_dir_format_output:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_FORMATS_PNG));
						button = new JButton(img);
						button.setBackground(Color.CYAN);
						button.setToolTipText(StaticValues.LBL.tools_dir_format_output.get()
								+ ". " + StaticValues.LBL.action_pending.get());
						mi = createMenuItem(name, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								File directory = choixDirectory("(format)");
								if (directory != null) {
									int nb = initDirectoryListing(directory);
									if (nb > 0) {
										config.nbDocument = nb;
										config.lastScannedDirectory = directory;
										initProgressBar();
										runCommand(label);
									} else {
										filesToScan = null;
									}
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBarWest.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_dir_digest:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_DIGEST_PNG));
						button = new JButton(img);
						button.setBackground(Color.CYAN);
						button.setToolTipText(StaticValues.LBL.tools_dir_digest.get());
						mi = createMenuItem(name, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								File directory = choixDirectory("(empreinte)");
								if (directory != null) {
									int nb = initDirectoryListing(directory);
									if (nb > 0) {
										config.nbDocument = nb;
										config.lastScannedDirectory = directory;
										initProgressBar();
										runCommand(label);
									} else {
										filesToScan = null;
									}
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBarWest.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_pdfa:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_PDFA_GIF));
						button = new JButton(img);
						button.setToolTipText(StaticValues.LBL.tools_pdfa.get()
								+ ". " + StaticValues.LBL.action_pending.get());
						mi = createMenuItem(name, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								File directory = choixDirectory("(source)");
								if (directory != null) {
									int nb = initDirectoryListing(directory);
									if (nb > 0) {
										config.lastScannedDirectory = directory;
										File directoryOut = choixDirectory("(target)");
										if (directoryOut != null) {
											if (directoryOut.isFile()) {
												directoryOut = directoryOut.getParentFile();
											}
											tempFile = directoryOut;
											config.nbDocument = nb;
											initProgressBar();
											runCommand(label);
										} else {
											filesToScan = null;
										}
									} else {
										filesToScan = null;
									}
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBarWest.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_digest:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_DIGEST_PNG));
						button = new JButton(img);
						button.setBackground(Color.GREEN);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								computeDigest();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBarWest.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					default:
						set_ExtraMenu(menu, name);
						break;
				}
			} catch (Exception e) {
				if (name.equals("-")) {
					toolBar.addSeparator();
					menu.addSeparator();
				} else if (name.equals("+")) {
					toolBarWest.addSeparator();
					menu.addSeparator();
				}

			}
		}
		return menu;
	}

	/**
	 * Method to enable other menu creation if extended
	 * 
	 * @param menu
	 * @param name
	 */
	protected void set_ExtraMenu(JMenu menu, String name) {
	}

	/**
	 * 
	 * @param directory
	 * @return the number of potential files to check
	 */
	protected int initDirectoryListing(File directory) {
		try {
			filesToScan = DroidHandler.matchedFiled(
					new File[] { directory },
					null,
					config.argument.recursive);
		} catch (CommandExecutionException e1) {
			System.err.println(StaticValues.LBL.error_error.get() + " " + e1.toString());
			return 0;
		}
		return filesToScan.size();
	}

	/**
	 * Init the progressBar from config.nbDocument
	 */
	protected void initProgressBar() {
		if (config.nbDocument == 0) {
			int result = countDocument(current_file, config);
			if (result > 0) {
				config.nbDocument = result;
			} else if (result == 0) {
				System.err.println("\n" + StaticValues.LBL.error_digest.get() +
						" [ no file ]");
				return;
			}
		}
		if (config.nbDocument > 0) {
			progressBar.setValue(0);
			progressBar.setMaximum(config.nbDocument);
			progressBar.setVisible(true);
		}
	}

	/**
	 * Init the progressBar in indeterminate mode
	 */
	protected void initIndeterminateProgressBar() {
		progressBar.setIndeterminate(true);
		progressBar.setVisible(true);
	}

	/**
	 * Finalize the progressBar
	 */
	protected void endProgressBar() {
		progressBar.setIndeterminate(false);
		progressBar.setVisible(false);
	}

	/**
	 * Ask for the configuration window
	 * 
	 */
	private void config() {
		this.setEnabled(false);
		frameDialog.setVisible(true);
		configDialog.initValue();
	}

	/**
	 * @param message
	 * @return the directory chosen or null if cancel
	 */
	private File choixDirectory(String message) {
		String fullmessage = StaticValues.LBL.tools_dir.get();
		if (message != null) {
			fullmessage += " " + message;
		}
		return openFile(config.lastScannedDirectory == null ?
				null : config.lastScannedDirectory.getAbsolutePath(),
				fullmessage, null, false);
	}

	/**
	 * 
	 * @param currentValue
	 *            current path where the parent path will be used
	 * @param text
	 *            text to show
	 * @param extension
	 *            extension filter (null if no filter)
	 * @return the chosen file or null if cancel
	 */
	private File openFile(String currentValue, String text, String extension) {
		return openFile(currentValue, text, extension, true);
	}

	/**
	 * 
	 * @param currentValue
	 *            current path where the exact path will be used
	 * @param text
	 *            text to show
	 * @param extension
	 *            extension filter (null if no filter)
	 * @param parent
	 *            if true currentValue will be changed to parent, else no change
	 * @return the chosen file or null if cancel
	 */
	private File openFile(String currentValue, String text, String extension, boolean parent) {
		JFileChooser chooser = null;
		if (currentValue != null) {
			String file = StaticValues.resourceToFile(currentValue);
			if (file != null) {
				File ffile = new File(file);
				if (parent) {
					ffile = ffile.getParentFile();
				}
				chooser = new JFileChooser(ffile);
			}
		}
		if (chooser == null) {
			if (current_file != null) {
			chooser = new JFileChooser(current_file.getParentFile());
			} else if (StaticValues.config.CURRENT_XSD != null) {
				chooser = new JFileChooser(new File(StaticValues.config.CURRENT_XSD).getParentFile());
			} else {
				chooser = new JFileChooser(System.getProperty("user.dir"));
			}
		}
		if (extension == null) {
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		} else {
			FileExtensionFilter filter = new FileExtensionFilter(extension, text);
			chooser.setFileFilter(filter);
		}
		chooser.setDialogTitle(text);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}
	/**
	 * 
	 * @param currentValue
	 *            current path where the exact path will be used
	 * @param text
	 *            text to show
	 * @param extension
	 *            extension filter (null if no filter)
	 * @param parent
	 *            if true currentValue will be changed to parent, else no change
	 * @return the chosen file or null if cancel
	 */
	private File saveFile(String currentValue, String text, String extension, boolean parent) {
		JFileChooser chooser = null;
		if (currentValue != null) {
			String file = StaticValues.resourceToFile(currentValue);
			if (file != null) {
				File ffile = new File(file);
				if (parent) {
					ffile = ffile.getParentFile();
				}
				chooser = new JFileChooser(ffile);
			}
		}
		if (chooser == null) {
			if (current_file != null) {
				chooser = new JFileChooser(current_file.getParentFile());
			} else if (StaticValues.config.CURRENT_XSD != null) {
				chooser = new JFileChooser(new File(StaticValues.config.CURRENT_XSD).getParentFile());
			} else {
				chooser = new JFileChooser(System.getProperty("user.dir"));
			}
		}
		if (extension == null) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		} else {
			FileExtensionFilter filter = new FileExtensionFilter(extension, text);
			chooser.setFileFilter(filter);
		}
		chooser.setDialogTitle(text);
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (extension != null) {
				String extfile =  FileExtensionFilter.getExtension(file);
				if (extfile == null || extfile.equalsIgnoreCase(extension)) {
					file = new File(file.getAbsolutePath()+"."+extension);
				}
			}
			return file;
		}
		return null;
	}

	/**
	 * 
	 * @param path
	 * @return a path if any
	 */
	private String getPreviousPath(String path) {
		if (path == null) {
			if (config.CURRENT_XSD != null) {
				return config.CURRENT_XSD;
			} else if (config.CURRENT_SCHEMATRON != null) {
				return config.CURRENT_SCHEMATRON;
			} else {
				return config.CURRENT_XSL;
			}
		}
		return path;
	}

	/**
	 * Open a XSD file
	 * 
	 * @return the file
	 */
	private File openXsd() {
		String path = getPreviousPath(config.CURRENT_XSD);
		File file = openFile(path, StaticValues.LBL.file_xsd.get(), "xsd");
		if (file != null) {
			JOptionPane.showMessageDialog(this, "Note:\n\tSCH = " +
					StaticValues.getCurrentSchFile() + "\nXSL = " +
					StaticValues.getCurrentXslFile());
		}
		return file;
	}

	/**
	 * XSD validation SwingWorker task
	 * 
	 * @return null if error, else either a List<String>
	 */
	private Object swingWorkerXsdValid() {
		XSModel xsModel = getXSModelFromXsdFile(tempFile);
		tempList = xsModel.getNamespaces();
		if (config.useSaxonToGetRootFromXSD) {
			List<String> roots = null;
			try {
				roots = getRootElementNamesUsingSaxon(tempFile, config);
			} catch (JDOMException e) {
				System.err.println(StaticValues.LBL.error_error.get() + e);
				return null;
			} catch (IOException e) {
				System.err.println(StaticValues.LBL.error_error.get() + e);
				return null;
			} catch (TransformerException e) {
				System.err.println("Error: " + e);
				return null;
			}
			if (roots.size() == 0) {
				System.err.println(StaticValues.LBL.error_error.get() + " no root element");
				return null;
			}
			return roots;
		} else {
			List<String> list = getRootElementNames(xsModel);
			if (list.size() == 0) {
				System.err.println(StaticValues.LBL.error_error.get() + " no root element");
				return null;
			}
			return list;
		}
	}

	/**
	 * Valid the XSD through its root selection
	 * 
	 * @param object
	 */
	private void finalXsdValid(Object object) {
		if (object != null) {
			@SuppressWarnings("unchecked")
			List<String> roots = (List<String>) object;
			if (roots.size() == 1) {
				config.CURRENT_XSD_ROOT = roots.get(0);
			} else {
				String val = (String) JOptionPane.showInputDialog(
						this,
						StaticValues.LBL.tools_list.get() + ":\n",
						"XSD Root",
						JOptionPane.PLAIN_MESSAGE,
						new ImageIcon(getClass().getResource(RESOURCES_IMG_XSD_PNG)),
						roots.toArray(), roots.get(0));
				if (val != null && val.length() > 0) {
					config.CURRENT_XSD_ROOT = val;
				} else {
					System.err.println(StaticValues.LBL.error_error.get() + " no root element");
					return;
				}
			}
		} else {
			// error
			return;
		}
		if (tempList.size() > 0) {
			config.DEFAULT_LOCATION = tempList.item(0);
			config.CURRENT_XSD = tempFile.getAbsolutePath();
		} else {
			config.DEFAULT_LOCATION = "";
			config.CURRENT_XSD = tempFile.getAbsolutePath();
		}
		tempFile = null;
		tempList = null;
		texteOut.insertIcon(new ImageIcon(getClass().getResource(
				RESOURCES_IMG_VALID_PNG)));
		System.out.println("TargetNamespace = " + config.DEFAULT_LOCATION +
				" TargetRoot = " + config.CURRENT_XSD_ROOT);
	}

	/**
	 * Open a profile
	 * 
	 * @return the corresponding file
	 */
	private File openProfil() {
		String path = getPreviousPath(config.CURRENT_XSD);
		File file = openFile(path, StaticValues.LBL.file_rng.get(), "rng");
		return file;
	}

	/**
	 * Open a schematron
	 * 
	 * @return the corresponding file
	 */
	private File openSchematron() {
		String path = getPreviousPath(config.CURRENT_SCHEMATRON);
		File file = openFile(path, StaticValues.LBL.file_sch.get(), "sch");
		return file;
	}

	/**
	 * Open a xslt file
	 * 
	 * @return the corresponding file
	 */
	private File openXslt() {
		String path = getPreviousPath(config.CURRENT_XSL);
		File file = openFile(path, StaticValues.LBL.file_xsl.get(), "xsl");
		return file;
	}

	/**
	 * Open a XML message
	 */
	private void openXmlMessage() {
		File file = null;
		if (current_file != null) {
			file = openFile(null, StaticValues.LBL.file_open.get(), "xml");
		} else {
			String path = getPreviousPath(config.CURRENT_XSD);
			file = openFile(path, StaticValues.LBL.file_open.get(), "xml");
		}
		if (file != null) {
			current_file = file;
			vitamResult = null;
			config.nbDocument = 0;
		}
	}

	/**
	 * Valid simply Xml
	 */
	private void xml_valid() {
		if (current_file != null) {
			if (xml_validation(current_file, config)) {
				texteOut.insertIcon(new ImageIcon(getClass().getResource(
						RESOURCES_IMG_VALID_PNG)));
				System.out.println(StaticValues.LBL.action_wellformat.get());
				changeButtonMenu(true);
			} else {
				current_file = null;
				changeButtonMenu(false);
			}
		} else {
			System.err.println(StaticValues.LBL.action_nofile.get());
			changeButtonMenu(false);
		}
	}

	/**
	 * About and license
	 */
	public void about() {
		StringBuffer content = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass()
				.getResourceAsStream(StaticValues.RESOURCES_LICENSE_TXT)));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		final JPanel panel = new JPanel(new BorderLayout());
		String credits = "<html><p><center>Copyright (c) 2012 Ministere de la Culture et de la Communication<br>"
				+
				"Sous-Direction du Systeme d'Information<br>Projet Vitam</center></p><p></p><p><center>Version: "
				+ Version.ID
				+
				"</center></p><p></p><p>Contributeurs: <br><i>Frederic Bregier</i></p><p></p>"
				+
				"<p>Site web: <a href='http://www.archivesnationales.culture.gouv.fr/'>http://www.archivesnationales.culture.gouv.fr/</a></p></html>";
		panel.add(new JLabel(credits), BorderLayout.NORTH);
		JTextArea textArea = new JTextArea(content.toString());
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(550, 500));
		// TIP: Make the JOptionPane resizable using the HierarchyListener
        panel.addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                Window window = SwingUtilities.getWindowAncestor(panel);
                if (window instanceof Dialog) {
                    Dialog dialog = (Dialog)window;
                    if (!dialog.isResizable()) {
                        dialog.setResizable(true);
                    }
                }
            }
        });
		JOptionPane.showConfirmDialog(this, panel,
				StaticValues.LBL.label_about.get(),
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Validation using an XSD
	 */
	private void schemaValid() {
		if (current_file != null) {
			if (xsd_validation(current_file, config)) {
				texteOut.insertIcon(new ImageIcon(getClass().getResource(
						RESOURCES_IMG_VALID_PNG)));
				System.out.println(StaticValues.LBL.action_xsd.get());
			}
		} else {
			System.err.println(StaticValues.LBL.action_nofile.get());
		}
	}

	/**
	 * Validation using a RNG
	 */
	private void profilValid() {
		if (current_file != null) {
			File rng = tempFile;
			tempFile = null;
			if (rng != null) {
				if (profil_validation(current_file, rng)) {
					texteOut.insertIcon(new ImageIcon(getClass().getResource(
							RESOURCES_IMG_VALID_PNG)));
					System.out.println(StaticValues.LBL.action_profil.get());
				}
			}
		} else {
			System.err.println(StaticValues.LBL.action_nofile.get());
		}
	}

	/**
	 * Validation using Schematron
	 */
	private void schematronValid() {
		if (current_file != null) {
			InputStream schematron = StaticValues.getCurrentSchInputStream();
			if (schematron != null) {
				if (schematron_validation(current_file, schematron, config)) {
					texteOut.insertIcon(new ImageIcon(getClass().getResource(
							RESOURCES_IMG_VALID_PNG)));
					System.out.println(StaticValues.LBL.action_schematron.get());
				}
			}
		} else {
			System.err.println(StaticValues.LBL.action_nofile.get());
		}
	}

	/**
	 * XSL transformation (generate xmloriginalname_xslname.xml)
	 */
	private void xslTranform() {
		if (current_file != null) {
			InputStream xsl = StaticValues.getCurrentXslInputStream();
			if (xsl != null) {
				String nameout = getNameFrom2Files(current_file.getName(),
						StaticValues.getCurrentXslFile()) + ".xml";
				File out = transform(current_file, xsl, config, nameout);
				if (out != null) {
					texteOut.insertIcon(new ImageIcon(getClass().getResource(
							RESOURCES_IMG_VALID_PNG)));
					System.out.println("Transform: " + out.getAbsolutePath());
				} else {
					System.err.println("Transform KO");
				}
			}
		} else {
			System.err.println(StaticValues.LBL.action_nofile.get());
		}
	}

	/**
	 * XSL transformation (generate seda_v1-0_filetype_code.xsd)
	 */
	private void xslDroidTranform() {
		File fic = tempFile;
		tempFile = null;
		if (fic != null) {
			InputStream xsl = StaticValues.class.getResourceAsStream(StaticValues.DROID2XSD);
			if (xsl != null) {
				String nameout = SEDA_V1_0_FILETYPE_CODE_XSD;
				File out = transform(fic, xsl, config, nameout);
				if (out != null) {
					texteOut.insertIcon(new ImageIcon(getClass().getResource(
							RESOURCES_IMG_VALID_PNG)));
					System.out.println("Transform: " + out.getAbsolutePath());
				} else {
					System.err.println("Transform KO");
				}
			}
		} else {
			System.err.println(StaticValues.LBL.action_nofile.get());
		}
	}

	/**
	 * XSL transformation (generate Archive XML as in SEDA XSD)
	 */
	private void xslArchiveTranform() {
		File fic = tempFile;
		tempFile = null;
		if (fic != null && fileToApplyXsl != null) {
			InputStream xsl;
			try {
				xsl = new FileInputStream(fileToApplyXsl);
			} catch (FileNotFoundException e) {
				System.err.println(StaticValues.LBL.action_nofile.get() + 
						" XSL: " + fileToApplyXsl.getAbsolutePath());
				return;
			}
			if (xsl != null) {
				String nameout = getNameFrom2Files(fic.getName(),
						fileToApplyXsl.getName()) + ".xml";
				File out = transform(fic, xsl, config, nameout);
				if (out != null) {
					texteOut.insertIcon(new ImageIcon(getClass().getResource(
							RESOURCES_IMG_VALID_PNG)));
					System.out.println("Transform: " + out.getAbsolutePath());
				} else {
					System.err.println("Transform KO");
				}
			}
		} else {
			System.err.println(StaticValues.LBL.action_nofile.get());
		}
	}

	/**
	 * Validation of existence of Attachments
	 */
	private void attachmentValid() {
		if (current_file != null) {
			config.nbDocument = 0;
			vitamResult = all_tests_in_one(current_file, null, config, config.argument,
					false, false, false);
			if (vitamResult.values[AllTestsItems.SystemError.ordinal()] +
					vitamResult.values[AllTestsItems.GlobalError.ordinal()] > 0) {
				// error
				System.err.println(StaticValues.LBL.error_attachment.get() +
						" [ " + vitamResult.labels[AllTestsItems.SystemError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.SystemError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FileError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FileError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FileSuccess.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FileSuccess.ordinal()] + " ]");
				vitamResult = null;
			} else {
				// OK
				texteOut.insertIcon(new ImageIcon(getClass().getResource(
						RESOURCES_IMG_VALID_PNG)));
				System.out
						.println(StaticValues.LBL.action_attachment.get() +
								" [ " + vitamResult.values[AllTestsItems.FileSuccess.ordinal()]
								+ " ]");
				config.nbDocument = vitamResult.values[AllTestsItems.FileSuccess.ordinal()];
			}
		} else {
			vitamResult = null;
			System.err.println(StaticValues.LBL.action_nofile.get());
		}
	}

	/**
	 * Validation of existence of Attachments and the corresponding digests
	 */
	private void digestValid(RunnerLongTask task) {
		if (current_file != null) {
			vitamResult = all_tests_in_one(current_file, task, config, config.argument,
					true, false, false);
			if (vitamResult.values[AllTestsItems.SystemError.ordinal()] +
					vitamResult.values[AllTestsItems.GlobalError.ordinal()] > 0) {
				// error
				System.err.println(StaticValues.LBL.error_digest.get() +
						" [ " + vitamResult.labels[AllTestsItems.SystemError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.SystemError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FileError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FileError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FileSuccess.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FileSuccess.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.DigestError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.DigestError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.DigestSuccess.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.DigestSuccess.ordinal()] +
						" ]");
				if (vitamResult.values[AllTestsItems.SystemError.ordinal()] > 0) {
					vitamResult = null;
				}
			} else {
				// OK
				texteOut.insertIcon(new ImageIcon(getClass().getResource(
						RESOURCES_IMG_VALID_PNG)));
				System.out
						.println(StaticValues.LBL.action_digest.get() +
								" [ " + vitamResult.values[AllTestsItems.DigestSuccess.ordinal()]
								+ " ]");
				config.nbDocument = vitamResult.values[AllTestsItems.DigestSuccess.ordinal()];
			}
		} else {
			System.err.println(StaticValues.LBL.action_nofile.get());
			vitamResult = null;
		}
	}

	/**
	 * Check format of attachment
	 */
	private void checkFormat(RunnerLongTask task) {
		if (current_file != null) {
			System.out.println("Starting checking format...\n");
			try {
				config.initDroid();
			} catch (CommandExecutionException e) {
				System.err.println(StaticValues.LBL.error_initdroid.get() + e.toString());
				vitamResult = null;
				return;
			}
			VitamArgument argument = config.argument.getOnlyOutputModel();
			argument.checkSubFormat = true;
			vitamResult = all_tests_in_one(current_file, task, config, argument,
					false, true, false);
			if (vitamResult.values[AllTestsItems.SystemError.ordinal()] +
					vitamResult.values[AllTestsItems.GlobalError.ordinal()] > 0) {
				// error
				System.err.println(StaticValues.LBL.error_format.get() +
						" [ " + vitamResult.labels[AllTestsItems.SystemError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.SystemError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FileError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FileError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FileSuccess.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FileSuccess.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FormatError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FormatError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FormatWarning.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FormatWarning.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FormatSuccess.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FormatSuccess.ordinal()] +
						" ]");
				if (vitamResult.values[AllTestsItems.SystemError.ordinal()] > 0) {
					vitamResult = null;
				}
			} else {
				// OK
				texteOut.insertIcon(new ImageIcon(getClass().getResource(
						RESOURCES_IMG_VALID_PNG)));
				if (vitamResult.values[AllTestsItems.FormatWarning.ordinal()] > 0) {
					// Warning
					System.out
							.println(StaticValues.LBL.action_format.get() +
									" [ Warning:" +
									vitamResult.values[AllTestsItems.FormatWarning.ordinal()] +
									" Success:" +
									vitamResult.values[AllTestsItems.FormatSuccess.ordinal()] +
									" ]");
				} else {
					System.out
							.println(StaticValues.LBL.action_format.get() +
									" [ " +
									vitamResult.values[AllTestsItems.FormatSuccess.ordinal()]
									+ " ]");
				}
				config.nbDocument = vitamResult.values[AllTestsItems.FormatSuccess.ordinal()] +
						vitamResult.values[AllTestsItems.FormatWarning.ordinal()];
			}
		} else {
			System.err.println(StaticValues.LBL.action_nofile.get());
			vitamResult = null;
		}
	}

	/**
	 * Output formats of attachment
	 */
	private void outputFormat(RunnerLongTask task) {
		if (current_file != null) {
			System.out.println("Starting getting format...\n");
			try {
				config.initDroid();
			} catch (CommandExecutionException e) {
				System.err.println(StaticValues.LBL.error_initdroid.get() + e.toString());
				vitamResult = null;
				return;
			}
			try {
				config.initFits();
			} catch (CommandExecutionException e) {
				System.err.println(StaticValues.LBL.error_initfits.get() + e.toString());
				vitamResult = null;
				return;
			}
			vitamResult = all_tests_in_one(current_file, task, config,
					config.argument.getOnlyOutputModel(),
					false, false, true);
			if (vitamResult.values[AllTestsItems.SystemError.ordinal()] +
					vitamResult.values[AllTestsItems.GlobalError.ordinal()] > 0) {
				// error
				System.err.println(StaticValues.LBL.error_format.get() +
						" [ " + vitamResult.labels[AllTestsItems.SystemError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.SystemError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FileError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FileError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FileSuccess.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FileSuccess.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.ShowError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.ShowError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.ShowSuccess.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.ShowSuccess.ordinal()] +
						" ]");
				if (vitamResult.values[AllTestsItems.SystemError.ordinal()] > 0) {
					vitamResult = null;
				}
			} else {
				// OK
				texteOut.insertIcon(new ImageIcon(getClass().getResource(
						RESOURCES_IMG_VALID_PNG)));
				System.out
						.println(StaticValues.LBL.action_outformat.get() +
								" [ " + vitamResult.values[AllTestsItems.ShowSuccess.ordinal()]
								+ " ]");
				config.nbDocument = vitamResult.values[AllTestsItems.DigestSuccess.ordinal()];
			}
		} else {
			System.err.println(StaticValues.LBL.action_nofile.get());
			vitamResult = null;
		}
	}

	/**
	 * Check one directory against the full format
	 * 
	 * @param task
	 */
	private void oneDirOrFileShow(RunnerLongTask task) {
		List<File> files = filesToScan;
		filesToScan = null;
		try {
			config.initDroid();
			vitamResult = null;
		} catch (CommandExecutionException e) {
			System.err.println(StaticValues.LBL.error_initdroid.get() + e.toString());
			vitamResult = null;
			return;
		}
		try {
			config.initFits();
		} catch (CommandExecutionException e) {
			System.err.println(StaticValues.LBL.error_initdroid.get() + e.toString());
			vitamResult = null;
			return;
		}
		System.out.println("Show File Type");
		Element root = null;
		vitamResult = new VitamResult();
		if (config.argument.outputModel == VitamOutputModel.OneXML) {
			root = XmlDom.factory.createElement("checkfiles");
			root.addAttribute("source", config.lastScannedDirectory.getAbsolutePath());
			vitamResult.unique = XmlDom.factory.createDocument(root);
		} else {
			// force multiple
			vitamResult.multiples = new ArrayList<Document>();
		}
		int currank = 0;
		int warning = 0;
		for (File file : files) {
			currank++;
			String shortname;
			if (config.lastScannedDirectory.isDirectory()) {
				shortname = StaticValues.getSubPath(file, config.lastScannedDirectory);
			} else {
				shortname = config.lastScannedDirectory.getName();
			}
			Element result = Commands.showFormat(shortname,
					null, null,
					file, config, config.argument);
			if (result.selectSingleNode(".[@status='warning']") != null) {
				warning++;
			}
			XmlDom.addDate(config.argument, config, result);
			if (root != null) {
				root.add(result);
			} else {
				// multiple
				root = XmlDom.factory.createElement("checkfiles");
				root.addAttribute("source", config.lastScannedDirectory.getAbsolutePath());
				Document document = XmlDom.factory.createDocument(root);
				root.add(result);
				vitamResult.multiples.add(document);
				root = null;
			}
			if (task != null) {
				task.setProgressExternal(currank);
			}
		}
		if (root != null) {
			XmlDom.addDate(config.argument, config, root);
		}

		texteOut.insertIcon(new ImageIcon(getClass().getResource(
				RESOURCES_IMG_VALID_PNG)));
		System.out
				.println(StaticValues.LBL.action_outformat.get() +
						" [ " + currank + (warning > 0 ? " (" + StaticValues.LBL.error_warning.get() + warning + " ) " : "" ) + " ]");
	}

	/**
	 * Check a directory only against type
	 * 
	 * @param task
	 */
	private void oneDirOrFileCheck(RunnerLongTask task) {
		List<File> files = filesToScan;
		filesToScan = null;
		try {
			config.initDroid();
			vitamResult = null;
		} catch (CommandExecutionException e) {
			System.err.println(StaticValues.LBL.error_initdroid.get() + e.toString());
			vitamResult = null;
			return;
		}
		System.out.println("Check File Type");
		Element root = null;
		vitamResult = new VitamResult();
		if (config.argument.outputModel == VitamOutputModel.OneXML) {
			root = XmlDom.factory.createElement("checkfiles");
			root.addAttribute("source", config.lastScannedDirectory.getAbsolutePath());
			vitamResult.unique = XmlDom.factory.createDocument(root);
			Element newElt = XmlDom.factory.createElement("toolsversion");
			if (StaticValues.config.droidHandler != null) {
				newElt.addAttribute("pronom", StaticValues.config.droidHandler.getVersionSignature());
			}
			if (StaticValues.config.droidHandler != null) {
				newElt.addAttribute("droid", "6.1");
			}
			root.add(newElt);
		} else {
			// force multiple
			vitamResult.multiples = new ArrayList<Document>();
		}
		List<DroidFileFormat> list = null;
		int warning = 0;
		try {
			list = config.droidHandler.checkFilesFormat(files,
					config.argument, task);
			String pathBeforeArg = config.lastScannedDirectory.getCanonicalPath();
			pathBeforeArg = pathBeforeArg.substring(0,
					pathBeforeArg.indexOf(config.lastScannedDirectory.getName()));
			for (DroidFileFormat droidFileFormat : list) {
				boolean warn = false;
				Element fileformat = droidFileFormat.toElement(true);
				if (config.preventXfmt && droidFileFormat.getPUID().startsWith(StaticValues.FORMAT_XFMT)) {
					warning ++;
					warn = true;
				}
				Attribute filename = fileformat.attribute("filename");
				if (filename != null) {
					String value = filename.getText();
					filename.setText(value.replace(pathBeforeArg, ""));
				}
				XmlDom.addDate(config.argument, config, fileformat);
				if (root != null) {
					root.add(fileformat);
				} else {
					// multiple
					root = XmlDom.factory.createElement("checkfiles");
					root.addAttribute("source", config.lastScannedDirectory.getAbsolutePath());
					Document document = XmlDom.factory.createDocument(root);
					root.add(fileformat);
					if (warn) {
						root.addAttribute("status", "warning");
					} else {
						root.addAttribute("status", "ok");
					}
					vitamResult.multiples.add(document);
					root = null;
				}
			}
		} catch (CommandExecutionException e) {
			System.err.println(StaticValues.LBL.error_analysis.get() + e);
			e.printStackTrace();
			vitamResult = null;
		} catch (IOException e) {
			System.err.println(StaticValues.LBL.error_analysis.get() + e);
			vitamResult = null;
		}
		if (root != null) {
			XmlDom.addDate(config.argument, config, root);
			if (warning > 0) {
				root.addAttribute("status", "warning");
			} else {
				root.addAttribute("status", "ok");
			}
		}
		if (list != null) {
			texteOut.insertIcon(new ImageIcon(getClass().getResource(
					RESOURCES_IMG_VALID_PNG)));
			System.out.println(StaticValues.LBL.action_format.get() +
					" [ " + list.size() + " / " + files.size() +
					(warning > 0 ? " (" + StaticValues.LBL.error_warning.get() + warning + " )" : "" ) + " ]");
		}
	}
	
	/**
	 * Digest for one directory in detail
	 * 
	 */
	private void computeDigest() {
		this.setEnabled(false);
		frameDigestDialog.setVisible(true);
		vitamDigestDialog.initValue();
	}
	/**
	 * Check one directory against digest
	 * 
	 * @param task
	 */
	private void oneDirDigest(RunnerLongTask task) {
		List<File> files = filesToScan;
		filesToScan = null;
		System.out.println("Digest...");
		Element root = null;
		vitamResult = new VitamResult();
		if (config.argument.outputModel == VitamOutputModel.OneXML) {
			root = XmlDom.factory.createElement("digests");
			root.addAttribute("source", config.lastScannedDirectory.getAbsolutePath());
			vitamResult.unique = XmlDom.factory.createDocument(root);
		} else {
			// force multiple
			vitamResult.multiples = new ArrayList<Document>();
		}
		int currank = 0;
		int error = 0;
		for (File file : files) {
			currank++;
			String shortname;
			if (config.lastScannedDirectory.isDirectory()) {
				shortname = StaticValues.getSubPath(file, config.lastScannedDirectory);
			} else {
				shortname = config.lastScannedDirectory.getName();
			}
			FileInputStream inputstream;
			try {
				inputstream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				System.err.println(StaticValues.LBL.error_computedigest.get() + ": " + shortname);
				continue;
			}
			String []shas = DigestCompute.computeDigest(inputstream, config.argument);
			//SEDA type since already configured
			Element result = XmlDom.factory.createElement(StaticValues.config.DOCUMENT_FIELD);
			Element attachment = XmlDom.factory.createElement(StaticValues.config.ATTACHMENT_FIELD);
			attachment.addAttribute(StaticValues.config.FILENAME_ATTRIBUTE.substring(1), shortname);
			result.add(attachment);
			if (shas[0] != null) {
				Element integrity = XmlDom.factory.createElement(StaticValues.config.INTEGRITY_FIELD);
				integrity.addAttribute(StaticValues.config.ALGORITHME_ATTRIBUTE.substring(1), StaticValues.XML_SHA1);
				integrity.setText(shas[0]);
				result.add(integrity);
			}
			if (shas[1] != null) {
				Element integrity = XmlDom.factory.createElement(StaticValues.config.INTEGRITY_FIELD);
				integrity.addAttribute(StaticValues.config.ALGORITHME_ATTRIBUTE.substring(1), StaticValues.XML_SHA256);
				integrity.setText(shas[1]);
				result.add(integrity);
			}
			if (shas[2] != null) {
				Element integrity = XmlDom.factory.createElement(StaticValues.config.INTEGRITY_FIELD);
				integrity.addAttribute(StaticValues.config.ALGORITHME_ATTRIBUTE.substring(1), StaticValues.XML_SHA512);
				integrity.setText(shas[2]);
				result.add(integrity);
			}
			if ((shas[0] == null && config.argument.sha1) || 
					(shas[1] == null && config.argument.sha256) ||
					(shas[2] == null && config.argument.sha512)) {
				result.addAttribute("status", "error");
				error ++;
			} else {
				result.addAttribute("status", "ok");
			}
			XmlDom.addDate(config.argument, config, result);
			if (root != null) {
				root.add(result);
			} else {
				// multiple
				root = XmlDom.factory.createElement("digests");
				root.addAttribute("source", config.lastScannedDirectory.getAbsolutePath());
				Document document = XmlDom.factory.createDocument(root);
				root.add(result);
				vitamResult.multiples.add(document);
				root = null;
			}
			if (task != null) {
				task.setProgressExternal(currank);
			}
		}
		if (root != null) {
			if (error == 0) {
				root.addAttribute("status", "ok");
			} else {
				root.addAttribute("status", "error on " + error + " / " + currank + " file checks");
			}
			XmlDom.addDate(config.argument, config, root);
		}

		texteOut.insertIcon(new ImageIcon(getClass().getResource(
				RESOURCES_IMG_VALID_PNG)));
		System.out
				.println(StaticValues.LBL.action_digest.get() +
						" [ " + currank + (error > 0 ? " (" + StaticValues.LBL.error_error.get() + error + " ) " : "" ) + " ]");
	}

	private void addPdfaElement(Element root, Element pdfa, File basedir, File baseoutdir,
			boolean error, String serror, String puid) {
		XmlDom.addDate(config.argument, config, pdfa);
		if (puid != null) {
			pdfa.addAttribute("puid", puid);
		}
		if (root != null) {
			root.add(pdfa);
		} else {
			// multiple
			root = XmlDom.factory.createElement("transform");
			root.addAttribute("source", basedir.getAbsolutePath());
			root.addAttribute("target", baseoutdir.getAbsolutePath());
			Document document = XmlDom.factory.createDocument(root);
			root.add(pdfa);
			if (error) {
				root.addAttribute("status", serror);
			} else {
				root.addAttribute("status", "ok");
			}
			vitamResult.multiples.add(document);
			root = null;
		}
	}
	/**
	 * Convert files to PDFA through LibreOffice/OpenOffice (temptative)
	 * @param task
	 */
	public void convertToPdf(RunnerLongTask task) {
		List<File> files = filesToScan;
		filesToScan = null;
		File basedir = config.lastScannedDirectory;
		File baseoutdir = tempFile;
		tempFile = null;
		int errorcpt = 0;
		boolean checkDroid = false;
		try {
			config.initDroid();
			vitamResult = null;
			checkDroid = true;
		} catch (CommandExecutionException e) {
			System.err.println(StaticValues.LBL.error_initdroid.get() + e.toString());
			vitamResult = null;
		}
		System.out.println("Transform PDF/A-1B");
		Element root = null;
		vitamResult = new VitamResult();
		if (config.argument.outputModel == VitamOutputModel.OneXML) {
			root = XmlDom.factory.createElement("transform");
			root.addAttribute("source", basedir.getAbsolutePath());
			root.addAttribute("target", baseoutdir.getAbsolutePath());
			vitamResult.unique = XmlDom.factory.createDocument(root);
		} else {
			// force multiple
			vitamResult.multiples = new ArrayList<Document>();
		}
		int currank = 0;
		for (File file : files) {
			currank ++;
			String basename = file.getName();
			File rootdir;
			String subpath = null;
			if (file.getParentFile().equals(basedir)) {
				rootdir = basedir;
				subpath = File.separator;
			} else {
				rootdir = file.getParentFile();
				subpath = rootdir.getAbsolutePath().replace(basedir.getAbsolutePath(), "") +
						File.separator;
			}
			String fullname = subpath + basename;
			String puid = null;
			if (checkDroid) {
				try {
					List<DroidFileFormat> list = 
							config.droidHandler.checkFileFormat(file, config.argument);
					if (list == null || list.isEmpty()) {
						System.err.println("Ignore: " + fullname);
						Element pdfa = XmlDom.factory.createElement("convert");
						Element newElt = XmlDom.factory.createElement("file");
						newElt.addAttribute("filename", fullname);
						pdfa.add(newElt);
						addPdfaElement(root, pdfa, basedir, baseoutdir, 
								true, "Error: filetype not found", null);
						if (task != null) {
							task.setProgressExternal(currank);
						}
						errorcpt ++;
						continue;
					}
					DroidFileFormat type = list.get(0);
					puid = type.getPUID();
					if (puid.startsWith(StaticValues.FORMAT_XFMT) ||
							puid.equals("fmt/411")) { // x-fmt or RAR
						System.err.println("Ignore: " + fullname + " " + puid);
						if (task != null) {
							task.setProgressExternal(currank);
						}
						Element pdfa = XmlDom.factory.createElement("convert");
						Element newElt = XmlDom.factory.createElement("file");
						newElt.addAttribute("filename", fullname);
						pdfa.add(newElt);
						addPdfaElement(root, pdfa, basedir, baseoutdir, 
								true, "Error: filetype not allowed", puid);
						errorcpt ++;
						continue;
					}
				} catch (CommandExecutionException e) {
					// ignore
				}
			}
			System.out.println("PDF/A-1B convertion... " + fullname);
			long start = System.currentTimeMillis();
			Element pdfa = PdfaConverter.convertPdfA(subpath, basename, basedir, baseoutdir, config);
			long end = System.currentTimeMillis();
			boolean error = false;
			if (pdfa.selectSingleNode(".[@status='ok']") == null) {
				error = true;
				errorcpt ++;
			}
			if (error) {
				System.err.println(StaticValues.LBL.error_pdfa.get() +
						" PDF/A-1B KO: " + fullname + 
						" " + ((end-start) * 1024 / file.length()) + " ms/KB " +
						(end-start) + " ms " + "\n");
			} else {
				System.out.println("PDF/A-1B OK: " + fullname + 
						" " + ((end-start) * 1024 / file.length()) + " ms/KB " +
						(end-start) + " ms " +
						"\n");
			}
			addPdfaElement(root, pdfa, basedir, baseoutdir, error, "error", puid);
			if (task != null) {
				task.setProgressExternal(currank);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		if (root != null) {
			XmlDom.addDate(config.argument, config, root);
			if (errorcpt > 0) {
				root.addAttribute("status", "error found");
			} else {
				root.addAttribute("status", "ok");
			}
		}
		if (errorcpt < files.size()) {
			texteOut.insertIcon(new ImageIcon(getClass().getResource(
					RESOURCES_IMG_VALID_PNG)));
			System.out.println(StaticValues.LBL.action_pdfa.get() +
					" [ " + files.size() +
					(errorcpt > 0 ? " (" + StaticValues.LBL.error_error.get() + errorcpt + " )" : "" ) + " ]");
		} else {
			System.err.println(StaticValues.LBL.error_pdfa.get() +
					" [ " + StaticValues.LBL.error_error.get() + errorcpt + " ]");
		}
	}

	/**
	 * Run a defined command according to label through RunnerLongTask
	 * 
	 * @param label
	 */
	protected final void runCommand(StaticValues.LBL label) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		changeAllButtonMenu(false);
		RunnerLongTask task = new RunnerLongTask(label, this);
		task.addPropertyChangeListener(this);
		task.execute();
	}

	/**
	 * Used to extend model
	 * 
	 * @param label
	 */
	protected void runSwingWorkerExtraCommand(StaticValues.LBL label) {
		System.err.println("SWExtraCommand: " + label.label);
	}

	/**
	 * Used to extend model
	 * 
	 * @param label
	 */
	protected void runEndExtraCommand(StaticValues.LBL label) {
		System.err.println("ExtraCommand: " + label.label);
	}

	/**
	 * Class to allow asynchronous execution of tasks
	 * 
	 * @author "Frederic Bregier"
	 * 
	 */
	public static class RunnerLongTask extends SwingWorker<Object, Void> {
		StaticValues.LBL label;
		VitamGui gui;

		private RunnerLongTask(StaticValues.LBL label, VitamGui gui) {
			super();
			this.label = label;
			this.gui = gui;
		}

		@Override
		protected void done() {
			Object object;
			boolean checkxml = false;
			try {
				object = get();
				gui.setCursor(null); // turn off the wait cursor
				switch (label) {
					case file_xsd:
						this.gui.finalXsdValid(object);
						break;
					case tools_dir_format_output:
					case tools_dir_format_test:
					case tools_dir_digest:
					case tools_pdfa:
						this.gui.config.nbDocument = 0;
						checkxml = false;
						break;
					case file_open:
					case tools_attachment_test:
					case tools_hashcode_test:
					case tools_profil_validation:
					case tools_schematron_validation:
					case tools_xsd_validation:
					case tools_transform:
					case tools_droidtransform:
					case tools_vitam2seda:
					case tools_xml_validation:
					case tools_attachment_format_test:
					case tools_attachment_format_output:
						checkxml = true;
						break;
					default:
						this.gui.runEndExtraCommand(label);
						break;
				}
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			} finally {
				gui.endProgressBar();
				gui.changeAllButtonMenu(true);
				if (gui.current_file == null) {
					gui.changeButtonMenu(false);
				}
			}
			if (gui.vitamResult != null) {
				if (gui.vitamResult.unique != null) {
					String text = null;
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					try {
						gui.writer.setOutputStream(out);
						gui.writer.write(gui.vitamResult.unique);
						gui.writer.flush();
						text = out.toString(StaticValues.CURRENT_OUTPUT_ENCODING);
					} catch (UnsupportedEncodingException e) {
						gui.vitamResult = null;
						return;
					} catch (IOException e) {
						gui.vitamResult = null;
						return;
					} finally {
						try {
							out.close();
						} catch (IOException e) {
						}
					}
					JTextArea textArea = new JTextArea(text);
					final JPanel panel = new JPanel(new BorderLayout());
					panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
					panel.setPreferredSize(new Dimension(500, 500));
					// TIP: Make the JOptionPane resizable using the HierarchyListener
			        panel.addHierarchyListener(new HierarchyListener() {
			            public void hierarchyChanged(HierarchyEvent e) {
			                Window window = SwingUtilities.getWindowAncestor(panel);
			                if (window instanceof Dialog) {
			                    Dialog dialog = (Dialog)window;
			                    if (!dialog.isResizable()) {
			                        dialog.setResizable(true);
			                    }
			                }
			            }
			        });
					JOptionPane.showConfirmDialog(gui, panel,
							"Xml Result",
							JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (gui.config.guiProposeFileSaving) {
						// XML write to file
						String current = null;
						if (checkxml) {
							current = (gui.current_file == null ? null : gui.current_file.getAbsolutePath());
						} else {
							current = (gui.config.lastScannedDirectory == null ? null : gui.config.lastScannedDirectory.getAbsolutePath());
						}
						File file = gui.saveFile(current, "XML output", "xml", true);
						if (file != null) {
							FileOutputStream out2 = null;
							try {
								out2 = new FileOutputStream(file);
								gui.writer.setOutputStream(out2);
								gui.writer.write(gui.vitamResult.unique);
								gui.writer.flush();
							} catch (UnsupportedEncodingException e) {
								gui.vitamResult = null;
								return;
							} catch (IOException e) {
								gui.vitamResult = null;
								return;
							} finally {
								try {
									if (out2 != null) {
										out2.close();
									}
								} catch (IOException e) {
								}
							}
						}
					}
				} else if (gui.vitamResult.multiples != null
						&& !gui.vitamResult.multiples.isEmpty()) {
					String text = null;
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					try {
						gui.writer.setOutputStream(out);
						for (Document document : gui.vitamResult.multiples) {
							gui.writer.write(document);
							gui.writer.flush();
							out.write("\n".getBytes());
						}
						text = out.toString(StaticValues.CURRENT_OUTPUT_ENCODING);
					} catch (UnsupportedEncodingException e) {
						gui.vitamResult = null;
						return;
					} catch (IOException e) {
						gui.vitamResult = null;
						return;
					} finally {
						try {
							out.close();
						} catch (IOException e) {
						}
					}
					JTextArea textArea = new JTextArea(text);
					JPanel panel = new JPanel(new BorderLayout());
					panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
					panel.setPreferredSize(new Dimension(550, 500));
					JOptionPane.showConfirmDialog(gui, panel,
							"Xml Result",
							JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (gui.config.guiProposeFileSaving) {
						// XMLs write to file
						String current = (gui.current_file == null ? null : gui.current_file.getAbsolutePath());
						File file = gui.saveFile(current, "XML output", "xml", true);
						if (file != null) {
							FileOutputStream out2 = null;
							try {
								out2 = new FileOutputStream(file);
								gui.writer.setOutputStream(out2);
								for (Document document : gui.vitamResult.multiples) {
									gui.writer.write(document.getRootElement());
									gui.writer.flush();
								}
							} catch (UnsupportedEncodingException e) {
								gui.vitamResult = null;
								return;
							} catch (IOException e) {
								gui.vitamResult = null;
								return;
							} finally {
								try {
									if (out2 != null) {
										out2.close();
									}
								} catch (IOException e) {
								}
							}
						}
					}
				}
				gui.vitamResult = null;
			}
		}

		public void setProgressExternal(final int progress) {
			setProgress(progress);
		}

		@Override
		protected Object doInBackground() throws Exception {
			setProgress(0);
			switch (label) {
				case file_open:
					gui.xml_valid();
					break;
				case file_xsd:
					return gui.swingWorkerXsdValid();
				case tools_attachment_test:
					gui.attachmentValid();
					break;
				case tools_hashcode_test:
					gui.digestValid(this);
					break;
				case tools_profil_validation:
					gui.profilValid();
					break;
				case tools_schematron_validation:
					gui.schematronValid();
					break;
				case tools_xsd_validation:
					gui.schemaValid();
					break;
				case tools_transform:
					gui.xslTranform();
					break;
				case tools_droidtransform:
					gui.xslDroidTranform();
					break;
				case tools_vitam2seda:
					gui.xslArchiveTranform();
					break;
				case tools_xml_validation:
					gui.xml_valid();
					break;
				case tools_attachment_format_test:
					gui.checkFormat(this);
					break;
				case tools_attachment_format_output:
					gui.outputFormat(this);
					break;
				case tools_dir_format_test:
					gui.oneDirOrFileCheck(this);
					break;
				case tools_dir_format_output:
					gui.oneDirOrFileShow(this);
					break;
				case tools_dir_digest:
					gui.oneDirDigest(this);
					break;
				case tools_pdfa:
					gui.convertToPdf(this);
					break;
				default:
					gui.runSwingWorkerExtraCommand(label);
					break;
			}
			return true;
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
		JOptionPane.showMessageDialog(this, "Note: " +
				"\n\tXSD = " +
				StaticValues.getCurrentXsdFile() +
				"\n\tSCH = " +
				StaticValues.getCurrentSchFile() +
				"\n\tXSL = " +
				StaticValues.getCurrentXslFile());
	}
}
