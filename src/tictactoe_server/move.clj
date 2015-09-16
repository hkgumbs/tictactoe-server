(ns tictactoe-server.move
  (:require [webserver.response :as response]
            [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.util :as util]
            [tictactoe-server.players :as players])
  (:import [me.hkgumbs.tictactoe.main.java.rules Rules]))

(defn- get-status [player-id {:keys [rules board player-ids]}]
  (cond
    (.gameIsOver ^Rules rules board) "terminated"
    (= player-id (first player-ids)) "ready"
    :default "waiting"))
(defn- update-status [player-id record]
  (assoc record :status (get-status player-id record)))

(defn- respond [player-id position record]
  (util/respond
    (select-keys
      (storage/modify
        into (update-status player-id (players/make-moves record position)))
      [:board :status])))

(defn- valid-move? [position player-id {:keys [player-ids board rules]}]
  (and
    (= player-id (first player-ids))
    (integer? position)
    (not (.gameIsOver ^Rules rules board))
    (.validateMove ^Rules rules board position)))

(defmethod app/route "/move" [{parameters :parameters}]
  (let [{:keys [position player-id]} (util/parse-parameters parameters)
        record (storage/retrieve)]
    (if (valid-move? position player-id record)
      (respond player-id position record)
      [(response/make 400)])))
