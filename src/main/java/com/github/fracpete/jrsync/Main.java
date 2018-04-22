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
import org.apache.commons.configuration2.INIConfiguration;

/**
 * Starts up the user interface, if no parameters given.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Main {

  /**
   * Displays the user interface.
   *
   * @param session	the session to select initially, can be null or empty
   */
  public static void showGUI(String session) {
    // TODO
  }

  /**
   * Executes the specified session.
   *
   * @param session	the session to execute
   * @throws Exception	if execution fails
   */
  public static void execute(String session) throws Exception {
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
	execute(session);
      else
	showGUI(session);
    }
  }
}
