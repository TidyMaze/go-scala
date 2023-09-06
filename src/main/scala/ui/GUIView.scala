package fr.yaro.go
package ui
import engine.{Black, State, White}

import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.binding.{Bindings, NumberBinding}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Node, Scene}
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

  private val backgroundColor = Color.rgb(219, 167, 94)
  private val backgroundColor2 = backgroundColor.darker

  var cellLength: NumberBinding = null

  private val blackStoneLightColor =
    Stop(0, Color.Black.deriveColor(1.0, 1.0, 10, 1.0))
  private val blackStoneDarkColor = Stop(1, Color.Black)

  private val whiteStoneLightColor = Stop(0, Color.White)
  private val whiteStoneDarkColor =
    Stop(1, Color.White.deriveColor(1.0, 1.0, 0.8, 1))

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
          val stack = cells(i)(j)

          // set black stone opacity to 1 if black stone is present
          stack.children(1).setOpacity(if (cell.contains(Black)) 1 else 0)

          // set white stone opacity to 1 if white stone is present
          stack.children(2).setOpacity(if (cell.contains(White)) 1 else 0)
        }
      }
    }
  }

  private def makeStoneColor(cell: Option[engine.Color]): Paint = {
    cell match {
      case None => Color.Transparent
      case Some(Black) =>
        makeStoneColor(blackStoneLightColor, blackStoneDarkColor)
      case Some(White) =>
        makeStoneColor(whiteStoneLightColor, whiteStoneDarkColor)
    }
  }

  def makeStone(color: Paint): StackPane = {
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
    new StackPane() {
      children = Seq(shadow, stone)
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

    cells.zipWithIndex.foreach { case (row, i) =>
      row.zipWithIndex.foreach { case (cell, j) =>
        cell.children = Seq(
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
          },
          makeStone(makeStoneColor(Some(Black))),
          makeStone(makeStoneColor(Some(White)))
        ).asInstanceOf[Seq[Node]]
      }
    }

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
