(ns tictactoe-server.new
  (:require [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.util :as util])
  (:import [me.hkgumbs.tictactoe.main.java.board
             SquareBoard Board$Mark]))

(defn- get-board [{size :size}]
  (SquareBoard. size))

(defmethod app/route "/new" [request]
  (let [parameters (util/parse-parameters (:parameters request))
        board (get-board parameters)]
    (storage/create {:board board  :turn Board$Mark/X})
    (util/respond {:board board})))
