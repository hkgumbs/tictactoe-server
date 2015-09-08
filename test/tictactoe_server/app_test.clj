(ns tictactoe-server.app-test
  (:require [speclj.core :refer :all]
            [tictactoe-server.app :as app]
            [tictactoe-server.mock-socket :as socket]
            [webserver.response :as response]))

(describe "Default route behavior"
  (before ((:initializer app/responder)))
  (it "404s on non-existent file"
    (should= (response/make 404) (socket/connect "/foobar" "")))
  (it "doesn't 404 on stylesheet"
    (should-not= (response/make 404) (socket/connect "style.css" ""))))
