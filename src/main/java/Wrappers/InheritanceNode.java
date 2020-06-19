package Wrappers;

import Data.Gamepack;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;

public class InheritanceNode {

    private ClassNode classNode;

    private List<InheritanceNode> subClasses = new ArrayList<>();
    private List<InheritanceNode> interfaces = new ArrayList<>();

    private static final String OBJECT = "java/lang/Object";

    public static Map<String, InheritanceNode> nodeFlyweight = new HashMap<>();

    private InheritanceNode(ClassNode classNode) {
        this.classNode = classNode;
    }

    private List<String> getSubClassStrings() {
        List<String> subclasses = new ArrayList<>();
        for(ClassNode c: Gamepack.getInstance().getClasses()) {
            if(c.superName.equals(classNode.name)){
                subclasses.add(c.name);
            }

            if(c.interfaces.contains(this.getName())) {
                subclasses.add(c.name);
            }
        }
        return subclasses;
    }

    public InheritanceNode getSuperClassNode() {
        return get(this.getSuperName());
    }

    public List<MethodNode> getMethods() {
        return this.classNode.methods;
    }

    public List<FieldNode> getFields() {
        return this.classNode.fields;
    }

    public List<InheritanceNode> getSubClasses() {
        if(subClasses.isEmpty()) {
            getSubClassStrings().forEach(s -> subClasses.add(get(s)));
        }

        return subClasses;
    }

    public List<InheritanceNode> getInterfaces() {
        if(interfaces.isEmpty()) {
            classNode.interfaces.forEach(s -> interfaces.add(get(s)));
        }

        return interfaces;
    }

    public ClassNode getClassNode() {
        return classNode;
    }


    public static InheritanceNode get(String className) {
        if(nodeFlyweight.containsKey(className)) {
            return nodeFlyweight.get(className);
        }

        InheritanceNode node = new InheritanceNode(Gamepack.getInstance().getClass(className));
        nodeFlyweight.put(node.classNode.name, node);
        return node;
    }

    public boolean isInterface() {
        return (classNode.access & Opcodes.ACC_INTERFACE) != 0;
    }

    public String getCommonSuperClass(InheritanceNode other) {
        if(other.isInterface() || this.isInterface() || this.getName().equals(OBJECT) || other.getName().equals(OBJECT)) {
            return OBJECT;
        }

        List<String> superClasses = other.getAllSuperClasses();

        InheritanceNode currentNode = this;
        while(!superClasses.contains(currentNode.getName())) {
            currentNode = currentNode.getSuperClassNode();
        }

        return currentNode.getName();
    }


    private List<String> getAllSuperClasses() {
        List<String> superClasses = new ArrayList<>();

        InheritanceNode currentNode = this;
        while(!currentNode.getSuperClassNode().getName().equals(OBJECT)) {
            superClasses.add(currentNode.getName());
            currentNode = currentNode.getSuperClassNode();
        }
        superClasses.add(OBJECT);
        return superClasses;
    }

    public static InheritanceNode get(ClassNode classNode) {
        if(nodeFlyweight.containsKey(classNode.name)) {
            return nodeFlyweight.get(classNode.name);
        }
        InheritanceNode node = new InheritanceNode(classNode);
        nodeFlyweight.put(classNode.name, node);
        return node;
    }

    public Set<Method> getMethodsInheritedFromJava(InheritanceNode node, Set<Method> methodNodeList)
    {
        if(node.isJavaNode()) {
            for(MethodNode child: this.getMethods()) {
                for(MethodNode parent : node.getMethods()) {
                    if(((child.name.equals(parent.name) && child.desc.equals(parent.desc)))) {
                        methodNodeList.add(new Method(this.getClassNode(), child));
                    }
                }
            }
        }

        if(node.getSuperName() != null) {
            return getMethodsInheritedFromJava(node.getSuperClassNode(), methodNodeList);
        }
        return methodNodeList;
    }

    public Set<Method> getMethodsImplementedFromJava(List<InheritanceNode> nodes, Set<Method> methodNodeSet) {

        for (InheritanceNode startingInterface : nodes) {
            if(startingInterface.isJavaNode()) {
                for (MethodNode child : this.getMethods()) {
                    for (MethodNode parent : startingInterface.getMethods()) {
                        if (child.name.equals(parent.name) && child.desc.equals(parent.desc)) {
                            methodNodeSet.add(new Method(this.getClassNode(), child));
                        }
                    }
                }
            }
        }

        for(InheritanceNode startingInterface: nodes) {
            return getMethodsImplementedFromJava(startingInterface.getInterfaces(), methodNodeSet);
        }

        return methodNodeSet;
    }


    public Set<Field> getRelatedFields(String name, String descriptor) {
        Set<Field> fields = new HashSet<>();
        iterateFields(name, descriptor, fields);
        this.getSuperClassNode().iterateSuperclassFields(name, descriptor, fields);
        this.getSubClasses().forEach(f -> f.iterateSubclassFields(name, descriptor, fields));
        return fields;
    }

    private void iterateSuperclassFields(String name, String descriptor, Set<Field> fields) {
        if(this.getName().equals(OBJECT)) {
            return;
        }
        iterateFields(name, descriptor, fields);
        getSuperClassNode().iterateSuperclassFields(name, descriptor, fields);
    }

    private void iterateSubclassFields(String name, String descriptor, Set<Field> fields) {
        iterateFields(name, descriptor, fields);
        this.getSubClasses().forEach(node -> node.iterateSubclassFields(name, descriptor, fields));
    }

    private void iterateFields(String name, String descriptor, Set<Field> fields) {
        for(FieldNode f: this.getFields()) {
            if(f.name.equals(name) && f.desc.equals(descriptor)) {
                fields.add(new Field(this.getClassNode(), f));
                break;
            }
        }
    }

    public boolean isJavaNode() {
        return this.getName().contains("/");
    }

    public String getName() {
        return this.classNode.name;
    }

    public String getSuperName() {
        return this.classNode.superName;
    }

    public Set<Method> getMethodsImplementedFromJava() {
        return getMethodsImplementedFromJava(this.getInterfaces(), new HashSet<>());
    }

    public Set<Method> getMethodsInheritedFromJava() {
        return getMethodsInheritedFromJava(this.getSuperClassNode(), new HashSet<>());
    }

    public Set <Method> getJavaMethodsInHierarchy() {
        Set<Method> output = getMethodsInheritedFromJava();
        output.addAll(getMethodsImplementedFromJava());
        return output;
    }

    public Set<Method> computeCallEdges(int opcode, String name, String descriptor) {
        Set<Method> resolvedMethods = new HashSet<>();
        switch (opcode) {
            case Opcodes.INVOKEINTERFACE:
                //Invoke Interface methods
            case Opcodes.INVOKEVIRTUAL:
                //invokes public or protected instance methods
                iterateMethods(name, descriptor, resolvedMethods);
                this.getSubClasses().forEach(subClass -> subClass.iterateSubclassMethods(name, descriptor, resolvedMethods));
                this.getInterfaces().forEach(inter -> inter.iterateInterfaceMethods(name, descriptor, resolvedMethods));
                this.getSuperClassNode().iterateSuperclassMethods(name, descriptor, resolvedMethods);
                break;
            case Opcodes.INVOKESPECIAL:
                //invokes constructors, private methods, methods in superclasses
            case Opcodes.INVOKESTATIC:
                //Invokes static
                iterateMethods(name, descriptor, resolvedMethods);
                this.getInterfaces().forEach(inter -> inter.iterateInterfaceMethods(name, descriptor, resolvedMethods));
                this.getSuperClassNode().iterateSuperclassMethods(name, descriptor, resolvedMethods);
                break;
        }
        return resolvedMethods;
    }

    private void iterateSuperclassMethods(String name, String descriptor, Set<Method> resolvedMethods) {
        if(this.getName().equals(OBJECT)) {
            return;
        }
        iterateMethods(name, descriptor, resolvedMethods);
        this.getSuperClassNode().iterateSuperclassMethods(name, descriptor, resolvedMethods);
    }


    private void iterateInterfaceMethods(String name, String descriptor, Set<Method> resolvedMethods) {
        iterateMethods(name, descriptor, resolvedMethods);
        this.getInterfaces().forEach(inter -> inter.iterateInterfaceMethods(name, descriptor, resolvedMethods));
    }

    private void iterateSubclassMethods(String name, String descriptor, Set<Method> resolvedMethods) {
        iterateMethods(name, descriptor, resolvedMethods);
        this.getSubClasses().forEach(subClass -> subClass.iterateSubclassMethods(name, descriptor, resolvedMethods));
    }

    private void iterateMethods(String name, String descriptor, Set<Method> resolvedMethods) {
        for(MethodNode method: this.getMethods()) {
            if(method.name.equals(name) && method.desc.equals(descriptor)) {
                resolvedMethods.add(new Method(this.getClassNode(), method));
                break;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getName() + " extends " + this.getSuperName());
        getInterfaces().forEach(s -> sb.append("\n----> Implements ").append(s.getName()));
        if(getSubClasses().size() > 0) {
            sb.append("\nIs Implemented/Extended By");
            getSubClasses().forEach(s -> sb.append("\n-").append(s.getName()));
        }
        return sb.toString();
    }
}