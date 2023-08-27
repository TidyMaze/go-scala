package fr.yaro.go

trait GridSize {
  def getSize: Int
}


case object Nine extends GridSize {
  override def getSize: Int = 9
}
case object Thirteen extends GridSize {
  override def getSize: Int = 13
}
case object Nineteen extends GridSize {
  override def getSize: Int = 19
}

case class Custom(size: Int) extends GridSize {
  override def getSize: Int = size
}
