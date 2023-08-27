package fr.yaro.go

object GoApp {

  def main(args: Array[String]): Unit = {
    val game = new GoGame()
    val goAI = new GoAI()
    var state = game.initialState(Custom(4))

    val presenter = new Presenter()
    val stateStr = presenter.showState(state)
    println(stateStr)

    while (!game.isOver(state)) {
      val move = goAI.findBestMove(game, state)
      move match {
        case Some(m) =>
          state = game.play(game, state, m)
          val stateStr = presenter.showState(state)
          println(stateStr)
        case None =>
          state = game.pass(state)
          println(s"${state.turn.opponent} passed")
      }
      
      Thread.sleep(3000)
    }
  }
}
