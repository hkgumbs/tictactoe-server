(ns tictactoe-server.storage
  (:require [cheshire.core :as json])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard]))

(def game (atom {}))

(defn create [{size :size}]
  (json/generate-string
  (reset!
    game
    {:board (.toString (SquareBoard. size))
     :game-id 0})))

(defn retrieve [id] @game)
