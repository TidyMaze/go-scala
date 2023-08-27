package fr.yaro.go

class Presenter {
  def showState(state: State): String = {
val gridStr = state.grid.map { row =>
      row.map {
        case None => "."
        case Some(Black) => "X"
        case Some(White) => "O"
      }.mkString(" ")
    }.mkString("\n")

    val capturedStr = state.captured.map {
      case (color, count) => s"$color: $count"
    }.mkString("  ")

    s"$gridStr\n$capturedStr"
  }
}
