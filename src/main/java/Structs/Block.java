package Structs;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;

import java.util.*;

public class Block {

    private List<AbstractInsnNode> instructions = new ArrayList<>();
    private int firstIndex = 0;
    private int lastIndex = 0;
    private Block successor;



    public Block() {}

    public Block(int firstIndex, int lastIndex, List<AbstractInsnNode> instructions) {
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
        this.instructions = instructions;
    }

    public void AddInstruction(AbstractInsnNode node, int index) {
        if(instructions.isEmpty()) {
            firstIndex = index;
        }
        lastIndex = index;
        instructions.add(node);
    }


    public boolean hasIndex(int index) {
        return index >= firstIndex && index <= lastIndex;
    }


    public List<Block> getMergableBlocks(Graph<Block> cfGraph) {
        List<Block> blocks = new ArrayList<>();
        Block currentBlock = this;
        while(cfGraph.getEdgesForNodeCount(currentBlock) == 1 && cfGraph.isEdgeCount(cfGraph.getEdges(currentBlock).get(0)) == 1) {
            currentBlock = cfGraph.getEdges(currentBlock).get(0);
            blocks.add(currentBlock);
        }
        return blocks;
    }

    public List<AbstractInsnNode> getInstructions() {
        return instructions;
    }

    @Override
    public boolean equals(Object other) {
        Block o;
        if(other instanceof Block) {
            o = (Block) other;
        }
        else {
            return false;
        }
        return this.firstIndex == o.firstIndex && this.lastIndex == o.lastIndex;
    }

    public boolean hasImmediateSuccessor() {
        return successor != null;
    }

    public void setImmediateSuccessor(Block b) {
        successor = b;
    }

    public Block getImmediateSuccessor() {
        return successor;
    }

    public LabelNode getLabelNode() {
        return (LabelNode)instructions.get(0);
    }

    @Override
    public String toString() {
        return firstIndex + "-" + lastIndex;
    }


}
