package fr.yaro.go
package ui
import engine.State

import scalafx.application.{JFXApp3, Platform}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.{Background, BackgroundFill, Border, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, HBox, Priority, VBox}
import scalafx.scene.{Group, Scene}
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.text.{Text, TextAlignment}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class GUIView(implicit ec: ExecutionContext) extends View with JFXApp3 {
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

  var text = new Label("Hello World!") {
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

  var text2 = new Label("Hello !!!!!!!!!!") {
    textAlignment = TextAlignment.Center
    // green background
    background = new Background(
      Array(
        new BackgroundFill(
          Color.rgb(255, 0, 0),
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

  override def start(): Unit = {
    assert(this.text != null)
    assert(this.text2 != null)

    val hBox: HBox = new HBox(text, text2) {
      border = new Border(
        new BorderStroke(
          Color.rgb(0, 0, 0),
          BorderStrokeStyle.Solid,
          CornerRadii.Empty,
          BorderWidths.Default
        )
      )
      spacing = 50
      alignment = Pos.Center
      fillHeight = true
      background = new Background(
        Array(
          new BackgroundFill(
            Color.rgb(0, 255, 0),
            CornerRadii.Empty,
            Insets.Empty
          )
        )
      )
      vgrow = Priority.Always
      hgrow = Priority.Always
      maxWidth = Double.MaxValue
      maxHeight = Double.MaxValue
    }

    val s = new Scene {
      fill = Color.rgb(0, 0, 200)
      content = hBox
    }

    stage = new PrimaryStage {
      title = "Go"
      width = 600
      height = 450
      scene = s
    }

    stage.show()
  }
}
