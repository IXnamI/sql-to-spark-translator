package com.github.xnam.repl;

import com.github.xnam.ast.Program;
import com.github.xnam.evaluator.Evaluator;
import com.github.xnam.lexer.Lexer;
import com.github.xnam.object.Environment;
import com.github.xnam.object.Object;
import com.github.xnam.parser.Parser;
import com.github.xnam.token.Token;
import com.github.xnam.token.TokenType;

import java.util.Scanner;
import java.util.List;

public class Repl {

    private static final String prompt = ">> ";

    public static void start() {
        Scanner userIn = new Scanner(System.in);
        Environment env = new Environment();
        while (true) {
            System.out.print(prompt);
            String input = userIn.nextLine();
            if (input.equals("quit")) break;
            Lexer lexer = new Lexer(input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            if (!parser.Errors().isEmpty()) {
                printParserErrors(parser.Errors());
                continue;
            }
            Object evaluated = Evaluator.eval(program, env);
            if (evaluated != null) {
                System.out.println(evaluated.inspect() + "\n");
            }
        }
    }

    private static void printParserErrors(List<String> errors) {
        System.out.println("Parser errors: ");
        for (String err : errors) {
            System.out.println("\t" + err + "\n");
        }
    }
}
