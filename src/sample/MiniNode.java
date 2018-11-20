package sample;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class MiniNode
{
    double x, vx;
    double y, vy;
    MiniNode friend;
    Node parent;
    int number = -1;

    ArrayList<MiniNode> neighbors = new ArrayList<>();
}
