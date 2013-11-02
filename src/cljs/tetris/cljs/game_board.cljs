(ns tetris.cljs.game-board)

(defprotocol GameBoard
  (board->node [_])

  (color-cell! [_ cell color])
  (color-cells! [_ cells color])
  (color-rows! [_ rows color])
  
  (command-ch [_])

  (flash-cells! [_ cells]))
