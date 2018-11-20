package sample;

import java.util.*;

class Graph
{
    ArrayList<Node> nodes;
    ArrayList<Edge> edges;
    boolean split = false;
    boolean connect = false;

    void split()
    {
        split = true;

        for (Node n : nodes)
            n.miniNodes = new ArrayList<>(n.neighbors.size());

        for (Edge e : edges)
        {
            MiniNode a = new MiniNode(), b = new MiniNode();
            Node nodeA = nodes.get(e.a), nodeB = nodes.get(e.b);

            double angleA = (((double) nodeA.miniNodes.size()) / nodeA.neighbors.size()) * 2 * Math.PI;
            double angleB = (((double) nodeB.miniNodes.size()) / nodeB.neighbors.size()) * 2 * Math.PI;

            a.x = nodeA.x + (15) * Math.cos(angleA);
            a.y = nodeA.y + (15) * Math.sin(angleA);
            b.x = nodeB.x + (15) * Math.cos(angleB);
            b.y = nodeB.y + (15) * Math.sin(angleB);

            a.friend = b;
            b.friend = a;
            a.parent = nodeA;
            b.parent = nodeB;
            a.number = nodeA.miniNodes.size();
            b.number = nodeB.miniNodes.size();

            nodeA.miniNodes.add(a);
            nodeB.miniNodes.add(b);
        }
    }

    void connect()
    {
        connect = true;

        for (Node node : nodes)
        {
            System.out.println("\n" + node);

            ArrayList<JoinedMiniNodes> miniNodes = new ArrayList<>(node.miniNodes.size());

            for (MiniNode miniNode : node.miniNodes)
                miniNodes.add(new JoinedMiniNodes(miniNode));

            label:
            while (node.miniEdges != node.neighbors.size() - 1)
            {
                System.out.println();
                for (JoinedMiniNodes joinedMiniNodes : miniNodes)
                {
                    Set<Node> temp = new HashSet<>();

                    System.out.print(joinedMiniNodes.fringe + ", " + joinedMiniNodes.doubleFringe + ", ");

                    for (Node fringeElement : joinedMiniNodes.fringe)
                        for (Node neighbor : fringeElement.neighbors)
                        {
                            if (!joinedMiniNodes.visited.contains(neighbor))
                            {
                                temp.add(neighbor);
                                joinedMiniNodes.visited.add(neighbor);
                            }
                        }

                    joinedMiniNodes.doubleFringe = new HashSet<>(joinedMiniNodes.fringe);
                    joinedMiniNodes.doubleFringe.addAll(temp);
                    joinedMiniNodes.fringe = temp;

                    System.out.println(temp);
                    System.out.println("\t" + joinedMiniNodes.fringe + ", " + joinedMiniNodes.doubleFringe);
                }

                for (int i = 0; i < miniNodes.size() - 1; i++)
                    inner:
                    for (int j = i + 1; j < miniNodes.size(); j++)
                        for (Node fringeElement : miniNodes.get(i).doubleFringe)
                            if (miniNodes.get(j).doubleFringe.contains(fringeElement))
                            {
                                JoinedMiniNodes joinedMiniNodes1 = miniNodes.get(i),
                                                joinedMiniNodes2 = miniNodes.get(j);

                                joinedMiniNodes1.join(joinedMiniNodes2);
                                miniNodes.remove(j);

                                node.miniEdges++;

                                if (node.miniEdges == node.neighbors.size() - 1)
                                    break label;

                                continue inner;
                            }

                for (JoinedMiniNodes miniNode : miniNodes)
                    if (!miniNode.fringe.isEmpty())
                        continue label;

                break;
            }

            PriorityQueue<JoinedMiniNodes> priorityQueue = new PriorityQueue<>(miniNodes);

            while (priorityQueue.size() > 1)
                priorityQueue.add(priorityQueue.poll().join(priorityQueue.poll()));
        }
    }

    Graph(int nodes)
    {
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>();

        for (int i = 0; i < nodes; i++)
            this.nodes.add(new Node("" + (char) ('a' + (char) i)));
    }

    void linkBoth(int a, int b)
    {
        nodes.get(b).neighbors.add(nodes.get(a));
        nodes.get(a).neighbors.add(nodes.get(b));
        edges.add(new Edge(a, b));
    }

    void link(int a, int b)
    {
        nodes.get(a).neighbors.add(nodes.get(b));
        edges.add(new Edge(a, b));
    }
}
