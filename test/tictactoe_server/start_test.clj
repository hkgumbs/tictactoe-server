(ns tictactoe-server.start-test
  (:require [speclj.core :refer :all]
            [tictactoe-server.app]
            [tictactoe-server.start]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.mock-socket :as socket])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard]))

(def ^:private player-id-matcher #"\"player-id\":(\d+)")
(defn- get-player-id [json-response]
  (Integer. ^String (second (re-find player-id-matcher json-response))))

(describe "Request to /new"
  (with game-state (socket/store {}))
  (for [opponent ["naive" "minimax" "local" "remote"]]
    (it (str "starts a new, ready game against " opponent)
      (should-contain
        player-id-matcher
        (socket/connect @game-state "/new" (str "size=3&vs=" opponent)))))
  (it "returns nothing on bad parameters"
    (should= "" (socket/connect @game-state "/new" "size=xyz&vs=naive"))
    (should= "" (socket/connect @game-state"/new" "size=-1&vs=naive"))))

(describe "Request to /join"
  (with game-state (socket/store {}))
  (it "joins a previously created game"
    (let [p1 (socket/connect @game-state "/new" "size=3&vs=remote")
          p2 (socket/connect @game-state "/join" "")]
      (should=
        (:player-ids (storage/-get @game-state :fake-id))
        (map get-player-id [p1 p2])))))
