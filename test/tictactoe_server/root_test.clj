(ns tictactoe-server.root-test
  (:require [speclj.core :refer :all]
            [tictactoe-server.root]
            [tictactoe-server.mock-socket :as socket]))

(describe "/"
  (it "responds with HTML"
    (let [response (socket/connect (socket/store {}) "/" "")]
      (should (.startsWith response "HTTP/1.1 200 OK"))
      (should-contain #"Content-Type:( )?text/html" response))))
