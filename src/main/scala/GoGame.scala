package fr.yaro.go

class GoGame {
  def initialState(gridSize: GridSize): State =
    State(
      Seq.fill(gridSize.getSize, gridSize.getSize)(None),
      Black,
      Map(Black -> 0, White -> 0),
      Map(Black -> false, White -> false)
    )


  def play(state: State, move: Move): State = {
    state.copy(
      grid = state.grid.updated(move.coord.y, state.grid(move.coord.y).updated(move.coord.x, Some(state.turn))),
      turn = state.turn.opponent,
      passed = state.passed.updated(state.turn, false)
    )
  }

  def pass(state: State): State =
    state.copy(turn = state.turn.opponent, passed = state.passed.updated(state.turn, true))

  def getValidMoves(state: State): Seq[Move] = {
    // find any empty cell
    val emptyCells = for {
      (row, rowIndex) <- state.grid.zipWithIndex
      (cell, colIndex) <- row.zipWithIndex
      if cell.isEmpty
    } yield Coord(colIndex, rowIndex)

    emptyCells.map(Move.apply)

  }


  def isOver(state: State): Boolean = {
    val (blackPassed, whitePassed) = (state.passed(Black), state.passed(White))
    blackPassed && whitePassed
  }
}
