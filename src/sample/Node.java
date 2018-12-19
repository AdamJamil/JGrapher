package sample;

import javafx.scene.paint.Color;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.Collections;

class Node
{
    static double k = .01;
    static double damp = .99825;
    static double rotationalDamp = 0.85;
    static double friendDistance = 150;
    static double miniDistance = 15;
    static double friendConstant = 1;
    static double nodeRepulsion = 2000;
    static double angleRegularization = 0.05;

    ArrayList<MiniNode> miniNodes;
    ArrayList<MiniEdge> miniEdges;
    ArrayList<Node> neighbors = new ArrayList<>();
    ArrayList<Edge> edges = new ArrayList<>();

    double x, vx;
    double y, vy;
    double forceX, forceY;
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
        if (neighbors.size() == 0)
            return;

        if (graph.split)
        {
            Collections.sort(miniNodes);

            for (int i = 0; i < miniNodes.size(); i++)
            {
                MiniNode miniNode = miniNodes.get(i);
                miniNode.omega %= 2 * Math.PI;
                if (miniNode.omega > 0)
                    miniNode.omega = FastMath.log(rotationalDamp * miniNode.omega + 1);
                else
                    miniNode.omega = -FastMath.log(-rotationalDamp * miniNode.omega + 1);

                double miniAlpha = 0, minifx = 0, minify = 0;
                double miniX = miniNode.getX();
                double miniY = miniNode.getY();

                for (MiniNode temp : miniNodes)
                {
                    if (temp == miniNode)
                        continue;

                    if (i == 0)
                        miniAlpha -= angleRegularization * (miniNode.theta + (2 * Math.PI) - miniNodes.get(miniNodes.size() - 1).theta - (2 * Math.PI) / miniNodes.size());
                    else
                        miniAlpha -= angleRegularization * (miniNode.theta - miniNodes.get(i - 1).theta - (2 * Math.PI) / miniNodes.size());

                    if (miniNodes.size() <= 2)
                        continue;

                    if (i == miniNodes.size() - 1)
                        miniAlpha -= angleRegularization * (miniNodes.get(0).theta + (2 * Math.PI) - miniNode.theta - (2 * Math.PI) / miniNodes.size());
                    else
                        miniAlpha -= angleRegularization * (miniNodes.get(i + 1).theta - miniNode.theta - (2 * Math.PI) / miniNodes.size());
                }

                {
                    double tempX = miniNode.friend.parent.x + miniDistance * FastMath.cos(miniNode.friend.theta);
                    double tempY = miniNode.friend.parent.y + miniDistance * FastMath.sin(miniNode.friend.theta);
                    double xDist = tempX - miniX, yDist = tempY - miniY;
                    double f = 1 * k * (FastMath.sqrt(xDist * xDist + yDist * yDist) - (friendDistance - miniDistance * 2));
                    //if (f > 0)
                        //f = FastMath.log(f + 1);
                    double theta = FastMath.atan2(yDist, xDist);
                    minifx += f * FastMath.cos(theta);
                    minify += f * FastMath.sin(theta);
                }

                //Fperp = F - (F.v)v
                double dotProd = minifx * FastMath.cos(miniNode.theta) + minify * FastMath.sin(miniNode.theta);
                minifx -= dotProd * FastMath.cos(miniNode.theta);
                minify -= dotProd * FastMath.sin(miniNode.theta);
                double f = FastMath.sqrt(minifx * minifx + minify * minify);

                //check direction
                if (minifx * FastMath.sin(miniNode.theta) - minify * FastMath.cos(miniNode.theta) < 0)
                    miniAlpha += f;
                else
                    miniAlpha -= f;

                miniNode.omega += miniAlpha * Main.dt;
            }
        }
    }

    void move()
    {
        if (selected)
        {
            forceX = 0;
            forceY = 0;
            return;
        }

        vx += forceX * Main.dt / FastMath.log(neighbors.size() + 1);
        vy += forceY * Main.dt / FastMath.log(neighbors.size() + 1);
        forceX = 0;
        forceY = 0;

        vx *= damp;
        vy *= damp;

        x += vx * Main.dt;
        y += vy * Main.dt;

        if (miniNodes != null)
            for (MiniNode miniNode : miniNodes)
            {
                miniNode.theta = (miniNode.theta + miniNode.omega * Main.dt) % (2 * Math.PI);
                if (miniNode.theta < 0)
                    miniNode.theta += 2 * Math.PI;
            }
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}