package Visitors;

import Wrappers.InheritanceNode;
import Wrappers.Field;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Set;

public class UsedFieldVisitor extends MethodVisitor {

    private Set<Field> rsFields;

    public UsedFieldVisitor(Set<Field> rsFields) {
        super(Opcodes.ASM8);
        this.rsFields = rsFields;
    }


    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (owner.contains("/") ) {
            return;
        }
        rsFields.addAll(InheritanceNode.get(owner).getRelatedFields(name, descriptor));
    }
}
