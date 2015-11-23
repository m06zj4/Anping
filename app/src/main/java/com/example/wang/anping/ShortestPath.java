package com.example.wang.anping;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wang on 6/22/15.
 */

public class ShortestPath {
    private final int V;  // Number of vertices in the graph
    private final int INFINITY = 999999999;
    private final int BRANCH = 4;

    private int nodeId = 0;

    private boolean showDetail = false;
    private int[] mPath = null;

    public FloydWarshell floydWarshell;
    public Dijkstra dijkstra;
    public vertex[] graph;
    public int[][] adjMatrix, distMatrix;
    public String TextOut = "";

    public class edge {
        public int id;
        public int weight;
    }

    public ShortestPath(int node) {
        V = node;
        adjMatrix = new int[V][V];
        distMatrix = new int[V][V];
        floydWarshell = new FloydWarshell();
        dijkstra = new Dijkstra();
        graph = new vertex[V];
        for (int i = 0; i < V; i++) {
            graph[i] = new vertex();
        }
    }

    public class vertex {
        public int id;
        public int edgeNumber;
        public edge[] adjacent = new edge[BRANCH];

        public vertex() {
            this.id = nodeId++;
            this.edgeNumber = 0;


            for (int i = 0; i < BRANCH; i++) {
                this.adjacent[i] = new edge();
                this.adjacent[i].id = this.id;
                this.adjacent[edgeNumber].weight = 0;
            }
        }

        public void adjacentEdge(int id, int weight) {
            if (edgeNumber < BRANCH) {
                this.adjacent[edgeNumber].id = id;
                this.adjacent[edgeNumber].weight = weight;
            } else {
                Log.e("ShortestPath", "ADJACENTEDGE ERROR");
                Log.e("ShortestPath", "Edge " + this.id + "->" + id + " " + weight + " ");
            }
            this.edgeNumber++;
        }
    }

    public class FloydWarshell {
        public void calculateDistance() {
            for (int i = 0; i < V; i++)
                for (int j = 0; j < V; j++)
                    distMatrix[i][j] = adjMatrix[i][j];

            for (int k = 0; k < V; k++) {
                for (int i = 0; i < V; i++) {     // Pick all vertices as source one by one
                    for (int j = 0; j < V; j++) { // Pick all vertices as destination for the above picked source
                        // If vertex k is on the shortest path from i to j, then update the value of distMatrix[i][j]
                        if (distMatrix[i][k] != INFINITY && distMatrix[k][j] != INFINITY && distMatrix[i][k] + distMatrix[k][j] < distMatrix[i][j])
                            distMatrix[i][j] = distMatrix[i][k] + distMatrix[k][j];
                    }
                }
            }
        }

        public void output() {
            TextOut += "SHORTEST DISTANCES MATRIX" + "\n";
            printMatrix(distMatrix);
        }
    }

    public class Dijkstra {
        private int[] predecessor = new int[V];
        private int[] distance = new int[V];
        private boolean[] mark = new boolean[V]; //keep track of visited node
        private int source;
        private int destination;

        private void specify_source(int source) {
            int min = 0;
            int max = V - 1;

            if (showDetail) {
                TextOut += "Enter the source vertex [" + min + "-" + max + "] ";
            }
            this.source = read(min, max, source);
        }

        private void specify_destination(int destination) {
            int min = 0;
            int max = V - 1;

            if (showDetail) {
                TextOut += "Enter the destination vertex [" + min + "-" + max + "] ";
            }
            this.destination = read(min, max, destination);
        }

        private int read(int min, int max, int in) {

            if (showDetail) {
                TextOut += in + "\n";
            }

            if (in >= min && in <= max) {
                return in;
            } else {
                Log.e("ShortestPath", "INPUT ERROR");
                return min;
            }
        }

        private void initialize() {
            for (int i = 0; i < V; i++) {
                mark[i] = false;
                predecessor[i] = -1;
                distance[i] = INFINITY;
            }
            distance[source] = 0;
        }

        private int getClosestUnmarkedNode() {
            int minDistance = INFINITY;
            int closestUnmarkedNode = 0;
            for (int i = 0; i < V; i++) {
                if ((!mark[i]) && (minDistance >= distance[i])) {
                    minDistance = distance[i];
                    closestUnmarkedNode = i;
                }
            }
            return closestUnmarkedNode;
        }

        public void calculateDistance(int in_source) {
            this.specify_source(in_source);
            this.initialize();
            int closestUnmarkedNode;
            int count = 0;

            while (count < V) {
                closestUnmarkedNode = getClosestUnmarkedNode();
                mark[closestUnmarkedNode] = true;
                for (int i = 0; i < V; i++) {
                    if ((!mark[i]) && (adjMatrix[closestUnmarkedNode][i] > 0)) {
                        if (distance[i] > distance[closestUnmarkedNode] + adjMatrix[closestUnmarkedNode][i]) {
                            distance[i] = distance[closestUnmarkedNode] + adjMatrix[closestUnmarkedNode][i];
                            predecessor[i] = closestUnmarkedNode;
                        }
                    }
                }
                count++;
            }
        }

        public void output(int option, int in_destination) {
            int min = 1;
            int max = 2;

            if (showDetail) {
                TextOut += "[1] Path to all vertex" + "\n";
                TextOut += "[2] Path to the specify destination" + "\n";
                TextOut += "Enter the output options [" + min + "-" + max + "] ";
            }

            switch (this.read(min, max, option)) {
                case 2:
                    this.output_pathToDestination(in_destination);
                    break;
                default:
                    this.output_pathToAllVertex();
            }
        }

        public void output_pathToAllVertex() {
            for (int destination = 0; destination < V; destination++) {
                if (destination == source)
                    TextOut += source + "->" + destination;
                else
                    printPath(destination, 0);
                TextOut += " " + distance[destination] + "\n";
            }
        }

        public void output_pathToDestination(int in_destination) {
            this.specify_destination(in_destination);
            if (destination == source)
                TextOut += source + "->" + destination;
            else
                printPath(destination, 0);
            TextOut += " " + distance[destination] + "\n";
        }

        private void printPath(int node, int count) {
            if (node == source) {
                TextOut += String.valueOf(node);
                mPath = new int[count + 1];
                mPath[count] = node;
            } else if (predecessor[node] == -1) {
                TextOut += "No path from " + source + " to " + node + "\n";
                mPath = null;
            } else {
                printPath(predecessor[node], count + 1);
                TextOut += "->" + node;
                mPath[count] = node;
            }
        }

        public int[] getPath() {

            return mPath;
        }

    }

    private void printMatrix(int inDistMatrix[][]) {
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (inDistMatrix[i][j] == INFINITY)
                    TextOut += "INF" + "\t";
                else
                    TextOut += inDistMatrix[i][j] + "\t";
            }
            TextOut += "\n";
        }
        TextOut += "\n";
    }

    public void initialMatrix() {
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                adjMatrix[i][j] = (i == j) ? 0 : INFINITY;
            }
        }

        for (int i = 0; i < V; i++) {
            for (int j = 0; j < graph[i].edgeNumber; j++) {
                adjMatrix[i][graph[i].adjacent[j].id] = graph[i].adjacent[j].weight;
            }
        }

        if (showDetail) {
            TextOut += "ADJACENT MATRIX" + "\n";
            printMatrix(adjMatrix);
        }
    }

    public void setMatrixWithJson(String json) {
        if (json == null) {
            return;
        }

        try {
            JSONObject jsonData = new JSONObject(json);
            JSONArray algorithm = jsonData.getJSONArray("algorithm");

            for (int i = 0; i < V; i++) {
                JSONObject nowNode = algorithm.getJSONObject(i);
                JSONArray neighbor = nowNode.getJSONArray("neighbor");
                JSONArray cost = nowNode.getJSONArray("cost");
                int now = nowNode.getInt("this");

                for (int j = 0; j < neighbor.length(); j++) {
                    this.graph[now].adjacentEdge(neighbor.getInt(j), cost.getInt(j));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.initialMatrix();
    }
}
