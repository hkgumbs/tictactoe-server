(ns tictactoe-server.move
  (:require [webserver.response :as response]
            [tictactoe-server.app :as app]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.util :as util])
  (:import [me.hkgumbs.tictactoe.main.java.player Algorithm]
           [me.hkgumbs.tictactoe.main.java.rules Rules]))

(defn- get-status [player-id {:keys [rules board player-ids]}]
  (cond
    (.gameIsOver ^Rules rules board) "terminated"
    (= player-id (first player-ids)) "ready"
    :default "waiting"))
(defn- update-status [player-id record]
  (assoc record :status (get-status player-id record)))

(defn- make-move [position {:keys [board turn player-ids] :as record}]
  (into record
        {:player-ids (reverse player-ids)
         :turn (.other turn)
         :board (.add board position turn)}))

(defn- get-cpu-move [{:keys [cpu board rules]}]
  (let [ongoing (not (.gameIsOver rules board))]
    (if (and cpu ongoing) (.run ^Algorithm cpu board))))

(defn- make-moves [position record]
  (let [record (make-move position record)
        cpu-move (get-cpu-move record)]
    (if cpu-move (make-move cpu-move record) record)))

(defn- respond [player-id position record]
  (util/respond
    (select-keys
      (storage/modify
        #(into % (update-status player-id (make-moves position record))))
      [:board :status])))

(defn- valid-move? [position player-id {:keys [player-ids status board]}]
  (and
    (= player-id (first player-ids))
    (not= status "terminated")
    (integer? position)
    (-> board .getEmptySpaceIds (.contains position))))

(defmethod app/route "/move" [{parameters :parameters}]
  (let [{:keys [position player-id]} (util/parse-parameters parameters)
        record (storage/retrieve)]
    (if (valid-move? position player-id record)
      (respond player-id position record)
      [(response/make 400)])))
