package Deobfuscators;

import Data.Gamepack;
import Wrappers.Method;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

public class DeadCodeDeobfuscator implements Deobfuscator {

    @Override
    public void execute() {
        System.out.println("\nRemoving Dead Instructions..");
        int insRemoved = 0;
        for(Method m: Gamepack.getInstance().getMethods()) {
            try {
                Frame<BasicValue>[] frames = new Analyzer<>(new BasicInterpreter()).analyze(m.getClassNode().name, m.getMethodNode());
                AbstractInsnNode[] ins = m.getMethodNode().instructions.toArray();
                for (int i = 0; i < frames.length; i++) {
                    if (frames[i] == null) {
                        m.getMethodNode().instructions.remove(ins[i]);
                        insRemoved++;
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("Removed " + insRemoved + " Dead Instructions");
    }
}
