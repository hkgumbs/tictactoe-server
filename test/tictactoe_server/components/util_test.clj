(ns tictactoe-server.components.util-test
  (:require [speclj.core :refer :all]
            [tictactoe-server.components.util :as util]
            [tictactoe-server.components.json :as json])
  (:import [me.hkgumbs.tictactoe.main.java.board
            SquareBoard Board$Mark]))

(describe "Integer parsing"
  (it "defaults to default value"
    (should= 5000 (util/parse-int nil 5000))
    (should= 5000 (util/parse-int "asdf" 5000)))
  (it "parses int correctly"
    (should= 8000 (util/parse-int "8000" nil))
    (should= 8000 (util/parse-int "8000" "asdf"))))

(describe "Parsing parameters"
 (it "correctly breaks up string"
    (should=
      {:hello "world" :number 39}
      (util/parse-parameters "hello=world&number=39"))))


