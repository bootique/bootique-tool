package io.bootique.tools.shell;

public class Formatter {

    private static final int LEFT_COLUMN_WIDTH = 16;

    public static String alignByColumns(String string) {
        int padding = LEFT_COLUMN_WIDTH - string.length();
        if(padding <= 0) {
            padding = 1;
        }
        for(int i=0; i<padding; i++) {
            string += ' ';
        }
        return string;
    }

}
