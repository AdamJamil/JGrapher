package sample;

import javafx.scene.paint.Color;

import java.util.ArrayList;

class Node
{
    private static double k = .01;
    private static double damp = .998;
    private static double friendDistance = 70;
    private static double miniDistance = 12;

    ArrayList<MiniNode> miniNodes;
    ArrayList<Node> neighbors = new ArrayList<>();
    ArrayList<Edge> edges = new ArrayList<>();
    int miniEdges = 0;

    double x, vx;
    double y, vy;
    boolean selected = false;
    String name;
    Color color = Color.BLACK;

    Node(String s)
    {
        name = s;

        x = Math.random() * Main.width;
        y = Math.random() * Main.height;
    }

    void computeForce(Graph graph)
    {
        ArrayList<Node> nodes = graph.nodes;

        vx *= damp;
        vy *= damp;

        if (neighbors.size() == 0)
            return;

        double fx = 0;
        double fy = 0;

        for (Node n : nodes)
        {
            if (this == n)
                continue;

            if (neighbors.contains(n))
            {
                double xDist = n.x - x, yDist = n.y - y;
                double f = k * (Math.sqrt(xDist * xDist + yDist * yDist) - friendDistance);
                double theta = Math.atan2(yDist, xDist);
                fx += f * Math.cos(theta);
                fy += f * Math.sin(theta);
            }
            else
            {
                double xDist = n.x - x, yDist = n.y - y;
                double f = k / (xDist * xDist + yDist * yDist);
                double theta = Math.atan2(yDist, xDist);
                fx -= 50000 * Math.cos(theta) * f;
                fy -= 50000 * Math.sin(theta) * f;
            }
        }

        if (graph.split)
        {
            for (MiniNode miniNode : miniNodes)
            {
                miniNode.vx *= damp;
                miniNode.vy *= damp;

                double minifx = 0;
                double minify = 0;

                for (Node n : nodes)
                {
                    if (n == this)
                    {
                        double xDist = n.x - miniNode.x, yDist = n.y - miniNode.y;
                        double f = 10 * k * (Math.sqrt(xDist * xDist + yDist * yDist) - miniDistance);
                        double theta = Math.atan2(yDist, xDist);
                        minifx += f * Math.cos(theta);
                        minify += f * Math.sin(theta);
                    }
                    else
                    {
                        double xDist = n.x - miniNode.x, yDist = n.y - miniNode.y;
                        double f = k / (xDist * xDist + yDist * yDist);
                        double theta = Math.atan2(yDist, xDist);
                        fx -= 10000 * Math.cos(theta) * f;
                        fy -= 10000 * Math.sin(theta) * f;
                    }
                }

                {
                    double xDist = miniNode.friend.x - miniNode.x, yDist = miniNode.friend.y - miniNode.y;
                    double f = k * (Math.sqrt(xDist * xDist + yDist * yDist) - (friendDistance - miniDistance * 2));
                    double theta = Math.atan2(yDist, xDist);
                    minifx += 10 * f * Math.cos(theta);
                    minify += 10 * f * Math.sin(theta);
                }

                miniNode.vx += minifx * Main.dt;
                miniNode.vy += minify * Main.dt;
            }
        }

        vx += fx * Main.dt / Math.log(neighbors.size() + 1);
        vy += fy * Main.dt / Math.log(neighbors.size() + 1);
    }

    void move()
    {
        if (!selected)
        {
            x += vx * Main.dt;
            y += vy * Main.dt;
        }

        if (miniNodes != null)
            for (MiniNode miniNode : miniNodes)
            {
                miniNode.x += miniNode.vx * Main.dt;
                miniNode.y += miniNode.vy * Main.dt;
            }
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}