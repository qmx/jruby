package org.jruby.compiler.ir.operands;

// Records the nil object

import org.jruby.interpreter.InterpreterContext;

public class Nil extends Constant {
    public static final Nil NIL = new Nil();

    private Nil() { }

    @Override
    public String toString() { 
        return "nil";
    }

    @Override
    public Operand fetchCompileTimeArrayElement(int argIndex, boolean getSubArray) { 
        return Nil.NIL;
    }

    @Override
    public Object retrieve(InterpreterContext interp) {
        return interp.getRuntime().getNil();
    }
}
