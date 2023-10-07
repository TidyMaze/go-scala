package fr.yaro.go
package ai

import fr.yaro.go.engine.{
  Action,
  Black,
  Coord,
  Game,
  Grid,
  PutStone,
  State,
  White
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AITest extends AnyWordSpec with Matchers {
  val ai = new LessDumbAI()
  val game = new Game()

  "AI" should {
    "find a move" in {
      val rawGrid =
        """...
          |...
          |...""".stripMargin

      runAI(rawGrid) shouldBe a[PutStone]
    }

    "close an eye" in {
      val rawGrid =
        """.X.
          |XOX
          |...""".stripMargin

      assert(runAI(rawGrid) === PutStone(Coord(1, 2)))
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

      assert(runAI(rawGrid) === PutStone(Coord(5, 2)))
    }

    "defend against opponent attack" in {
      val rawGrid =
        """.O.
          |OXO
          |...""".stripMargin

      assert(runAI(rawGrid) === PutStone(Coord(1, 2)))
    }
  }

  private def runAI(rawGrid: String): Action = {
    val grid = Grid.fromString(rawGrid)

    println(grid)

    val state = State(
      grid = grid,
      turn = Black,
      captured = Map(White -> 0, Black -> 0),
      passed = Map(White -> false, Black -> false),
      Set.empty
    )
    val move = ai.findBestAction(game, state)
    move
  }
}
