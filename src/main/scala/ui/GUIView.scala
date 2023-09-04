package fr.yaro.go
package ui
import engine.State

import scalafx.application.{JFXApp3, Platform}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.{
  Background,
  BackgroundFill,
  CornerRadii,
  HBox,
  Priority,
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

  var text = new Label("Hello World!")
  text.textAlignment = TextAlignment.Center
  // yellow background
  text.setBackground(
    new Background(
      Array(
        new BackgroundFill(
          Color.rgb(255, 255, 0),
          CornerRadii.Empty,
          Insets.Empty
        )
      )
    )
  )

  var text2 = new Label("Hello World2!")
  text2.textAlignment = TextAlignment.Center
  // red background
  text2.setBackground(
    new Background(
      Array(
        new BackgroundFill(
          Color.rgb(255, 0, 0),
          CornerRadii.Empty,
          Insets.Empty
        )
      )
    )
  )

  override def start(): Unit = {
    assert(this.text != null)
    assert(this.text2 != null)

    val hBox: HBox = new HBox(text, text2) {
      alignment = Pos.Center
      fillHeight = true
    }

    text.hgrow = Priority.Always
    text.vgrow = Priority.Always

    text.maxWidth(Double.MaxValue)
    text.maxHeight(Double.MaxValue)

    text2.hgrow = Priority.Always
    text2.vgrow = Priority.Always

    text2.maxWidth(Double.MaxValue)
    text2.maxHeight(Double.MaxValue)

    hBox.hgrow = Priority.Always
    hBox.vgrow = Priority.Always

    hBox.maxWidth(Double.MaxValue)
    hBox.maxHeight(Double.MaxValue)

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
