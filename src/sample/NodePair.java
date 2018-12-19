package sample;

import org.apache.commons.math3.util.FastMath;

class NodePair
{
    Node a, b;
    boolean neighbor;
    double friendConstant = 0.1;
    double nodeRepulsion = 20;
    static double friendDistance = 150;

    void computeForce(double stability)
    {
        if (neighbor)
        {
            double xDist = a.x - b.x, yDist = a.y - b.y;
            double dist = FastMath.sqrt(xDist * xDist + yDist * yDist);

            if (dist < friendDistance)
                friendConstant += 0.0001 * stability;

            double f = friendConstant * (dist - friendDistance - 2 * Node.miniDistance);
            double theta = FastMath.atan2(yDist, xDist);
            a.forceX -= f * FastMath.cos(theta);
            a.forceY -= f * FastMath.sin(theta);
            b.forceX += f * FastMath.cos(theta);
            b.forceY += f * FastMath.sin(theta);
        }
        else
        {
            double xDist = a.x - b.x, yDist = a.y - b.y;
            double dist = FastMath.sqrt(xDist * xDist + yDist * yDist);

            if (dist < friendDistance)
                nodeRepulsion += 0.03 * stability;

            double f = nodeRepulsion / dist;
            double theta = FastMath.atan2(yDist, xDist);
            a.forceX += f * FastMath.cos(theta);
            a.forceY += f * FastMath.sin(theta);
            b.forceX -= f * FastMath.cos(theta);
            b.forceY -= f * FastMath.sin(theta);
        }
    }

    NodePair(Node a, Node b, boolean neighbor)
    {
        this.a = a;
        this.b = b;
        this.neighbor = neighbor;
    }
}