(ns tictactoe-server.storage)

(defprotocol Storage
  (-list [this])
  (-get [this id])
  (-update [this id attributes]))

(defrecord AtomStorage [^clojure.lang.Atom state]
  Storage
  (-list [this] @state)
  (-get [this id] (@state id))
  (-update [this id attributes]
    ((swap! state assoc id attributes) id)))
