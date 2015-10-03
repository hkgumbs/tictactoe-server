(ns tictactoe-server.components.json
  (:require [cheshire.core :as cheshire]
            [cheshire.generate :as generate])
  (:import [me.hkgumbs.tictactoe.main.java.board Board Board$Mark]))

(defn- encode-with-to-string [board builder]
  (.writeString builder (.toString board)))

(generate/add-encoder Board encode-with-to-string)
(generate/add-encoder Board$Mark encode-with-to-string)

(defn encode [object] (cheshire/encode object))

(defn decode [string] (cheshire/decode string true))
