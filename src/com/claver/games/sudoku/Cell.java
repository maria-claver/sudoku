package com.claver.games.sudoku;

public class Cell {

  public final Position position;
  private Integer value;
  private boolean original;

  public Cell(Integer row, Integer column) {
    this(new Position(row, column));
  }

  public Cell(Position position) {
    this.position = position;
    this.value = null;
    this.original = true;
  }

  public Position getPosition() {
    return position;
  }
  public Integer getRow() {
    return position.row;
  }
  public Integer getColumn() {
    return position.column;
  }
  public Integer getValue() {
    return value;
  }
  public boolean isOriginal() {
    return original;
  }

  public void setOriginal(boolean original) {
    this.original = original;
  }

  public void setOriginalValue(Integer value) {
    this.value = value;
  }

  public void setValue(Integer value) {
    if (!original) {
      this.value = value;
    }
  }

  public String toString() {
    if (isOriginal()) {
      return "<" + getValue() + ">";
    } else {
      return " " + getValue() + " ";
    }
  }
}
