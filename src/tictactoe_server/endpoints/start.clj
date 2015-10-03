(ns tictactoe-server.endpoints.start
  (:require [webserver.response :as response]
            [tictactoe-server.router.app :as app]
            [tictactoe-server.storage.protocol :as storage]
            [tictactoe-server.endpoints.game :as game])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard]
           [me.hkgumbs.tictactoe.main.java.rules DefaultRules]))

(defn- positive-int? [i] (and (integer? i) (pos? i)))

(def ^:private necessary-parameters
  {:size positive-int? :vs game/valid-type?})

(defn- contains-necessary-parameters? [parameters]
  (every? (fn [[k f]] (f (k parameters))) necessary-parameters))

(defn- get-start-state [{:keys [size vs]}]
  (game/set-game-state
    {:vs vs
     :rules (DefaultRules. size)
     :board (SquareBoard. size)}))

(defn- get-public-fields [[game-id mark {:keys [player-ids]}]]
  {:player-id (first player-ids) :game-id game-id :mark mark})

(defmethod app/route "/new" [{parameters :parameters store :storage}]
  (if (contains-necessary-parameters? parameters)
    (get-public-fields
      (let [[game-id _ game-state :as fields] (get-start-state parameters)]
        (storage/-update store game-id game-state) fields))))

(defmethod app/route "/join" [{store :storage}]
  (let [all-states (storage/-list store)]
    (if-let [[game-id mark player-id new-game-state] (game/join all-states)]
      (do
        (storage/-update store game-id new-game-state)
        {:player-id player-id :game-id game-id :mark mark}))))
