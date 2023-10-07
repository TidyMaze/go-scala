package fr.yaro.go
import engine.{Game, Move, State}

import scala.util.{Failure, Success}

class Human extends Player {
  override def play(game: Game, state: State): Option[Move] = {
    println("Enter your move:")
    val input = scala.io.StdIn.readLine()
    val move = Move.parse(input)

    val validMoves = game.getValidMoves(state)

    if (validMoves.isEmpty) {
      println("No valid moves")
      None
    } else {
      move
        .map {
          case m if !validMoves.contains(m) =>
            println(s"Invalid move: $m, valid moves are: $validMoves")
            play(game, state)
          case m => Some(m)
        }
        .getOrElse {
          println(s"Invalid move format: $input")
          play(game, state)
        }
    }
  }
}
