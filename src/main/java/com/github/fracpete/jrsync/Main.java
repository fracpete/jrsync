/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Main.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.jrsync;

import com.github.fracpete.jrsync.Execute.ConsoleOutputListener;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import nz.ac.waikato.cms.core.BrowserHelper;
import nz.ac.waikato.cms.gui.core.BaseFrame;
import nz.ac.waikato.cms.gui.core.BasePanel;
import nz.ac.waikato.cms.gui.core.BaseScrollPane;
import nz.ac.waikato.cms.gui.core.DirectoryChooserPanel;
import nz.ac.waikato.cms.gui.core.GUIHelper;
import nz.ac.waikato.cms.gui.core.ParameterPanel;
import org.apache.commons.configuration2.INIConfiguration;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Starts up the user interface, if no parameters given.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Main
  extends BasePanel {

  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /** the File -> Browse source menu item. */
  protected JMenuItem m_MenuItemFileBrowseSource;

  /** the File -> Browse destination menu item. */
  protected JMenuItem m_MenuItemFileBrowseDestination;

  /** the File -> Switch menu item. */
  protected JMenuItem m_MenuItemFileSwitch;

  /** the File -> Simulation menu item. */
  protected JMenuItem m_MenuItemFileSimulation;

  /** the File -> Execute menu item. */
  protected JMenuItem m_MenuItemFileExecute;

  /** the File -> Rsync commandline menu item. */
  protected JMenuItem m_MenuItemFileRsyncCmdline;

  /** the File -> Quit menu item. */
  protected JMenuItem m_MenuItemFileQuit;

  /** the Sessions -> Add menu item. */
  protected JMenuItem m_MenuItemSessionsAdd;

  /** the Sessions -> Delet menu item. */
  protected JMenuItem m_MenuItemSessionsDelete;

  /** the Sessions -> Save menu item. */
  protected JMenuItem m_MenuItemSessionsSave;

  /** the Sessions -> Import menu item. */
  protected JMenuItem m_MenuItemSessionsImport;

  /** the Sessions -> Export menu item. */
  protected JMenuItem m_MenuItemSessionsExport;

  /** the Help -> Homepage. */
  protected JMenuItem m_MenuItemHelpHomepage;

  /** the underlying sessions. */
  protected INIConfiguration m_Sessions;

  /** the combobox for the sessions. */
  protected JComboBox<String> m_ComboBoxSessions;

  /** the button for adding a session. */
  protected JButton m_ButtonAdd;

  /** the button for deleting a session. */
  protected JButton m_ButtonDelete;

  /** the button for testing a session. */
  protected JButton m_ButtonSimulate;

  /** the button for execute a session. */
  protected JButton m_ButtonExecute;

  /** the tabbed pane. */
  protected JTabbedPane m_TabbedPane;

  /** the source dir. */
  protected DirectoryChooserPanel m_ChooserSource;

  /** the destination dir. */
  protected DirectoryChooserPanel m_ChooserDestination;

  /** the GUI elements. */
  protected Map<String,Component> m_Params;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Sessions = Configuration.read();
    if (m_Sessions == null)
      m_Sessions = new INIConfiguration();
    m_Params = new HashMap<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel		panel;
    JPanel		panelTab;
    ParameterPanel 	panelParams;
    ParameterPanel 	panelParams1;
    ParameterPanel 	panelParams2;
    final JTextArea 	areaAdditional;
    JLabel		label;

    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new JTabbedPane();
    add(m_TabbedPane, BorderLayout.CENTER);

    // config selection
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panel, BorderLayout.NORTH);
    m_ComboBoxSessions = new JComboBox<>();
    m_ComboBoxSessions.setPreferredSize(new Dimension(300, 25));
    panel.add(m_ComboBoxSessions);
    m_ButtonAdd = new JButton(GUIHelper.getIcon("Add.png"));
    m_ButtonAdd.addActionListener((ActionEvent e) -> addSession());
    panel.add(m_ButtonAdd);
    m_ButtonDelete = new JButton(GUIHelper.getIcon("Delete.png"));
    m_ButtonDelete.addActionListener((ActionEvent e) -> deleteSession());
    panel.add(m_ButtonDelete);
    m_ButtonSimulate = new JButton(GUIHelper.getIcon("Simulate.png"));
    m_ButtonSimulate.addActionListener((ActionEvent e) -> simulate());
    panel.add(m_ButtonSimulate);
    m_ButtonExecute = new JButton(GUIHelper.getIcon("Execute.png"));
    m_ButtonExecute.addActionListener((ActionEvent e) -> execute());
    panel.add(m_ButtonExecute);

    // Basic
    panelTab = new JPanel(new BorderLayout());
    m_TabbedPane.addTab("Basic", panelTab);
    panelParams = new ParameterPanel();
    panelTab.add(panelParams, BorderLayout.NORTH);
    m_ChooserSource = new DirectoryChooserPanel();
    m_ChooserSource.setInlineEditingEnabled(true);
    m_ChooserSource.addChangeListener((ChangeEvent e) -> set("text_source"));
    m_Params.put("text_source", m_ChooserSource);
    panelParams.addParameter("Source", m_ChooserSource);
    m_ChooserDestination = new DirectoryChooserPanel();
    m_ChooserDestination.setInlineEditingEnabled(true);
    m_ChooserDestination.addChangeListener((ChangeEvent e) -> set("text_dest"));
    m_Params.put("text_dest", m_ChooserDestination);
    panelParams.addParameter("Destination", m_ChooserDestination);
    panel = new JPanel(new GridLayout(1, 2));
    panelTab.add(panel, BorderLayout.CENTER);
    panelParams1 = new ParameterPanel();
    panel.add(panelParams1);
    addCheckBox(panelParams1, "Preserve time", "check_time");
    addCheckBox(panelParams1, "Preserve owner", "check_owner");
    addCheckBox(panelParams1, "Delete on destination", "check_delete");
    addCheckBox(panelParams1, "Verbose", "check_verbose");
    addCheckBox(panelParams1, "Ignore existing", "check_exist");
    addCheckBox(panelParams1, "Skip newer", "check_skipnew");
    panelParams2 = new ParameterPanel();
    panel.add(panelParams2);
    addCheckBox(panelParams2, "Preserve permissions", "check_perm");
    addCheckBox(panelParams2, "Preserve group", "check_group");
    addCheckBox(panelParams2, "Do not leave filesystem", "check_onefs");
    addCheckBox(panelParams2, "Show transfer progress", "check_progr");
    addCheckBox(panelParams2, "Size only", "check_size");
    addCheckBox(panelParams2, "Windows compatibility", "check_windows");

    // Advanced
    panelTab = new JPanel(new BorderLayout());
    m_TabbedPane.addTab("Advanced options", panelTab);
    panel = new JPanel(new GridLayout(1, 2));
    panelTab.add(panel, BorderLayout.NORTH);
    panelParams1 = new ParameterPanel();
    panel.add(panelParams1);
    addCheckBox(panelParams1, "Always checksum", "check_sum");
    addCheckBox(panelParams1, "Preserve devices", "check_dev");
    addCheckBox(panelParams1, "Keep partially transferred files", "check_keepart");
    addCheckBox(panelParams1, "Copy symlinks as symlinks", "check_symlink");
    addCheckBox(panelParams1, "Make backups", "check_backup");
    addCheckBox(panelParams1, "Disable recursion", "check_norecur");
    panelParams2 = new ParameterPanel();
    panel.add(panelParams2);
    addCheckBox(panelParams2, "Compress file data", "check_compr");
    addCheckBox(panelParams2, "Only update existing", "check_update");
    addCheckBox(panelParams2, "Don't map uid/gid values", "check_mapuser");
    addCheckBox(panelParams2, "Copy hardlinks as hardlinks", "check_hardlink");
    addCheckBox(panelParams2, "Show itemized changes list", "check_itemized");
    addCheckBox(panelParams2, "Protect remote args", "check_protectargs");
    panel = new JPanel(new BorderLayout(5, 5));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panelTab.add(panel, BorderLayout.CENTER);
    label = new JLabel("Additional options:");
    panel.add(label, BorderLayout.NORTH);
    areaAdditional = new JTextArea(5, 40);
    areaAdditional.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	set("text_addit", areaAdditional.getText());
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	set("text_addit", areaAdditional.getText());
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	set("text_addit", areaAdditional.getText());
      }
    });
    m_Params.put("text_addit", areaAdditional);
    panel.add(new BaseScrollPane(areaAdditional), BorderLayout.CENTER);

    // Extra
    panelTab = new JPanel(new BorderLayout());
    m_TabbedPane.addTab("Extra options", panelTab);
    panelParams = new ParameterPanel();
    panelTab.add(panelParams, BorderLayout.CENTER);
    addCheckBox(panelParams, "Execute command before rsync", "check_com_before");
    addTextField(panelParams, "", "text_com_before");
    addCheckBox(panelParams, "Halt on failure", "check_com_halt");
    addCheckBox(panelParams, "Execute command after rsync", "check_com_after");
    addTextField(panelParams, "", "text_com_after");
    addCheckBox(panelParams, "On rsync error only", "check_com_onerror");
    addCheckBox(panelParams, "Browse files instead of folders", "check_browse_files");
    addCheckBox(panelParams, "Run as superuser", "check_superuser");
    addTextField(panelParams, "Notes", "text_notes");
  }

  /**
   * Adds a checkbox.
   *
   * @param paramPanel	the panel to add the check box to
   * @param label	the label for the checkbox
   * @param key		the key to store it under and for updating the session
   */
  protected void addCheckBox(ParameterPanel paramPanel, String label, final String key) {
    JCheckBox box;

    box = new JCheckBox();
    box.addActionListener((ActionEvent e) -> set(key));
    m_Params.put(key, box);
    paramPanel.addParameter(label, box);
  }

  /**
   * Adds a text area.
   *
   * @param paramPanel	the panel to add the text area to
   * @param label	the label for the text area
   * @param key		the key to store it under and for updating the session
   */
  protected void addTextArea(ParameterPanel paramPanel, String label, final String key) {
    final JTextArea area;

    area = new JTextArea(5, 40);
    area.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	set(key, area.getText());
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	set(key, area.getText());
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	set(key, area.getText());
      }
    });
    m_Params.put(key, area);
    paramPanel.addParameter(label, area);
  }

  /**
   * Adds a text field.
   *
   * @param paramPanel	the panel to add the text field to
   * @param label	the label for the text field
   * @param key		the key to store it under and for updating the session
   */
  protected void addTextField(ParameterPanel paramPanel, String label, final String key) {
    final JTextField text;

    text = new JTextField(20);
    text.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	set(key, text.getText());
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	set(key, text.getText());
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	set(key, text.getText());
      }
    });
    m_Params.put(key, text);
    paramPanel.addParameter(label, text);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    initMenuBar();
    selectSession(Configuration.DEFAULT);
  }

  /**
   * Initializes the menu bar.
   */
  protected void initMenuBar() {
    JMenu	menu;
    JMenuItem	item;

    m_MenuBar = new JMenuBar();

    // File
    menu = new JMenu("File");
    menu.setMnemonic('F');
    menu.addActionListener((ActionEvent e) -> updateMenu());
    m_MenuBar.add(menu);

    item = new JMenuItem("Browse source", GUIHelper.getIcon("Empty.png"));
    item.addActionListener((ActionEvent e) -> m_ChooserSource.choose());
    menu.add(item);
    m_MenuItemFileBrowseSource = item;

    item = new JMenuItem("Browse destination", GUIHelper.getIcon("Empty.png"));
    item.addActionListener((ActionEvent e) -> m_ChooserDestination.choose());
    menu.add(item);
    m_MenuItemFileBrowseDestination = item;

    item = new JMenuItem("Switch source and destination", GUIHelper.getIcon("Empty.png"));
    item.addActionListener((ActionEvent e) -> switchSourceDestination());
    menu.add(item);
    m_MenuItemFileSwitch = item;

    menu.addSeparator();

    item = new JMenuItem("Simulation", GUIHelper.getIcon("Simulate.png"));
    item.addActionListener((ActionEvent e) -> simulate());
    menu.add(item);
    m_MenuItemFileSimulation = item;

    item = new JMenuItem("Execute", GUIHelper.getIcon("Execute.png"));
    item.addActionListener((ActionEvent e) -> execute());
    menu.add(item);
    m_MenuItemFileExecute = item;

    item = new JMenuItem("Rsync command-line", GUIHelper.getIcon("Empty.png"));
    item.addActionListener((ActionEvent e) -> cmdline());
    menu.add(item);
    m_MenuItemFileRsyncCmdline = item;

    menu.addSeparator();

    item = new JMenuItem("Quit", GUIHelper.getIcon("Exit.png"));
    item.addActionListener((ActionEvent e) -> close());
    menu.add(item);
    m_MenuItemFileQuit = item;

    // Sessions
    menu = new JMenu("Sessions");
    menu.setMnemonic('S');
    menu.addActionListener((ActionEvent e) -> updateMenu());
    m_MenuBar.add(menu);

    item = new JMenuItem("Add", GUIHelper.getIcon("Add.png"));
    item.addActionListener((ActionEvent e) -> addSession());
    menu.add(item);
    m_MenuItemSessionsAdd = item;

    item = new JMenuItem("Delete", GUIHelper.getIcon("Delete.png"));
    item.addActionListener((ActionEvent e) -> deleteSession());
    menu.add(item);
    m_MenuItemSessionsDelete = item;

    item = new JMenuItem("Save", GUIHelper.getIcon("Save.png"));
    item.addActionListener((ActionEvent e) -> saveSessions());
    menu.add(item);
    m_MenuItemSessionsSave = item;

    item = new JMenuItem("Import", GUIHelper.getIcon("Empty.png"));
    item.addActionListener((ActionEvent e) -> importSession());
    menu.add(item);
    m_MenuItemSessionsImport = item;

    item = new JMenuItem("Export", GUIHelper.getIcon("Empty.png"));
    item.addActionListener((ActionEvent e) -> exportSession());
    menu.add(item);
    m_MenuItemSessionsExport = item;

    // Help
    menu = new JMenu("Help");
    menu.setMnemonic('H');
    menu.addActionListener((ActionEvent e) -> updateMenu());
    m_MenuBar.add(menu);

    item = new JMenuItem("Homepage", GUIHelper.getIcon("Homepage.png"));
    item.addActionListener((ActionEvent e) -> BrowserHelper.openURL("https://github.com/fracpete/jrsync"));
    menu.add(item);
    m_MenuItemHelpHomepage = item;
  }

  /**
   * Updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    // TODO
  }

  /**
   * Returns the menu bar.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    return m_MenuBar;
  }

  /**
   * Switches source and destination.
   */
  protected void switchSourceDestination() {
    File	dir;

    dir = m_ChooserSource.getCurrent();
    m_ChooserSource.setCurrent(m_ChooserDestination.getCurrent());
    m_ChooserDestination.setCurrent(dir);
  }

  /**
   * Simulates the current session.
   */
  protected void simulate() {
    // TODO
  }

  /**
   * Executes the current session.
   */
  protected void execute() {
    // TODO
  }

  /**
   * Displays the rsync command line.
   */
  protected void cmdline() {
    // TODO
  }

  /**
   * Closes the application
   */
  protected void close() {
    GUIHelper.closeParent(this);
  }

  /**
   * Adds a new session.
   */
  protected void addSession() {
    String	session;

    session = JOptionPane.showInputDialog("Please enter session name");
    if (session == null)
      return;

    Configuration.newSession(m_Sessions, session);
    m_ComboBoxSessions.setModel(new DefaultComboBoxModel<>(m_Sessions.getSections().toArray(new String[0])));
    m_ComboBoxSessions.setSelectedItem(session);
    selectSession(session);
  }

  /**
   * Deletes the current session.
   */
  protected void deleteSession() {
    if (m_ComboBoxSessions.getSelectedIndex() == -1)
      return;
    m_Sessions.clearTree("" + m_ComboBoxSessions.getSelectedItem());
    if (m_Sessions.getSections().size() == 0)
      Configuration.newSession(m_Sessions, Configuration.DEFAULT);
    m_ComboBoxSessions.setModel(new DefaultComboBoxModel<>(m_Sessions.getSections().toArray(new String[0])));
    m_ComboBoxSessions.setSelectedIndex(0);
    selectSession("" + m_ComboBoxSessions.getSelectedItem());
  }

  /**
   * Saves the sessions to disk.
   */
  protected void saveSessions() {
    Configuration.write(m_Sessions);
  }

  /**
   * Imports a session.
   */
  protected void importSession() {
    // TODO
  }

  /**
   * Exports the current session.
   */
  protected void exportSession() {
    // TODO
  }

  /**
   * Selects the active session.
   *
   * @param session	the session to select, first available (or empty) if null or empty
   */
  public void selectSession(String session) {
    if (session == null)
      session = Configuration.DEFAULT;

    if (session.isEmpty()) {
      if (m_Sessions.getSections().size() > 0)
        session = m_Sessions.getSections().iterator().next();
    }
    if (!m_Sessions.getSections().contains(session))
      Configuration.newSession(m_Sessions, session);

    m_ComboBoxSessions.setModel(new DefaultComboBoxModel<>(m_Sessions.getSections().toArray(new String[0])));
    sessionToFields(session);
  }

  /**
   * Updates the fields from the configuration.
   *
   * @param session	the session to use
   */
  protected void sessionToFields(String session) {
    Iterator<String>	keys;
    String		key;

    keys = m_Sessions.getSection(session).getKeys();
    while (keys.hasNext()) {
      key = keys.next();
      if (!m_Params.containsKey(key)) {
	System.err.println("Unknown key: " + key);
      }
      else {
        if (key.startsWith("text_")) {
          if (m_Params.get(key) instanceof DirectoryChooserPanel)
	    ((DirectoryChooserPanel) m_Params.get(key)).setCurrent(new File(m_Sessions.getString(session + "." + key)));
	  else
	    ((JTextComponent) m_Params.get(key)).setText(m_Sessions.getString(session + "." + key));
	}
        else if (key.startsWith("check_"))
	  ((JCheckBox) m_Params.get(key)).setSelected(m_Sessions.getBoolean(session + "." + key));
        else
          System.err.println("Unknown key type: " + key);
      }
    }
  }

  /**
   * Sets the value of the component in the current session.
   *
   * @param key		the key
   */
  protected void set(String key) {
    Component	comp;

    comp = m_Params.get(key);
    if (comp instanceof JCheckBox)
      set(key, ((JCheckBox) comp).isSelected());
    else if (comp instanceof DirectoryChooserPanel)
      set(key, ((DirectoryChooserPanel) comp).getCurrent().toString());
    else if (comp instanceof JTextField)
      set(key, ((JTextField) comp).getText());
  }

  /**
   * Sets the key/value in the current session.
   *
   * @param key		the key
   * @param value	the associated value
   */
  protected void set(String key, String value) {
    String	session;

    if (m_ComboBoxSessions.getSelectedIndex() == -1) {
      System.err.println("No session selected, cannot set string value");
      return;
    }
    session = "" + m_ComboBoxSessions.getSelectedItem();
    m_Sessions.setProperty(session + "." + key, value);
  }

  /**
   * Sets the key/value in the current session.
   *
   * @param key		the key
   * @param value	the associated value
   */
  protected void set(String key, boolean value) {
    String	session;

    if (m_ComboBoxSessions.getSelectedIndex() == -1) {
      System.err.println("No session selected, cannot set boolean value");
      return;
    }
    session = "" + m_ComboBoxSessions.getSelectedItem();
    m_Sessions.setProperty(session + "." + key, value);
  }

  /**
   * Displays the user interface.
   *
   * @param session	the session to select initially, can be null or empty
   */
  public static void showGUI(String session) {
    Main	main;
    BaseFrame	frame;

    main = new Main();
    main.selectSession(session);
    frame = new BaseFrame("jrsync");
    frame.setIconImage(GUIHelper.getIcon("jrsync.png").getImage());
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(main, BorderLayout.CENTER);
    frame.setJMenuBar(main.getMenuBar());
    frame.setSize(600, 400);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  /**
   * Executes the specified session.
   *
   * @param session	the session to execute
   * @param simulate	whether to simulate
   * @throws Exception	if execution fails
   */
  public static void execute(String session, boolean simulate) throws Exception {
    String		msg;
    INIConfiguration	config;
    Execute		exec;

    config = Configuration.read();
    if (config == null)
      throw new IllegalStateException("Failed to read configuration???");

    exec = new Execute();
    msg  = exec.execute(config, session, false, new ConsoleOutputListener());
    if (msg != null)
      throw new Exception(msg);
  }

  /**
   * Executes the application.
   *
   * @param args	the command-line options
   * @throws Exception	if parsing fails
   */
  public static void main(String[] args) throws Exception {
    ArgumentParser 	parser;
    Namespace 		ns;
    String		session;

    parser = ArgumentParsers.newArgumentParser(Main.class.getName());
    parser.description("Frontend for rsync command-line execution.");
    parser.addArgument("-e")
      .action(Arguments.storeTrue())
      .required(false)
      .dest("execute")
      .help("Automatically executes the specified session and exits.");
    parser.addArgument("-s")
      .action(Arguments.storeTrue())
      .required(false)
      .dest("simulate")
      .help("Whether to perform a dry-run ('simulate').");
    parser.addArgument("session")
      .nargs("?")
      .setDefault("")
      .help("The session to use.");

    ns = null;
    try {
      ns = parser.parseArgs(args);
    }
    catch (ArgumentParserException e) {
      parser.handleError(e);
    }
    catch (Throwable t) {
      throw t;
    }

    if (ns != null) {
      session = ns.getString("session");
      if (!session.isEmpty() && ns.getBoolean("execute"))
	execute(session, ns.getBoolean("simulate"));
      else
	showGUI(session);
    }
  }
}
