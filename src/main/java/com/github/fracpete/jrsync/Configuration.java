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
 * Configuration.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.jrsync;

import nz.ac.waikato.cms.core.FileUtils;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.DefaultExpressionEngineSymbols;
import org.apache.commons.configuration2.tree.NodeNameMatchers;
import org.apache.commons.lang.SystemUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Manages the configurations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Configuration {

  /**
   * Returns the configuration directory to use.
   *
   * @return the directory
   */
  public static String configurationDirectory() {
    String result;

    if (SystemUtils.IS_OS_WINDOWS)
      result = System.getProperty("user.home") + File.separator + "jrsyncfiles";
    else
      result = System.getProperty("user.home") + File.separator + ".local" + File.separator + "jrsync";

    return result;
  }

  /**
   * Returns the default configuration file.
   *
   * @return the file name
   */
  public static String configurationFile() {
    return configurationDirectory() + File.separator + "jrsync.ini";
  }

  /**
   * Reads the configuration.
   *
   * @return		null if failed to read, empty if not present (yet)
   */
  public static INIConfiguration read() {
    DefaultExpressionEngine 				engine;
    Parameters 						params;
    FileBasedConfigurationBuilder<INIConfiguration> 	builder;
    File						file;

    file = new File(configurationFile());
    if (!file.exists())
      return new INIConfiguration();

    engine = new DefaultExpressionEngine(
      DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS,
      NodeNameMatchers.EQUALS_IGNORE_CASE);

    params = new Parameters();
    builder =
      new FileBasedConfigurationBuilder<>(INIConfiguration.class)
	.configure(params.hierarchical()
	  .setFileName(configurationFile())
	  .setExpressionEngine(engine));
    try {
      return builder.getConfiguration();
    }
    catch (Exception e) {
      System.err.println("Failed to parse: " + configurationFile());
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Writes the configuration to disk.
   *
   * @param config	the configuration to write
   * @return		null if successfully written, otherwise error message
   */
  public static String write(INIConfiguration config) {
    FileWriter		fwriter;
    BufferedWriter	bwriter;
    File		file;
    File		dir;

    fwriter = null;
    bwriter = null;
    file    = new File(configurationFile());
    dir     = file.getParentFile();
    if (!dir.exists()) {
      if (!dir.mkdirs())
        return "Failed to create directory for configuration file: " + dir;
    }

    try {
      fwriter = new FileWriter(file);
      bwriter = new BufferedWriter(fwriter);
      config.write(bwriter);
      return null;
    }
    catch (Exception e) {
      System.err.println("Failed to write configuration to: " + file);
      e.printStackTrace();
      return "Failed to write configuration to: " + file + "\n" + e;
    }
    finally {
      FileUtils.closeQuietly(bwriter);
      FileUtils.closeQuietly(fwriter);
    }
  }

  /**
   * Generates a new section with default values.
   *
   * @param config	the configuration to update
   * @param session  	the session to initialize
   */
  public static void newSession(INIConfiguration config, String session) {
    config.setProperty(session + ".text_source", "");
    config.setProperty(session + ".text_dest", "");
    config.setProperty(session + ".text_notes", "");
    config.setProperty(session + ".text_com_before", "");
    config.setProperty(session + ".text_com_after", "");
    config.setProperty(session + ".text_addit", "");
    config.setProperty(session + ".check_time", "false");
    config.setProperty(session + ".check_perm", "false");
    config.setProperty(session + ".check_owner", "false");
    config.setProperty(session + ".check_group", "false");
    config.setProperty(session + ".check_onefs", "false");
    config.setProperty(session + ".check_verbose", "false");
    config.setProperty(session + ".check_progr", "false");
    config.setProperty(session + ".check_delete", "false");
    config.setProperty(session + ".check_exist", "false");
    config.setProperty(session + ".check_size", "false");
    config.setProperty(session + ".check_skipnew", "false");
    config.setProperty(session + ".check_windows", "false");
    config.setProperty(session + ".check_sum", "false");
    config.setProperty(session + ".check_symlink", "false");
    config.setProperty(session + ".check_hardlink", "false");
    config.setProperty(session + ".check_dev", "false");
    config.setProperty(session + ".check_update", "false");
    config.setProperty(session + ".check_keepart", "false");
    config.setProperty(session + ".check_mapuser", "false");
    config.setProperty(session + ".check_compr", "false");
    config.setProperty(session + ".check_backup", "false");
    config.setProperty(session + ".check_itemized", "false");
    config.setProperty(session + ".check_norecur", "false");
    config.setProperty(session + ".check_protectargs", "false");
    config.setProperty(session + ".check_com_before", "false");
    config.setProperty(session + ".check_com_halt", "false");
    config.setProperty(session + ".check_com_after", "false");
    config.setProperty(session + ".check_com_onerror", "false");
    config.setProperty(session + ".check_browse_files", "false");
  }
}