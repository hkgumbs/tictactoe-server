(ns tictactoe-server.new-game-test
  (:require [speclj.core :refer :all]
            [tictactoe-server.app]
            [tictactoe-server.routes :as routes]
            [tictactoe-server.mock-socket :as socket]
            [webserver.response :as response]
            [cheshire.core :as json])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard]))

(defn- get-body [response-string]
  (second (.split response-string "\r\n\r\n" 2)))

(defn- includes-parameters? [json-response parameters]
  (let [decoded-response (json/decode json-response true)]
    (for [[map-key map-value] parameters]
      (should= (map-key decoded-response) map-value))))

(defn- validate-response [response-string parameters]
  (should (.startsWith response-string (response/make 200)))
  (should (.contains response-string "game-id"))
  (includes-parameters? (get-body response-string) parameters))

(describe "Request to /new"
  (it "starts a new game"
    (validate-response
      (socket/connect
        {:method "GET" :uri "/new" :version "HTTP/1.1"
         :parameters "first=human&second=minimax&size=3"})
      {:board (.toString (SquareBoard. 3))})))

(describe "Parsing parameters"
  (it "correctly breaks up string"
    (should=
      {:hello "world" :number 39}
      (routes/parse-parameters {:parameters "hello=world&number=39"}))))
