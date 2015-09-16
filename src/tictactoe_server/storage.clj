(ns tictactoe-server.storage)

(def ^:private cache (atom {}))
(def modify (partial swap! cache))
(defn create [entry] (reset! cache entry))
(defn retrieve [] @cache)
