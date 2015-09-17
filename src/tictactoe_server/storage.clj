(ns tictactoe-server.storage)

(defprotocol Storage
  (-get [this id])
  (-update [this id attributes]))

(defrecord AtomStorage [^clojure.lang.Atom state]
  Storage
  (-get [this id] @state)
  (-update [this id attributes] (swap! state into attributes)))
