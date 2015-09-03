(ns tictactoe-server.json
  (:require [cheshire.core :as cheshire]
            [cheshire.generate :as generate])
  (:import [me.hkgumbs.tictactoe.main.java.board Board Board$Mark]))

(defn- encode-board [board builder]
  (.writeString builder (.toString board)))
(generate/add-encoder Board encode-board)

(defn encode [object] (cheshire/encode object))
(defn decode [string] (println string) (cheshire/decode string true))
