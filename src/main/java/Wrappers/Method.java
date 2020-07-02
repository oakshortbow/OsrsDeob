package Wrappers;
import Structs.Block;
import Structs.Graph;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.Frame;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Method {

    private ClassNode classNode;
    private MethodNode methodNode;

    public Method(ClassNode classNode, MethodNode methodNode) {
        this.classNode = classNode;
        this.methodNode = methodNode;
    }

    public ClassNode getClassNode() {
        return classNode;
    }


    public MethodNode getMethodNode() {
        return methodNode;
    }

    @Override
    public boolean equals(Object other) {
        Method m;
        if (other instanceof Method) {
            m = (Method) other;
        } else {
            return false;
        }
        return this.getMethodNode().equals(m.getMethodNode()) && this.getClassNode().equals(m.getClassNode());
    }

    @Override
    public String toString() {
        return getClassNode().name + "." + getMethodNode().name + ":" + getMethodNode().desc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(classNode, methodNode);
    }
}

