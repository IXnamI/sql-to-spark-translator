package com.github.xnam.codegen;

public class SparkCodeFormatter {

    private static final String[] CHAIN_METHODS = {
            ".filter(", ".join(", ".groupBy(", ".agg(", ".select(", ".orderBy(", ".limit(", ".withColumn("
    };

    public String format(String rawCode) {
        String lineBroken = breakBeforeMethods(rawCode);

        String[] lines = lineBroken.split("\n");
        StringBuilder formatted = new StringBuilder();
        int indentLevel = 0;

        for (String line : lines) {
            String trimmed = line.trim();

            if (trimmed.startsWith("val ")) {
                formatted.append(trimmed).append("\n");
                indentLevel = 1;
            } else if (!trimmed.isEmpty()) {
                formatted.append(indent(indentLevel)).append(trimmed).append("\n");
            }
        }

        return formatted.toString().trim();
    }

    private String breakBeforeMethods(String code) {
        String result = code;
        for (String method : CHAIN_METHODS) {
            result = result.replace(method, "\n" + method);
        }
        return result;
    }

    private String indent(int level) {
        return repeatString(" ", level);
    }

    private String repeatString(String initialString, int times) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < times; i++) output.append(initialString);
        return output.toString();
    }
}
