(ns tictactoe-server.move
  (:require [webserver.response :as response]
            [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.players :as players])
  (:import [me.hkgumbs.tictactoe.main.java.rules Rules]))

(defn- get-game-state [{store :storage {game-id :game-id} :parameters}]
  (storage/-get store game-id))
(defn- update-game-state [{store :storage parameters :parameters} attributes]
  (storage/-update store (:game-id parameters) attributes))

(defn- get-status [player-id {:keys [rules board player-ids]}]
  (cond
    (.gameIsOver ^Rules rules board) "terminated"
    (= player-id (first player-ids)) "ready"
    :default "waiting"))

(defn- get-public-fields [player-id {board :board :as game-state}]
  {:board board :status (get-status player-id game-state)})
(defn- process [check modifier {parameters :parameters :as request}]
  (if-let [game-state (get-game-state request)]
    (if (check parameters game-state)
      (get-public-fields
        (:player-id parameters)
        (update-game-state request (modifier parameters game-state))))))

(defn- valid-move?
  [{:keys [position player-id]} {:keys [player-ids board rules]}]
  (and
    (= player-id (first player-ids))
    (integer? position)
    (not (.gameIsOver ^Rules rules board))
    (.validateMove ^Rules rules board position)))
(defn- move [{:keys [position]} game-state]
  (players/make-moves game-state position))
(defmethod app/route "/move" [request] (process valid-move? move request))

(defn- valid-player? [{:keys [player-id]} {:keys [player-ids]}]
  (and player-ids (.contains player-ids player-id)))
(defmethod app/route "/status" [request]
  (process valid-player? (constantly (get-game-state request)) request))
