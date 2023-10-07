package fr.yaro.go
package ai

import engine.{Action, Color, Game, Pass, PutStone, State}

import scala.util.Random

/** This AI:
  * - plays the best killing move it finds.
  * - else tries to block the enemy move that would kill the nost stones.
  */
class LessDumbAI extends AI {

  def name: String = "LessDumbAI"
  def shuffled[A](seq: Seq[A]): Seq[A] = Random.shuffle(seq)

  def findBestAction(game: Game, state: State): Action = {
    val currentStateScore = game.getScore(state, state.turn)

    val movesDepth1 = shuffled(game.getValidPutStones(state))
    movesDepth1
      .map { m =>
        val stateDepth1 = game.putStone(state, m)
        val movesDepth2 = shuffled(game.getValidPutStones(stateDepth1))
        val score = movesDepth2
          .map { m2 =>
            val stateDepth2 = game.putStone(stateDepth1, m2)
            val currentPlayer = state.turn
            val score: Int = game.getScore(stateDepth2, currentPlayer)
            (m, score)
          }
          .minByOption(_._2)
          .map(_._2)
          .getOrElse(Integer.MAX_VALUE)

        (m, score)
      }
      .maxByOption(_._2)
      .map { case (m, score) =>
        println(s"Best move: $m with score $score")
        (m, score)
      }
      .filter { case (m, score) =>
        if (score < currentStateScore) {
          println(
            s"Passing best move $m with score $score because it's not worth it"
          )
          false
        } else {
          true
        }
      }
      .map(_._1)
      .getOrElse(Pass)
  }
}
