(ns tictactoe-server.start-test
  (:require [speclj.core :refer :all]
            [webserver.response :as response]
            [tictactoe-server.app]
            [tictactoe-server.start]
            [tictactoe-server.mock-socket :as socket])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard]))

(describe "Request to /new"
  (for [opponent ["naive" "minimax" "local" "remote"]]
    (it (str "starts a new, ready game against " opponent)
      (socket/validate-body
        (socket/connect "/new" (str "size=3&vs=" opponent))
        {:board (.toString (SquareBoard. 3))})))
  (it "400s on bad parameters"
    (should= (response/make 400) (socket/connect "/new" "size=xyz&vs=naive"))
    (should= (response/make 400) (socket/connect "/new" "size=-1&vs=naive"))))

(describe "Request to /join"
  (it "joins a previously created game"
    (socket/connect "/new" "size=3&vs=remote")
    (socket/validate-body
      (socket/connect "/join" "") {:board (.toString (SquareBoard. 3))})))
