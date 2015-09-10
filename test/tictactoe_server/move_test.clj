(ns tictactoe-server.move-test (:require [speclj.core :refer :all]
            [webserver.response :as response]
            [tictactoe-server.new]
            [tictactoe-server.move]
            [tictactoe-server.mock-socket :as socket])
  (:import [me.hkgumbs.tictactoe.main.java.board SquareBoard Board$Mark]))

(defn- get-player-id [opponent]
  (str "&player-id="
       (second (re-find #"\"player-id\":(\d+)"
                        (socket/connect "/new" (str "size=3&vs=" opponent))))))

(describe "Naive CPU"
  (with player-id (get-player-id "naive"))
  (it "loses"
    (socket/connect "/move" (str "position=8" @player-id))
    (socket/connect "/move" (str "position=7" @player-id))
    (socket/validate-body
       (socket/connect "/move" (str "position=6" @player-id))
       {:status "terminated"
        :board (-> (SquareBoard. 3)
                   (.add 8 Board$Mark/X) (.add 0 Board$Mark/O)
                   (.add 7 Board$Mark/X) (.add 1 Board$Mark/O)
                   (.add 6 Board$Mark/X) .toString)})
    (should=
      (response/make 400)
      (socket/connect "/move" (str "position=4" @player-id)))))

(describe "Minimax"
  (with player-id (get-player-id "minimax"))
  (it "adds piece at best slot"
    (socket/validate-body
      (socket/connect "/move" (str "position=0" @player-id))
      {:status "ready"
       :board (-> (SquareBoard. 3)
                  (.add 0 Board$Mark/X) (.add 4 Board$Mark/O) .toString)})))

(describe "Local human"
  (with player-id (get-player-id "local"))
  (it "has chance to respond with move"
    (socket/validate-body
      (socket/connect "/move" (str "position=0" @player-id))
      {:status "ready"
       :board (-> (SquareBoard. 3) (.add 0 Board$Mark/X) .toString)})
    (socket/validate-body
       (socket/connect "/move" (str "position=8" @player-id))
       {:status "ready"
        :board (-> (SquareBoard. 3)
                   (.add 0 Board$Mark/X) (.add 8 Board$Mark/O) .toString)})))

(describe "Remote human"
  (with player-id (get-player-id "remote"))
  (it "waits for human opponent"
    (socket/validate-body
      (socket/connect "/move" (str "position=0" @player-id))
      {:status "waiting"
       :board (-> (SquareBoard. 3) (.add 0 Board$Mark/X) .toString)})
    (should=
       (response/make 400)
       (socket/connect "/move" (str"position=1" @player-id)))))

(describe "Invalid input to /move"
  (with player-id (get-player-id "naive"))
  (it "400s"
    (should=
      (response/make 400)
      (socket/connect "/move" (str "position=-1" @player-id)))
    (should=
       (response/make 400)
       (socket/connect "/move" (str "position=foobar" @player-id)))
    (should=
       (response/make 400)
       (socket/connect "/move" "position=4&player-id=nonsense"))))
