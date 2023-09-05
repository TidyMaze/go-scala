package fr.yaro.go
package ai

import engine.{GoGame, Move, State}

/** This AI plays the first move it finds.
  */
class FirstMoveAI extends AI {
  def findBestMove(game: GoGame, state: State): Option[Move] = {
    val moves = game.getValidMoves(state)
    moves.headOption
  }
}
