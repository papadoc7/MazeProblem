package test;

import mazeproblem.MazeProblem;

import java.io.IOException;

public class Tests {
    public static void main (String [] args) throws IOException {
        MazeProblem mazeProblem = new MazeProblem();
        mazeProblem.setInputFile("input/fileName.txt");
        mazeProblem.consumeInput();
        mazeProblem.breadthFirstSearch();
        mazeProblem.printSolvedMaze();
    }
}