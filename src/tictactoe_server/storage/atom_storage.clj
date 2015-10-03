(ns tictactoe-server.storage.atom_storage
  (:require [tictactoe-server.storage.protocol :as storage]))

(defrecord AtomStorage [^clojure.lang.Atom state]
  storage/Storage
  (storage/-list [this] @state)
  (storage/-get [this id] (@state id))
  (storage/-update [this id attributes]
    ((swap! state assoc id attributes) id)))
