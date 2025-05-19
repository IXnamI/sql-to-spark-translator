package com.github.xnam;

import com.github.xnam.ast.Program;
import com.github.xnam.codegen.SparkScalaCodeGenerator;
import com.github.xnam.lexer.Lexer;
import com.github.xnam.parser.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String inputPath = null;

        for (int i = 0; i < args.length; i++) {
            if ("--input".equals(args[i]) && i + 1 < args.length) {
                inputPath = args[i + 1];
                i++;
            }
        }

        if (inputPath == null) {
            System.err.println("Usage: --input <file>");
            System.exit(1);
        }
        StringBuilder fullQuery = new StringBuilder();

        try {
            File Obj = new File(inputPath);
            Scanner Reader = new Scanner(Obj);

            while (Reader.hasNextLine()) {
                String data = Reader.nextLine();
                fullQuery.append(data).append("\n");
            }

            Reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File was not found");
            System.exit(1);
        } catch (Exception e) {
            System.out.println("An unknown error has occurred");
            e.printStackTrace();
            System.exit(1);
        }

        Lexer lexer = new Lexer(fullQuery.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkDebugStatements(parser);
        checkParserErrors(parser);

        SparkScalaCodeGenerator codeGenerator = new SparkScalaCodeGenerator();

        try {
            StringSelection stringSelection = new StringSelection(codeGenerator.generateCode(program));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            System.out.println("The result have been pasted into your clipboard");
        } catch (Exception e) {
            System.out.println("An unknown error has occurred.");
            e.printStackTrace();
        }
    }

    private static void checkParserErrors(Parser p) {
        List<String> errors = p.Errors();
        if (errors.isEmpty()) return;
        System.out.println("Parser has " + errors.size() + " errors");
        for (String err : errors) {
            System.out.println("Parser error: " + err);
        }
        System.exit(1);
    }

    private static void checkDebugStatements(Parser p) {
        List<String> debug = p.Debug();
        if (debug.isEmpty()) return;
        System.out.println("Parser has " + debug.size() + " debug statements");
        for (String d : debug) {
            System.out.println("Debug(): " + d);
        }
    }
}