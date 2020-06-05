package Deobfuscators;

import Data.Gamepack;
import Structs.Block;
import Structs.Graph;
import Wrappers.RSMethod;
import com.triptheone.joda.Stopwatch;
import org.objectweb.asm.tree.InsnList;

public class ControlFlowDeobfuscator implements Deobfuscator {

    @Override
    public void execute() {
        Stopwatch s = Stopwatch.start();
        System.out.println("\nFixing Control Flow..");


        for(RSMethod m: Gamepack.getInstance().getAllMethods()) {
            if(m.getMethodNode().instructions.size() == 0 || m.getReducedBlocks().size() == m.getBlocks().size() || !m.getClassNode().name.equals("client")) {
                continue;
            }

            System.out.println(m);

            InsnList newInsn = new InsnList();
            m.getReducedBlocks().forEach(block -> block.getInstructions().forEach(newInsn::add));
            m.getMethodNode().instructions = newInsn;
        }

        System.out.println("Control Flow done in " + s.getElapsedTime().getMillis()/1000.0F + " Seconds");
    }


    public void printGraph(Graph<Block> graph) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (Block v : graph.getMap().keySet()) {
            i = 0;
            builder.append("\"");
            builder.append(v.toString());
            builder.append("\"");
            if(graph.getMap().get(v).size() > 0) {
                builder.append( " -> ");
            }

            for (Block w : graph.getMap().get(v)) {
                i++;
                builder.append("\"");
                builder.append(w.toString());
                builder.append("\"");
                if(graph.getMap().get(v).size() != i) {
                    builder.append(", ");
                }
            }
            builder.append(";");
            builder.replace(builder.toString().length() - 2, builder.toString().length() - 2, "");
            builder.append("\n");
        }

        System.out.println(builder.toString());
    }

}
