package fr.yaro.go
package helpers

import scala.util.Random

object RandomHelpers {
  def randomIn[A](arr: Seq[A]): A = arr(Random.nextInt(arr.length))
}
