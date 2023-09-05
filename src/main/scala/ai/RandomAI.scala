package fr.yaro.go
package ai

import engine.{GoGame, Move, State}
import helpers.RandomHelpers.randomIn

class RandomAI extends AI {
  def findBestMove(game: GoGame, state: State): Option[Move] = {
    val moves = game.getValidMoves(state)
    if (moves.isEmpty) None
    else Some(randomIn(moves))
  }
}
