(ns tetris.cljs.game-board)

(defprotocol GameBoard
  (board->node [_])

  (color-cells! [_ cells color]))
