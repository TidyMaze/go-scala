package fr.yaro.go

import ai.{AI, BestKillingMoveAI, LessDumbAI}
import engine.{Black, Custom, Game, Grid, GridSize, Nineteen, Thirteen, White}
import ui.{GUIView, View}

import javafx.application.Application
import scalafx.application.Platform

import java.util.concurrent.ForkJoinPool
import scala.concurrent.ExecutionContext

class Controller(view: View, playerBlack: Player, playerWhite: Player) {
  var turn = 0
  var startTime: Long = 0

  def startGame(gridSize: GridSize) = {
    val game = new Game()
    var state = game.initialState(gridSize)

    view.render(state)

//    Thread.sleep(2000)

    while (!game.isOver(state)) {
      println(s"\n\n\n====== Turn $turn ======")

      if (startTime == 0 && turn > 10) {
        startTime = System.currentTimeMillis()
        turn = 0
      }

      cleanConsole()

      val currentPlayer = state.turn match {
        case Black => playerBlack
        case White => playerWhite
      }

      println("Current player: " + currentPlayer.name)

      val move = currentPlayer.play(game, state)
      move match {
        case Some(move) =>
          state = game.play(state, move)
          println(
            s"${state.turn.opponent} (${currentPlayer.name}) played $move"
          )
          view.render(state)
        case None =>
          state = game.pass(state)
          println(
            s"${state.turn.opponent} passed. Passed turns: ${state.passed}"
          )
      }

      turn += 1

      Thread.sleep(200)
      if (startTime != 0) {
        println(
          s"Average time per turn: ${(System.currentTimeMillis().toDouble - startTime.toDouble) / turn.toDouble}ms"
        )
      }
    }

    println(
      s"Game over. Winner is: ${game.getWinner(state).getOrElse("nobody")}"
    )
  }

  private def cleanConsole(): Unit = {
    // clean the console
    print("\u001b[H\u001b[2J")
  }
}

object App {

  def main(args: Array[String]): Unit = {
    implicit val ec: ExecutionContext =
      ExecutionContext.fromExecutor(new ForkJoinPool())

    val player1 = new Human()
    val player2 = new BestKillingMoveAI()
    val gridSize = Thirteen

    val view = new GUIView(gridSize)
    val controller = new Controller(view, player1, player2)
    controller.startGame(gridSize)

    Thread.sleep(5000)
    Platform.exit()
  }
}
