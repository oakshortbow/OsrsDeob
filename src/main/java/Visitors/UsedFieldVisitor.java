package Visitors;

import Wrappers.InheritanceNode;
import Wrappers.Field;
import org.objectweb.asm.MethodVisitor;

import java.util.Set;

public class UsedFieldVisitor extends MethodVisitor {

    private Set<Field> rsFields;

    public UsedFieldVisitor(Set<Field> rsFields) {
        super(524288);
        this.rsFields = rsFields;
    }


    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (owner.contains("/")) {
            return;
        }
        rsFields.addAll(InheritanceNode.get(owner).getRelatedFields(name, descriptor));
    }
}
