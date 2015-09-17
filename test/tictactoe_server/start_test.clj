(ns tictactoe-server.start-test
  (:require [speclj.core :refer :all]
            [webserver.response :as response]
            [tictactoe-server.app]
            [tictactoe-server.start]
            [tictactoe-server.mock-socket :as socket])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard]))

(describe "Request to /new"
  (with game-state (socket/store {}))
  (for [opponent ["naive" "minimax" "local" "remote"]]
    (it (str "starts a new, ready game against " opponent)
      (socket/validate-body
        (socket/connect @game-state "/new" (str "size=3&vs=" opponent))
        {:board (.toString (SquareBoard. 3))})))
  (it "400s on bad parameters"
    (should= (response/make 400)
             (socket/connect @game-state "/new" "size=xyz&vs=naive"))
    (should= (response/make 400)
             (socket/connect @game-state"/new" "size=-1&vs=naive"))))

(describe "Request to /join"
  (with game-state
    (socket/store
      {:player-ids [123 456] :status "waiting" :board (SquareBoard. 3)}))
  (it "joins a previously created game"
    (socket/validate-body
      (socket/connect @game-state "/join" "")
      {:board (.toString (SquareBoard. 3))})))
