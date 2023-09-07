package fr.yaro.go
package ai

import fr.yaro.go.engine.{Black, Coord, Game, Grid, Move, State, White}
import org.scalatest.wordspec.AnyWordSpec

class AITest extends AnyWordSpec {
  val ai = new LessDumbAI()
  val game = new Game()

  "AI" should {
    "find a move" in {
      val rawGrid =
        """...
          |...
          |...""".stripMargin

      assert(runAI(rawGrid).isDefined)
    }

    "close an eye" in {
      val rawGrid =
        """.X.
          |XOX
          |...""".stripMargin

        assert(runAI(rawGrid) === Some(Move(Coord(1, 2))))
    }

    "close the biggest group of stones" in {
      val rawGrid =
        """XXX.XXXX
          |XOX.XOOX
          |X.X.X.XX
          |........
          |........
          |........
          |........
          |........""".stripMargin

      assert(runAI(rawGrid) === Some(Move(Coord(5,2))))
    }
  }

  private def runAI(rawGrid: String): Option[Move] = {
    val grid = Grid.fromString(rawGrid)

    println(grid)

    val state = State(
      grid = grid,
      turn = Black,
      captured = Map(White -> 0, Black -> 0),
      passed = Map(White -> false, Black -> false),
      Set.empty
    )
    val move = ai.findBestMove(game, state)
    move
  }
}
