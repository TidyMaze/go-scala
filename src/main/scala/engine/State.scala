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
  def countCaptured(color: Color): Int = captured.getOrElse(color, 0)

  def countStones(color: Color): Int =
    grid.grid.flatten.count(_.contains(color))

  def at(coord: Coord): Option[Color] = grid(coord)

  def gridSize: Int = grid.size
}
