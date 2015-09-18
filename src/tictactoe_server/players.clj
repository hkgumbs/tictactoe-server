(ns tictactoe-server.players
  (:require [tictactoe-server.storage :as storage])
  (:import [me.hkgumbs.tictactoe.main.java.board Board Board$Mark]
           [me.hkgumbs.tictactoe.main.java.rules Rules]
           [me.hkgumbs.tictactoe.main.java.player
            Algorithm Minimax NaiveChoice]))

(def ^:private marks [Board$Mark/X Board$Mark/O])

(def ^:private types ["local" "remote" "minimax" "naive"])
(defn valid-type? [vs] (.contains types vs))

(defn- get-cpu [{:keys [vs rules]}]
  ({"minimax" (Minimax. (second marks) rules) "naive" (NaiveChoice.)} vs))

(defn- get-unique-id []
  (Integer. ^String (apply str (repeatedly 5 #(rand-int 10)))))

(defn- get-local-ids [] [(get-unique-id)])
(defn- get-remote-ids [] [(get-unique-id) (get-unique-id)])
(defn- get-player-ids [{vs :vs}]
  (if (= vs "remote") (get-remote-ids) (get-local-ids)))

(defn set-game-state [game-state]
  (let [player-ids (get-player-ids game-state)
        state {:cpu (get-cpu game-state)
               :player-ids player-ids
               :available-id (second player-ids)
               :turn (first marks)}]
    [(get-unique-id) (into game-state state)]))

(defn- make-move [position {:keys [board turn player-ids] :as game-state}]
  (into game-state
        {:player-ids (reverse player-ids)
         :turn (.other turn)
         :board (.add board position turn)}))

(defn- get-cpu-move [{:keys [cpu board rules]}]
  (let [ongoing (not (.gameIsOver ^Rules rules board))]
    (if (and cpu ongoing) (.run ^Algorithm cpu board))))

(defn make-moves [game-state position]
  (let [game-state (make-move position game-state)
        cpu-move (get-cpu-move game-state)]
    (if cpu-move (make-move cpu-move game-state) game-state)))

(defn- find-available [game-states]
  (first (filter #(:available-id (second %)) game-states)))
(defn join [game-states]
  (if-let [[game-id game-state] (find-available game-states)]
    [game-id (:available-id game-state) (dissoc game-state :available-id)]))
