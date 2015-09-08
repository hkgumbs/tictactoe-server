(ns tictactoe-server.move
  (:require [webserver.response :as response]
            [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.util :as util])
  (:import [me.hkgumbs.tictactoe.main.java.player Algorithm]
           [me.hkgumbs.tictactoe.main.java.rules Rules]))

(defn- get-status [rules board]
  (if (.gameIsOver ^Rules rules board) "terminated" "ready"))
(defn- update-storage [position]
  (let [{:keys [board turn rules]} (storage/retrieve)
        board (.add board position turn)]
    (storage/modify
      #(into % {:board board
                :status (get-status rules board)
                :turn (.other turn)}))))

(defn- get-opponent-move []
  (let [{:keys [opponent board rules]} (storage/retrieve)
        ongoing (not (.gameIsOver rules board))]
    (if (and opponent ongoing) (.run ^Algorithm opponent board))))

(defn- make-moves [position]
  (update-storage position)
  (if-let [opponent-move (get-opponent-move)] (update-storage opponent-move))
  (let [{:keys [board status rules]} (storage/retrieve)]
    (util/respond {:board board :status status})))

(defn valid-position? [position]
  (and
    (integer? position)
    (-> (storage/retrieve) :board .getEmptySpaceIds (.contains position))))

(defmethod app/route "/move" [request]
  (let [{position :position} (util/parse-parameters (:parameters request))]
    (if (valid-position? position)
      (make-moves position)
      [(response/make 400)])))
