package sample;

import java.util.ArrayList;

class MiniNode
{
    double x, vx;
    double y, vy;
    MiniNode friend;

    ArrayList<MiniNode> neighbors = new ArrayList<>();
}