package fr.yaro.go

class GoGame {
  def initialState(gridSize: GridSize): State =
    State(
      Seq.fill(gridSize.getSize, gridSize.getSize)(None),
      Black,
      Map(Black -> 0, White -> 0),
      Map(Black -> false, White -> false)
    )


  def play(state: State, move: Move): State = ???

  def pass(state: State): State =
    state.copy(turn = state.turn.opponent, passed = state.passed.updated(state.turn, true))

  def getValidMoves(state: State): Seq[Move] = Seq.empty


  def isOver(state: State): Boolean = {
    val (blackPassed, whitePassed) = (state.passed(Black), state.passed(White))
    blackPassed && whitePassed
  }
}
