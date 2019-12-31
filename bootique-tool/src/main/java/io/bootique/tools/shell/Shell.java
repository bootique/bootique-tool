/*
 *   Licensed to ObjectStyle LLC under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ObjectStyle LLC licenses
 *   this file to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package io.bootique.tools.shell;

import java.nio.file.Path;

import io.bootique.tools.shell.command.ParsedCommand;

/**
 * A simple interactive Shell interface.
 */
public interface Shell extends AutoCloseable {

    /**
     * If message is a throwable, it's printed with stack trace,
     * all other objects converted to string via {@link Object#toString()} method.
     *
     * @param message to print
     */
    void println(Object message);

    /**
     * @param prompt to show before input
     * @return input string
     */
    String readln(String prompt);

    /**
     * read and parse command from command line
     * @return parsed command
     */
    ParsedCommand readCommand();

    /**
     * @return current working directory
     */
    Path workingDir();

    /**
     * @param newPath to set as a working dir
     * @return old working dir
     */
    Path changeWorkingDir(Path newPath);
}
