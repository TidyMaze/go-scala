package fr.yaro.go
package ui
import engine.State

import javafx.application.Application
import scalafx.Includes.handle
import scalafx.application.{JFXApp3, Platform}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{
  Background,
  BackgroundFill,
  Border,
  BorderStroke,
  BorderStrokeStyle,
  BorderWidths,
  CornerRadii,
  GridPane,
  HBox,
  Priority,
  Region,
  VBox
}
import scalafx.scene.{Group, Scene}
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.text.{Text, TextAlignment}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class GUIView(implicit ec: ExecutionContext) extends View with JFXApp3 {
  var text: Label = null
  var text2: Label = null

  // watchable count
  var frame = 0

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
      text.text = "Frame " + frame
    }
  }

  override def start(): Unit = {

    text = new Label("Hello World!") {
      textAlignment = TextAlignment.Center
      // green background
      background = new Background(
        Array(
          new BackgroundFill(
            Color.rgb(0, 255, 0),
            CornerRadii.Empty,
            Insets.Empty
          )
        )
      )
      hgrow = Priority.Always
      vgrow = Priority.Always
      maxWidth = Double.MaxValue
      maxHeight = Double.MaxValue
    }

    val cells = (0 to 9 map { i: Int =>
      0 to 9 map { j =>
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
      prefWidth = 400
      prefHeight = 400
      hgrow = Priority.Always
      vgrow = Priority.Always
      border = new Border(
        new BorderStroke(
          Color.Red,
          BorderStrokeStyle.Dashed,
          CornerRadii.Empty,
          new BorderWidths(10)
        )
      )
    }

    cells.zipWithIndex.foreach { case (row, i) =>
      row.zipWithIndex.foreach { case (cell, j) =>
        grid.add(cell, j, i)
      }
    }

    grid.columnConstraints = (0 to 9).map { _ =>
      new scalafx.scene.layout.ColumnConstraints() {
        hgrow = Priority.Always
      }
    }

    grid.rowConstraints = (0 to 9).map { _ =>
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
          new BorderWidths(10)
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
