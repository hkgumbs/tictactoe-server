(ns tictactoe-server.players
  (:require [tictactoe-server.storage :as storage])
  (:import [me.hkgumbs.tictactoe.main.java.board Board Board$Mark]
           [me.hkgumbs.tictactoe.main.java.player
            Algorithm Minimax NaiveChoice]))

(def marks [Board$Mark/X Board$Mark/O])

(def types ["local" "remote" "minimax" "naive"])
(defn valid-type? [vs] (.contains types vs))

(defn- get-cpu [{:keys [vs rules]}]
  ({"minimax" (Minimax. (second marks) rules) "naive" (NaiveChoice.)} vs))

(defn- get-unique-id []
  (Integer. ^String (apply str (repeatedly 5 #(rand-int 10)))))
(defn- get-player-ids [{vs :vs}]
  (if (= vs "remote") [(get-unique-id) (get-unique-id)] [(get-unique-id)]))

(defn- get-turn [_] (first marks))
(defn set-record [record]
  (into record
        (map
          (fn [[k f]] [k (f record)])
          {:cpu get-cpu :player-ids get-player-ids :turn get-turn})))

(defn- make-move [position {:keys [board turn player-ids] :as record}]
  (into record
        {:player-ids (reverse player-ids)
         :turn (.other turn)
         :board (.add board position turn)}))

(defn- get-cpu-move [{:keys [cpu board rules]}]
  (let [ongoing (not (.gameIsOver rules board))]
    (if (and cpu ongoing) (.run ^Algorithm cpu board))))

(defn make-moves [record & [position & more]]
  (let [record (make-move position record)
        cpu-move (get-cpu-move record)]
    (if cpu-move (make-move cpu-move record) record)))

(defn get-join-id []
  (let [{:keys [turn player-ids]} (storage/retrieve)]
    (nth (reverse player-ids) (.indexOf marks turn))))
