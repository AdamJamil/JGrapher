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
    boolean fast = false;
    static double width = 500;
    static double height = 500;
    static double dt = 0.02;
    static ArrayList<Graph> graphs = new ArrayList<>();
    Graph graph = GraphCollection.test();

    Node selectedNode = null;
    boolean RMBHeld = false;
    
    int graphIndex = 0;

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
        graphs.get(graphIndex).partition(2);

        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(true);

        gc.setFill(Color.BLACK);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0.1), event ->
        {
            gc.clearRect(0, 0, width, height);

            if (RMBHeld)
                rotate();

            graphs.get(graphIndex).stable = false;

            while (fast && !graphs.get(graphIndex).stable)
                moveGraph(graphs.get(graphIndex));
            moveGraph(graphs.get(graphIndex));
            moveGraph(graphs.get(graphIndex));
            moveGraph(graphs.get(graphIndex));
            moveGraph(graphs.get(graphIndex));
            moveGraph(graphs.get(graphIndex));
            moveGraph(graphs.get(graphIndex));

            drawGraph(gc, graphs.get(graphIndex));
        }));

        scene.setOnKeyPressed(event ->
        {
            Graph graph = graphs.get(graphIndex);

            if (event.getCode() == KeyCode.SPACE)
                if (!graph.split)
                    graph.split();
                else if (!graph.connect)
                    graph.connect();

            if (event.getCode() == KeyCode.W)
            {
                graphIndex++;
                graphIndex %= graphs.size();

                for (Node a : graphs.get(0).nodes)
                    for (Node b : graphs.get(graphIndex).nodes)
                        if (a.toString().equals(b.toString()))
                        {
                            b.x = a.x;
                            b.y = a.y;
                        }

                for (NodePair a : graphs.get(0).nodePairs)
                    for (NodePair b : graphs.get(graphIndex).nodePairs)
                        if ((a.a.toString().equals(b.a.toString()) && a.b.toString().equals(b.b.toString()) ||
                                (a.b.toString().equals(b.a.toString()) && a.a.toString().equals(b.b.toString()))))
                        {
                            b.friendConstant = a.friendConstant;
                            b.nodeRepulsion = a.nodeRepulsion;
                        }
            }
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
                    for (Node node : graph.nodes)
                    {
                        double dist = ((x - node.x) * (x - node.x)) + ((y - node.y) * (y - node.y));

                        if (dist < min)
                        {
                            selectedNode = node;
                            min = dist;
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
        ArrayList<Node> nodes = g.nodes;
        double avgvx = 0, avgvy = 0, avgx = 0, avgy = 0;

        double instability = 0;

        for (Node node : graphs.get(graphIndex).nodes)
            instability += node.forceX * node.forceX + node.forceY * node.forceY;

        double stability = 1 - Math.tanh(instability);

        for (NodePair nodePair : g.nodePairs)
            nodePair.computeForce(stability);

        for (Node n : nodes)
        {
            n.move();
            avgvx += n.vx;
            avgvy += n.vy;
            avgx += (n.x - (width / 2));
            avgy += (n.y - (height / 2));
        }

        avgvx /= nodes.size();
        avgvy /= nodes.size();
        avgx /= nodes.size();
        avgy /= nodes.size();

        g.stable = true;

        for (Node n : nodes)
        {
            n.vx -= avgvx;
            n.vy -= avgvy;
            n.x -= avgx;
            n.y -= avgy;
            g.stable &= (n.vx * n.vx + n.vy * n.vy) < 0.004;
            n.move();
        }

        boolean visible = true;

        for (Node node : graphs.get(graphIndex).nodes)
        {
            visible &= node.x > 30;
            visible &= node.x < width - 30;
            visible &= node.y > 30;
            visible &= node.y < height - 30;
        }

        if (!visible)
            NodePair.friendDistance *= 1 - (0.00004 * stability);
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
                    for (Edge edge : miniNode.parent.edges)
                        if (edge.a.miniNodes.contains(miniNode.friend) || edge.b.miniNodes.contains(miniNode.friend))
                        {
                            gc.setStroke(edge.color);
                            break;
                        }

                    gc.strokeLine(miniNode.getX(), miniNode.getY(), miniNode.friend.getX(), miniNode.friend.getY());
                }

                if (graph.connect)
                {
                    gc.setLineWidth(0.5);
                    for (MiniNode miniNode : n.miniNodes)
                        for (MiniNode neighbor : miniNode.neighbors)
                            gc.strokeLine(miniNode.getX(), miniNode.getY(), neighbor.getX(), neighbor.getY());
                }
            }
        }

        gc.setFill(Color.BLACK);

        for (Node n : node)
        {
            gc.setLineWidth(2);
            if (graph.split)
                for (MiniNode miniNode : n.miniNodes)
                    gc.fillOval(miniNode.getX() - 4, miniNode.getY() - 4, 4 * 2, 4 * 2);
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
                    graphics2D.drawOval((int) miniNode.getX() - 4, (int) miniNode.getY() - 4, 4 * 2, 4 * 2);
                    graphics2D.drawLine((int) miniNode.getX(), (int) miniNode.getY(), (int) miniNode.friend.getX(), (int) miniNode.friend.getY());
                }

                if (graph.connect)
                {
                    graphics2D.setStroke(thin);
                    for (MiniNode miniNode : n.miniNodes)
                        for (MiniNode neighbor : miniNode.neighbors)
                            graphics2D.drawLine((int) miniNode.getX(), (int) miniNode.getY(), (int) neighbor.getX(), (int) neighbor.getY());
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