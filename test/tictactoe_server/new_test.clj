(ns tictactoe-server.new-test
  (:require [speclj.core :refer :all]
            [webserver.response :as response]
            [tictactoe-server.app]
            [tictactoe-server.new]
            [tictactoe-server.mock-socket :as socket])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard]))

(describe "Request to /new"
  (it "starts a new game"
    (socket/validate-body
      (socket/connect "/new" "size=3&vs=naive")
      {:board (.toString (SquareBoard. 3))}))
  (it "400s on bad parameters"
    (should=
      (response/make 400)
      (socket/connect "/new" "size=yomama&vs=naive"))
    (should=
      (response/make 400)
      (socket/connect "/new" "size=-1&vs=naive"))))
