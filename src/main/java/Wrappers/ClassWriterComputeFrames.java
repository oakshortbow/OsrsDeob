package Wrappers;

import org.objectweb.asm.ClassWriter;

public class ClassWriterComputeFrames extends ClassWriter {

    public ClassWriterComputeFrames() {
        super(ClassWriter.COMPUTE_FRAMES);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        return InheritanceNode.get(type1).getCommonSuperClass(InheritanceNode.get(type2));
    }

}
