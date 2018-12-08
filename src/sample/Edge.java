package sample;

import javafx.scene.paint.Color;

class Edge
{
    Node a, b;
    Color color = Color.BLACK;

    Edge(Node a, Node b)
    {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString()
    {
        return a + "" + b;
    }
}