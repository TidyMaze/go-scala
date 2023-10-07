package fr.yaro.go

import engine.{Game, Move, State}

trait Player {
  def play(game: Game, state: State): Option[Move]

  def name: String
}
