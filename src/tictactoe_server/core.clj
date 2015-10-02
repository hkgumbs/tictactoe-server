(ns tictactoe-server.core
  (:gen-class)
  (:require [webserver.servlet :as servlet]
            [tictactoe-server.app :as app]
            [tictactoe-server.start]
            [tictactoe-server.move]
            [tictactoe-server.storage]
            [tictactoe-server.util :as util])
  (:import tictactoe_server.storage.AtomStorage))

(def storage (AtomStorage. (atom {})))

(defn handle-with-atom-storage [handler]
  (fn [socket request] (handler socket (assoc request :storage storage))))

(defn -main [& args]
  (servlet/start
    (concat ["-d" "assets"] args)
    (handle-with-atom-storage app/handle)))
