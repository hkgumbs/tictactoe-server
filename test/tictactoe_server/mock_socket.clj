(ns tictactoe-server.mock-socket
  (:require [speclj.core :refer :all]
            [tictactoe-server.app :as app]
            [tictactoe-server.json :as json]
            [tictactoe-server.storage])
  (:import tictactoe_server.storage.AtomStorage))

(def ^:private template {:method "GET" :version "HTTP/1.1"})

(defn- make [input]
  (let [ouput-stream (java.io.ByteArrayOutputStream.)]
    (proxy [java.net.Socket]
      []
      (getInputStream [] (java.io.ByteArrayInputStream. (.getBytes input)))
      (getOutputStream [] ouput-stream))))

(defn connect [storage uri parameters]
  (let [socket (make "")
         args {:storage storage :uri uri :parameters parameters}
         request (into template args)]
     (app/handle socket request)
     (str (.getOutputStream socket))))

(defn- includes-parameters [json-response parameters]
  (let [decoded-response (json/decode json-response)]
    (doseq [[map-key map-value] parameters]
      (should= map-value (map-key decoded-response)))))

(defn validate-body [response parameters]
  (includes-parameters (second (.split response "\r\n\r\n" 2)) parameters))

(defn store [game-state] (AtomStorage. (atom game-state)))
