package sample;

import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;

class MiniNode implements Comparable<MiniNode>
{
    double theta, omega;
    MiniNode friend;
    Node parent;
    int number = -1;

    ArrayList<MiniNode> neighbors = new ArrayList<>();

    MiniNode(Node parent)
    {
        this.parent = parent;
    }

    double getX()
    {
        return parent.x + Node.miniDistance * FastMath.cos(theta);
    }

    double getY()
    {
        return parent.y + Node.miniDistance * FastMath.sin(theta);
    }

    @Override
    public int compareTo(MiniNode o)
    {
        return Double.compare(this.theta, o.theta);
    }
}
