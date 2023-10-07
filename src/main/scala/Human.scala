package fr.yaro.go
import engine.{Action, Game, Pass, PutStone, State}

import scala.util.{Failure, Success}

class Human extends Player {

  def name: String = "Human"

  override def play(game: Game, state: State): Action = {
    println("Enter your move:")
    val input = scala.io.StdIn.readLine()
    val move = PutStone.parse(input)

    val validMoves = game.getValidPutStones(state)

    if (validMoves.isEmpty) {
      println("No valid moves")
      Pass
    } else {
      move
        .map {
          case m if !validMoves.contains(m) =>
            println(s"Invalid move: $m, valid moves are: $validMoves")
            play(game, state)
          case m =>
            m
        }
        .getOrElse {
          println(s"Invalid move format: $input")
          play(game, state)
        }
    }
  }
}
