package fr.yaro.go
package ui
import engine.State

import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.{JFXApp3, Platform}
import scalafx.geometry.Pos
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

  var cells: Seq[Seq[Button]] = null

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
          val button = cells(i)(j)
          button.text = cell.toString
        }
      }
    }
  }

  override def start(): Unit = {

    cells = (0 until 9 map { i: Int =>
      0 until 9 map { j =>
        new Button(s"$i,$j") {
          maxWidth = Double.MaxValue
          maxHeight = Double.MaxValue
          onAction = _ => {
            println(s"clicked $i,$j")
          }
        }
      }
    })

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

    cells.zipWithIndex.foreach { case (row, i) =>
      row.zipWithIndex.foreach { case (cell, j) =>
        grid.add(cell, j, i)
      }
    }

    grid.columnConstraints = (0 until 9).map { _ =>
      new scalafx.scene.layout.ColumnConstraints() {
        hgrow = Priority.Always
      }
    }

    grid.rowConstraints = (0 until 9).map { _ =>
      new scalafx.scene.layout.RowConstraints() {
        vgrow = Priority.Always
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
