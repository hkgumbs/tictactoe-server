(ns tictactoe-server.move
  (:require [webserver.response :as response]
            [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.util :as util]
            [tictactoe-server.players :as players])
  (:import [me.hkgumbs.tictactoe.main.java.rules Rules]))

(defn- get-game-state [request] (storage/-get (:storage request) :fake-id))
(defn- update-game-state [request attributes]
  (storage/-update (:storage request) :fake-id attributes))

(defn- get-status [player-id {:keys [rules board player-ids]}]
  (cond
    (.gameIsOver ^Rules rules board) "terminated"
    (= player-id (first player-ids)) "ready"
    :default "waiting"))

(defn- respond [player-id {board :board :as entry}]
  (util/respond {:board board :status (get-status player-id entry)}))
(defn- process [check modifier {parameters :parameters :as request}]
  (let [parameters (util/parse-parameters parameters)
        entry (get-game-state request)]
    (if (check parameters entry)
      (respond
        (:player-id parameters)
        (update-game-state request (modifier parameters entry)))
      [(response/make 400)])))

(defn- valid-move?
  [{:keys [position player-id]} {:keys [player-ids board rules]}]
  (and
    (= player-id (first player-ids))
    (integer? position)
    (not (.gameIsOver ^Rules rules board))
    (.validateMove ^Rules rules board position)))
(defn- move [{:keys [position]} entry] (players/make-moves entry position))
(defmethod app/route "/move" [request]
  (process valid-move? move request))

(defn- valid-player? [{player-id :player-id} {player-ids :player-ids}]
  (.contains player-ids player-id))
(defmethod app/route "/status" [request]
  (process valid-player? (constantly (get-game-state request)) request))
