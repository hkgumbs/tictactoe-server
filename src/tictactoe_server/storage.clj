(ns tictactoe-server.storage)

(def cache (atom {}))

(defn create [object] (reset! cache object))
(defn retrieve [] @cache)
(defn modify [f] (swap! cache f))
