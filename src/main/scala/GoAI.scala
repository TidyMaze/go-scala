package fr.yaro.go

import scala.util.Random

class GoAI {
  def findBestMove(game: GoGame, state: State): Option[Move] = {
    val moves = game.getValidMoves(state)
    if (moves.isEmpty) None
    else Some(randomIn(moves))
  }

  private def randomIn[A](arr: Seq[A]): A = arr(Random.nextInt(arr.length))
}
