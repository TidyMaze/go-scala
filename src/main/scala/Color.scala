package fr.yaro.go

sealed trait Color {
  def opponent: Color = this match {
    case Black => White
    case White => Black
  }
}
case object Black extends Color
case object White extends Color
