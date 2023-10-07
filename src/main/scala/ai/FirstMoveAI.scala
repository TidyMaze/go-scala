package fr.yaro.go
package ai

import engine.{Game, Move, State}

/** This AI plays the first move it finds.
  */
class FirstMoveAI extends AI {

  def name: String = "FirstMoveAI"
  def findBestMove(game: Game, state: State): Option[Move] = {
    val moves = game.getValidMoves(state)
    moves.headOption
  }
}
