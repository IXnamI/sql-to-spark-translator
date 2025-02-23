package com.github.xnam.repl;

import com.github.xnam.lexer.Lexer;
import com.github.xnam.token.Token;
import com.github.xnam.token.TokenType;

import java.util.Scanner;

public class Repl {

    private static final String prompt = ">> ";

    public static void start() {
        Scanner userIn = new Scanner(System.in);
        while (true) {
            System.out.print(prompt);
            String input = userIn.nextLine();
            if (input.equals("quit")) break;
            Lexer lexer = new Lexer(input);
            for (Token tok = lexer.nextToken(); !tok.getType().equals(TokenType.EOF); tok = lexer.nextToken()) {
                System.out.println(tok);
            }
        }
    }
}
