package Wrappers;

import Structs.Block;
import Structs.Graph;
import Wrappers.RSMethod;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.util.*;


public class FlowAnalyzer extends Analyzer<BasicValue> {

    private Graph<Block> graph;
    private List<Block> blocks;
    private List<Block> reducedBlocks;
    private RSMethod method;

    public FlowAnalyzer(RSMethod method) {
        super(new BasicInterpreter());
        this.method = method;
    }

    private void initializeBlocks() {
        blocks = new ArrayList<>();

        InsnList insnList = method.getMethodNode().instructions;
        int blockIndex = 0;

        for(int i = 0; i < insnList.size(); i++) {
            if(insnList.get(i) instanceof LineNumberNode) {
                insnList.remove(insnList.get(i));
            }
        }

        if(insnList.size() > 0) {
            blocks.add(new Block());
        }

        for (int i = 0; i < insnList.size(); i++) {
            AbstractInsnNode insn = insnList.get(i);
            blocks.get(blockIndex).AddInstruction(insn, i);
            if(insn.getNext() == null) {
                break;
            }
            if(insn instanceof JumpInsnNode ||
                    insn instanceof LookupSwitchInsnNode ||
                    insn instanceof TableSwitchInsnNode) {
                blockIndex++;
                blocks.add(new Block());
            }
        }
    }

    @Override
    protected void newControlFlowEdge(int insnIndex, int successorIndex) {
        graph.addEdge(getBlockByIndex(insnIndex), getBlockByIndex(successorIndex));
    }

    @Override
    protected boolean newControlFlowExceptionEdge(int insnIndex, int successorIndex) {
        graph.addEdge(getBlockByIndex(insnIndex), getBlockByIndex(successorIndex));
        return true;
    }

    private void reduceBlocks() {
        reducedBlocks = new ArrayList<>();
        List<Block> visitedBlocks = new ArrayList<>();

        for(Block b: graph.DFS()) {
            if(visitedBlocks.contains(b) || b.getMergableBlocks(graph).isEmpty()) {
                continue;
            }

            List<Block> mergableBlocks = b.getMergableBlocks(graph);
            visitedBlocks.addAll(mergableBlocks);
            reducedBlocks.add(b.merge(mergableBlocks));
        }
        reducedBlocks.sort(Comparator.comparing(Block::getFirstIndex));
    }


    public Graph<Block> createFlowGraph() {
        if(graph == null) {
            initializeBlocks();
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

    public List<Block> getBlocks(boolean reduce) {
        if(graph == null) {
            createFlowGraph();
        }

        if(reduce) {
            if(reducedBlocks == null) {
                reduceBlocks();
            }
            return reducedBlocks;
        }

        return blocks;
    }


}
