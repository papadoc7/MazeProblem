package mazeproblem;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MazeProblem {
    private Node [][] mazeMatrix;
    private String inputFile;
    private Dimension startPoint, endPoint, mazeDimension;
    private boolean mazeSolved;

    private enum NodeType {
        NODE_START, NODE_END, NODE_PASSAGE, NODE_PATH, NODE_WALL
    }

    private static final String STRING_WALL = "#";
    private static final String STRING_START = "S";
    private static final String STRING_END = "E";
    private static final String STRING_PATH = "X";
    private static final String STRING_PASSAGE = " ";
    private static final String STRING_NO_SOLUTION = "No solution is possible";


    public static void main (String [] args) throws IOException {
        for (String arg : args) {
            long startTime = System.currentTimeMillis();

            MazeProblem mazeProblem = new MazeProblem();
            mazeProblem.setInputFile(arg);
            mazeProblem.consumeInput();
            mazeProblem.breadthFirstSearch();
            mazeProblem.printSolvedMaze();

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("total time: " + totalTime + "ms");
        }
    }


    /**
     * Class which models field in the maze.
     */
    private class Node {
        public int distance;
        public Node parent;
        public MazeProblem.NodeType type;
        private final Dimension dimension = new Dimension();

        public Node(NodeType type) {
            this.type = type;

            distance = -1;
            parent = null;
        }

        public void setWidth(int position) {
            this.dimension.width = position;
        }

        public int getWidth() {
            return this.dimension.width;
        }

        public void setHeight(int position) {
            this.dimension.height = position;
        }

        public int getHeight() {
            return this.dimension.height;
        }
    }


    /**
     * Consume input file and process it.
     * @throws IOException
     */
    public void consumeInput() throws IOException{
        FileReader fileReader = new FileReader(inputFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        int lineNo = 0;
        int matrixLine = 0;
        String line = bufferedReader.readLine();

        while (line != null) {
            if (lineNo == 0) {
                setMazeDimension(line);
            } else if (lineNo == 1) {
                setStartPoint(line);
            } else if (lineNo == 2) {
                setEndPoint(line);
            } else {
                lineToNode(line, matrixLine);
                matrixLine++;
            }

            line = bufferedReader.readLine();
            lineNo++;
        }

        bufferedReader.close();
    }


    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }


    /**
     * Consume input and set start point coordinates.
     * @param startLine
     */
    private void setStartPoint(String startLine) {
        String[] dimension = startLine.split("\\s+");
        // height x width
        startPoint = new Dimension( Integer.parseInt(dimension[1]), Integer.parseInt(dimension[0]) );
    }


    /**
     * Consume input and set end point coordinates.
     * @param endLine
     */
    private void setEndPoint(String endLine) {
        String[] dimension = endLine.split("\\s+");
        // height x width:
        endPoint = new Dimension( Integer.parseInt(dimension[1]), Integer.parseInt(dimension[0]) );
    }


    /**
     * Consume input line and set maze dimension.
     * @param line
     */
    private void setMazeDimension(String line) {
        String[] dimension = line.split("\\s+");
        // height x width
        mazeDimension = new Dimension( Integer.parseInt(dimension[1]), Integer.parseInt(dimension[0]) );

        mazeMatrix = new Node[mazeDimension.height][mazeDimension.width];
    }


    /**
     * Consume input line of maze matrix and convert it to nodes.
     * @param line
     * @param matrixLine
     */
    private void lineToNode(String line, int matrixLine) {
        if (matrixLine < mazeDimension.height) {
            String[] nodesLine = line.split("\\s+");

            for (int matrixColumn = 0; matrixColumn < nodesLine.length; matrixColumn++) {
                Node node;
                Dimension currentPosition = new Dimension(matrixColumn, matrixLine);

                if ( currentPosition.equals(startPoint) ) {
                    node = new Node(NodeType.NODE_START);
                } else if ( currentPosition.equals(endPoint) ) {
                    node = new Node(NodeType.NODE_END);
                } else {
                    int matrixValue = Integer.parseInt( nodesLine[matrixColumn] );

                    switch (matrixValue) {
                        case 0:
                            node = new Node(NodeType.NODE_PASSAGE);
                            break;
                        case 1: default:
                            node = new Node(NodeType.NODE_WALL);
                    }
                }

                node.setHeight(matrixLine);
                node.setWidth(matrixColumn);
                mazeMatrix[matrixLine][matrixColumn] = node;
            }
        }
    }


    /**
     * Find End node using Breadth First Search(BFS) Algorithm.
     */
    public void breadthFirstSearch() {
        Queue<Node> queue = new LinkedList<>();
        Node start = mazeMatrix[startPoint.height][startPoint.width];

        start.distance = 0;
        queue.add(start);

        while ( !queue.isEmpty() ) {
            Node currentNode = queue.remove();

            List<Node> adjacentNodes = getAdjacentNodes(currentNode);

            adjacentNodes.stream().filter(adjacentNode -> adjacentNode.distance < 0).forEach(adjacentNode -> {
                adjacentNode.distance = currentNode.distance + 1;
                adjacentNode.parent = currentNode;
                queue.add(adjacentNode);

                if (adjacentNode.type == NodeType.NODE_END) {
                    queue.clear();
                    mazeSolved = true;
                    setPath();
                }
            });
        }
    }


    /**
     * @param node
     * @return ArrayList of adjacent nodes
     */
    private List<Node> getAdjacentNodes(Node node) {
        List<Node> adjacentNodes = new ArrayList<>();

        // get N node
        if (node.getHeight() > 0) {
            Node adjacentNodeN = mazeMatrix[node.getHeight() - 1][node.getWidth()];
            if (adjacentNodeN.type != NodeType.NODE_WALL) {
                adjacentNodes.add(adjacentNodeN);
            }
        }
        // get W node
        if (node.getWidth() < mazeDimension.width - 1) {
            Node adjacentNodeW = mazeMatrix[node.getHeight()][node.getWidth() + 1];
            if (adjacentNodeW.type != NodeType.NODE_WALL) {
                adjacentNodes.add(adjacentNodeW);
            }
        }
        // get S node
        if (node.getHeight() < mazeDimension.height - 1) {
            Node adjacentNodeS = mazeMatrix[node.getHeight() + 1][node.getWidth()];
            if (adjacentNodeS.type != NodeType.NODE_WALL) {
                adjacentNodes.add(adjacentNodeS);
            }
        }
        // get E node
        if (node.getWidth() > 0) {
            Node adjacentNodeE = mazeMatrix[node.getHeight()][node.getWidth() - 1];
            if (adjacentNodeE.type != NodeType.NODE_WALL) {
                adjacentNodes.add(adjacentNodeE);
            }
        }

        return adjacentNodes;
    }


    private void setPath() {
        Node node = mazeMatrix[endPoint.height][endPoint.width];

        while (node.parent.type != NodeType.NODE_START) {
            if (node.parent.type == NodeType.NODE_PASSAGE) {
                node.parent.type = NodeType.NODE_PATH;
                node = node.parent;
            }
        }
    }


    /**
     * Outputs solution to stdout.
     */
    public void printSolvedMaze() {
        if (mazeSolved) {
            for (Node[] nodeLine : mazeMatrix) {
                String line = "";

                for (Node node : nodeLine) {
                    switch (node.type) {
                        case NODE_WALL:
                            line += STRING_WALL;
                            break;
                        case NODE_START:
                            line += STRING_START;
                            break;
                        case NODE_PATH:
                            line += STRING_PATH;
                            break;
                        case NODE_PASSAGE:
                            line += STRING_PASSAGE;
                            break;
                        case NODE_END:
                            line += STRING_END;
                            break;
                        default:
                            break;
                    }
                }

                System.out.println(line);
            }
        } else {
            System.out.println(STRING_NO_SOLUTION);
        }
    }
}