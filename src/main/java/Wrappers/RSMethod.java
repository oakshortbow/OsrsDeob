package Wrappers;
import Structs.Block;
import Structs.Graph;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.Objects;

public class RSMethod {

    private ClassNode classNode;
    private MethodNode methodNode;
    private FlowAnalyzer flowAnalyzer;

    public RSMethod(ClassNode classNode, MethodNode methodNode) {
        this.classNode = classNode;
        this.methodNode = methodNode;
        flowAnalyzer = new FlowAnalyzer(this);
    }

    public ClassNode getClassNode() {
        return classNode;
    }


    public MethodNode getMethodNode() {
        return methodNode;
    }

    public Graph<Block> generateCfg() {
        return flowAnalyzer.createFlowGraph();
    }

    public List<Block> getBlocks() {
        return flowAnalyzer.getBlocks(false);
    }

    public List<Block> getReducedBlocks() {
        return flowAnalyzer.getBlocks(true);
    }

    @Override
    public boolean equals(Object other) {
        RSMethod m;
        if (other instanceof RSMethod) {
            m = (RSMethod) other;
        } else {
            return false;
        }
        return this.getMethodNode().equals(m.getMethodNode()) && this.getClassNode().equals(m.getClassNode());
    }

    @Override
    public String toString() {
        return getClassNode().name + "." + getMethodNode().name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(classNode, methodNode);
    }
}

