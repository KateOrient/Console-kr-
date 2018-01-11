import Console.Console;

import java.io.IOException;

public class Main{
    public static void main (String[] args) throws IOException{
        Console console = new Console();
        console.loadFromFile("input.txt");
        console.printSortedByLenght("output1.txt");
        console.printProcessDir("output2.txt");
    }
}
