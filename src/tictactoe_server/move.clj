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

(defn- get-opponent-move []
  (let [{:keys [opponent board]} (storage/retrieve)]
    (if opponent (.run ^Algorithm opponent board))))

(defn- make-moves [position]
  (update-storage position)
  (if-let [opponent-move (get-opponent-move)] (update-storage opponent-move))
    (util/respond (select-keys (storage/retrieve) [:board])))

(defn valid-position? [position]
  (and
    (integer? position)
    (-> (storage/retrieve) :board .getEmptySpaceIds (.contains position))))

(defmethod app/route "/move" [request]
  (let [{position :position} (util/parse-parameters (:parameters request))]
    (if (valid-position? position)
      (make-moves position)
      [(response/make 400)])))
