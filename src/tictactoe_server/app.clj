(ns tictactoe-server.app
  (:require [tictactoe-server.util :as util]
            [tictactoe-server.json :as json]
            [clojure.java.io :as io]))

(defmulti route :uri)
(defmethod route :default [_])

(def ^:private static
  {"/" ["index.html" "text/html"]
   "/style.css" ["style.css" "text/css"]
   "/js/src/game.js" ["js/src/game.js" "application/javascript"]
   "/js/src/ui.js" ["js/src/ui.js" "application/javascript"]})

(defn- get-static [uri]
  (if-let [[file-name content-type] (static uri)]
    [(slurp (str "assets/" file-name)) content-type]))

(defn map-parameters [request]
  (update request :parameters #(if % (util/parse-parameters %))))

(defn- write [response output-stream]
  (doseq [r response] (io/copy r output-stream)) true)

(defn- get-response [{uri :uri :as request}]
  (if-let [json-response (route (map-parameters request))]
    [(json/encode json-response) "application/json"] (get-static uri)))

(defn handle [socket request]
  (if-let [response (get-response request)]
    (write (apply util/respond response) (.getOutputStream socket))))
