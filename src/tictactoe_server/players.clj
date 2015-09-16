(ns tictactoe-server.players
  (:require [tictactoe-server.storage :as storage])
  (:import [me.hkgumbs.tictactoe.main.java.board Board Board$Mark]
           [me.hkgumbs.tictactoe.main.java.rules Rules]
           [me.hkgumbs.tictactoe.main.java.player
            Algorithm Minimax NaiveChoice]))

(def ^:private marks [Board$Mark/X Board$Mark/O])

(def ^:private available-id (atom nil))
(defn- reset-available-id ([newval] (reset! available-id newval)))
(defn- clear-available-id []
  (let [oldval @available-id] (reset-available-id nil) oldval))

(def ^:private types ["local" "remote" "minimax" "naive"])
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
(defn set-entry [entry]
  (let [setters {:cpu get-cpu :player-ids get-player-ids :turn get-turn}]
    (into entry (map (fn [[k f]] [k (f entry)]) setters))))

(defn- make-move [position {:keys [board turn player-ids] :as entry}]
  (into entry
        {:player-ids (reverse player-ids)
         :turn (.other turn)
         :board (.add board position turn)}))

(defn- get-cpu-move [{:keys [cpu board rules]}]
  (let [ongoing (not (.gameIsOver ^Rules rules board))]
    (if (and cpu ongoing) (.run ^Algorithm cpu board))))

(defn make-moves [entry & [position & more]]
  (let [entry (make-move position entry)
        cpu-move (get-cpu-move entry)]
    (if cpu-move (make-move cpu-move entry) entry)))

(defn join [] (clear-available-id))
