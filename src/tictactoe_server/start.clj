(ns tictactoe-server.start
  (:require [webserver.response :as response]
            [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.util :as util]
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

(defn- get-public-fields [game-state & [player-id]]
  {:board (:board game-state)
   :status "ready"
   :player-id (if player-id player-id (first (:player-ids game-state)))})

(defmethod app/route "/new" [request]
  (let [parameters (util/parse-parameters (:parameters request))]
    (if (contains-necessary-parameters? parameters)
      (let [game-state (:storage request)]
        (util/respond
          (get-public-fields
            (storage/-update
              game-state :fake-id (get-start-game-state parameters)))))
      [(response/make 400)])))

(def ^:private status-swapper {"ready" "waiting" "waiting" "ready"})
(defn- correct-status [{status :status :as game-state}]
  (assoc game-state :status (status-swapper status status)))
(defmethod app/route "/join" [{game-state :storage}]
  (if-let [player-id (players/join)]
    (util/respond
      (get-public-fields
        (correct-status (storage/-get game-state :fake-id)) player-id))
    [(response/make 400)]))
