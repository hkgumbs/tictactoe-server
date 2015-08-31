(ns tictactoe-server.state
  (:require [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.util :as util])
  (:import [me.hkgumbs.tictactoe.main.java.board
             SquareBoard Board$Mark]))

(defn- get-board [{size :size}]
  (SquareBoard. size))

(defmethod app/route "/new" [request]
  (let [parameters (util/parse-parameters (:parameters request))]
    (storage/create
      {:board (get-board parameters) :turn Board$Mark/X})
    (util/respond "")))

(defmethod app/route "/status" [_]
  (let [{board :board} (storage/retrieve)]
    (util/respond {:status "waiting" :board (.toString board)})))
