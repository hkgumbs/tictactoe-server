(ns tictactoe-server.move
  (:require [webserver.response :as response]
            [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.util :as util])
  (:import [me.hkgumbs.tictactoe.main.java.player Algorithm]))

(defn- update-storage [position]
  (let [{:keys [board turn]} (storage/retrieve)]
    (storage/modify
      #(into % {:board (.add board position turn) :turn (.other turn)}))))

(defn- get-cpu-move []
  (let [{:keys [algorithm board]} (storage/retrieve)]
    (.run ^Algorithm algorithm board)))

(defn valid-position? [position]
  (and
    (integer? position)
    (-> (storage/retrieve) :board .getEmptySpaceIds (.contains position))))

(defmethod app/route "/move" [request]
  (let [{position :position} (util/parse-parameters (:parameters request))]
    (if (valid-position? position)
      (do (update-storage position)
          (util/respond
            (select-keys (update-storage (get-cpu-move)) [:board])))
      (response/make 400))))
