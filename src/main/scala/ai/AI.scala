package fr.yaro.go
package ai

import engine.{Game, Move, State}

trait AI extends Player {
  def findBestMove(game: Game, state: State): Option[Move]

  override def play(game: Game, state: State): Option[Move] = findBestMove(game, state)
}
