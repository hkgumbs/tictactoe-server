(ns tictactoe-server.core
  (:gen-class)
  (:require [webserver.app :as app]
            [tictactoe-server.app :as ttt]
            [tictactoe-server.root]
            [tictactoe-server.start]
            [tictactoe-server.move]
            [tictactoe-server.util :as util]))

(defn -main [& [port]]
  (let [server (java.net.ServerSocket. (util/parse-int port 5000))]
    (ttt/initialize)
    (println "Serving Tic Tac Toe over HTTP...")
    (while (not (.isClosed server))
      (let [socket (.accept server)]
        (app/relay ttt/handle socket)))))
