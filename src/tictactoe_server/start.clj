(ns tictactoe-server.start
  (:require [webserver.response :as response]
            [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.players :as players])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard]
           [me.hkgumbs.tictactoe.main.java.rules DefaultRules]))

(defn- positive-int? [i] (and (integer? i) (pos? i)))
(def ^:private necessary-parameters
  {:size positive-int? :vs players/valid-type?})
(defn- contains-necessary-parameters? [parameters]
  (every? (fn [[k f]] (f (k parameters))) necessary-parameters))

(defn- get-start-game-state [{:keys [size vs]}]
  (players/set-game-state
    {:vs vs
     :rules (DefaultRules. size)
     :board (SquareBoard. size)}))

(defn- map-first-player-id [game-state]
  {:player-id (first (:player-ids game-state))})
(defmethod app/route "/new" [{parameters :parameters :as request}]
  (if (contains-necessary-parameters? parameters)
    (map-first-player-id
      (storage/-update
        (:storage request) :fake-id (get-start-game-state parameters)))))

(defmethod app/route "/join" [{game-state :storage}]
  (if-let [player-id (players/join)] {:player-id player-id}))
