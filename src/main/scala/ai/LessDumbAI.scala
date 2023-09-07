package fr.yaro.go
package ai

import engine.{Game, Move, State}

import scala.util.Random

/** This AI:
  * - plays the best killing move it finds.
  * - else tries to block the enemy move that would kill the nost stones.
  */
class LessDumbAI extends AI {
  def findBestMove(game: Game, state: State): Option[Move] = {
    val moves = game.getValidMoves(state)

    val shuffled = Random.shuffle(moves)

    val best = shuffled
      .map { m =>
        val killed = game.getKilledGroups(state, m.coord)
        val killedCount = killed.flatten.size

        val stateIfEnemyWasPlaying = state.copy(turn = state.turn.opponent)
        val enemyKilled = game.getKilledGroups(stateIfEnemyWasPlaying, m.coord)
        val enemyKilledCount = enemyKilled.flatten.size

//        val touchingAtLeastOneEnemyStone =
//          game.getNeighborsMemo(state.grid.size, m.coord).exists { c =>
//            state.grid(c).contains(state.turn.opponent)
//          }
//
//        val touchingBonus = if (touchingAtLeastOneEnemyStone) 1 else 0

        println(
          s"Playing $m would kill $killedCount stones in those groups: $killed. Enemy would kill $enemyKilledCount stones in those groups: $enemyKilled"
        )

        // It's better to make a move that kills stones and doesn't let the enemy kill stones.
//        (m, killedCount + enemyKilledCount + touchingBonus)
        (m, killedCount + enemyKilledCount)
      }
      .maxByOption(_._2)
    println(s"Best killing move: $best")

    best.map(_._1)
  }
}
