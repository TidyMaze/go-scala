package fr.yaro.go
package ai

import engine.{Action, Game, PutStone, State}

trait AI extends Player {
  def findBestAction(game: Game, state: State): Action

  override def play(game: Game, state: State): Action = findBestAction(game, state)
}
