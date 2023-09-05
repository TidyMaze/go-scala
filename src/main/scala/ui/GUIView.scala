package fr.yaro.go
package ui
import engine.{Black, State, White}

import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.{JFXApp3, Platform}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout._
import scalafx.scene.paint.Color

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class GUIView(implicit ec: ExecutionContext) extends View with JFXApp3 {
  var text: Label = null
  var text2: Label = null

  // watchable count
  var frame = 0

  var cells: Seq[Seq[(Button, StackPane)]] = null

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

    val backgroundColor = Color.rgb(219, 167, 94)

    Platform.runLater {
      println("running later frame " + frame)

      // update grid cells
      state.grid.grid.zipWithIndex.foreach { case (row, i) =>
        row.zipWithIndex.foreach { case (cell, j) =>
          val color = cell match {
            case None => backgroundColor
            case Some(Black) => Color.Black
            case Some(White) => Color.White
          }

          val button = cells(i)(j)
          button._1.text = cell.toString
          button._1.background = new Background(Array(new BackgroundFill(color, CornerRadii.Empty, Insets.Empty)))
        }
      }
    }
  }

  override def start(): Unit = {

    val grid = new GridPane() {
      alignment = Pos.Center
      hgrow = Priority.Always
      vgrow = Priority.Always
      border = new Border(
        new BorderStroke(
          Color.Red,
          BorderStrokeStyle.Dashed,
          CornerRadii.Empty,
          new BorderWidths(1)
        )
      )
    }

    cells = (0 until 9 map { i: Int =>
      0 until 9 map { j =>

        val stack = new StackPane() {

        }

        val button = new Button(s"$i,$j") {
          maxWidth = Double.MaxValue
          maxHeight = Double.MaxValue
          onAction = _ => {
            println(s"clicked $i,$j")
          }
        }

        stack.children.add(button)

        (button, stack)
      }
    })

    cells.zipWithIndex.foreach { case (row, i) =>
      row.zipWithIndex.foreach { case (cell, j) =>
        grid.add(cell._2, j, i)
      }
    }

    grid.columnConstraints = (0 until 9).map { _ =>
      new scalafx.scene.layout.ColumnConstraints() {
        hgrow = Priority.Always
        prefWidth <== grid.width / 9
      }
    }

    grid.rowConstraints = (0 until 9).map { _ =>
      new scalafx.scene.layout.RowConstraints() {
        vgrow = Priority.Always
        prefHeight <== grid.height / 9
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
      fill = Color.rgb(0, 0, 200)
      content = vBox
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
