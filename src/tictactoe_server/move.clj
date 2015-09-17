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
(defn- update-status [player-id entry]
  (assoc entry :status (get-status player-id entry)))

(defn- respond [player-id position entry game-state]
  (util/respond
    (select-keys
      (storage/-update
        game-state :fake-id
        (update-status player-id (players/make-moves entry position)))
      [:board :status])))

(defn- valid-move? [position player-id {:keys [player-ids board rules]}]
  (and
    (= player-id (first player-ids))
    (integer? position)
    (not (.gameIsOver ^Rules rules board))
    (.validateMove ^Rules rules board position)))

(defmethod app/route "/move" [{parameters :parameters game-state :storage}]
  (let [{:keys [position player-id]} (util/parse-parameters parameters)
        entry (storage/-get game-state :fake-id)]
    (if (valid-move? position player-id entry)
      (respond player-id position entry game-state)
      [(response/make 400)])))

(defmethod app/route "/status" [{parameters :parameters game-state :storage}]
  (let [player-id (:player-id (util/parse-parameters parameters))
        entry (storage/-get game-state :fake-id)
        {:keys [player-ids board]} entry]
    (if (.contains player-ids player-id)
      (util/respond {:board board :status (get-status player-id entry)})
      [(response/make 400)])))
