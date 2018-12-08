package sample;

import javafx.scene.paint.Color;

import java.lang.reflect.Array;
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
            Node nodeA = e.a, nodeB = e.b;

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

    void partition(int k)
    {
        if (k != 2)
        {
            System.out.println(k + "-partition not allowed");
            return;
        }

        int i;
        for (i = 1; i < nodes.size(); i++)
        {
            int ways = 0;

            boolean[] arr = new boolean[nodes.size()];

            for (int j = 0; j < i; j++)
                arr[j] = true;

            while (arr != null)
            {
                Graph copy = copy();

                for (int j = copy.nodes.size() - 1; j >= 0; j--)
                    if (arr[j])
                        copy.nodes.remove(j);

                ArrayList<Graph> components = components(copy);

                int[] sizes = new int[components.size()];

                for (int j = 0; j < components.size(); j++)
                    sizes[j] = components.get(j).edges.size();

                int sum  = edges.size() / 2;
                int[][][] dp = new int[sizes.length][sum + 1][];

                for (int size : sizes)
                    if (size <= sum)
                        dp[0][size] = new int[]{size};

                for (int j = 1; j < sizes.length; j++)
                {
                    for (int l = 0; l <= sum; l++)
                    {
                        if (dp[j - 1][l] != null)
                            if (sizes[j] + l < sum)
                            {
                                dp[j][sizes[j] + l] = new int[dp[j - 1][l].length + 1];
                                System.arraycopy(dp[j - 1][l], 0, dp[j][sizes[j] + l], 0, dp[j - 1][l].length);
                                dp[j][sizes[j] + l][dp[j - 1][l].length] = sizes[j];
                            }
                            else if (l == sum)
                                dp[j][sum] = dp[j - 1][sum];
                    }
                }

                int[] result = dp[sizes.length - 1][sum];

                if (result != null)
                {
                    ways++;
                    Graph one = new Graph(0), two = new Graph(0);

                    System.out.println("here we go");
                    outer: for (Graph component : components)
                    {
                        System.out.println(component);
                        for (int j = 0; j < result.length; j++)
                        {
                            if (component.edges.size() == result[j])
                            {
                                result[j] = -1;
                                one.join(component);
                                continue outer;
                            }
                        }

                        two.join(component);
                    }

                    outer: for (Node node : nodes)
                    {
                        for (Node temp : one.nodes)
                            if (temp.toString().equals(node.toString()))
                            {
                                node.color = Color.RED;
                                continue outer;
                            }

                        for (Node temp : two.nodes)
                            if (temp.toString().equals(node.toString()))
                            {
                                node.color = Color.BLUE;
                                continue outer;
                            }

                        node.color = Color.GREY;
                    }

                    outer: for (Edge edge : edges)
                    {
                        for (Edge temp : one.edges)
                            if (temp.toString().equals(edge.toString()))
                            {
                                edge.color = Color.RED;
                                continue outer;
                            }

                        edge.color = Color.BLUE;
                    }

                    return;
                }

                arr = getNextChoice(arr);
            }

            if (ways > 0)
                break;
        }
    }

    boolean[] getNextChoice(boolean[] arr)
    {
        int counter = 0;

        for (int i = 0; i < arr.length - 1; i++)
        {
            if (arr[i])
            {
                if (arr[i + 1])
                    counter++;
                else
                {
                    arr[i + 1] = true;
                    arr[i] = false;
                    for (int j = 0; j < counter; j++)
                        arr[j] = true;
                    for (int j = counter; j < i; j++)
                        arr[j] = false;

                    return arr;
                }
            }
        }

        return null;
    }

    ArrayList<Graph> components(Graph graph)
    {
        HashSet<Node> visited = new HashSet<>(graph.nodes.size()), fringe = new HashSet<>(graph.nodes.size()), temp;
        HashSet<Edge> edges = new HashSet<>();
        Graph rest = new Graph(0); //not necessary
        rest.nodes = new ArrayList<>(graph.nodes);
        rest.edges = new ArrayList<>(graph.edges);

        rest.nodes.remove(0);
        fringe.add(graph.nodes.get(0));
        visited.add(graph.nodes.get(0));

        while (!fringe.isEmpty())
        {
            temp = new HashSet<>();

            for (Node node : fringe)
                for (Edge edge : node.edges)
                {
                    Node neighbor = (edge.a == node) ? edge.b : edge.a;

                    if (graph.nodes.contains(neighbor) && !visited.contains(neighbor))
                    {
                        temp.add(neighbor);
                        edges.add(edge);
                        rest.nodes.remove(neighbor);
                        rest.edges.remove(edge);
                    }
                    else if (!edges.contains(edge))
                    {
                        edges.add(edge);
                        rest.edges.remove(edge);
                    }
                }

            fringe = temp;
            visited.addAll(fringe);
        }

        Graph component = new Graph(0);
        component.nodes = new ArrayList<>(visited);
        component.edges = new ArrayList<>(edges);
        ArrayList<Graph> out = (rest.nodes.size() > 0) ? components(rest) : new ArrayList<>();
        out.add(component);

        return out;
    }

    Graph(int nodes)
    {
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>();

        for (int i = 0; i < nodes; i++)
            this.nodes.add(new Node("" + (char) ('a' + (char) i)));
    }

    void linkBoth(Node a, Node b)
    {
        a.neighbors.add(b);
        b.neighbors.add(a);
        Edge edge = new Edge(a, b);
        a.edges.add(edge);
        b.edges.add(edge);
        edges.add(edge);
    }

    void linkBoth(int i, int j)
    {
        linkBoth(nodes.get(i), nodes.get(j));
    }

    void link(int i, int j)
    {
        Node a = nodes.get(i), b = nodes.get(j);
        a.neighbors.add(b);
        Edge edge = new Edge(a, b);
        a.edges.add(edge);
        edges.add(edge);
    }

    Graph copy()
    {
        Graph graph = new Graph(nodes.size());

        for (Edge edge : edges)
        {
            if (nodes.indexOf(edge.a) == -1 || nodes.indexOf(edge.b) == - 1)
                continue;
            graph.linkBoth(nodes.indexOf(edge.a), nodes.indexOf(edge.b));
        }

        return graph;
    }

    void join(Graph graph)
    {
        nodes.addAll(graph.nodes);
        edges.addAll(graph.edges);
    }

    @Override
    public String toString()
    {
        String out = "";
        /*for (Node node : nodes)
        {
            out += node + ": ";
            for (Node neighbor : node.neighbors)
                out += neighbor + " ";
            out += "\n";
        }

        for (Edge edge : edges)
            out += edge + "\n";*/

        out += "{";

        if (nodes.size() != 0)
        {
            out += nodes.get(0);
            for (int i = 1; i < nodes.size(); i++)
                out += ", " + nodes.get(i);
        }

        if (edges.size() != 0)
        {
            out += " | " + edges.get(0);
            for (int i = 1; i < edges.size(); i++)
                out += ", " + edges.get(i);
        }

        return out + "}";
    }
}
