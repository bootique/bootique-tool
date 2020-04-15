package io.bootique.tools.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JavaVersion {

    private static final String DEFAULT_VERSION = "11";

    public static String getJavaVersion() {
        String version = getJavacVersion();
        if("".equals(version)) {
            return DEFAULT_VERSION;
        }

        int javaMajorVersion = getJavaMajorVersion(version);
        return String.valueOf(javaMajorVersion);
    }

    static String getJavacVersion() {
        // output is in form "javac version"
        String version = readJavacOutput();
        if(version == null || "".equals(version) || !version.startsWith("javac")) {
            return "";
        }
        String[] versionParts = version.split(" ");
        if(versionParts.length < 2) {
            return "";
        }
        return versionParts[1];
    }

    private static String readJavacOutput() {
        String version = null;
        String input;
        try {
            Process process = Runtime.getRuntime().exec(getJavacBinary());
            try (BufferedReader bufferedReaderInput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((input = bufferedReaderInput.readLine()) != null) {
                    version = input;
                }
            }
            if(version == null || "".equals(version)) {
                try (BufferedReader bufferedReaderError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    while ((input = bufferedReaderError.readLine()) != null) {
                        version = input;
                    }
                }
            }
        } catch (IOException ignore) {
        }
        return version;
    }

    static String getJavacBinary() {
        String java_home = System.getenv("JAVA_HOME");
        if (java_home != null && !java_home.equals("")) {
            return java_home + "/bin/javac -version";
        }

        return "javac -version";
    }

    static int getJavaMajorVersion(String versionString) {
        int index;
        int prevIndex = 0;
        int version = 0;

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

        return Math.max(version, 8);
    }
}