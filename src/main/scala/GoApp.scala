package fr.yaro.go

object GoApp {

  def main(args: Array[String]): Unit = {
    val game = new GoGame()
    val goAI = new GoAI()
    var state = game.initialState(Custom(9))

    val presenter = new Presenter()
    val stateStr = presenter.showState(state)
    println(stateStr)

    while (!game.isOver(state)) {
      // clean the console
      print("\u001b[H\u001b[2J")

      val move = goAI.findBestMove(game, state)
      move match {
        case Some(m) =>
          state = game.play(state, m)
          val stateStr = presenter.showState(state)
          println(s"${state.turn.opponent} played $m")
          println(stateStr)
          println("\n")
        case None =>
          state = game.pass(state)
          println(s"${state.turn.opponent} passed")
      }

      Thread.sleep(100)
    }
  }
}
