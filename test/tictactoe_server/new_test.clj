(ns tictactoe-server.new-test
  (:require [speclj.core :refer :all]
            [tictactoe-server.app]
            [tictactoe-server.new]
            [tictactoe-server.mock-socket :as socket])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard]))

(describe "Request to /new"
  (it "starts a new game"
    (socket/validate-body
      (socket/connect "/new" "size=3")
      {:board (.toString (SquareBoard. 3))})))

