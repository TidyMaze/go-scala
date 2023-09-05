package fr.yaro.go
package ai

import engine.{Game, Move, State}

trait AI {
  def findBestMove(game: Game, state: State): Option[Move]
}
