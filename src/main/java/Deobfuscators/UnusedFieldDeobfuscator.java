package Deobfuscators;

import Data.Gamepack;
import Wrappers.RSField;
import Visitors.UsedFieldVisitor;
import com.triptheone.joda.Stopwatch;
import org.objectweb.asm.MethodVisitor;

import java.util.HashSet;
import java.util.Set;

public class UnusedFieldDeobfuscator implements Deobfuscator {

    private Set<RSField> usedFields = new HashSet<>();

    @Override
    public void execute() {
        Stopwatch s = Stopwatch.start();
        System.out.println("\nFinding Unused Fields..");

        MethodVisitor visitor = new UsedFieldVisitor(usedFields);
        Gamepack.getInstance().getAllMethods().forEach(method -> method.getMethodNode().accept(visitor));

        Set<RSField> unusedFields = getUnusedFields();
        System.out.println("\nFound " + usedFields.size() + "/" + Gamepack.getInstance().getAllFields().size() + " (" + unusedFields.size() + " Unused)");

        unusedFields.forEach(field -> Gamepack.getInstance().removeField(field));
        System.out.println("Unused Fields Removed in " + s.getElapsedTime().getMillis()/1000.0F + " Seconds");
    }

    public Set<RSField> getUnusedFields() {
        Set<RSField> fields = new HashSet<>(Gamepack.getInstance().getAllFields());
        fields.removeAll(usedFields);
        return fields;
    }

    public Set<RSField> getUsedFields() {
        return usedFields;
    }
}
