package fr.yaro.go
package ai

import engine.{Action, Game, Pass, PutStone, State}
import helpers.RandomHelpers.randomIn

/** This AI plays a random move.
  */
class RandomAI extends AI {

  def name: String = "RandomAI"

  def findBestAction(game: Game, state: State): Action = {
    val moves = game.getValidPutStones(state)
    if (moves.isEmpty) Pass
    else randomIn(moves)
  }
}
