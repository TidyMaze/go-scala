package fr.yaro.go
package ui
import engine.{Black, GridSize, State, White}

import javafx.scene.layout
import scalafx.Includes.when
import scalafx.animation._
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.binding.{Bindings, NumberBinding}
import scalafx.beans.property.IntegerProperty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.effect.BoxBlur
import scalafx.scene.layout._
import scalafx.scene.paint._
import scalafx.scene.shape.Circle
import scalafx.scene.{Node, Scene}
import scalafx.util.Duration

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class GUIView(gridSize: GridSize)(implicit ec: ExecutionContext)
    extends View
    with JFXApp3 {
  var text: Label = null
  var cells: Seq[Seq[StackPane]] = null

  private val backgroundColor = Color.rgb(219, 167, 94)
  private val backgroundColor2 = backgroundColor.darker

  var cellLength: IntegerProperty = IntegerProperty(0)

  private val blackStoneLightColor =
    Stop(0, Color.Black.deriveColor(1.0, 1.0, 10, 1.0))
  private val blackStoneDarkColor = Stop(1, Color.Black)

  private val whiteStoneLightColor = Stop(0, Color.White)
  private val whiteStoneDarkColor =
    Stop(1, Color.White.deriveColor(1.0, 1.0, 0.8, 1))

  private val backgroundButton = new Background(
    Array(
      new BackgroundFill(
        Color.Transparent,
        CornerRadii.Empty,
        Insets.Empty
      )
    )
  )

  private val backgroundButtonHover = new Background(
    Array(
      new BackgroundFill(
        new Color(Color.Red.opacity(0.1)),
        CornerRadii.Empty,
        Insets.Empty
      )
    )
  )

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
    if (this.stage == null) {
      println("stage is null")
      return
    }

    Platform.runLater {
      // update grid cells
      state.grid.grid.zipWithIndex.foreach { case (row, i) =>
        row.zipWithIndex.foreach { case (cell, j) =>
          val stack = cells(i)(j)

          val addBlack =
            cell.contains(Black) && stack.children(1).getOpacity == 0.0
          val addWhite =
            cell.contains(White) && stack.children(2).getOpacity == 0.0

          val removeBlack =
            !cell.contains(Black) && stack.children(1).getOpacity == 1.0
          val removeWhite =
            !cell.contains(White) && stack.children(2).getOpacity == 1.0

          if (addBlack) {
            val pane =
              new StackPane(stack.children(1).asInstanceOf[layout.StackPane])

            val animation = makePutStoneTransition(pane)
            animation.play()
          } else if (removeBlack) {
            val pane =
              new StackPane(stack.children(1).asInstanceOf[layout.StackPane])

            val animation = makeRemoveStoneTransition(pane)
            animation.play()
          }

          if (addWhite) {
            val pane =
              new StackPane(stack.children(2).asInstanceOf[layout.StackPane])

            val animation = makePutStoneTransition(pane)
            animation.play()
          } else if (removeWhite) {
            val pane =
              new StackPane(stack.children(2).asInstanceOf[layout.StackPane])

            val animation = makeRemoveStoneTransition(pane)
            animation.play()
          }
        }
      }
    }
  }

  private def makeRemoveStoneTransition(
      pane: StackPane
  ): Transition = {
    val translateTransition = new TranslateTransition() {
      node = pane
      duration = new Duration(100)

      fromX = 0
      fromY = 0
      toX = 0
      toY = 50
    }

    val fadeTransition = new FadeTransition() {
      node = pane
      duration = new Duration(200)

      fromValue = 1.0
      toValue = 0.0
    }

    val scaleTransition = new ScaleTransition() {
      node = pane
      duration = new Duration(100)

      fromX = 1.0
      fromY = 1.0
      toX = 0.5
      toY = 0.5
    }

    new ParallelTransition() {
      children = Seq(translateTransition, fadeTransition, scaleTransition)
    }
  }

  private def makePutStoneTransition(pane: StackPane): Transition = {
    val translateTransition = new TranslateTransition() {
      node = pane
      duration = new Duration(100)

      fromX = 0
      fromY = -50
      toX = 0
      toY = 0
    }

    val fadeTransition = new FadeTransition() {
      node = pane
      duration = new Duration(200)

      fromValue = 0.0
      toValue = 1.0
    }

    val scaleTransition = new ScaleTransition() {
      node = pane
      duration = new Duration(100)

      fromX = 2.0
      fromY = 2.0
      toX = 1.0
      toY = 1.0
    }

    new ParallelTransition() {
      children = Seq(translateTransition, fadeTransition, scaleTransition)
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
      opacity = 0.0
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
    // initial cells
    cells = (0 until gridSize.getSize map { i: Int =>
      0 until gridSize.getSize map { j =>
        val node = new StackPane() {
          val button = new Button() {
            maxWidth = Double.MaxValue
            maxHeight = Double.MaxValue

            background <== when(hover)
              .choose(backgroundButtonHover)
              .otherwise(backgroundButton)
            onAction = _ => {
              println(s"clicked $i,$j")
            }
          }
          children = Seq(
            button,
            makeStone(makeStoneColor(Some(Black))),
            makeStone(makeStoneColor(Some(White)))
          ).asInstanceOf[Seq[Node]]
        }

        node
      }
    })

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

      prefWidth <== cellLength * gridSize.getSize
      prefHeight <== cellLength * gridSize.getSize
      maxWidth <== cellLength * gridSize.getSize
      maxHeight <== cellLength * gridSize.getSize

      columnConstraints = (0 until gridSize.getSize).map { _ =>
        new scalafx.scene.layout.ColumnConstraints() {
          hgrow = Priority.Always
          prefWidth <== cellLength
        }
      }

      rowConstraints = (0 until gridSize.getSize).map { _ =>
        new scalafx.scene.layout.RowConstraints() {
          vgrow = Priority.Always
          prefHeight <== cellLength
        }
      }
    }

    // add cells to grid
    for (i <- 0 until gridSize.getSize) {
      for (j <- 0 until gridSize.getSize) {
        grid.add(cells(i)(j), j, i)
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

    cellLength <== min(s.width / gridSize.getSize, s.height / gridSize.getSize)

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
