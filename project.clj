(defproject cafe "0.1.0-SNAPSHOT"
  :description "E-commerce platform powered by Clojure"
  :url "http://cafecommerce.com.ec"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.nrepl "0.2.0-beta9"]
                 [compojure "1.1.5"]
                 [lib-noir "0.3.5"]
                 [enlive "1.0.1"]
                 [korma "0.3.0-beta7" :exclusions [org.clojure/clojure]]
                 [postgresql "9.0-801.jdbc4"]
                 [lobos "1.0.0-SNAPSHOT"]
                 [cheshire "5.0.1"]]
  :plugins [[lein-marginalia "0.7.1"]
            [lein-ring "0.8.0"]]
  :aliases {"migrate" ["run" "-m" "lobos.migrations"]}
  :ring {:handler cafe.core.server/app})