package Visitors;

import Structs.Graph;
import Wrappers.InheritanceNode;
import Wrappers.Method;
import org.objectweb.asm.MethodVisitor;

import java.util.Set;

public class UsedMethodVisitor extends MethodVisitor {

    private Graph<Method> callGraph;
    private Method caller;

    public UsedMethodVisitor(Graph<Method> callGraph, Method caller) {
        super(524288);
        this.callGraph = callGraph;
        this.caller = caller;
    }

    public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        if(owner.contains("/")) {
            return;
        }

        Set<Method> methods = InheritanceNode.get(owner).computeCallEdges(opcodeAndSource, name, descriptor);

        for(Method m: methods) {
            if (!callGraph.edgeExists(caller, m)) {
                callGraph.addEdge(caller, m);
                caller = m;
                m.getMethodNode().accept(this);
            }
        }
    }

}
