package fr.yaro.go
package ui

import engine.{Black, Coord, State, White}

trait View {
  def render(state: State): Unit
}

class ConsoleView extends View {
  def showState(state: State): String = {
    val gridStr =
      for (y <- 0 until state.grid.size) yield {
        val rowStr =
          for (x <- 0 until state.grid.size) yield {
            val color = state.grid(Coord(x, y))
            color match {
              case Some(Black) => "X"
              case Some(White) => "O"
              case None        => "."
            }
          }
        rowStr.mkString(" ")
      }

    val capturedStr = state.captured
      .map { case (color, count) =>
        s"$color: $count"
      }
      .mkString(" ")

    s"${gridStr.mkString("\n")}\n$capturedStr\n${state.alreadyPlayedStates.size} played states"
  }

  override def render(state: State): Unit = println(showState(state) + "\n")
}
