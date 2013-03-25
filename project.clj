(defproject cafe "0.1.1-SNAPSHOT"
  :description "E-commerce platform powered by Clojure"
  :url "http://cafecommerce.com.ec"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.nrepl "0.2.0-beta9"]
                 [compojure "1.1.5"]
                 [ring/ring-core "1.1.8"]
                 [lib-noir "0.4.6"]
                 [enlive "1.1.1"]
                 [korma "0.3.0-RC5" :exclusions [org.clojure/clojure]]
                 [postgresql "9.0-801.jdbc4"]
                 [lobos "1.0.0-SNAPSHOT"]
                 [cheshire "5.0.2"]]
                 ; [sandbar/sandbar "0.4.0-SNAPSHOT" :exclusions [org.clojure/clojure]]]
  :plugins [[lein-marginalia "0.7.1"]
            [lein-ring "0.8.3"]]
  :aliases {"migrate" ["run" "-m" "lobos.migrations"]}
  :ring {:handler cafe.core.server/app})