(ns tictactoe-server.state-test
  (:require [speclj.core :refer :all]
            [tictactoe-server.app]
            [tictactoe-server.state]
            [tictactoe-server.mock-socket :as socket])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard]))

(describe "Request to /new"
  (it "starts a new game"
    (socket/validate-body
      (socket/connect "/new" "size=3") {})))

(describe "Request to /status"
  (it "returns waiting when user's turn"
    (socket/connect "/new" "size=3")
    (socket/validate-body
      (socket/connect "/status" "")
      {:status "waiting" :board (.toString (SquareBoard. 3))})))
