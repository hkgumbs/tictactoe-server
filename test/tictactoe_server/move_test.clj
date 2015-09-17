(ns tictactoe-server.move-test (:require [speclj.core :refer :all]
            [webserver.response :as response]
            [tictactoe-server.storage]
            [tictactoe-server.move]
            [tictactoe-server.mock-socket :as socket])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard Board$Mark]
           [me.hkgumbs.tictactoe.main.java.rules DefaultRules Rules]
           [me.hkgumbs.tictactoe.main.java.player Minimax NaiveChoice]))

(def ^:private rules ^Rules (DefaultRules. 3))
(def ^:private storage-template
  {:board (SquareBoard. 3) :rules rules :status "ready" :turn Board$Mark/X})
(defn- initialize [game-state] (socket/store (into storage-template game-state)))

(def first-player-id 12345)
(def second-player-id 67890)
(defn- get-parameters
  ([position] (get-parameters position first-player-id))
  ([position player-id] (str "position=" position "&player-id=" player-id)))

(describe "Naive CPU"
  (with storage
    (initialize
      {:cpu (NaiveChoice.) :vs "naive" :player-ids [first-player-id]}))
  (it "loses"
    (socket/connect @storage "/move" (get-parameters 6))
    (socket/connect @storage "/move" (get-parameters 7))
    (socket/validate-body
      (socket/connect @storage "/move" (get-parameters 8))
      {:board (-> (SquareBoard. 3)
                  (.add 8 Board$Mark/X) (.add 0 Board$Mark/O)
                  (.add 7 Board$Mark/X) (.add 1 Board$Mark/O)
                  (.add 6 Board$Mark/X) .toString)})
    (should=
      (response/make 400)
      (socket/connect @storage "/move" (get-parameters 8)))))

(describe "Minimax"
  (with storage
    (initialize
      {:cpu (Minimax. Board$Mark/O rules)
       :vs "minimax"
       :player-ids [first-player-id]}))
  (it "adds piece at best slot"
    (socket/validate-body
      (socket/connect @storage "/move" (get-parameters 0))
      {:board (-> (SquareBoard. 3)
                  (.add 0 Board$Mark/X) (.add 4 Board$Mark/O) .toString)})))

(describe "Local human"
  (with storage (initialize {:vs "local" :player-ids [first-player-id]}))
  (it "has chance to respond with move"
    (socket/validate-body
      (socket/connect @storage "/move" (get-parameters 0))
      {:board (-> (SquareBoard. 3) (.add 0 Board$Mark/X) .toString)})
    (socket/validate-body
      (socket/connect @storage "/move" (get-parameters 8))
       {:board (-> (SquareBoard. 3)
                   (.add 0 Board$Mark/X) (.add 8 Board$Mark/O) .toString)})))

(describe "Remote human"
  (with storage
    (initialize
      {:vs "remote" :player-ids [first-player-id second-player-id]}))
  (it "waits for human opponent"
    (socket/validate-body
      (socket/connect @storage "/move" (get-parameters 0))
      {:board (-> (SquareBoard. 3) (.add 0 Board$Mark/X) .toString)})
    (should=
       (response/make 400)
       (socket/connect @storage "/move" (get-parameters 1))))

  (it "uses same game when joined"
      (should= (response/make 400)
               (socket/connect @storage "/move" (get-parameters 0 second-player-id)))
      (socket/validate-body
        (socket/connect @storage "/move" (get-parameters 0))
        {:board (-> (SquareBoard. 3) (.add 0 Board$Mark/X) .toString)})
      (socket/validate-body
        (socket/connect @storage "/move" (get-parameters 1 second-player-id))
        {:board (-> (SquareBoard. 3)
                    (.add 0 Board$Mark/X) (.add 1 Board$Mark/O) .toString)}))

  (it "is allowed to play before someone else joins"
      (socket/validate-body
        (socket/connect @storage "/move" (get-parameters 0))
        {:board (.toString (.add (SquareBoard. 3) 0 Board$Mark/X))})
      (should-not-contain
        (str first-player-id) (socket/connect @storage "/join" "")))

  (it "400s when no game is available to join"
    (let [game-state (initialize
                       {:vs "naive" :player-ids [first-player-id]})]
      (should= (response/make 400) (socket/connect game-state "/join" ""))))

  (it "400s when game has already been joined"
    (let [game-state (initialize
                       {:vs "remote"
                        :player-ids [first-player-id second-player-id]})]
      (socket/connect game-state "/new" "size=3&vs=remote")
      (socket/connect game-state "/join" "")
      (should= (response/make 400) (socket/connect game-state "/join" "")))))

(describe "Invalid input to /move"
  (with storage (initialize {:vs "local" :player-ids [first-player-id]}))
  (it "400s"
    (should=
      (response/make 400)
      (socket/connect @storage "/move" (get-parameters -1)))
    (should=
       (response/make 400)
       (socket/connect @storage "/move" (get-parameters "foobar")))
    (should=
       (response/make 400)
       (socket/connect @storage "/move" (get-parameters 0 "foobar")))))

(describe "Request to /status"
  (with storage
    (initialize
      {:board (SquareBoard. 3)
       :player-ids [first-player-id second-player-id]}))
  (it "returns empty board and status"
    (socket/validate-body
      (socket/connect @storage "/status" (str "player-id=" first-player-id))
      {:status "ready" :board (.toString (SquareBoard. 3))})
    (socket/validate-body
      (socket/connect @storage "/status" (str "player-id=" second-player-id))
      {:status "waiting"
       :board (-> (SquareBoard. 3) .toString)}))
  (it "400s with inactive id"
    (should=
      (response/make 400)
      (socket/connect @storage "/status" "player-id=11111"))))
