(ns tictactoe-server.storage-test
  (:require [speclj.core :refer :all]
            [tictactoe-server.storage :as storage]
            [cheshire.core :as json]))

(describe "Storage"
  (it "keeps track of game by id"
    (let [serialized-game (storage/create {:size 2})
          {game-id :game-id} (json/decode serialized-game true)]
      (should (.contains serialized-game "\"board\":\"----\""))
      (should= "----" (:board (storage/retrieve game-id))))))
