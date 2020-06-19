package Deobfuscators;

import Data.Gamepack;
import Wrappers.Method;
import Structs.Graph;
import Wrappers.InheritanceNode;
import Visitors.UsedMethodVisitor;
import com.triptheone.joda.Stopwatch;

import java.util.HashSet;
import java.util.Set;

public class UnusedMethodDeobfuscator implements Deobfuscator {

    /*
    Finds Entry Points
    Builds Call Graph
    Removes Unhit methods
    Requires Multiple passes to be efficient
     */

    @Override
    public void execute() {
        Stopwatch s = Stopwatch.start();
        System.out.println("\nFinding Unused Methods..");
        Set<Method> entryPoints = new HashSet<>();


        Gamepack.getInstance().getClasses().forEach(node -> entryPoints.addAll(InheritanceNode.get(node).getJavaMethodsInHierarchy()));

        Graph<Method> callGraph = new Graph<>();
        entryPoints.forEach(callGraph::addNode);
        entryPoints.forEach(entryPoint -> entryPoint.getMethodNode().accept(new UsedMethodVisitor(callGraph, entryPoint)));

        System.out.println("Found " + getUsedMethods(callGraph).size() + "/" + Gamepack.getInstance().getMethods().size() + " (" + (getUnusedMethods(callGraph).size()) + " Unused)");

        getUnusedMethods(callGraph).forEach(rsMethod -> Gamepack.getInstance().removeMethod(rsMethod));
        System.out.println("Unused Methods Removed in " + s.getElapsedTime().getMillis()/1000.0F + " Seconds");

    }


    public Set<Method> getUsedMethods(Graph<Method> callGraph) {
        Set<Method> usedMethods =  new HashSet<>();
        for(Method m : callGraph.getNodes()) {
            if(Gamepack.getInstance().getMethods().contains(m)) {
                usedMethods.add(m);
            }
        }

        return usedMethods;
    }

    public Set<Method> getUnusedMethods(Graph<Method> callGraph) {
        Set<Method> allMethods = new HashSet<>(Gamepack.getInstance().getMethods());
        allMethods.removeAll(getUsedMethods(callGraph));
        return allMethods;
    }

}
