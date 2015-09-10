(ns tictactoe-server.new-test
  (:require [speclj.core :refer :all]
            [webserver.response :as response]
            [tictactoe-server.app]
            [tictactoe-server.new]
            [tictactoe-server.mock-socket :as socket])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard]))

(describe "Request to /new"
  (for [opponent ["naive" "minimax" "local" "remote"]]
    (it (str "starts a new, ready game against " opponent)
      (socket/validate-body
        (socket/connect "/new" (str "size=3&vs=" opponent))
        {:board (.toString (SquareBoard. 3)) :status "ready"})))
  (it "400s on bad parameters"
    (should= (response/make 400) (socket/connect "/new" "size=xyz&vs=naive"))
    (should= (response/make 400) (socket/connect "/new" "size=-1&vs=naive"))))
