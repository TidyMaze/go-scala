package fr.yaro.go
package engine

import engine.{Color, Coord}

class Grid(val grid: Seq[Seq[Option[Color]]]) {
  def apply(coord: Coord): Option[Color] = {
    assert(coord.x >= 0 && coord.x < size)
    assert(coord.y >= 0 && coord.y < size)
    grid(coord.y)(coord.x)
  }

  def updated(c: Coord, color: Option[Color]): Grid = {
    assert(c.x >= 0 && c.x < size)
    assert(c.y >= 0 && c.y < size)
    new Grid(grid.updated(c.y, grid(c.y).updated(c.x, color)))
  }

  def size: Int = grid.size

  private def canEqual(other: Any): Boolean = other.isInstanceOf[Grid]

  override def equals(other: Any): Boolean = other match {
    case that: Grid =>
      that.canEqual(this) &&
        grid == that.grid
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(grid)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString: String = {
    grid
      .map { row =>
        row
          .map {
            case Some(Black) => 'X'
            case Some(White) => 'O'
            case None        => '.'
          }
          .mkString("")
      }
      .mkString("\n")
  }
}

object Grid {

  def empty(size: Int): Grid = new Grid(
    Seq.fill[Option[Color]](size, size)(None)
  )

  def fromString(str: String): Grid = {
    val lines = str.split("\n")
    val grid = lines.map { line =>
      line.map {
        case '.' => None
        case 'X' => Some(Black)
        case 'O' => Some(White)
      }
    }
    new Grid(grid)
  }
}
