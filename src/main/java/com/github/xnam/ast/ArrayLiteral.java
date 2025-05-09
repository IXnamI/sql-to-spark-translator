package com.github.xnam.ast;

import com.github.xnam.token.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArrayLiteral implements Expression {
    Token token;
    List<Expression> elements;

    public ArrayLiteral(Token token) {
        this.token = token;
        this.elements = new ArrayList<>();
    }

    public void expressionNode() {}
    public String tokenLiteral() { return token.getLiteral(); }
    public String toString() {
        StringBuilder output = new StringBuilder();
        ArrayList<String> stringElems = new ArrayList<>();
        for (Expression elem : elements) {
            stringElems.add(elem.toString());
        }
        output.append("[");
        output.append(String.join(", ", stringElems));
        output.append("]");
        return output.toString();
    }
}
