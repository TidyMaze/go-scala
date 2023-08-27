package fr.yaro.go

class GoAI {
  def findBestMove(game: GoGame, state: State): Option[Move] = {
    val moves = game.getValidMoves(state)
    moves.headOption
  }
}
