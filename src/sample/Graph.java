package sample;

import java.util.ArrayList;

class Graph
{
    ArrayList<Node> nodes;
    ArrayList<Edge> edges;
    boolean split = false;

    void split()
    {
        if (split)
            return;

        split = true;

        for (Node n : nodes)
            n.miniNodes = new ArrayList<>(n.neighbors.size());

        for (Edge e : edges)
        {
            MiniNode a = new MiniNode(), b = new MiniNode();

            double angleA = (((double) nodes.get(e.a).miniNodes.size()) / nodes.get(e.a).neighbors.size()) * 2 * Math.PI;
            double angleB = (((double) nodes.get(e.b).miniNodes.size()) / nodes.get(e.b).neighbors.size()) * 2 * Math.PI;

            a.x = nodes.get(e.a).x + (15) * Math.cos(angleA);
            a.y = nodes.get(e.a).y + (15) * Math.sin(angleA);
            b.x = nodes.get(e.b).x + (15) * Math.cos(angleB);
            b.y = nodes.get(e.b).y + (15) * Math.sin(angleB);

            a.friend = b;
            b.friend = a;
            nodes.get(e.a).miniNodes.add(a);
            nodes.get(e.b).miniNodes.add(b);
        }
    }

    Graph(int nodes)
    {
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>();
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
