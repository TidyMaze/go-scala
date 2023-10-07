package fr.yaro.go

import engine.{Action, Game, PutStone, State}

trait Player {
  def play(game: Game, state: State): Action

  def name: String
}
