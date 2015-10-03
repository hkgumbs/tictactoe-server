(ns tictactoe-server.router.app)

(defmulti route :uri)
(defmethod route :default [_])

