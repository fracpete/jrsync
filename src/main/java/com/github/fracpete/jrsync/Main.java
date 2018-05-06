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
import nz.ac.waikato.cms.gui.core.GUIHelper;
import org.apache.commons.configuration2.INIConfiguration;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

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

  /** the Sessions -> Import menu item. */
  protected JMenuItem m_MenuItemSessionsImport;

  /** the Sessions -> Export menu item. */
  protected JMenuItem m_MenuItemSessionsExport;

  /** the Help -> Homepage. */
  protected JMenuItem m_MenuItemHelpHomepage;

  /** the underlying sessions. */
  protected INIConfiguration m_Sessions;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Sessions = Configuration.read();
    if (m_Sessions == null)
      m_Sessions = new INIConfiguration();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());
    // TODO
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    initMenuBar();
    selectSession("");
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
    item.addActionListener((ActionEvent e) -> browse(true));
    menu.add(item);
    m_MenuItemFileBrowseSource = item;

    item = new JMenuItem("Browse destination", GUIHelper.getIcon("Empty.png"));
    item.addActionListener((ActionEvent e) -> browse(false));
    menu.add(item);
    m_MenuItemFileBrowseSource = item;

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
   * Lets the user select the source/destination directory.
   *
   * @param source	whether to select source or destination
   */
  protected void browse(boolean source) {
    // TODO
  }

  /**
   * Switches source and destination.
   */
  protected void switchSourceDestination() {
    // TODO
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
    // TODO
  }

  /**
   * Deletes the current session.
   */
  protected void deleteSession() {
    // TODO
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
      session = "";

    if (session.isEmpty()) {
      if (m_Sessions.getSections().size() > 0)
        session = m_Sessions.getSections().iterator().next();
    }

    // TODO
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
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(main, BorderLayout.CENTER);
    frame.setJMenuBar(main.getMenuBar());
    frame.setSize(600, 600);
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
