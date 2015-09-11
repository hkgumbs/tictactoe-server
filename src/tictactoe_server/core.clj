(ns tictactoe-server.core
  (:gen-class)
  (:require [webserver.servlet :as servlet]
            [tictactoe-server.app :as app]
            [tictactoe-server.root]
            [tictactoe-server.start]
            [tictactoe-server.move]
            [tictactoe-server.util :as util]))

(defn -main [& args]
  (servlet/start (concat args ["-d" "assets"]) app/handle))
