(ns tictactoe-server.start
  (:require [webserver.response :as response]
            [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.util :as util])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard Board$Mark]
           [me.hkgumbs.tictactoe.main.java.player Minimax NaiveChoice]
           [me.hkgumbs.tictactoe.main.java.rules DefaultRules]))

(def necessary-parameters
  {:size #(and (integer? %) (pos? %))
   :vs #(.contains ["naive" "minimax" "local" "remote"] %)})
(defn- contains-necessary-parameters? [parameters]
  (every? (fn [[k f]] (f (k parameters))) necessary-parameters))

(defn- get-opponent [vs rules]
  ({"minimax" (Minimax. Board$Mark/O rules) "naive" (NaiveChoice.)} vs))

(defn- get-unique-id []
  (Integer. ^String (apply str (repeatedly 5 #(rand-int 10)))))
(defn- get-player-ids [vs]
  (if (= vs "remote") [(get-unique-id) (get-unique-id)] [(get-unique-id)]))

(defn- get-start-record [{:keys [size vs]}]
  (let [rules (DefaultRules. size)
        player-ids [(get-unique-id) (get-unique-id)]]
    {:vs vs
     :rules rules
     :board (SquareBoard. size)
     :cpu (get-opponent vs rules)
     :player-ids (get-player-ids vs)
     :status "ready"
     :turn Board$Mark/X}))

(defn- get-public-fields [record which]
  {:board (:board record)
   :status (:status record)
   :player-id (which (:player-ids record))})

(defmethod app/route "/new" [request]
  (let [parameters (util/parse-parameters (:parameters request))]
    (if (contains-necessary-parameters? parameters)
      (util/respond
        (get-public-fields
          (storage/create (get-start-record parameters)) first))
      [(response/make 400)])))

(defn- get-corrected-status [{status :status :as record}]
  (let [corrected-status ({"ready" "waiting" "waiting" "ready"} status)]
    (assoc record :status (if corrected-status corrected-status status))))
(defmethod app/route "/join" [request]
  (util/respond
    (get-public-fields (get-corrected-status (storage/retrieve)) second)))
