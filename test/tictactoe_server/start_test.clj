(ns tictactoe-server.start-test
  (:require [speclj.core :refer :all]
            [tictactoe-server.app]
            [tictactoe-server.start]
            [tictactoe-server.storage :as storage]
            [tictactoe-server.mock-socket :as socket])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard]))

(def ^:private player-matcher #"\"player-id\":(\d+)")
(def ^:private game-matcher #"\"game-id\":(\d+)")
(defn- get-id [matcher json-response]
  (Integer. ^String (second (re-find matcher json-response))))

(describe "Request to /new"
  (with game-state (socket/store))
  (for [opponent ["naive" "minimax" "local" "remote"]]
    (it (str "starts a new, ready game against " opponent)
      (let [response
            (socket/connect @game-state "/new" (str "size=3&vs=" opponent))]
        (should-contain player-matcher response)
        (should-contain game-matcher response))))
  (it "returns nothing on bad parameters"
    (should= "" (socket/connect @game-state "/new" "size=xyz&vs=naive"))
    (should= "" (socket/connect @game-state"/new" "size=-1&vs=naive"))))

(describe "Request to /join"
  (with game-state (socket/store))
  (it "joins a previously created game"
    (let [p1 (socket/connect @game-state "/new" "size=3&vs=remote")
          p2 (socket/connect @game-state "/join" "")
          game-id (get-id game-matcher p1)]
      (should=
        ((storage/-get @game-state game-id) :player-ids)
        (map (partial get-id player-matcher) [p1 p2]))
      (should= game-id (get-id game-matcher p2)))))
