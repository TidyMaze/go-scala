package fr.yaro.go
package ai

import engine.{Action, Game, Pass, PutStone, State}

/** This AI plays the first move it finds.
  */
class FirstMoveAI extends AI {

  def name: String = "FirstMoveAI"
  def findBestAction(game: Game, state: State): Action = {
    val moves = game.getValidPutStones(state)
    moves.headOption
      .getOrElse(Pass)
  }
}
