package fr.yaro.go
package ai

import engine.{GoGame, Move, State}

trait AI {
  def findBestMove(game: GoGame, state: State): Option[Move]
}
