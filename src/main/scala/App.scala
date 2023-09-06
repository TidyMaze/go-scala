package fr.yaro.go

import ai.{AI, BestKillingMoveAI}
import engine.{Custom, Game}
import ui.{GUIView, View}

import java.util.concurrent.ForkJoinPool
import scala.concurrent.ExecutionContext

class Controller(view: View, ai: AI) {
  def startGame() = {
    val game = new Game()
    var state = game.initialState(Custom(9))

    view.render(state)

    while (!game.isOver(state)) {
      // clean the console
      print("\u001b[H\u001b[2J")

      val move = ai.findBestMove(game, state)
      move match {
        case Some(m) =>
          state = game.play(state, m)
          println(s"${state.turn.opponent} played $m")
          view.render(state)
        case None =>
          state = game.pass(state)
          println(s"${state.turn.opponent} passed")
      }

      Thread.sleep(100)
    }
  }
}

object App {

  def main(args: Array[String]): Unit = {
    implicit val ec: ExecutionContext =
      ExecutionContext.fromExecutor(new ForkJoinPool())

    val ai = new BestKillingMoveAI()
    val view = new GUIView()
    val controller = new Controller(view, ai)
    controller.startGame()
  }
}