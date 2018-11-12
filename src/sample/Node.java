package sample;

import java.lang.reflect.Array;
import java.util.ArrayList;

class Node
{
    static double k = 0.01;

    ArrayList<Node> neighbors = new ArrayList<>();
    double x, vx;
    double y, vy;

    ArrayList<MiniNode> miniNodes;

    Node()
    {
        x = Math.random() * Main.width;
        y = Math.random() * Main.height;
    }

    void computeForce(Graph graph)
    {
        ArrayList<Node> nodes = graph.nodes;

        vx *= 0.95;
        vy *= 0.95;

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
                double f = k * (Math.sqrt(xDist * xDist + yDist * yDist) - 70);
                double theta = Math.atan2(yDist, xDist);
                fx += f * Math.cos(theta);
                fy += f * Math.sin(theta);
            }
            else
            {
                double xDist = n.x - x, yDist = n.y - y;
                double f = k / (xDist * xDist + yDist * yDist);
                double theta = Math.atan2(yDist, xDist);
                fx -= 100000 * Math.cos(theta) * f;
                fy -= 100000 * Math.sin(theta) * f;
            }
        }

        if (graph.split)
        {
            for (MiniNode miniNode : miniNodes)
            {
                miniNode.vx *= .95;
                miniNode.vy *= .95;

                double minifx = 0;
                double minify = 0;

                for (Node n : nodes)
                {
                    if (n == this)
                    {
                        double xDist = n.x - miniNode.x, yDist = n.y - miniNode.y;
                        double f = 10 * k * (Math.sqrt(xDist * xDist + yDist * yDist) - 9);
                        double theta = Math.atan2(yDist, xDist);
                        minifx += f * Math.cos(theta);
                        minify += f * Math.sin(theta);
                    }
                    else
                    {
                        double xDist = n.x - miniNode.x, yDist = n.y - miniNode.y;
                        double f = k / (xDist * xDist + yDist * yDist);
                        double theta = Math.atan2(yDist, xDist);
                        //fx -= 100000 * Math.cos(theta) * f;
                        //fy -= 100000 * Math.sin(theta) * f;
                    }
                }

                {
                    double xDist = miniNode.friend.x - miniNode.x, yDist = miniNode.friend.y - miniNode.y;
                    double f = k * (Math.sqrt(xDist * xDist + yDist * yDist) - 52);
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
        x += vx * Main.dt;
        y += vy * Main.dt;

        if (miniNodes != null)
            for (MiniNode miniNode : miniNodes)
            {
                miniNode.x += miniNode.vx * Main.dt;
                miniNode.y += miniNode.vy * Main.dt;
            }
    }
}