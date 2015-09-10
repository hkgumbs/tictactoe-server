(ns tictactoe-server.root
  (:require [tictactoe-server.app :as app]
            [webserver.response :as response]))

(defmethod app/route "/" [_]
  (let [html (slurp "assets/index.html")]
    [(response/make 200 {:Content-Type "text/html" :Content-Length (count html)})
     html]))
