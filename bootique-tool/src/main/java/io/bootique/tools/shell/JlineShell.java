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

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;

import io.bootique.tools.shell.command.CommandLineParser;
import io.bootique.tools.shell.command.ParsedCommand;
import org.fusesource.jansi.Ansi;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;

public class JlineShell implements Shell {

    @Inject
    private Terminal terminal;

    @Inject
    private Provider<LineReader> lineReaderProvider;

    @Inject
    private CommandLineParser commandLineParser;

    private final String prompt;

    public JlineShell() {
        prompt = Ansi.ansi().render("@|green bq> |@").toString();
    }

    @Override
    public void println(Object message) {
        if(message instanceof String) {
            terminal.writer().println(Ansi.ansi().render((String)message));
        } else if(message instanceof Throwable) {
            printException((Throwable)message, false);
        } else {
            terminal.writer().println(message.toString());
        }
        terminal.flush();
    }

    @Override
    public String readln(String prompt) {
        prompt = "  @|green >|@ " + prompt;
        return lineReaderProvider.get().readLine(Ansi.ansi().render(prompt).toString());
    }

    private void printException(Throwable exception, boolean chained) {
        String prompt = Ansi.ansi().render("@|red   < |@").toString();
        String message = compactPackageName(exception.getClass().getName());
        if(exception.getMessage() != null) {
            message += ": " + exception.getMessage();
        }
        terminal.writer().print(prompt);
        if(chained) {
            terminal.writer().print("Caused by ");
        }
        terminal.writer().println(message);
        for(StackTraceElement element: exception.getStackTrace()) {
            String src = element.isNativeMethod()
                    ? "native"
                    : compactPackageName(element.getFileName()) + ":" + element.getLineNumber();
            String stack = prompt + "\tat "
                    + compactPackageName(element.getClassName()) + "." + element.getMethodName()
                    + "(" + src + ")";
            terminal.writer().println(stack);
        }
        if(exception.getCause() != null) {
            printException(exception.getCause(), true);
        }
    }

    static String compactPackageName(String className) {
        if(className == null) {
            return "unknown";
        }
        int length = className.length();
        int maxLength = 35;

        String[] path = className.split("\\.");
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<path.length-1; i++) {
            String next = path[i];
            if(length > maxLength) {
                sb.append(next.charAt(0));
                length -= next.length() - 1;
            } else {
                sb.append(next);
            }
            sb.append('.');
        }
        sb.append(path[path.length - 1]);
        return sb.toString();
    }

    @Override
    public ParsedCommand readCommand() {
        LineReader reader = lineReaderProvider.get();
        try {
            String line = reader.readLine(prompt);
            if(line == null) {
                return null;
            }
            return commandLineParser.parse(line);
        } catch (UserInterruptException | EndOfFileException ignored) {
            return null;
        }
    }

    @Override
    public void shutdown() {
        try {
            terminal.close();
        } catch (IOException ignored) {
        }
    }
}
