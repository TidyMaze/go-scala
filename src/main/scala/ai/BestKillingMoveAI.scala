package fr.yaro.go
package ai

import engine.{Game, Move, State}

import scala.util.Random

/** This AI plays the best killing move it finds.
  */
class BestKillingMoveAI extends AI {

  def name: String = "BestKillingMoveAI"

  def findBestMove(game: Game, state: State): Option[Move] = {
    val moves = game.getValidMoves(state)

    val shuffled = Random.shuffle(moves)

    shuffled
      .map(m => (m, game.getKilledGroups(state, m.coord).flatten.size))
      .maxByOption(_._2)
      .map(_._1)
  }
}
