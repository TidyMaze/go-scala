package fr.yaro.go

import scala.util.{Failure, Try}

case class State(
    grid: Grid,
    turn: Color,
    captured: Map[Color, Int],
    passed: Map[Color, Boolean],
    alreadyPlayedStates: Set[Grid]
) {
  def at(coord: Coord): Option[Color] = {
    assert(coord.x >= 0 && coord.x < grid.size)
    assert(coord.y >= 0 && coord.y < grid.size)
    grid(coord)
  }

  def gridSize: Int = grid.size
}
