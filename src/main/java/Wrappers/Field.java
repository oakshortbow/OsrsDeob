package Wrappers;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Objects;

public class Field {

    private ClassNode classNode;
    private FieldNode fieldNode;

    public Field(ClassNode classNode, FieldNode fieldNode) {
        this.classNode = classNode;
        this.fieldNode = fieldNode;
    }

    public ClassNode getClassNode() {
        return classNode;
    }


    public FieldNode getFieldNode() {
        return fieldNode;
    }

    @Override
    public String toString() {
        return getClassNode().name + "." + getFieldNode().name;
    }

    @Override
    public boolean equals(Object other) {
        Field m;
        if(other instanceof Field) {
            m = (Field)other;
        }
        else {
            return false;
        }
        return this.getFieldNode().equals(m.getFieldNode()) && this.getClassNode().equals(m.getClassNode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(classNode, fieldNode);
    }

}

