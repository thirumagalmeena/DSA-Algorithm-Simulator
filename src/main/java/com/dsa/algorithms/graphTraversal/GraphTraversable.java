package com.dsa.algorithms.graphTraversal;

import java.util.List;

public interface GraphTraversable {
    List<Integer> traverse(int startNode);
    List<String> getTraversalSteps();
}
