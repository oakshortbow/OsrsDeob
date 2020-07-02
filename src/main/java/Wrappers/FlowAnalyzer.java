package Wrappers;

import Structs.Block;
import Structs.Graph;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.util.*;


public class FlowAnalyzer extends Analyzer<BasicValue> {

    private Graph<Block> graph;
    private List<Block> blocks;
    private Method method;

    public FlowAnalyzer(Method method) {
        super(new BasicInterpreter());
        this.method = method;
    }

    @Override
    protected void init(String owner, MethodNode method) {
        blocks = new ArrayList<>();

        InsnList insnList = method.instructions;

        int blockIndex = 0;

        if(insnList.size() > 0) {
            blocks.add(new Block());
        }

        for (int i = 0; i < insnList.size(); i++) {
            AbstractInsnNode insn = insnList.get(i);
            blocks.get(blockIndex).AddInstruction(insn, i);
            if(insn.getNext() == null) {
                break;
            }
            else if(insn.getNext() instanceof LabelNode) {
                blockIndex++;
                blocks.add(new Block());
            }
        }

        for(Block b: blocks) {

        }
    }

    @Override
    protected void newControlFlowEdge(int insnIndex, int successorIndex) {
        Block firstBlock = getBlockByIndex(insnIndex);
        Block secondBlock = getBlockByIndex(successorIndex);

        assert firstBlock != null && secondBlock != null;

        graph.addEdge(firstBlock, secondBlock);

        if(firstBlock != secondBlock && insnIndex + 1 == successorIndex) {
            firstBlock.setImmediateSuccessor(secondBlock);
        }
    }

    @Override
    protected boolean newControlFlowExceptionEdge(int insnIndex, TryCatchBlockNode tryCatchBlock) {
        Block firstBlock = getBlockByIndex(insnIndex);
        Block secondBlock = getBlockByLabelNode(tryCatchBlock.handler);

        assert firstBlock != null && secondBlock != null;

        graph.addEdge(firstBlock, secondBlock);
        return true;
    }


    public Graph<Block> getGraph() {
        if(graph == null) {
            graph = new Graph<>();
            try {
                analyze(method.getClassNode().name, method.getMethodNode());
            } catch (AnalyzerException ae) {
                ae.printStackTrace();
            }
        }
        return graph;
    }

    private Block getBlockByIndex(int index) {
        for(Block b: blocks) {
            if(b.hasIndex(index)) {
                return b;
            }
        }
        return null;
    }

    public Block getBlockByLabelNode(LabelNode node) {
        for(Block b: blocks) {
            if(b.getLabelNode().equals(node)) {
                return b;
            }
        }
        return null;
    }

    public List<Block> getBlocks() {
        if (graph == null) {
            getGraph();
        }
        return blocks;
    }

}
