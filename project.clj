(defproject tictactoe-server "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :main ^:skip-aot tictactoe-server.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
