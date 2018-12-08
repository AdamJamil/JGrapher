package sample;

import java.util.ArrayList;

class MiniNode
{
    double x, vx;
    double y, vy;
    MiniNode friend;
    Node parent;
    int number = -1;

    ArrayList<MiniNode> neighbors = new ArrayList<>();
}
