package Structs;

import org.objectweb.asm.tree.*;

import java.util.*;

public class Block {

    private List<AbstractInsnNode> instructions = new ArrayList<>();
    private int firstIndex = 0;
    private int lastIndex = 0;



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

    public Block merge(List<Block> blocks) {

        List<AbstractInsnNode> instructions = new ArrayList<>(this.instructions);
        blocks.forEach(block -> instructions.addAll(block.instructions));
        //Removing ASM Constructs for all except first block
        instructions.removeIf(ins -> ins.getOpcode() == -1);

        AbstractInsnNode lastIns = instructions.get(instructions.size() -1);
        //Stripping all jump instructions from the new block except for the last instruction
        instructions.removeIf(ins -> !ins.equals(lastIns) && (ins instanceof JumpInsnNode || ins instanceof LookupSwitchInsnNode || ins instanceof TableSwitchInsnNode));

        if(lastIns instanceof JumpInsnNode) {
            JumpInsnNode ins = (JumpInsnNode) lastIns;
            //Set New Jump Target
        }
        else if(lastIns instanceof LookupSwitchInsnNode) {
            LookupSwitchInsnNode ins = (LookupSwitchInsnNode) lastIns;
            //Set New Jump Target
        }
        else if (lastIns instanceof TableSwitchInsnNode) {
            TableSwitchInsnNode ins = (TableSwitchInsnNode) lastIns;
            //Set TableSwitchInsNode Jump Targets
        }

        //These Aren't accurate index's after the blocks get merged, more so the index's of the parent block all blocks were merged into
        return new Block(firstIndex, lastIndex, instructions);
    }

    public List<Block> getMergableBlocks(Graph<Block> cfGraph) {
        List<Block> blocks = new ArrayList<>();

        if(cfGraph.getEdgesForNodeCount(this) != 1 || cfGraph.isEdgeCount(cfGraph.getEdges(this).get(0)) > 1) {
            return blocks;
        }

        Block currentBlock = cfGraph.getEdges(this).get(0);
        blocks.add(currentBlock);

        while(cfGraph.getEdgesForNodeCount(currentBlock) == 1 && cfGraph.isEdgeCount(cfGraph.getEdges(currentBlock).get(0)) == 1) {
            currentBlock = cfGraph.getEdges(currentBlock).get(0);
            blocks.add(currentBlock);
        }

        return blocks;
    }

    public List<AbstractInsnNode> getInstructions() {
        return instructions;
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public int getLastIndex() {
        return lastIndex;
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

    @Override
    public String toString() {
        return firstIndex + "-" + lastIndex;
    }


}
