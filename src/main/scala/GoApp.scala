package fr.yaro.go

import ai.GoAI
import engine.{Custom, GoGame}
import ui.{ConsoleView, GUIView, View}

import java.util.concurrent.{Executor, ForkJoinPool}
import scala.concurrent.ExecutionContext

class Controller(view: View) {
  def startGame() = {
    val game = new GoGame()
    val goAI = new GoAI()
    var state = game.initialState(Custom(9))

    view.render(state)

    while (!game.isOver(state)) {
      // clean the console
      print("\u001b[H\u001b[2J")

      val move = goAI.findBestMove(game, state)
      move match {
        case Some(m) =>
          state = game.play(state, m)
          println(s"${state.turn.opponent} played $m")
          view.render(state)
        case None =>
          state = game.pass(state)
          println(s"${state.turn.opponent} passed")
      }

      Thread.sleep(5000)
    }
  }
}

object GoApp {

  def main(args: Array[String]): Unit = {
    implicit val ec: ExecutionContext =
      ExecutionContext.fromExecutor(new ForkJoinPool())
    val view = new GUIView()
    val controller = new Controller(view)
    controller.startGame()
  }
}
