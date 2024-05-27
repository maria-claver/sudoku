package com.claver.games.sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SudokuMain {

  public static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) {
    System.out.println("**********************");
    System.out.println("* Welcome to Sudoku! *");
    System.out.println("**********************");

    try {
      play();
    } catch (IOException e) {
      System.out.println("Ooopsie! Error reading from terminal! :S\n" + e.getMessage());
    }
  }

  public static void play() throws IOException {
    System.out.println("> What difficulty would you like to play? (1 - low / 2 - medium / 3 - high)");
    Integer difficulty = readIntegerFromTerminal(1, 4);

    Sudoku sudoku = new Sudoku(difficulty);

    System.out.println("Let's play!");

    boolean finished = false;
    while (!finished) {
      System.out.println(sudoku);
      Position position = getCellToWrite(sudoku);
      writeToCell(sudoku, position);
      finished = checkStatus(sudoku);
    }

    System.out.println("Wanna play again (Y) or exit (any other key) ?");
    if (readBooleanFromTerminal("Y")) {
      play();
    } else {
      quit();
    }
  }

  private static Position getCellToWrite(Sudoku sudoku) throws IOException {
    Position position = null;
    while (position == null) {
      System.out.println("> Enter row of cell to write: ");
      Integer row = readIntegerFromTerminal(0, 9);
      System.out.println("> Enter column of cell to write: ");
      Integer column = readIntegerFromTerminal(0, 9);
      if (sudoku.isEmptyCell(row, column)) {
        position = new Position(row, column);
      } else {
        System.out.println("This is not a valid Cell to write to! Please try again:");
      }
    }
    return position;
  }

  private static void writeToCell(Sudoku sudoku, Position position) throws IOException {
    System.out.println("> Enter value to write: ");
    Integer value = readIntegerFromTerminal(1, 10);
    sudoku.setValue(position, value);
  }

  private static boolean checkStatus(Sudoku sudoku) throws IOException {
    if (sudoku.isComplete()) {
      if (sudoku.isValidComplete()) {
        System.out.println(sudoku);
        System.out.println("YAYYY!!! YOU WON! :D\n");
        return true;
      } else {
        System.out.println("Oooops... Looks like you were wrong somewhere! :S\n");
        System.out.println("Do you want to Reset the game (Y) or continue? ");
        if (readBooleanFromTerminal("Y")) {
          sudoku.reset();
        }
      }
    }
    return false;
  }

  private static Integer readIntegerFromTerminal(Integer minValue, Integer maxValue) throws IOException {
    Integer value = null;
    while (value == null) {
      try {
        String stringValue = reader.readLine();
        value = Integer.valueOf(stringValue);
        if (value < minValue) {
          System.out.println("Too small! Please, try again: ");
          value = null;
        } else if (value > maxValue) {
          System.out.println("Too big! Please, try again: ");
          value = null;
        }
      } catch (NumberFormatException e) {
        System.out.println("Sorry, didn't catch that, could you please try again?");
      }
    }
    return value;
  }

  private static boolean readBooleanFromTerminal(String key) throws IOException {
    try {
      String stringValue = reader.readLine();
      return stringValue.equalsIgnoreCase(key);
    } catch (IOException e) {
      System.out.println("ERROR reading from terminal: " + e.getMessage());
      throw e;
    }
  }

  private static void quit() {
    System.out.println("Thank you for playing with me! Bye!");
    System.out.println("             _ o . o _/¨           ");
    System.exit(0);
  }

}
