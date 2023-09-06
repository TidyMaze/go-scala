package fr.yaro.go

import ai.{AI, BestKillingMoveAI}
import engine.{Custom, Game}
import ui.{GUIView, View}

import java.util.concurrent.ForkJoinPool
import scala.concurrent.ExecutionContext

class Controller(view: View, ai: AI) {
  var turn = 0
  var startTime: Long = 0

  def startGame() = {
    val game = new Game()
    var state = game.initialState(Custom(9))

    view.render(state)

    while (!game.isOver(state)) {
      if (startTime == 0 && turn > 1000) {
        startTime = System.currentTimeMillis()
        turn = 0
      }

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

      turn += 1

      println(s"Turn $turn started at ${startTime}")

//      Thread.sleep(100)
      if (startTime != 0) {
        println(
          s"Average time per turn: ${(System.currentTimeMillis().toDouble - startTime.toDouble) / turn.toDouble}ms"
        )
      }
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
