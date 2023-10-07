package fr.yaro.go
package ai

import engine.{Action, Game, Pass, PutStone, State}

import scala.util.Random

/** This AI plays the best killing move it finds.
  */
class BestKillingMoveAI extends AI {

  def name: String = "BestKillingMoveAI"

  def findBestAction(game: Game, state: State): Action = {
    val moves = game.getValidPutStones(state)

    val shuffled = Random.shuffle(moves)

    shuffled
      .map(m => (m, game.getKilledGroups(state, m.coord).flatten.size))
      .maxByOption(_._2)
      .map(_._1)
      .getOrElse(Pass)
  }
}
