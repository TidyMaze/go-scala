package fr.yaro.go
package ui
import engine.State

import scalafx.application.{JFXApp3, Platform}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.Pos
import scalafx.scene.layout.HBox
import scalafx.scene.{Group, Scene}
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.text.{Text, TextAlignment}

import scala.concurrent.{ExecutionContext, Future}

class GUIView(implicit ec: ExecutionContext) extends View with JFXApp3 {
  // watchable count
  var frame = 0

  Future {
    println("starting GUI in background")
    this.main(Array())
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

  var text = new Text("Hello World!")
  text.fill = Color.rgb(0, 0, 0, 1)
  text.stroke = Color.rgb(255, 255, 255, 1)
  text.textAlignment = TextAlignment.Center
//  text.alignmentInParent = Pos.Center

  override def start(): Unit = {
    val s = new Scene {
      fill = Color.rgb(0, 0, 255)
      content = new HBox(text) {
        alignment = Pos.Center
      }
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
