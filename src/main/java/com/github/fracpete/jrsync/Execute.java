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
import com.github.fracpete.processoutput4j.output.StreamingProcessOutput;
import com.github.fracpete.rsync4j.RSync;
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
   * Configures RSync.
   *
   * @return		the configured RSync instance
   */
  protected RSync configureRsync() throws Exception {
    RSync result;

    result = new RSync();
    if (!m_Configuration.getString(m_Session + ".text_addit").isEmpty())
      result.setOptions(m_Configuration.getString(m_Session + ".text_addit").split(" "));  // TODO does not adhere to quotes!
    result.source(m_Configuration.getString(m_Session + ".text_source"));
    result.destination(m_Configuration.getString(m_Session + ".text_dest"));
    result.times(m_Configuration.getBoolean(m_Session + ".check_time"));
    result.perms(m_Configuration.getBoolean(m_Session + ".check_perm"));
    result.owner(m_Configuration.getBoolean(m_Session + ".check_owner"));
    result.group(m_Configuration.getBoolean(m_Session + ".check_group"));
    result.perms(m_Configuration.getBoolean(m_Session + ".check_perm"));
    result.oneFileSystem(m_Configuration.getBoolean(m_Session + ".check_onefs"));
    result.verbose(m_Configuration.getBoolean(m_Session + ".check_verbose"));
    result.progress(m_Configuration.getBoolean(m_Session + ".check_progr"));
    result.delete(m_Configuration.getBoolean(m_Session + ".check_delete"));
    result.ignoreExisting(m_Configuration.getBoolean(m_Session + ".check_exist"));
    result.sizeOnly(m_Configuration.getBoolean(m_Session + ".check_size"));
    result.update(m_Configuration.getBoolean(m_Session + ".check_skipnew"));
    if (m_Configuration.getBoolean(m_Session + ".check_windows"))
      result.modifyWindow(1);
    result.checksum(m_Configuration.getBoolean(m_Session + ".check_sum"));
    result.links(m_Configuration.getBoolean(m_Session + ".check_symlink"));
    result.hardLinks(m_Configuration.getBoolean(m_Session + ".check_hardlink"));
    result.devices(m_Configuration.getBoolean(m_Session + ".check_dev"));
    result.hardLinks(m_Configuration.getBoolean(m_Session + ".check_hardlink"));
    result.existing(m_Configuration.getBoolean(m_Session + ".check_update"));
    result.partial(m_Configuration.getBoolean(m_Session + ".check_keepart"));
    result.numericIds(m_Configuration.getBoolean(m_Session + ".check_mapuser"));
    result.compress(m_Configuration.getBoolean(m_Session + ".check_compr"));
    result.backup(m_Configuration.getBoolean(m_Session + ".check_backup"));
    result.itemizeChanges(m_Configuration.getBoolean(m_Session + ".check_itemized"));
    result.dirs(!m_Configuration.getBoolean(m_Session + ".check_norecur"));
    result.protectArgs(m_Configuration.getBoolean(m_Session + ".check_protectargs"));
    result.super_(m_Configuration.getBoolean(m_Session + ".check_superuser"));

    // ignored:
    // check_browse_files=false

    if (m_DryRun)
      result.dryRun(true);

    return result;
  }

  /**
   * Executes the specified command.
   *
   * @param cmd		the command to execute
   * @param halt	true if to trigger halt (will suppress error message if 'false')
   * @return		null if successful, otherwise error message
   */
  protected String executeCommand(String cmd, boolean halt) {
    String			result;
    ProcessBuilder 		builder;
    StreamingProcessOutput 	output;

    result  = null;
    builder = new ProcessBuilder();
    builder.command(cmd);
    output = new StreamingProcessOutput(this);
    try {
      output.monitor(builder);
    }
    catch (Exception e) {
      if (halt)
        result = "Failed to execute: " + cmd + "\n" + e;
    }

    return result;
  }

  /**
   * Performs the rsync execution.
   *
   * @return		null if successful, otherwise error message
   */
  protected String doExecute() {
    String			result;
    RSync			rsync;
    StreamingProcessOutput	output;

    result = null;

    // pre-execute command
    if (m_Configuration.getBoolean(m_Session + ".check_com_before"))
      result = executeCommand(m_Configuration.getString(m_Session + ".text_com_before"), m_Configuration.getBoolean(m_Session + ".check_com_halt"));

    if (result == null) {
      try {
	rsync  = configureRsync();
	output = new StreamingProcessOutput(this);
	output.monitor(rsync.builder());
      }
      catch (Exception e) {
	System.err.println("Failed to execute rsync!");
	e.printStackTrace();
	result = "Failed to execute rsync: " + e;
      }
    }

    // post-execute command
    if (m_Configuration.getBoolean(m_Session + ".check_com_after")) {
      if ((result == null) || m_Configuration.getBoolean(m_Session + ".check_com_onerror"))
	result = executeCommand(m_Configuration.getString(m_Session + ".text_com_after"), false);
    }

    return result;
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
