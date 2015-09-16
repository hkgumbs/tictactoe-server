(ns tictactoe-server.storage)

(def ^:private cache (atom {}))
(def modify (partial swap! cache))
(defn create [record] (reset! cache record))
(defn retrieve [] @cache)
