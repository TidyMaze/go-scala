package fr.yaro.go
package engine

import engine.Coord

import scala.util.Try

case class Move(coord: Coord) {
  override def toString: String = {
    val x = ('A' + coord.x).toChar.toUpper
    val y = coord.y + 1
    s"$x$y"
  }
}

object Move {

  // Parse a move from a string. Ex: "B3" => Move(Coord(1, 2))
  def parse(input: String): Try[Move] = {
    val parts = input.split("")
    if (parts.length == 2) {
      val x = parts(0).toUpperCase()(0) - 'A'
      val y = parts(1).toInt - 1
      Try(Move(Coord(x, y)))
    } else {
      Try(throw new Exception(s"Invalid move: $input"))
    }
  }

}
