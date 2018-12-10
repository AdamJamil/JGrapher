package sample;

import javafx.scene.paint.Color;

class MiniEdge
{
    MiniNode a, b;
    Color color = Color.BLACK;

    MiniEdge(MiniNode a, MiniNode b)
    {
        this.a = a;
        this.b = b;
    }
}
