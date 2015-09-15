(ns tictactoe-server.start
  (:require [webserver.response :as response]
            [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.util :as util]
            [tictactoe-server.players :as players])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard]
           [me.hkgumbs.tictactoe.main.java.rules DefaultRules]))

(defn- positive-int? [i] (and (integer? i) (pos? i)))
(def necessary-parameters {:size positive-int? :vs players/valid-type?})
(defn- contains-necessary-parameters? [parameters]
  (every? (fn [[k f]] (f (k parameters))) necessary-parameters))

(defn- get-start-record [{:keys [size vs]}]
  (players/set-record
    {:vs vs
     :rules (DefaultRules. size)
     :board (SquareBoard. size)
     :status "ready"}))

(defn- get-public-fields [record & [player-id]]
  {:board (:board record)
   :status (:status record)
   :player-id (if player-id player-id (first (:player-ids record)))})

(defmethod app/route "/new" [request]
  (let [parameters (util/parse-parameters (:parameters request))]
    (if (contains-necessary-parameters? parameters)
      (util/respond
        (get-public-fields
          (storage/create (get-start-record parameters))))
      [(response/make 400)])))

(defn- correct-status [{status :status :as record}]
  (assoc
    record
    :status
    (case status "ready" "waiting" "waiting" "ready" :default status)))
(defmethod app/route "/join" [request]
  (util/respond
    (get-public-fields
      (correct-status (storage/retrieve)) (players/get-join-id))))
