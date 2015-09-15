(ns tictactoe-server.players
  (:require [tictactoe-server.storage :as storage])
  (:import [me.hkgumbs.tictactoe.main.java.board Board Board$Mark]
           [me.hkgumbs.tictactoe.main.java.rules Rules]
           [me.hkgumbs.tictactoe.main.java.player
            Algorithm Minimax NaiveChoice]))

(def marks [Board$Mark/X Board$Mark/O])

(def available-id (atom nil))
(defn- reset-available-id ([newval] (reset! available-id newval)))
(defn- clear-available-id []
  (let [oldval @available-id] (reset-available-id nil) oldval))

(def types ["local" "remote" "minimax" "naive"])
(defn valid-type? [vs] (.contains types vs))

(defn- get-cpu [{:keys [vs rules]}]
  ({"minimax" (Minimax. (second marks) rules) "naive" (NaiveChoice.)} vs))

(defn- get-unique-id []
  (Integer. ^String (apply str (repeatedly 5 #(rand-int 10)))))
(defn- get-local-ids [] (clear-available-id) [(get-unique-id)])
(defn- get-remote-ids []
  (let [player-ids [(get-unique-id) (get-unique-id)]]
    (reset-available-id (second player-ids)) player-ids))
(defn- get-player-ids [{vs :vs}]
  (if (= vs "remote") (get-remote-ids) (get-local-ids)))

(defn- get-turn [_] (first marks))
(defn set-record [record]
  (let [setters {:cpu get-cpu :player-ids get-player-ids :turn get-turn}]
    (into record (map (fn [[k f]] [k (f record)]) setters))))

(defn- make-move [position {:keys [board turn player-ids] :as record}]
  (into record
        {:player-ids (reverse player-ids)
         :turn (.other turn)
         :board (.add board position turn)}))

(defn- get-cpu-move [{:keys [cpu board rules]}]
  (let [ongoing (not (.gameIsOver ^Rules rules board))]
    (if (and cpu ongoing) (.run ^Algorithm cpu board))))

(defn make-moves [record & [position & more]]
  (let [record (make-move position record)
        cpu-move (get-cpu-move record)]
    (if cpu-move (make-move cpu-move record) record)))

(defn join [] (clear-available-id))
