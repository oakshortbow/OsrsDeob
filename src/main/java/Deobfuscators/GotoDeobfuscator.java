package Deobfuscators;

import Data.Gamepack;
import Wrappers.Method;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

public class GotoDeobfuscator implements Deobfuscator {

    @Override
    public void execute() {
        System.out.println("\nRemoving Redundant Jump Instructions..");

        int insRemoved = 0;
        for (Method m: Gamepack.getInstance().getMethods()) {
            AbstractInsnNode[] instructions = m.getMethodNode().instructions.toArray();
            for(AbstractInsnNode instruction: instructions) {
                if (instruction.getOpcode() == Opcodes.GOTO) {
                    JumpInsnNode jumpIns = (JumpInsnNode)instruction;
                    if (instruction.getNext() != null && instruction.getNext().equals(jumpIns.label)) {
                        m.getMethodNode().instructions.remove(instruction);
                        insRemoved++;
                    }
                }
            }
        }

        System.out.println("Removed " + insRemoved + " Redundant Jump Instructions");

    }
}
