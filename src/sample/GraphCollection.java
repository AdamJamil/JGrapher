package sample;

public class GraphCollection
{
    static Graph graph1()
    {
        Graph graph = new Graph(6);

        graph.linkBoth(0, 1);
        graph.linkBoth(0, 2);
        graph.linkBoth(1, 2);
        graph.linkBoth(1, 3);
        graph.linkBoth(1, 5);
        graph.linkBoth(3, 4);

        graph.makePairs();

        return graph;
    }

    static Graph graph2()
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

        graph.makePairs();

        return graph;
    }

    static Graph graph3()
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

        graph.makePairs();

        return graph;
    }

    static Graph graph4()
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

        graph.makePairs();

        return graph;
    }

    static Graph graph5()
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

        graph.makePairs();

        return graph;
    }

    static Graph graph6()
    {
        Graph graph = new Graph(3);

        graph.linkBoth(0, 1);
        graph.linkBoth(1, 2);

        graph.makePairs();

        return graph;
    }

    static Graph fakeCounterexample()
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

        graph.makePairs();

        return graph;
    }

    static Graph counterexample()
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

        graph.makePairs();

        return graph;
    }

    static Graph test()
    {
        Graph graph = new Graph(10);

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
        graph.linkBoth(1, 9);
        graph.linkBoth(9, 6);

        graph.makePairs();

        return graph;
    }
}
