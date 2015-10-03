(ns tictactoe-server.storage.protocol)

(defprotocol Storage
  (-list [this])
  (-get [this id])
  (-update [this id attributes]))
