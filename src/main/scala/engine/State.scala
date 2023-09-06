package fr.yaro.go
package engine

import engine.{Color, Coord, Grid}

import scala.util.{Failure, Try}

case class State(
    grid: Grid,
    turn: Color,
    captured: Map[Color, Int],
    passed: Map[Color, Boolean],
    alreadyPlayedStates: Set[Grid]
) {
  def at(coord: Coord): Option[Color] = grid(coord)

  def gridSize: Int = grid.size
}
