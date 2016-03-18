package geotrellis.core

package object poly {
  def createTiles[K, V](tile: (K, V)): Seq[(K, V)] = Seq(tile)
  def mergeTiles1[K, V](tiles: Seq[(K, V)], tile: (K, V)): Seq[(K, V)] = tiles :+ tile
  def mergeTiles2[K, V](tiles1: Seq[(K, V)], tiles2: Seq[(K, V)]): Seq[(K, V)] = tiles1 ++ tiles2
}
