(ns tetris.cljs.tetraminos)

(def shapes
  #{
    ;; Line
    {:raw-cells [[0 0] [0 1] [0 2] [0 3]]
     :order 2}
    
    ;; T
    {:raw-cells [[0 0] [0 1] [0 2] [1 1]]
     :order 4}

    ;; step
    {:raw-cells [[0 0] [0 1] [1 1] [1 2]]
     :order 2}

    ;; mirrored step
    {:raw-cells [[1 0] [0 1] [1 1] [0 2]]
     :order 2}

    ;; L
    {:raw-cells [[0 0] [0 1] [1 1] [2 1]]
     :order 4}

    ;; mirrored L
    {:raw-cells [[0 2] [0 1] [1 1] [2 1]]
     :order 4}

    ;; square
    {:raw-cells [[0 0] [0 1] [1 1] [1 0]]
     :order 1}})

(def colors
  #{"#a00" "#00a" "#080" "orange" "#a50" "#a0a" "yellow"})

(defn translate [[t-x t-y]]
  (fn [[x y]]
    [(+ t-x x) (+ t-y y)]))

(defn rotate-shape-90-about-origin [[x y]]
  [y (- x)])

;; Here we're assuming that the fixpoint of all of the shapes is at [0 1]
(defn rotate-90 [raw-cells]
  (let [fixpoint [0 1]]
    (map (comp (translate fixpoint)
               rotate-shape-90-about-origin
               (translate (map - fixpoint)))
         raw-cells)))

(defn rotate-shape-cells [{:keys [raw-cells order] :as shape} rotation]
  (nth (iterate rotate-90 raw-cells) (mod rotation order)))

(defn piece->cells [{:keys [shape location rotation]}]
  ;; returns [[x y]]
  (-> shape
      (rotate-shape-cells rotation)
      (->> (map (translate location)))))
