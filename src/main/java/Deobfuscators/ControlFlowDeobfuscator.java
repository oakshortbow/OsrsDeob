package Deobfuscators;

import Data.Gamepack;
import Structs.Block;
import Structs.Graph;
import Wrappers.FlowAnalyzer;
import Wrappers.Method;
import com.triptheone.joda.Stopwatch;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.List;

public class ControlFlowDeobfuscator implements Deobfuscator {

    @Override
    public void execute() {
        Stopwatch s = Stopwatch.start();
        System.out.println("\nFixing Control Flow..");


        for(Method m: Gamepack.getInstance().getAllMethods()) {
            if(m.getMethodNode().instructions.size() == 0) {
                continue;
            }
            FlowAnalyzer flow = new FlowAnalyzer(m);
            Graph<Block> graph = flow.createFlowGraph();
            if(flow.getBlocks(true).size() != 1 || (flow.getBlocks(true).size() == flow.getBlocks(false).size())) {
                continue;
            }
            System.out.println(m);
            printGraph(graph);
            for(Block b: flow.getBlocks(true)) {
                System.out.println(b);
                for(AbstractInsnNode ins: b.getInstructions()) {
                    System.out.println(ins);
                }
            }

            while(m.getMethodNode().instructions.size() != 0) {
                m.getMethodNode().instructions.remove(m.getMethodNode().instructions.getFirst());
            }

            m.getMethodNode().instructions = flow.getReducedInstructions();

            System.out.println("NEW INSTRUCTIONS");

            AbstractInsnNode currentIns = m.getMethodNode().instructions.getFirst();
            while(currentIns != null) {
                System.out.println(currentIns);
                currentIns = currentIns.getNext();
            }

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
