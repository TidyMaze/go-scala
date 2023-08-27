package fr.yaro.go

import scala.util.{Failure, Try}

type Grid = Seq[Seq[Option[Color]]]

case class State (grid: Grid, turn: Color, captured: Map[Color, Int], passed: Map[Color, Boolean]) {
  def at(coord: Coord): Option[Color] = {
    assert(coord.x >= 0 && coord.x < grid.size)
    assert(coord.y >= 0 && coord.y < grid.size)
    grid(coord.y)(coord.x)
  }
}