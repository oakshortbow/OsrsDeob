import Data.Gamepack;
import Deobfuscators.*;

import java.util.ArrayList;
import java.util.List;

public class Deob {


    public static void main(String[] args) {
        System.out.println("Starting Deobfuscation Process..");
        List<Deobfuscator> deobfuscator = new ArrayList<>();

        deobfuscator.add(new UnusedMethodDeobfuscator());
        deobfuscator.add(new UnusedFieldDeobfuscator());
        deobfuscator.add(new RuntimeExceptionDeobfuscator());
        deobfuscator.add(new DeadCodeDeobfuscator());
        deobfuscator.add(new ControlFlowDeobfuscator());
        deobfuscator.add(new GotoDeobfuscator());


        deobfuscator.forEach(Deobfuscator::execute);

        Gamepack.getInstance().saveJar();
    }




}

