package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Main extends Application
{
    static double width = 500;
    static double height = 500;
    static double dt = 0.02;
    static ArrayList<Graph> graphs = new ArrayList<>();
    Graph graph = counterexample();

    Node selectedNode = null;
    boolean RMBHeld = false;

    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("JGrapher");
        Group root = new Group();
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        graphs.add(graph);
        graphs.get(0).partition(2);

        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(true);

        gc.setFill(Color.BLACK);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0.1), event ->
        {
            gc.clearRect(0, 0, width, height);

            if (RMBHeld)
                rotate();

            for (Graph graph : graphs)
            {
                moveGraph(graph);
                drawGraph(gc, graph);
            }
        }));

        scene.setOnKeyPressed(event ->
        {
            if (graphs.size() > 1)
                return;

            Graph graph = graphs.get(0);

            if (event.getCode() == KeyCode.SPACE)
                if (!graph.split)
                    graph.split();
                else if (!graph.connect)
                    graph.connect();
        });

        scene.setOnMousePressed(event ->
        {
            if (event.isSecondaryButtonDown())
                RMBHeld = true;

            double x = event.getX(), y = event.getY();

            if (selectedNode == null)
            {
                double min = 70;
                for (Graph graph : graphs)
                {
                    for (Node node : graph.nodes)
                    {
                        double dist = ((x - node.x) * (x - node.x)) + ((y - node.y) * (y - node.y));

                        if (dist < min)
                        {
                            selectedNode = node;
                            min = dist;
                        }
                    }
                }

                if (selectedNode == null)
                    return;

                selectedNode.x = x;
                selectedNode.y = y;
                selectedNode.selected = true;
            }
        });

        scene.setOnMouseDragged(event ->
        {
            if (selectedNode != null)
            {
                selectedNode.x = event.getX();
                selectedNode.y = event.getY();
            }
        });

        scene.setOnMouseReleased(event ->
        {
            RMBHeld = false;

            if (selectedNode != null)
                selectedNode.selected = false;
            selectedNode = null;
        });

        timeline.play();
    }

    void moveGraph(Graph g)
    {
        ArrayList<Node> node = g.nodes;
        double avgvx = 0, avgvy = 0, avgx = 0, avgy = 0;

        for (Node n : node)
        {
            n.computeForce(g);
            avgvx += n.vx;
            avgvy += n.vy;
            avgx += (n.x - (width / 2));
            avgy += (n.y - (height / 2));
        }

        avgvx /= node.size();
        avgvy /= node.size();
        avgx /= node.size();
        avgy /= node.size();

        for (Node n : node)
        {
            n.vx -= avgvx;
            n.vy -= avgvy;
            n.x -= avgx;
            n.y -= avgy;
            n.move();
        }
    }

    void drawGraph(GraphicsContext gc, Graph graph)
    {
        ArrayList<Node> node = graph.nodes;

        if (!graph.split)
            for (Edge edge : graph.edges)
            {
                gc.setStroke(edge.color);
                gc.strokeLine(edge.a.x, edge.a.y, edge.b.x, edge.b.y);
            }

        gc.setFill(Color.BLACK);

        for (Node n : node)
        {
            gc.setLineWidth(2);
            if (graph.split)
            {
                for (MiniNode miniNode : n.miniNodes)
                {
                    gc.fillOval(miniNode.x - 4, miniNode.y - 4, 4 * 2, 4 * 2);

                    gc.strokeLine(miniNode.x, miniNode.y, miniNode.friend.x, miniNode.friend.y);
                }

                if (graph.connect)
                {
                    gc.setLineWidth(0.5);
                    for (MiniNode miniNode : n.miniNodes)
                        for (MiniNode neighbor : miniNode.neighbors)
                            gc.strokeLine(miniNode.x, miniNode.y, neighbor.x, neighbor.y);
                }
            }
            else
            {
                gc.setFill(n.color);
                double size = (Math.sqrt(n.neighbors.size() * 2)) + 4;
                gc.fillOval(n.x - size, n.y - size, size * 2, size * 2);
            }
        }

        if (graph.split)
            gc.setFill(Color.BLACK);
        else
            gc.setFill(Color.WHITE);

        for (Node n : node)
            gc.fillText(n.toString(), n.x - 3, n.y + 3);
    }

    Graph graph1()
    {
        Graph graph = new Graph(6);

        graph.linkBoth(0, 1);
        graph.linkBoth(0, 2);
        graph.linkBoth(1, 2);
        graph.linkBoth(1, 3);
        graph.linkBoth(1, 5);
        graph.linkBoth(3, 4);

        return graph;
    }

    Graph graph2()
    {
        Graph graph = new Graph(8);

        graph.linkBoth(0, 1);
        graph.linkBoth(0, 2);
        graph.linkBoth(0, 3);
        graph.linkBoth(0, 4);
        graph.linkBoth(1, 2);
        graph.linkBoth(2, 5);
        graph.linkBoth(3, 4);
        graph.linkBoth(3, 7);
        graph.linkBoth(5, 6);
        graph.linkBoth(6, 7);

        return graph;
    }

    Graph graph3()
    {
        Graph graph = new Graph(7);

        graph.linkBoth(0, 6);
        graph.linkBoth(1, 6);
        graph.linkBoth(2, 6);
        graph.linkBoth(3, 6);
        graph.linkBoth(4, 6);
        graph.linkBoth(5, 6);
        graph.linkBoth(0, 1);
        graph.linkBoth(1, 2);
        graph.linkBoth(2, 3);
        graph.linkBoth(3, 4);
        graph.linkBoth(4, 5);
        graph.linkBoth(5, 0);

        return graph;
    }

    Graph graph4()
    {
        Graph graph = new Graph(7);

        graph.linkBoth(0, 1);
        graph.linkBoth(0, 2);
        graph.linkBoth(0, 3);
        graph.linkBoth(1, 2);
        graph.linkBoth(2, 3);
        graph.linkBoth(2, 4);
        graph.linkBoth(3, 5);
        graph.linkBoth(4, 6);
        graph.linkBoth(5, 6);

        return graph;
    }

    Graph graph5()
    {
        Graph graph = new Graph(10);

        graph.linkBoth(0, 1);
        graph.linkBoth(0, 2);
        graph.linkBoth(1, 2);
        graph.linkBoth(1, 3);
        graph.linkBoth(1, 4);
        graph.linkBoth(2, 4);
        graph.linkBoth(2, 5);
        graph.linkBoth(3, 4);
        graph.linkBoth(3, 6);
        graph.linkBoth(3, 7);
        graph.linkBoth(4, 5);
        graph.linkBoth(4, 7);
        graph.linkBoth(4, 8);
        graph.linkBoth(5, 8);
        graph.linkBoth(5, 9);
        graph.linkBoth(6, 7);
        graph.linkBoth(7, 8);
        graph.linkBoth(8, 9);

        return graph;
    }

    Graph graph6()
    {
        Graph graph = new Graph(3);

        graph.linkBoth(0, 1);
        graph.linkBoth(1, 2);

        return graph;
    }

    Graph fakeCounterexample()
    {
        Graph graph = new Graph(8);

        graph.linkBoth(0, 2);
        graph.linkBoth(0, 3);
        graph.linkBoth(0, 4);
        graph.linkBoth(0, 5);
        graph.linkBoth(0, 6);
        graph.linkBoth(0, 7);
        graph.linkBoth(1, 2);
        graph.linkBoth(1, 3);
        graph.linkBoth(1, 4);
        graph.linkBoth(1, 5);
        graph.linkBoth(1, 6);
        graph.linkBoth(1, 7);

        return graph;
    }

    Graph counterexample()
    {
        Graph graph = new Graph(9);

        graph.linkBoth(0, 1);
        graph.linkBoth(2, 1);
        graph.linkBoth(3, 1);
        graph.linkBoth(1, 4);
        graph.linkBoth(1, 5);
        graph.linkBoth(4, 6);
        graph.linkBoth(5, 6);
        graph.linkBoth(4, 7);
        graph.linkBoth(5, 7);
        graph.linkBoth(7, 8);

        return graph;
    }

    void rotate()
    {
        double rotate = Math.PI / 6000;

        for (Graph temp : graphs)
            for (Node node : temp.nodes)
            {
                double tempX = node.x - width / 2;
                double tempY = node.y - height / 2;
                node.x = Math.cos(rotate) * tempX - Math.sin(rotate) * tempY + width / 2;
                node.y = Math.sin(rotate) * tempX + Math.cos(rotate) * tempY + height / 2;
            }
    }

    static BufferedImage snapShotOf(Graph graph)
    {
        BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.fillRect(0, 0, 500, 500);

        ArrayList<Node> node = graph.nodes;

        graphics2D.setPaint(java.awt.Color.BLACK);

        BasicStroke thicc = new BasicStroke(2), thin = new BasicStroke(0.5f);

        for (Node n : node)
        {
            graphics2D.setStroke(thicc);

            if (graph.split)
            {
                for (MiniNode miniNode : n.miniNodes)
                {
                    graphics2D.drawOval((int) miniNode.x - 4, (int) miniNode.y - 4, 4 * 2, 4 * 2);
                    graphics2D.drawLine((int) miniNode.x, (int) miniNode.y, (int) miniNode.friend.x, (int) miniNode.friend.y);
                }

                if (graph.connect)
                {
                    graphics2D.setStroke(thin);
                    for (MiniNode miniNode : n.miniNodes)
                        for (MiniNode neighbor : miniNode.neighbors)
                            graphics2D.drawLine((int) miniNode.x, (int) miniNode.y, (int) neighbor.x, (int) neighbor.y);
                }
            }
            else
            {
                double size = (Math.sqrt(n.neighbors.size() * 2)) + 4;
                graphics2D.fillOval((int) (n.x - size), (int) (n.y - size), (int) (size * 2), (int) (size * 2));

                for (Node neighbor : n.neighbors)
                    graphics2D.drawLine((int) n.x, (int) n.y, (int) neighbor.x, (int) neighbor.y);
            }
        }

        if (graph.split)
            graphics2D.setColor(java.awt.Color.BLACK);
        else
            graphics2D.setColor(java.awt.Color.WHITE);

        for (Node n : node)
            graphics2D.drawString(n.toString(), (int) n.x - 3, (int) n.y + 3);

        return image;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}