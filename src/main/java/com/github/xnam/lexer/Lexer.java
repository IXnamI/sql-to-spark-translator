package com.github.xnam.lexer;

import com.github.xnam.token.Token;
import com.github.xnam.token.TokenType;
import com.github.xnam.utils.NumberUtils;
import com.github.xnam.utils.StringUtils;

public class Lexer {
   String input;
   int position;
   int readPosition;
   Character currentChar;

   public Lexer(String input) {
    this.input = input;
    this.position = 0;
    this.readPosition = 0;
    this.currentChar = 0;
    readChar();
   }

   public void readChar() {
      if (readPosition >= input.length()) currentChar = 0;
      else currentChar = input.charAt(readPosition);
      position = readPosition;
      readPosition++;
   }

   public Token nextToken() {
      Token tok;
      skipWhiteSpaces();
      switch (currentChar) {
         case '=':
            if (peekChar() == '=') {
               tok = Token.newToken(TokenType.EQ, currentChar.toString() + peekChar());
               readChar();
               break;
            }
            tok = Token.newToken(TokenType.ASSIGN, currentChar.toString());
            break;
         case '!':
            if (peekChar() == '=') {
               tok = Token.newToken(TokenType.NOT_EQ, currentChar.toString() + peekChar());
               readChar();
               break;
            }
            tok = Token.newToken(TokenType.BANG, currentChar.toString());
            break;
         case '-':
            tok = Token.newToken(TokenType.MINUS, currentChar.toString());
            break;
         case '*':
            tok = Token.newToken(TokenType.ASTERISK, currentChar.toString());
            break;
         case '/':
            tok = Token.newToken(TokenType.SLASH, currentChar.toString());
            break;
         case '>':
            tok = Token.newToken(TokenType.GT, currentChar.toString());
            break;
         case '<':
            tok = Token.newToken(TokenType.LT, currentChar.toString());
            break;
         case ';':
            tok = Token.newToken(TokenType.SEMICOLON, currentChar.toString());
            break;
         case '(':
            tok = Token.newToken(TokenType.LPAREN, currentChar.toString());
            break;
         case ')':
            tok = Token.newToken(TokenType.RPAREN, currentChar.toString());
            break;
         case ',':
            tok = Token.newToken(TokenType.COMMA, currentChar.toString());
            break;
         case '+':
            tok = Token.newToken(TokenType.PLUS, currentChar.toString());
            break;
         case '{':
            tok = Token.newToken(TokenType.LBRACE, currentChar.toString());
            break;
         case '}':
            tok = Token.newToken(TokenType.RBRACE, currentChar.toString());
            break;
         case 0:
            tok = Token.newToken(TokenType.EOF, "");
            break;
         default:
            if (StringUtils.isLetter(currentChar)) {
               tok = readIdentifier();
               tok.setType(TokenType.searchIdent(tok.getLiteral()));
               return tok;
            } else if (NumberUtils.isDigit(currentChar)) {
               tok = readNumber();
               return tok;
            }
            tok = Token.newToken(TokenType.ILLEGAL, currentChar.toString());
            break;
      }
      readChar();
      return tok;
   }

   private Token readIdentifier() {
      StringBuilder ident = new StringBuilder();
      while (StringUtils.isLetter(currentChar)) {
         ident.append(currentChar);
         readChar();
      }
      return Token.newToken(TokenType.IDENT, ident.toString());
   }

   private Token readNumber() {
      StringBuilder number = new StringBuilder();
      while(NumberUtils.isDigit(currentChar)){
         number.append(currentChar);
         readChar();
      }
      return Token.newToken(TokenType.INT, number.toString());
   }

   private Character peekChar() {
      if (readPosition >= input.length()) return 0;
      return input.charAt(readPosition);
   }

   private void skipWhiteSpaces() {
      while(currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r'){
         readChar();
      }
   }
}
