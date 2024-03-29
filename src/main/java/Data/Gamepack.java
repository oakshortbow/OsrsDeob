package Data;

import Wrappers.ClassWriterComputeFrames;
import Wrappers.Field;
import Wrappers.Method;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;


public class Gamepack {

    private Set<ClassNode> rsClasses = new HashSet<>();
    private Set<Field> rsFields = new HashSet<>();
    private Set<Method> rsMethods = new HashSet<>();

    private static Gamepack gamepack = null;

    private static final String GAMEPACK_NAME = "190.jar";

    private static final String OUTPUT_PATH = "src/test/resources/190-deob.jar";


    private Gamepack(String path) {
        try {
            JarFile jar = new JarFile(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(path)).getFile());

            for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();)
            {
                JarEntry entry = entries.nextElement();
                String file = entry.getName();
                if (file.endsWith(".class"))
                {
                    ClassNode classNode = new ClassNode();
                    ClassReader reader = new ClassReader(jar.getInputStream(entry));
                    reader.accept(classNode, ClassReader.SKIP_FRAMES);
                    rsClasses.add(classNode);
                    classNode.methods.forEach(m -> rsMethods.add(new Method(classNode, m)));
                    classNode.fields.forEach(f -> rsFields.add(new Field(classNode, f)));
                }
            }
        }
        catch(IOException iex) {
            iex.printStackTrace();
        }
    }

    public static Gamepack getInstance() {
        if(gamepack == null) {
            gamepack = new Gamepack(GAMEPACK_NAME);
        }
        return gamepack;
    }

    public Set<ClassNode> getClasses() {
        return rsClasses;
    }

    public Set<Method> getMethods() {
        return rsMethods;
    }

    public Set<Field> getAllFields() {
        return rsFields;
    }

    public void removeField(Field field) {
        field.getClassNode().fields.remove(field.getFieldNode());
        rsFields.remove(field);
    }

    public void removeMethod(Method method) {
        method.getClassNode().methods.remove(method.getMethodNode());
        rsMethods.remove(method);
    }

    public ClassNode getClass(String className) {
        for(ClassNode c: rsClasses) {
             if(c.name.equals(className)) {
                 return c;
             }
        }
        return getJavaClass(className);
    }

    private ClassNode getJavaClass(String className) {
        ClassNode classNode = new ClassNode();
        try {
            ClassReader reader = new ClassReader(className);
            reader.accept(classNode, ClassReader.SKIP_FRAMES);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return classNode;
    }


    public void saveJar() {
        System.out.println("\nSaving Deobfuscated Jar..");
        try {
            JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(OUTPUT_PATH), new Manifest());
            for (ClassNode c : this.getClasses()) {
                System.out.println("Saving " + c.name);
                ClassWriterComputeFrames classWriter = new ClassWriterComputeFrames();
                c.accept(classWriter);
                JarEntry newEntry = new JarEntry(c.name + ".class");
                jarOut.putNextEntry(newEntry);
                jarOut.write(classWriter.toByteArray());
                jarOut.closeEntry();
            }
            jarOut.flush();
            jarOut.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}
