import Data.Gamepack;
import Deobfuscators.Deobfuscator;
import Deobfuscators.ControlFlowDeobfuscator;
import Deobfuscators.UnusedFieldDeobfuscator;
import Deobfuscators.UnusedMethodDeobfuscator;

import java.util.ArrayList;
import java.util.List;

public class Deob {


    public static void main(String[] args) {
        System.out.println("Starting Deobfuscation Process..");
        List<Deobfuscator> deobfuscator = new ArrayList<>();

        deobfuscator.add(new UnusedMethodDeobfuscator());
        deobfuscator.add(new UnusedFieldDeobfuscator());
        deobfuscator.add(new ControlFlowDeobfuscator());

        deobfuscator.forEach(Deobfuscator::execute);

        Gamepack.getInstance().saveJar();
    }




}

