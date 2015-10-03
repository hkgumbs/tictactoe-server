(ns tictactoe-server.core
  (:gen-class)
  (:require [webserver.servlet :as servlet]
            [tictactoe-server.components.controller :as controller]
            [tictactoe-server.endpoints.start]
            [tictactoe-server.endpoints.move]
            [tictactoe-server.storage.atom-storage])
  (:import tictactoe_server.storage.atom_storage.AtomStorage))

(def storage (AtomStorage. (atom {})))

(defn handle-with-atom-storage [handler]
  (fn [socket request] (handler socket (assoc request :storage storage))))

(defn -main [& args]
  (servlet/start args (handle-with-atom-storage controller/handle)))
