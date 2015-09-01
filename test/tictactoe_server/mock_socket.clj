(ns tictactoe-server.mock-socket
  (:require [speclj.core :refer :all]
            [tictactoe-server.app :as app]
            [cheshire.core :as json]))

(def handler (:valid-request-handler app/responder))
(def template {:method "GET" :version "HTTP/1.1"})

(defn- make [input]
  (let [ouput-stream (java.io.ByteArrayOutputStream.)]
    (proxy [java.net.Socket]
      []
      (getInputStream [] (java.io.ByteArrayInputStream. (.getBytes input)))
      (getOutputStream [] ouput-stream))))

(defn connect [uri parameters]
  (let [socket (make "")
        request (into template {:uri uri :parameters parameters})]
    (handler socket request)
    (str (.getOutputStream socket))))

(defn- includes-parameters [json-response parameters]
  (let [decoded-response (json/decode json-response true)]
    (doall (for [[map-key map-value] parameters]
             (should= (map-key decoded-response) map-value)))))

(defn validate-body [response parameters]
  (includes-parameters (second (.split response "\r\n\r\n" 2)) parameters))
