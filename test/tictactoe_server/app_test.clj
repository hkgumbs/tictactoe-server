(ns tictactoe-server.app-test
  (:require [speclj.core :refer :all]
            [tictactoe-server.app :as app]
            [tictactoe-server.mock-socket :as socket]))

(describe "/"
  (it "responds with HTML"
    (let [response (socket/connect {} "/" "")]
      (should (.startsWith response "HTTP/1.1 200 OK"))
      (should-contain #"Content-Type:( )?text/html" response))))

(describe "Parameter mapping"
  (it "re-assigns correctly"
    (should=
      {:parameters {:number 123 :string "hello"}}
      (app/map-parameters {:parameters "number=123&string=hello"}))))
