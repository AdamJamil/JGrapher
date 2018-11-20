package sample;

import java.util.HashSet;
import java.util.Set;

class JoinedMiniNodes implements Comparable<JoinedMiniNodes>
{
    Set<MiniNode> miniNodes = new HashSet<>();
    Set<Node> fringe = new HashSet<>();
    Set<Node> doubleFringe = new HashSet<>();
    Set<Node> visited = new HashSet<>();

    MiniNode left, right;

    JoinedMiniNodes join(JoinedMiniNodes other)
    {
        miniNodes.addAll(other.miniNodes);
        fringe.addAll(other.fringe);
        doubleFringe.addAll(other.doubleFringe);
        visited.addAll(other.visited);

        right.neighbors.add(other.left);
        other.left.neighbors.add(right);
        right = other.right;

        return this;
    }

    JoinedMiniNodes(MiniNode miniNode)
    {
        miniNodes.add(miniNode);

        visited.add(miniNode.parent);
        fringe.add(miniNode.friend.parent);

        left = miniNode;
        right = miniNode;
    }

    @Override
    public int compareTo(JoinedMiniNodes other)
    {
        return Integer.compare(miniNodes.size(), other.miniNodes.size());
    }
}
