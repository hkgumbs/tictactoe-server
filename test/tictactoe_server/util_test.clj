(ns tictactoe-server.util-test
  (:require [speclj.core :refer :all]
            [tictactoe-server.util :as util]
            [cheshire.core :as json])
  (:import [me.hkgumbs.tictactoe.main.java.board
            SquareBoard Board$Mark]))

(describe "Standard 200 response"
  (it "encodes as JSON correctly"
    (should=
      (let [body (json/encode {:hello "world"})]
        (str
          "HTTP/1.1 200 OK\r\n"
          "Content-Type: application/json; charset=utf-8\r\n"
          "Content-Length: " (count body) "\r\n\r\n"
          body))
      (util/respond {:hello "world"})))
  (it "responds with bare 200 without parameters"
    (should= "HTTP/1.1 200 OK\r\n\r\n" (util/respond))))

(describe "Board decoding"
  (it "words with empty board"
    (should=
      (.toString (SquareBoard. 3))
      (.toString (util/decode-square-board "---------" 3))))
  (it "words with pieces on board"
    (should=
      (.toString (.add (.add (SquareBoard. 3) 4 Board$Mark/X) 3 Board$Mark/O))
      (.toString (util/decode-square-board "---OX----" 3)))))

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


