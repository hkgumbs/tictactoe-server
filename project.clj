(defproject tictactoe-server "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :main ^:skip-aot tictactoe-server.core
  :target-path "target/%s"
  :java-source-paths ["jcheckouts/tictactoe-java/src/me/hkgumbs/tictactoe/main"]
  :plugins [[speclj "3.3.0"]]
  :profiles {:dev {:dependencies [[speclj "3.3.0"]]}
             :uberjar {:aot :all}})