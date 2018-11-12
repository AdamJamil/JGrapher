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

import java.util.ArrayList;

public class Main extends Application
{
    static double width = 500;
    static double height = 500;
    static double dt = 0.1;

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

        Graph graph = new Graph(7);

        for (int i = 0; i < 7; i++)
            graph.nodes.add(new Node());

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
        
        graph.split();

        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(true);

        gc.setFill(Color.BLACK);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0.5), event ->
        {
            gc.clearRect(0, 0, width, height);

            moveGraph(graph);
            drawGraph(gc, graph);
        }));

        scene.setOnKeyPressed(event ->
        {
            if (event.getCode() == KeyCode.SPACE)
                graph.split();
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
        for (Node n : node)
        {
            if (graph.split)
            {
                for (MiniNode miniNode : n.miniNodes)
                {
                    gc.fillOval(miniNode.x - 4, miniNode.y - 4, 4 * 2, 4 * 2);

                    gc.strokeLine(miniNode.x, miniNode.y, miniNode.friend.x, miniNode.friend.y);
                }
            }
            else
            {
                double size = (Math.sqrt(n.neighbors.size() * 2)) + 4;
                gc.fillOval(n.x - size, n.y - size, size * 2, size * 2);

                for (Node neighbor : n.neighbors)
                    gc.strokeLine(n.x, n.y, neighbor.x, neighbor.y);
            }
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
