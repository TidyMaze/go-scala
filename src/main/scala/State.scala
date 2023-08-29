package fr.yaro.go

import scala.util.{Failure, Try}

class Grid(grid: Seq[Seq[Option[Color]]]) {
  def apply(coord: Coord): Option[Color] = {
    assert(coord.x >= 0 && coord.x < size)
    assert(coord.y >= 0 && coord.y < size)
    grid(coord.y)(coord.x)
  }

  def updated(c: Coord, color: Option[Color]): Grid = {
    assert(c.x >= 0 && c.x < size)
    assert(c.y >= 0 && c.y < size)
    Grid(grid.updated(c.y, grid(c.y).updated(c.x, color)))
  }

  def size: Int = grid.size
}

object Grid {
  def empty(size: Int): Grid = new Grid(
    Seq.fill[Option[Color]](size, size)(None)
  )
}

case class State(
    grid: Grid,
    turn: Color,
    captured: Map[Color, Int],
    passed: Map[Color, Boolean]
) {
  def at(coord: Coord): Option[Color] = {
    assert(coord.x >= 0 && coord.x < grid.size)
    assert(coord.y >= 0 && coord.y < grid.size)
    grid(coord)
  }

  def gridSize: Int = grid.size
}
