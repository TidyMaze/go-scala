package fr.yaro.go

class GoGame {
  def initialState(gridSize: GridSize): State =
    State(
      Seq.fill(gridSize.getSize, gridSize.getSize)(None),
      Black,
      Map(Black -> 0, White -> 0),
      Map(Black -> false, White -> false)
    )


  def play(game: GoGame, state: State, move: Move): State = {
    val libertiesByGroup = game.getLibertiesByGroup(state)

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

  def getLibertiesByGroup(state: State): Map[Seq[Coord], Int] = {
    val groups = getGroups(state)
    groups.map { group =>
      val liberties = group.flatMap(getLiberties(state, _)).distinct
      group -> liberties.size
    }.toMap
  }

  def getGroups(state: State): Seq[Seq[Coord]] = {
    val groups = for {
      (row, rowIndex) <- state.grid.zipWithIndex
      (cell, colIndex) <- row.zipWithIndex
      if cell.isDefined
    } yield getGroup(state, Coord(colIndex, rowIndex))
    groups.distinct
  }

  def getGroup(state: State, coord: Coord): Seq[Coord] = {
    val color = state.grid(coord.y)(coord.x).get
    val neighbors = getNeighbors(state, coord)
    val sameColorNeighbors = neighbors.filter(n => state.at(n).contains(color))
    val group = sameColorNeighbors.flatMap(getGroup(state, _))
    (coord +: group).distinct
  }

  def getLiberties(state: State, coord: Coord): Seq[Coord] = {
    val neighbors = getNeighbors(state, coord)
    neighbors.filter(state.at(_).isEmpty)
  }

  def getNeighbors(state: State, coord: Coord) = {
    val neighbors = Seq(
      Coord(coord.x - 1, coord.y),
      Coord(coord.x + 1, coord.y),
      Coord(coord.x, coord.y - 1),
      Coord(coord.x, coord.y + 1)
    )
    neighbors.filter(isValidCoord(state, _))
  }
  
  def isValidCoord(state: State, coord: Coord): Boolean = {
    val gridSize = state.grid.size
    coord.x >= 0 && coord.x < gridSize && coord.y >= 0 && coord.y < gridSize
  }
}
