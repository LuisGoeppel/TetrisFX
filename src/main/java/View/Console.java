package View;

import Model.Field;
import Model.TetrisField;

import java.util.Scanner;

public class Console {

    public static void main(String[] args) {
        playGame();
    }

    public static void playGame() {
        Scanner scanner = new Scanner(System.in);
        TetrisField field = new Field();
        boolean play = true;
        System.out.println("The game starts!");
        field.startGame();
        System.out.println("Enter n to simulate a gameTick, l to move left or r to move right");
        System.out.println("You can also enter d to move the current Shape down or t to turn the shape");
        System.out.println();
        System.out.println(field);
        System.out.println();
        while (play) {
            String input = scanner.nextLine();
            if (input.contains("n")) {
                if (input.length() == 1) {
                    field.gameTick();
                    System.out.println(field);
                    System.out.println();
                } else {
                    String number = input.substring(2);
                    try {
                        int n = Integer.parseInt(number);
                        for (int i = 0; i < n; i++) {
                            field.gameTick();
                        }
                        System.out.println(field);
                        System.out.println();
                    } catch (Exception e) {
                        System.out.println("Problem with number converting");
                    }
                }

            } else if (input.equals("l")) {
                field.moveLeft();
                System.out.println(field);
                System.out.println();
            } else if (input.equals("r")) {
                field.moveRight();
                System.out.println(field);
                System.out.println();
            } else if (input.equals("t")) {
                field.turnActiveShape();
                System.out.println(field);
                System.out.println();
            } else if (input.equals("d")) {
                while (!field.gameTick());
                System.out.println(field);
                System.out.println();
            } else if (input.equals("s")) {
                System.out.println(field.getNextShape());
                System.out.println();
            } else if (input.equals("q")) {
                play = false;
            }
        }
    }
}
