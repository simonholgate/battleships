(ns battleships.demo)

;; This is an example of how to create a player to submit to the server.
;; The entire namespace is submitted to the server.
;; You submit the file using the submit function in the client namespace.

(defn- random-coord 
  "Generates a random valid coordinate."
  []
  (let [rows ["A" "B" "C" "D" "E" "F" "G" "H" "I" "J"]
        columns (vec (range 1 11))]
    (str (rows (rand-int 10))
         (columns (rand-int 10)))))

(defn- random-orientation 
  "Generates a random valid orientation."
  []
  ([:h :v] (rand-int 2)))

;; (defn place-ship
;;   "The ship is a map which represents the ship. You must return a map with the square you want to place it, and the orientation (:v is vertical, :h is horizontal)"
;;   [ship]
;;   (cond 
;;    (= ship :patrol-boat)  {:square "A1" :orientation :h}
;;    (= ship :submarine)  {:square "B1" :orientation :h}
;;    (= ship :destroyer)  {:square "C1" :orientation :h}
;;    (= ship :battleship)  {:square "D1" :orientation :h}
;;    (= ship :aircraft-carrier)  {:square "E1" :orientation :h}))

(defn place-ship
  "The ship is a map which represents the ship. You must return a map with the square you want to place it, and the orientation (:v is vertical, :h is horizontal)"
  [ship]
  {:square (random-coord) :orientation (random-orientation)})

(defn adj-row
  "Find a row adjacent to the last one"
  [last-row]
  (let [rows ["A" "B" "C" "D" "E" "F" "G" "H" "I" "J"]
        last-row-ind (.indexOf rows (str last-row))
        count-rows (count rows)
        next-row ([-1 1] (rand-int 2))]
    (cond 
     (= last-row-ind (- count-rows 1)) (rows (- count-rows 2))
     (= last-row-ind 0) (rows 1)
     :else (rows (+ last-row-ind next-row)))))

(defn adj-col
  "Find a column adjacent to the last one"
  [last-col]
  (let [columns (vec (range 1 11))
        count-cols (count columns)
        next-col ([-1 1] (rand-int 2))]
    (cond 
     (>= last-col count-cols) (columns (- count-cols 1))
     (= last-col 1) 2
     :else (+ last-col next-col))))

(defn random-next
  "Choose a random square around the last hit"
  [last-hit]
  (let [last-row (first last-hit)
        last-col (Integer. (clojure.string/join (rest last-hit)))
        row-or-col ([:r :c] (rand-int 2))] ; Randomly choose a row or column 
        (if (= :r row-or-col)
          (str (adj-row last-row) last-col) ; Use the same column but choose an
                                        ; adjacent row
          (str last-row (adj-col last-col))))) ; Use the same row but choose an
                                        ; adjacent column

(defn next-shot
    "Where do you want to attack next?"
    [context opponent-context]
    (if (or (= (context :last-result) :miss) (= (context :last-result) nil)) 
      (random-coord)
      (random-next (last (context :hits)))))
