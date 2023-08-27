package fr.yaro.go

type Grid = Seq[Seq[Option[Color]]]

case class State (grid: Grid, turn: Color, captured: Map[Color, Int], passed: Map[Color, Boolean])