package com.example.project;

/**
 * Utility methods for extra functions
 * @author Andy
 */
public class Util {
    /**
     * Class should not be initialized
     */
    private Util() {}

    /**
     * Format a specific portion of text using ANSI
     * @param str The string to format
     * @param colorCodes The codes to format with, splitting with ";" (ex: "31;1" for bold and red)
     * @return The printable formatted string
     */
    public static String ansiText(String str, String colorCodes) {
        // Format: "\033[{code}m"
        // "\033[0m" to reset to initial color and format
        return "\033[" + colorCodes + "m" + str + "\033[0m";
    }
}
