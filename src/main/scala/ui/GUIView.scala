package fr.yaro.go
package ui
import engine.{Black, State, White}

import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.binding.{Bindings, NumberBinding}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.effect.BoxBlur
import scalafx.scene.layout._
import scalafx.scene.paint._
import scalafx.scene.shape.Circle

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class GUIView(implicit ec: ExecutionContext) extends View with JFXApp3 {
  var text: Label = null

  // watchable count
  var frame = 0

  var cells: Seq[Seq[StackPane]] = null

  val backgroundColor = Color.rgb(219, 167, 94)
  val backgroundColor2 = backgroundColor.darker

  var cellLength: NumberBinding = null

  Future {
    println("starting GUI in background")
    this.main(Array())
  }.andThen {
    case Success(_) => println("GUI started")
    case Failure(e) =>
      println("GUI failed to start: " + e)
      e.printStackTrace()
      System.exit(1)
  }

  override def render(state: State): Unit = {
    println("rendering frame " + frame)

    // watchable count
    frame += 1

    if (this.stage == null) {
      println("stage is null")
      return
    }

    Platform.runLater {
      println("running later frame " + frame)

      // update grid cells
      state.grid.grid.zipWithIndex.foreach { case (row, i) =>
        row.zipWithIndex.foreach { case (cell, j) =>
          val color = cell match {
            case None => Color.Transparent
            case Some(Black) =>
              val blackStoneLightColor =
                Stop(0, Color.Black.deriveColor(1.0, 1.0, 10, 1.0))
              val blackStoneDarkColor = Stop(1, Color.Black)
              makeStoneColor(blackStoneLightColor, blackStoneDarkColor)
            case Some(White) =>
              val whiteStoneLightColor = Stop(0, Color.White)
              val whiteStoneDarkColor =
                Stop(1, Color.White.deriveColor(1.0, 1.0, 0.8, 1))
              makeStoneColor(whiteStoneLightColor, whiteStoneDarkColor)
          }

          val stack = cells(i)(j)

          stack.children = cell match {
            case None =>
              new Button() {
                maxWidth = Double.MaxValue
                maxHeight = Double.MaxValue
                background = new Background(
                  Array(
                    new BackgroundFill(
                      Color.Transparent,
                      CornerRadii.Empty,
                      Insets.Empty
                    )
                  )
                )
                onAction = _ => {
                  println(s"clicked $i,$j")
                }
              }

            case other =>
              new StackPane() {
                val shadow: Circle = new Circle() {
                  fill = Color.rgb(0, 0, 0, 0.5)
                  radius <== cellLength / 2 * 0.8
                }

                shadow.translateX <== cellLength / 2 * 0.2
                shadow.translateY <== cellLength / 2 * 0.2

                val bb = new BoxBlur(10, 10, 5)

                shadow.effect = bb

                val stone: Circle = new Circle() {
                  fill = color
                  radius <== cellLength / 2 * 0.8
                }
                children = Seq(shadow, stone)
              }
          }
        }
      }
    }
  }

  private def makeStoneColor(
      whiteStoneLightColor: Stop,
      whiteStoneDarkColor: Stop
  ) = {
    RadialGradient(
      0,
      0,
      0.3,
      0.3,
      0.6,
      true,
      CycleMethod.NoCycle,
      whiteStoneLightColor,
      whiteStoneDarkColor
    )
  }

  def min(binding: NumberBinding, binding1: NumberBinding): NumberBinding =
    Bindings
      .`when`(binding.lessThan(binding1))
      .choose(binding)
      .otherwise(binding1)

  override def start(): Unit = {

    val grid = new GridPane() {
      alignment = Pos.Center
      hgrow = Priority.Never
      vgrow = Priority.Never
      border = new Border(
        new BorderStroke(
          Color.Red,
          BorderStrokeStyle.Dashed,
          CornerRadii.Empty,
          new BorderWidths(1)
        )
      )
      background = new Background(
        Array(
          new BackgroundFill(
            LinearGradient(
              0,
              0,
              1,
              1,
              true,
              CycleMethod.NoCycle,
              Stop(0, backgroundColor),
              Stop(1, backgroundColor2)
            ),
            CornerRadii.Empty,
            Insets.Empty
          )
        )
      )
    }

    cells = (0 until 9 map { i: Int =>
      0 until 9 map { j =>
        new StackPane()
      }
    })

    cells.zipWithIndex.foreach { case (row, i) =>
      row.zipWithIndex.foreach { case (cell, j) =>
        grid.add(cell, j, i)
      }
    }

    val vBox: VBox = new VBox(grid) {
      alignment = Pos.Center
      border = new Border(
        new BorderStroke(
          Color.Green,
          BorderStrokeStyle.Dashed,
          CornerRadii.Empty,
          new BorderWidths(1)
        )
      )
    }

    val s = new Scene {
      fill = Color.DarkSlateGrey
      content = vBox
    }

    cellLength = min(s.width / 9, s.height / 9)

    grid.prefWidth <== cellLength * 9
    grid.prefHeight <== cellLength * 9
    grid.maxWidth <== cellLength * 9
    grid.maxHeight <== cellLength * 9

    grid.columnConstraints = (0 until 9).map { _ =>
      new scalafx.scene.layout.ColumnConstraints() {
        hgrow = Priority.Always
        prefWidth <== cellLength
      }
    }

    grid.rowConstraints = (0 until 9).map { _ =>
      new scalafx.scene.layout.RowConstraints() {
        vgrow = Priority.Always
        prefHeight <== cellLength
      }
    }

    stage = new PrimaryStage {
      title = "Go"
      scene = s
      width = 800
      height = 800
    }

    vBox.prefWidth <== s.width
    vBox.prefHeight <== s.height

    stage.onCloseRequest = _ => {
      println("closing")
      System.exit(0)
    }

    stage.show()
  }
}
