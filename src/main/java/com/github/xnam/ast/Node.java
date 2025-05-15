package com.github.xnam.ast;

import com.github.xnam.codegen.CodegenVisitor;

public interface Node {
    public String tokenLiteral();
    public String toString();
    public <R> R accept(CodegenVisitor<R> visitor);
}
