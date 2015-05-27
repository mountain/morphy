(defproject morphy "0.1.0"
  :description "morphy"
  :url "http://morphy.org"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :global-vars  {*warn-on-reflection* true}
  :plugins      [[lein-expectations "0.0.7"]
                 [lein-junit "1.1.8"]]

  :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                 [http-kit "2.1.19"]
                 [ring "1.3.2"]
                 [compojure "1.3.3"]
                 [ring/ring-defaults "0.1.4"]
                 [ring/ring-headers "0.1.2"]
                 [ring/ring-codec "1.0.0"]
                 [ring/ring-json "0.3.1"]
                 [ring.middleware.jsonp "0.1.6"]
                 [net.mikera/vectorz-clj "0.29.0"]
                 [net.mikera/vectorz "0.46.0"]
                 [net.mikera/core.matrix "0.34.0"]
                 [net.mikera/imagez "0.5.0"]
                 [com.climate/claypoole "1.0.0"]
                 [org.clojure/data.codec "0.1.0"]
                 [org.apache.commons/commons-math3 "3.4.1"]
                 [org.jkee.gtree/gtree "0.66"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-api "1.7.12"]
                 [org.slf4j/slf4j-log4j12 "1.7.12"]
                 [log4j/log4j "1.2.17"]
                 [junit/junit "4.12"]
                 [expectations "2.0.9"]]

  :jvm-opts ["-Xms1g" "-Xmx1g" "-server"]
  :javac-options ["-target" "1.8" "-source" "1.8" "-Xlint:-options"]
  :omit-source true

  :source-paths ["src"]
  :java-source-paths ["base" "lib" "db"]

  :test-paths ["test"]
  :junit ["junit"]

  :jar-name "morphy.jar"
  :uberjar-name "morphy-standalone.jar"

  :aot :all
  :main morphy.main)
