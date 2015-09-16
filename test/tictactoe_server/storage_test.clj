(ns tictactoe-server.storage-test
  (:require [speclj.core :refer :all]
            [tictactoe-server.storage :as storage]))

(describe "Storage"
  (it "keeps track of one arbitrary objects"
    (storage/create {:hello 2})
    (should= {:hello 2} (storage/retrieve)))

  (it "can update objects by key"
    (storage/create {:size 3})
    (storage/modify assoc :size 4)
    (should= {:size 4} (storage/retrieve))))
