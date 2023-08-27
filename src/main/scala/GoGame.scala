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
    val killedGroups = game.getKilledGroups(state, move.coord)

    if (killedGroups.nonEmpty) {
      println(s"Killed groups: $killedGroups")
    }

    state.copy(
      grid =
        put(removeKilledGroups(state.grid, killedGroups), move, state.turn),
      turn = state.turn.opponent,
      captured = state.captured.updated(
        state.turn,
        state.captured(state.turn) + killedGroups.flatten.size
      ),
      passed = state.passed.updated(state.turn, false)
    )
  }

  def put(grid: Grid, move: Move, color: Color): Grid =
    grid.updated(
      move.coord.y,
      grid(move.coord.y).updated(move.coord.x, Some(color))
    )

  def removeKilledGroups(grid: Grid, killedGroups: Set[Set[Coord]]): Grid = {
    grid.zipWithIndex.map { case (row, rowIndex) =>
      row.zipWithIndex.map { case (cell, colIndex) =>
        val coord = Coord(colIndex, rowIndex)
        if (
          grid(rowIndex)(colIndex).isEmpty || killedGroups.exists(
            _.contains(coord)
          )
        ) {
          None
        } else {
          cell
        }
      }
    }
  }

  def pass(state: State): State =
    state.copy(
      turn = state.turn.opponent,
      passed = state.passed.updated(state.turn, true)
    )

  def getValidMoves(state: State): Seq[Move] = {
    // find any empty cell
    val emptyCells = for {
      (row, rowIndex) <- state.grid.zipWithIndex
      (cell, colIndex) <- row.zipWithIndex
      if cell.isEmpty
    } yield Coord(colIndex, rowIndex)

    // remove all suicide moves
    emptyCells
      .map(Move.apply)
      .filterNot(m => isSuicide(state, m))
  }

  def isSuicide(state: State, move: Move): Boolean = {
    // a suicide is a move that immediately leads to the group being dead
    // if an enemy group is killed, then it's not a suicide

    val resultingGrid = put(state.grid, move, state.turn)
    val resultingState = state.copy(
      grid = resultingGrid,
      turn = state.turn.opponent,
      passed = state.passed.updated(state.turn, false)
    )

    val killed = getKilledGroups(resultingState, move.coord)
    killed.isEmpty && getLiberties(resultingState, move.coord).exists(_.isEmpty)
  }

  def isOver(state: State): Boolean = {
    val (blackPassed, whitePassed) = (state.passed(Black), state.passed(White))
    blackPassed && whitePassed
  }

  def getKilledGroups(state: State, from: Coord): Set[Set[Coord]] = {
    val adjacentGroups = getAdjacentGroups(state, from)
    adjacentGroups.filter { group =>
      val color = state.at(group.head).get

      val liberties = getLiberties(state, group.head)
      println(
        s"Group $group of color $color has ${liberties.get.size} liberties: ${liberties.get}"
      )
      color == state.turn.opponent && liberties.exists(l =>
        l.size == 1 && l.head == from
      )
    }
  }

  def getAdjacentGroups(state: State, coord: Coord): Set[Set[Coord]] = {
    val neighbors = getNeighbors(state, coord)
    val groups = neighbors.flatMap(getGroup(state, _)).toSet
    groups
  }

  def getGroup(state: State, coord: Coord): Option[Set[Coord]] = {
    val color = state.at(coord)

    color match {
      case None => None
      case Some(c) => {
        var res = Set.empty[Coord]
        var visited = Set.empty[Coord]
        var toVisit = Set(coord)

        while (toVisit.nonEmpty) {
          val current = toVisit.head
          toVisit = toVisit.tail
          visited = visited + current
          if (state.at(current).contains(c)) {
            res = res + current
            val neighbors = getNeighbors(state, current)
            val newNeighbors = neighbors.filterNot(visited.contains)
            toVisit = toVisit ++ newNeighbors
          }
        }

        Some(res)
      }
    }
  }

  def getLiberties(state: State, coord: Coord): Option[Set[Coord]] = {
    // count the number of empty cells around the group
    val group = getGroup(state, coord)
    group.map { group =>
      group.flatMap(getNeighbors(state, _)).filter(state.at(_).isEmpty)
    }
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
