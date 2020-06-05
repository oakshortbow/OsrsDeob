package Visitors;

import Wrappers.InheritanceNode;
import Wrappers.RSField;
import org.objectweb.asm.MethodVisitor;

import java.util.Set;

public class UsedFieldVisitor extends MethodVisitor {

    private Set<RSField> rsFields;

    public UsedFieldVisitor(Set<RSField> rsFields) {
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
