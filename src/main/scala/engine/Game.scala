package fr.yaro.go
package engine

import scala.collection.{View, mutable}

class Game {
  def initialState(gridSize: GridSize): State =
    State(
      Grid.empty(gridSize.getSize),
      Black,
      Map(Black -> 0, White -> 0),
      Map(Black -> false, White -> false),
      Set.empty
    )

  def play(state: State, move: Move): State = {
    val killedGroups = getKilledGroups(state, move.coord)

    val newGrid =
      put(removeKilledGroups(state.grid, killedGroups), move, state.turn)
    state.copy(
      grid = newGrid,
      turn = state.turn.opponent,
      captured = state.captured.updated(
        state.turn,
        state.captured(state.turn) + killedGroups.flatten.size
      ),
      passed = state.passed.updated(state.turn, false),
      alreadyPlayedStates = state.alreadyPlayedStates + newGrid
    )
  }

  def put(grid: Grid, move: Move, color: Color): Grid =
    grid.updated(move.coord, Some(color))

  def removeKilledGroups(grid: Grid, killedGroups: Seq[Set[Coord]]): Grid = {
    killedGroups.foldLeft(grid) { (grid, group) =>
      group.foldLeft(grid) { (grid, coord) =>
        grid.updated(coord, None)
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
      x <- 0 until state.gridSize
      y <- 0 until state.gridSize
      coord = Coord(x, y)
      if state.at(coord).isEmpty
    } yield coord

    // remove all suicide moves
    emptyCells
      .map(Move.apply)
      .filterNot(m => isSuicide(state, m))
      .filterNot(m => resultsInAlreadyPlayedState(state, m))
  }

  def resultsInAlreadyPlayedState(state: State, move: Move) = {
    val resState = play(state, move)
    state.alreadyPlayedStates.contains(resState.grid)
  }

  def isSuicide(state: State, move: Move): Boolean = {
    // a suicide is a move that immediately leads to the group being dead
    // if an enemy group is killed, then it's not a suicide

    val resultingGrid = put(state.grid, move, state.turn)
    val resultingState = state.copy(
      grid = resultingGrid,
      turn = state.turn,
      passed = state.passed.updated(state.turn, false)
    )

    val killed = getKilledGroups(state, move.coord)
    val res = killed.isEmpty && getLiberties(resultingState, move.coord).exists(
      _.isEmpty
    )
//    if (res) {
//      println(s"Playing $move is a suicide")
//    }
    res
  }

  def isOver(state: State): Boolean = {
    val (blackPassed, whitePassed) = (state.passed(Black), state.passed(White))
    blackPassed && whitePassed
  }

  def getKilledGroups(state: State, from: Coord): Seq[Set[Coord]] = {
    val adjacentGroups = getAdjacentGroups(state, from, state.turn.opponent)
    adjacentGroups.filter { group =>
      val liberties = getLiberties(state, group)
      liberties.size == 1 && liberties.head == from
    }
  }

  def getAdjacentGroups(
      state: State,
      coord: Coord,
      ofColor: Color
  ): Seq[Set[Coord]] = {
    val neighbors = getNeighborsMemo(state.gridSize, coord)
    val groups = neighbors
      .foldLeft((Set.empty[Coord], Seq.empty[Set[Coord]])) {
        case ((alreadyFoundCoords, resultGroups), c)
            if state.grid(c).contains(ofColor) &&
              !alreadyFoundCoords.contains(c) =>
          val newGroup = getGroup(state, c)
          (
            alreadyFoundCoords ++ newGroup.toSet.flatten,
            resultGroups ++ newGroup
          )
        case (other, _) => other
      }
    groups._2
  }

  def getGroup(state: State, coord: Coord): Option[Set[Coord]] = {
    val color = state.at(coord)

    color match {
      case None => None
      case Some(c) =>
        val res = mutable.Set.empty[Coord]
        val visited = mutable.Set.empty[Coord]
        val toVisit = mutable.Set(coord)

        while (toVisit.nonEmpty) {
          val current = toVisit.head
          toVisit.remove(current)
          visited.addOne(current)
          if (state.at(current).contains(c)) {
            res.addOne(current)
            val neighbors = getNeighborsMemo(state.gridSize, current)
            val newNeighbors = neighbors.filterNot(visited.contains)
            toVisit.addAll(newNeighbors)
          }
        }

        Some(res.toSet)
    }
  }

  def getLiberties(state: State, coord: Coord): Option[Set[Coord]] = {
    // count the number of empty cells around the group
    val group = getGroup(state, coord)
    group.map { getLiberties(state, _) }
  }

  def getLiberties(state: State, group: Set[Coord]): Set[Coord] =
    group
      .flatMap(getNeighborsMemo(state.gridSize, _))
      .filter(state.at(_).isEmpty)

  var neighborsCache = Array.empty[Array[Seq[Coord]]]

  def initNeighborsCache(gridSize: Int) = {
    neighborsCache = Array.fill(gridSize, gridSize)(Seq.empty)
    for {
      x <- 0 until gridSize
      y <- 0 until gridSize
      coord = Coord(x, y)
    } {
      neighborsCache(y)(x) = getNeighbors(gridSize, coord)
    }
  }

  // memoize the result of getNeighbors
  def getNeighborsMemo: (Int, Coord) => Seq[Coord] = (gridSize, coord) => {
    if (neighborsCache.length != gridSize) {
      initNeighborsCache(gridSize)
    }

    neighborsCache(coord.y)(coord.x)
  }

  def getNeighbors(gridSize: Int, coord: Coord) = {
    val neighbors = Seq(
      Coord(coord.x - 1, coord.y),
      Coord(coord.x + 1, coord.y),
      Coord(coord.x, coord.y - 1),
      Coord(coord.x, coord.y + 1)
    )
    neighbors.filter(isValidCoord(gridSize, _))
  }

  def isValidCoord(gridSize: Int, coord: Coord): Boolean = {
    coord.x >= 0 && coord.x < gridSize && coord.y >= 0 && coord.y < gridSize
  }
}
