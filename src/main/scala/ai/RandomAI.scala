package fr.yaro.go
package ai

import engine.{Game, Move, State}
import helpers.RandomHelpers.randomIn

/** This AI plays a random move.
  */
class RandomAI extends AI {

  def name: String = "RandomAI"

  def findBestMove(game: Game, state: State): Option[Move] = {
    val moves = game.getValidMoves(state)
    if (moves.isEmpty) None
    else Some(randomIn(moves))
  }
}
