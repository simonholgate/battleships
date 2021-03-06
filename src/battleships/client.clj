;; This is the namespace that teams should use to submit / update players.
(ns battleships.client
  (:require [clojure.java.io :as io]
            [battleships.core :as core]
            [battleships.loader :as loader]
            [clj-http.client :as http]))

;; Use this to test the player locally, prior to submitting. A full game will be played against the computer.
;;
;; Example usage:
;; => (test-player "src/battleships/demo.clj")
;; Test Player B4 = :battleship, ships sunk = [:patrol-boat :submarine :destroyer :aircraft-carrier :battleship]
;; Test Player won in 84 shots!
;; cpu lost!
(defn test-player
  "player-ns is the path to the file that contains the namespace definition. This is for use when developing the player locally and want to make sure that it can play a game."
  [player-ns-file]
  (if-let [player-ns (loader/eval-ns player-ns-file)]
    (if (loader/valid-player-ns? player-ns)
        (let [game (core/test-player (loader/make-player player-ns))]
          (loader/cleanup-ns player-ns)
          game)
        (loader/cleanup-ns player-ns))))

(defn- post-to-server
  [player-ns-file name id server path]
  (let [server-address (str server path)] 
    (if (loader/valid-player-ns? (loader/eval-ns player-ns-file))
      (do
        (println (str "Submitting to " server-address))
        (http/post server-address {:body (slurp player-ns-file)
                                   :content-type "text/plain"
                                   :query-params {:name name :id id}})))))

;; Once you are happy with the player, use this to submit to the server.
;;
;; Example usage:
;; <pre>
;; => (submit-player "src/battleships/demo.clj" 
;;      "My Player" 
;;      "http://localhost:3000")
;; Submitting to http://localhost:3000/create
;; {:status 200, :headers {"date" "Wed, 16 Nov 2011 19:51:17 GMT", "content-type" "text/html; charset=utf-8", "connection" "close", "server" "Jetty(6.1.25)"}, :body "player1023"}
;; </pre>
;; Note that the body returns the namespace that was created for the player on the server. You need this if you want to update the player.
;; Whether or not you want to update is a different matter. From a tactical point of view you should probably just submit a new player.
(defn submit-player
  "Submit your player to the server."
  ([player-ns-file name]
     (submit-player player-ns-file name "http://localhost:3000"))
  ([player-ns-file name server-address]
     (post-to-server player-ns-file name nil server-address "/create")))

;; If you do want to update a player, use this.
;; <pre>
;; Example usage:
;; => (update-player "src/battleships/demo.clj" 
;;      "My Player" 
;;      "player1023" 
;;      "http://localhost:3000")
;; Submitting to http://localhost:3000/update
;; {:status 200, :headers {"date" "Wed, 16 Nov 2011 19:54:27 GMT", "content-type" "text/html; charset=utf-8", "connection" "close", "server" "Jetty(6.1.25)"}, :body "player1023"}
;; </pre>
(defn update-player
  "Use this when you want to overwrite your current player. You need the id returned from the original submission."
  ([player-ns-file name id]
     (update-player player-ns-file name id "http://localhost:3000"))
  ([player-ns-file name id server-address]
     (post-to-server player-ns-file name id server-address "/update")))

(defn- file-exists? [file]
  (.. (java.io.File. file) exists))

(defn- spit-java-policy-file! 
  "Creates the policy file that we need to use Clojail sandbox."
  [java-policy-file]
  (spit java-policy-file 
"grant {
    permission java.security.AllPermission;
};"))

(defn create-java-policy-file!
  "Helper function which creates the policy file in the user home directory"
  []
  (let [java-policy-file (str (System/getProperty "user.home") "/.java.policy")]
    (when-not (file-exists? java-policy-file)
      (println "Creating " java-policy-file "...")
      (spit-java-policy-file! java-policy-file))
    (if-not (file-exists? java-policy-file)
      (throw (IllegalStateException. (str java-policy-file " not created!"))))))
