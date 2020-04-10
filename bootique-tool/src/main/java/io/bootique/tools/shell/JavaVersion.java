package io.bootique.tools.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JavaVersion {

    public static String getJavaVersion() {

        String version;
        String versionError = null;
        String versionInput = null;
        String stringInput, stringError, command;

        String java_home = System.getenv("JAVA_HOME");

        try {
            if (java_home != null && !java_home.equals("")) {
                command = java_home + "/bin/javac -version";
            } else {
                command = "javac -version";
            }

            Process process = Runtime.getRuntime().exec(command);

            try (BufferedReader bufferedReaderInput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((stringInput = bufferedReaderInput.readLine()) != null) {
                    versionInput = stringInput;
                }
            }

            try (BufferedReader bufferedReaderError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                while ((stringError = bufferedReaderError.readLine()) != null) {
                    versionError = stringError;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (versionInput != null && !versionInput.equals("")) {
            version = versionInput;
        } else if (versionError != null && !versionError.equals("") && versionError.startsWith("javac")) {
            version = versionError;
        } else {
            version = "11";
        }

        String[] strings = version.split(" ");

        version = getJavaMajorVersion(strings[1]);

        return version;
    }

    private static String getJavaMajorVersion(String versionString) {
        int index = 0, prevIndex = 0, version = 0;
        if((index = versionString.indexOf("-")) >= 0) {
            versionString = versionString.substring(0, index);
        }
        if((index = versionString.indexOf("+")) >= 0) {
            versionString = versionString.substring(0, index);
        }

        while(version < 2) {
            index = versionString.indexOf(".", prevIndex);
            if(index == -1) {
                index = versionString.length();
            }
            version = Integer.parseInt(versionString.substring(prevIndex, index));
            prevIndex = index + 1;
        }

        if (version < 8) {
            version = 8;
        }
        return String.valueOf(version);
    }
}