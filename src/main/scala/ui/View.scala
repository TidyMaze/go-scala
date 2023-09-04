package fr.yaro.go
package ui

import engine.State

trait View {
  def render(state: State): Unit
}
