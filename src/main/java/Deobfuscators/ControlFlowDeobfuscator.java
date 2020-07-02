package Deobfuscators;

import Data.Gamepack;
import Structs.Block;
import Structs.Graph;
import Wrappers.FlowAnalyzer;
import Wrappers.Method;
import com.triptheone.joda.Stopwatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControlFlowDeobfuscator implements Deobfuscator {

    /*
        Not happy with current implementation
        would rather reduce and reorder rather then expand and reorder
        TODO Reduce Blocks and reorder them
     */

    @Override
    public void execute() {
        Stopwatch s = Stopwatch.start();
        System.out.println("\nFixing Control Flow..");
        int jumps = 0;

        for(Method m: Gamepack.getInstance().getMethods()) {


            if(m.getMethodNode().instructions.size() == 0) {
                continue;
            }

            //TODO Find a way to reorder control flow without excluding methods with tryCatchBlocks
            if(m.getMethodNode().tryCatchBlocks.size() > 0) {
                continue;
            }

            FlowAnalyzer flow = new FlowAnalyzer(m);

            List<Block> blocks = flow.getBlocks();

            for(Block b: blocks) {
                if(b.hasImmediateSuccessor()) {
                    jumps++;
                    b.getInstructions().add(new JumpInsnNode(Opcodes.GOTO, b.getImmediateSuccessor().getLabelNode()));
                }
            }

            while(m.getMethodNode().instructions.size() != 0) {
                m.getMethodNode().instructions.remove(m.getMethodNode().instructions.getFirst());
            }

            for (Block b : flow.getGraph().DFS()) {
                b.getInstructions().forEach(ins -> m.getMethodNode().instructions.add(ins));
                blocks.remove(b);
            }
        }


        System.out.println("Inserted " + jumps + " Jumps");
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
