package fr.yaro.go
package ui
import engine.State

import scalafx.application.{JFXApp3, Platform}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text

import scala.concurrent.{ExecutionContext, Future}

class GUIView(implicit ec: ExecutionContext) extends View with JFXApp3 {
  var frame = 0

  Future {
    println("starting GUI in background")
    this.main(Array())
  }

  override def render(state: State): Unit = {
    println("rendering frame " + frame)
    frame += 1

    if (this.stage == null) {
      println("stage is null")
      return
    }

    Platform.runLater {
      println("running later frame " + frame)

      stage.scene = new Scene {
        fill = Color.rgb(200, 38, 38)
        content = new Text(0,0,s"frame $frame") {
          fill = Color.White
        }
      }

      stage.show()
    }
  }

  override def start(): Unit = {
    stage = new PrimaryStage {
      title = "Go"
      width = 600
      height = 450
      scene = new Scene {
        fill = Color.rgb(38, 38, 38)
        content = new Text {
          text = "Scala"
          style = "-fx-font: normal bold 100pt sans-serif"
          fill = new LinearGradient(endX = 0, stops = Stops(Red, DarkRed))
        }
      }
    }
  }
}
