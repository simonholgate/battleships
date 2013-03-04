(defproject battleships "1.0.0-SNAPSHOT"
  :description "I wrote this project to be used in the London Clojure Dojo, which meets every month. The motivation was that it is difficult to produce something satisfying in the short
time we have at the dojo every month. On a couple of occasions groups have spent more time setting up a development environment than they have writing code. I wanted to produce something
that would enable groups to get coding quickly, and have something satisfying by the end of the session.

The idea is that teams will start with demo.clj, and modify it. The aim is to implement 2 functions which will play a game of battleships. The 2 functions are for placing the ships at the
beginning of the game, and then firing shots at the opponents ships. The winner is the player who sinks all of the opponent's ships first."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [ring/ring-core "1.1.8"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [compojure "1.1.5"]
                 [clj-http "0.6.4"]
                 [hiccup "1.0.2"]
                 [clojail "1.0.5"]]
  :plugins [[lein-ring "0.8.3"]]
  :main battleships.core
  :ring {:handler battleships.server/handler})
