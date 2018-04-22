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
 * Execute.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.jrsync;

import com.github.fracpete.processoutput4j.core.StreamingProcessOutputType;
import com.github.fracpete.processoutput4j.core.StreamingProcessOwner;
import org.apache.commons.configuration2.INIConfiguration;

/**
 * Executes actual rsync command.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Execute
  implements StreamingProcessOwner {

  /**
   * Interface for output listeners of the rsync command.
   */
  public interface OutputListener {

    /**
     * Gets called whenever rsync generates output.
     *
     * @param line	the generated output
     * @param stdout	true if stdout, stderr otherwise
     */
    public void outputOccurred(String line, boolean stdout);
  }

  /**
   * Simple output listener that just outputs all the output in the console
   * to stdout/stderr.
   */
  public static class ConsoleOutputListener
    implements OutputListener {

    /**
     * Gets called whenever rsync generates output.
     *
     * @param line	the generated output
     * @param stdout	true if stdout, stderr otherwise
     */
    public void outputOccurred(String line, boolean stdout) {
      if (stdout)
        System.out.println(line);
      else
        System.err.println(line);
    }
  }

  /** the underlying configuration. */
  protected INIConfiguration m_Configuration;

  /** the session to execute. */
  protected String m_Session;

  /** whether to only simulate. */
  protected boolean m_DryRun;

  /** the (optional) output listener. */
  protected OutputListener m_Listener;

  /**
   * Returns what output from the process to forward.
   *
   * @return 		the output type
   */
  @Override
  public StreamingProcessOutputType getOutputType() {
    return StreamingProcessOutputType.BOTH;
  }

  /**
   * Processes the incoming line.
   *
   * @param line	the line to process
   * @param stdout	whether stdout or stderr
   */
  @Override
  public void processOutput(String line, boolean stdout) {
    if (m_Listener != null)
      m_Listener.outputOccurred(line, stdout);
  }

  /**
   * Performs the rsync execution.
   *
   * @return		null if successful, otherwise error message
   */
  protected String doExecute() {
    // TODO
    return null;
  }

  /**
   * Executes the rsync command.
   *
   * @param config	the underlying configuration
   * @param session	the session to execute
   * @param dryRun	whether to only simulate
   * @param listener	the listener for rsync output, null to ignore
   * @return		null if successful, otherwise error message
   */
  public String execute(INIConfiguration config, String session, boolean dryRun, OutputListener listener) {
    m_Configuration = config;
    m_Session       = session;
    m_DryRun        = dryRun;
    m_Listener      = listener;
    return doExecute();
  }
}
