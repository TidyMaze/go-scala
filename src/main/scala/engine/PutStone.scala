package fr.yaro.go
package engine

import scala.util.Try

sealed trait Action

case object Pass extends Action {
  override def toString: String = "pass"
}

case class PutStone(coord: Coord) extends Action {
  override def toString: String = {
    val x = ('A' + coord.x).toChar.toUpper
    val y = coord.y + 1
    s"$x$y"
  }
}

object PutStone {

  // Parse a move from a string. Ex: "B3" => Move(Coord(1, 2)), "G13" => Move(Coord(6, 12))
  def parse(input: String): Try[PutStone] =
    Try {
      val x = input.charAt(0).toLower - 'a'
      val y = input.substring(1).toInt - 1
      PutStone(Coord(x, y))
    }

}
