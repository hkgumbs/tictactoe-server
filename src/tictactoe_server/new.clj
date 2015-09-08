(ns tictactoe-server.new
  (:require [webserver.response :as response]
            [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.util :as util])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard Board$Mark]
           [me.hkgumbs.tictactoe.main.java.player Minimax NaiveChoice]
           [me.hkgumbs.tictactoe.main.java.rules DefaultRules]))

(def necessary-parameters
  {:size #(and (integer? %) (pos? %))
   :vs #(.contains ["naive" "minimax" "local"] %)})

(defn- get-opponent [vs rules]
  ({"minimax" (Minimax. Board$Mark/O rules)
    "naive" (NaiveChoice.)} vs))

(defn- get-start-record [{:keys [size vs]}]
  (let [rules (DefaultRules. size)]
    {:rules rules
     :board (SquareBoard. size)
     :opponent (get-opponent vs rules)
     :turn Board$Mark/X}))

(defn- contains-necessary-parameters? [parameters]
  (every? (fn [[k f]] (f (k parameters))) necessary-parameters))

(defmethod app/route "/new" [request]
  (let [parameters (util/parse-parameters (:parameters request))]
    (if (contains-necessary-parameters? parameters)
      (util/respond
        (select-keys
          (storage/create (get-start-record parameters))
          [:board]))
      [(response/make 400)])))
