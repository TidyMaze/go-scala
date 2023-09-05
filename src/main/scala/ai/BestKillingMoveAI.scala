package fr.yaro.go
package ai

import engine.{Game, Move, State}

import scala.util.Random

/** This AI plays the best killing move it finds.
  */
class BestKillingMoveAI extends AI {
  def findBestMove(game: Game, state: State): Option[Move] = {
    val moves = game.getValidMoves(state)

    val shuffled = Random.shuffle(moves)

    val best = shuffled
      .map(m => (m, game.getKilledGroups(state, m.coord).flatten.size))
      .maxByOption(_._2)
    println(s"Best killing move: $best")

    best.map(_._1)
  }
}
