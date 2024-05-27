package com.claver.games.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Sudoku {

  private final List<List<Cell>> rows;
  private final List<List<Cell>> columns;
  private final List<List<Cell>> squares;
  private final List<Cell> allCells;
  private final Random random;

  private static final Integer DEFAULT_DIFFICULTY = 1;
  private static final Integer BASE_PURGES = 10;
  private static final Integer MAX_TRANSFORMATIONS = 5;
  private static final Integer MAX_RETRIES = 10;
  private static final List<Integer> VALID_SUDOKU_VALUES = new ArrayList<>(Arrays.asList(
      1, 2, 3, 4, 5, 6, 7, 8, 9,
      4, 5, 6, 7, 8, 9, 1, 2, 3,
      7, 8, 9, 1, 2, 3, 4, 5, 6,
      2, 3, 4, 5, 6, 7, 8, 9, 1,
      5, 6, 7, 8, 9, 1, 2, 3, 4,
      8, 9, 1, 2, 3, 4, 5, 6, 7,
      3, 4, 5, 6, 7, 8, 9, 1, 2,
      6, 7, 8, 9, 1, 2, 3, 4, 5,
      9, 1, 2, 3, 4, 5, 6, 7, 8
  ));

  public Sudoku() {
    this(DEFAULT_DIFFICULTY);
  }

  public Sudoku(Integer difficulty) {
    rows = new ArrayList<>();
    columns = new ArrayList<>();
    squares = new ArrayList<>();
    allCells = new ArrayList<>();
    random = new Random();

    for (int i=0; i<9; i++) {
      rows.add(new ArrayList<>());
      columns.add(new ArrayList<>());
      squares.add(new ArrayList<>());
    }

    for (int i=0; i<81; i++) {
      int x = i/9;
      int y = i%9;
      int z = getSquareNumber(x, y);
      Cell cell = new Cell(x,y);
      allCells.add(cell);
      rows.get(x).add(cell);
      columns.get(y).add(cell);
      squares.get(z).add(cell);
    }

    setValidValues();
    transformSudoku();
    purgeSudoku(difficulty);
  }

  private void setValidValues() {
    if (VALID_SUDOKU_VALUES.size() > 81) {
      throw new IndexOutOfBoundsException();
    }
    for (int i=0; i<81; i++) {
      Integer value = i < VALID_SUDOKU_VALUES.size() ? VALID_SUDOKU_VALUES.get(i) : null;
      if (value != null && (value <= 0 || value > 9)) {
        throw new IllegalArgumentException();
      }
      allCells.get(i).setOriginalValue(value);
    }
  }

  private void transformSudoku() {
    // Transformations we can do:
    // 0- swap rows of same square
    // 1- swap columns of same square
    // 2- swap groups of 3 rows of two different squares
    // 3- swap groups of 3 columns of two different squares
    int numTransformations = random.nextInt(MAX_TRANSFORMATIONS) + 1;
    System.out.println("Transforming: " + numTransformations);
    for (int i = 0; i < numTransformations; i++) {
      int transformation = random.nextInt(4);
      System.out.println("Transformation " + i + ": " + transformation);
      if (transformation == 0) {
        swapLists(rows);
      } else if (transformation == 1) {
        swapLists(columns);
      } else if (transformation == 2) {
        swapGroupLists(rows);
      } else {
        swapGroupLists(columns);
      }
    }
  }

  private void swapLists(List<List<Cell>> list) {
    int group = random.nextInt(3);
    int index1 = group * random.nextInt(3);
    System.out.println("index1: " + index1);
    int index2;
    int retries = 0;
    do {
      index2 = group * random.nextInt(3);
      System.out.println("index2: " + index2);
      retries++;
    } while (index1 == index2 && retries < MAX_RETRIES);
    if (index1 == index2) {
      index2 = (index1 + 1) % 3;
      System.out.println("Max retries! index2: " + index2);
    }

    swapTwoBasicLists(list, index1, index2);
  }

  private void swapGroupLists(List<List<Cell>> list) {
    int group1 = random.nextInt(3);
    System.out.println("group1: " + group1);
    int group2;
    int retries = 0;
    do {
      group2 = random.nextInt(3);
      System.out.println("group2: " + group2);
      retries++;
    } while (group1 == group2 && retries < MAX_RETRIES);
    if (group1 == group2) {
      group2 = (group1 + 1) % 3;
      System.out.println("group2: " + group2);
    }
    for (int i = 0; i < 3; i++) {
      swapTwoBasicLists(list, 3*group1+i, 3*group2+i);
    }
  }

  private void swapTwoBasicLists(List<List<Cell>> list, int index1, int index2) {
    List<Cell> list1 = list.get(index1);
    List<Cell> list2 = list.get(index2);
    for (int i = 0; i < 9; i++) {
      Cell cell1 = list1.get(i);
      Cell cell2 = list2.get(i);
      Integer value1 = cell1.getValue();
      Integer value2 = cell2.getValue();
      cell1.setOriginalValue(value2);
      cell2.setOriginalValue(value1);
    }
  }

  private void purgeSudoku(Integer difficulty) {
    System.out.println("Purging: " + (BASE_PURGES * difficulty));
    for (int i = 0; i < (BASE_PURGES * difficulty); i++) {
      int nunRow = random.nextInt(9);
      int numColumn = random.nextInt(9);
      rows.get(nunRow).get(numColumn).setOriginal(false);
      rows.get(nunRow).get(numColumn).setValue(null);
    }
  }

  public void setValue(Position position, Integer value) {
    if (position.row < 0 || position.row >= 9
        || position.column < 0 || position.column >= 9
        || value <= 0 || value > 9) {
      throw new IndexOutOfBoundsException();
    }
    allCells.get(position.row * 9 + position.column).setValue(value);
  }

  public void reset() {
    for (Cell cell : allCells) {
      cell.setValue(null);
    }
  }

  public boolean isEmptyCell(int row, int column) {
    return row >= 0 && row < rows.size() && rows.get(row) != null
        && column >= 0 && column < rows.get(row).size() && rows.get(row).get(column) != null
        && rows.get(row).get(column).getValue() == null;
  }

  public boolean isComplete() {
    return allCells.stream().noneMatch(cell -> cell.getValue() == null);
  }

  public boolean isValidIncomplete() {
    for (Cell cell : allCells) {
      if (!isCellValid(cell)) {
        return false;
      }
    }
    return true;
  }

  public boolean isValidComplete() {
    if (!isComplete()) {
      return false;
    }
    for (int i=0; i<9; i++) {
      if (!(isCellListValid(rows.get(i)) && isCellListValid(columns.get(i)) && isCellListValid(squares.get(i)))) {
        return false;
      }
    }
    return true;
  }

  public boolean isCellValid(Cell cell) {
    if (cell.getValue() == null) {
      return true;
    }
    if (cell.getValue() < 1 || cell.getValue() > 9) {
      return false;
    }
    return isCellValidInRow(cell) && isCellValidInColumn(cell) && isCellValidInSquare(cell);
  }

  public String printCells() {
    StringBuilder result = new StringBuilder("All Cells:\n");
    for (Cell cell : allCells) {
      result.append(cell.toString()).append(" ");
    }
    return result.toString();
  }

  public String printValues() {
    StringBuilder result = new StringBuilder();
    result.append("      0     1     2      3     4     5      6     7     8");
    for (List<Cell> row : rows) {
      if (rows.indexOf(row)%3 == 0) {
        result.append("\n    ===== ===== =====  ===== ===== =====  ===== ===== =====\n");
      } else {
        result.append("\n    ----- ----- -----  ----- ----- -----  ----- ----- -----\n");
      }
      for (Cell cell : row) {
        String value = cell.getValue() != null ? cell.getValue().toString() : " ";
        if (row.indexOf(cell) == 0) {
          result.append(rows.indexOf(row)).append(" ");
        }
        if (row.indexOf(cell)%3 == 0) {
          result.append("||  ");
        } else {
          result.append("|  ");
        }
        result.append(value).append("  ");
      }
      result.append("||");
    }
    result.append("\n    ===== ===== =====  ===== ===== =====  ===== ===== =====\n");
    return result.toString();
  }

  public String printAll() {
    return printCells()
        + printCellsList(rows, "Row")
        + printCellsList(columns, "Column")
        + printCellsList(squares, "Square")
        + printValues();
  }

  @Override
  public String toString() {
    return printValues();
  }

  private String printCellsList(List<List<Cell>> cellsList, String word) {
    StringBuilder result = new StringBuilder(word).append("s:\n");
    for (List<Cell> cells : cellsList) {
      result.append(word).append(" #").append(cellsList.indexOf(cells)).append(": [ ");
      for (Cell cell : cells) {
        result.append(cell).append(" ");
      }
      result.append("]\n");
    }
    return result.toString();
  }

  // Helpers

  private int getSquareNumber(int x, int y) {
    return (x/3)*3 + y/3;
  }

  private boolean isCellValidInRow(Cell cell) {
    return isCellListValid(rows.get(cell.getRow()));
  }

  private boolean isCellValidInColumn(Cell cell) {
    return isCellListValid(columns.get(cell.getColumn()));
  }

  private boolean isCellValidInSquare(Cell cell) {
    return isCellListValid(squares.get(getSquareNumber(cell.getRow(), cell.getColumn())));
  }

  private boolean isCellListValid(List<Cell> cellList) {
    Set<Integer> differentValues = new HashSet<>();
    if (cellList != null && cellList.size() == 9) {
      for (Cell cell : cellList) {
        if (cell.getValue() != null && cell.getValue() >= 1 && cell.getValue() <= 9) {
          if (differentValues.contains(cell.getValue())) {
            return false;
          } else {
            differentValues.add(cell.getValue());
          }
        }
      }
    }
    return true;
  }

}
