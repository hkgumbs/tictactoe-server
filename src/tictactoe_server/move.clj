(ns tictactoe-server.move
  (:require [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.util :as util])
  (:import [me.hkgumbs.tictactoe.main.java.player NaiveChoice]))

(defn- update-storage [position]
  (storage/modify
    (fn [{:keys [board turn]}]
      {:board (.add board position turn)
       :turn (.other turn)})))

(defn- get-cpu-move []
  (let [{board :board} (storage/retrieve)]
    (.run (NaiveChoice.) board)))

(defmethod app/route "/move" [request]
  (let [{position :position} (util/parse-parameters (:parameters request))]
    (doall (map update-storage [position (get-cpu-move)])))
  (util/respond))
