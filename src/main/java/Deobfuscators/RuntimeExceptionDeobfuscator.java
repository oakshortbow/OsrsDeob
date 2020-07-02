package Deobfuscators;

import Data.Gamepack;
import Wrappers.Method;
import com.triptheone.joda.Stopwatch;

public class RuntimeExceptionDeobfuscator implements Deobfuscator {


    private static final String RUNTIME_EXCEPTION = "java/lang/RuntimeException";

    //Removes All Unused RunTimeExceptions as they are never hit in the client
    @Override
    public void execute() {
        System.out.println("\nFinding Unused Exceptions..");
        Stopwatch s = Stopwatch.start();
        int tryCatchesRemoved = 0;

        for (Method m: Gamepack.getInstance().getMethods()) {
            int size = m.getMethodNode().tryCatchBlocks.size();
            m.getMethodNode().tryCatchBlocks.removeIf(tryCatchBlockNode -> tryCatchBlockNode.type != null && tryCatchBlockNode.type.equals(RUNTIME_EXCEPTION));
            tryCatchesRemoved += size - m.getMethodNode().tryCatchBlocks.size();
        }

        System.out.println(tryCatchesRemoved + " RuntimeExceptions Removed in " + s.getElapsedTime().getMillis()/1000.0F + " Seconds");
    }
}
