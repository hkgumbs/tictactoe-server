(ns tictactoe-server.app-test
  (:require [speclj.core :refer :all]
            [tictactoe-server.app :as app]
            [tictactoe-server.mock-socket :as socket]))

(defn- validate-static-response [uri matcher]
  (let [response (socket/connect {} uri "")]
    (should (.startsWith response "HTTP/1.1 200 OK"))
    (should-contain matcher response)))

(describe "Static endpoints"
  (it "responds with HTML at root"
    (validate-static-response "/" #"Content-Type:( )?text/html"))

  (it "responds with CSS at style.css"
    (validate-static-response "/style.css" #"Content-Type:( )?text/css"))

  (it "responds with Javascript at js/src/*"
    (validate-static-response
      "/js/src/game.js" #"Content-Type:( )?application/javascript")
    (validate-static-response
      "/js/src/ui.js" #"Content-Type:( )?application/javascript"))

  (it "responds with nothing at undefined uri"
    (should= "" (socket/connect {} "some/non-existent/path" ""))))

(describe "Parameter mapping"
  (it "re-assigns correctly"
    (should=
      {:parameters {:number 123 :string "hello"}}
      (app/map-parameters {:parameters "number=123&string=hello"}))))
