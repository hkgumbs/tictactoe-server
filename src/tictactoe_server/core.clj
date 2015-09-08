(ns tictactoe-server.core
  (:gen-class)
  (:require [webserver.app :as app]
            [tictactoe-server.app :as backing-app]
            [tictactoe-server.root]
            [tictactoe-server.new]
            [tictactoe-server.move]
            [tictactoe-server.util :as util]))

(defn -main [& [port]]
  (let [server (java.net.ServerSocket. (util/parse-int port 5000))
        {:keys [valid-request-handler]} backing-app/responder]
    (println "Serving Tic Tac Toe over HTTP...")
    (while (not (.isClosed server))
      (let [socket (.accept server)]
        (app/relay valid-request-handler socket)))))
