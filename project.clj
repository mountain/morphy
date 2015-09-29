(defproject morphy "0.1.0"
  :description "morphy"
  :url "http://morphy.org"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :global-vars  {*warn-on-reflection* true}
  :plugins      [[lein-expectations "0.0.7"]
                 [lein-junit "1.1.8"]]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [net.mikera/vectorz-clj "0.29.0"]
                 [net.mikera/vectorz "0.46.0"]
                 [net.mikera/core.matrix "0.34.0"]
                 [net.mikera/imagez "0.5.0"]
                 [com.climate/claypoole "1.0.0"]
                 [org.clojure/data.codec "0.1.0"]
                 [commons-io/commons-io "2.4"]
                 [org.apache.commons/commons-math3 "3.4.1"]
                 [org.jkee.gtree/gtree "0.66"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-api "1.7.12"]
                 [org.slf4j/slf4j-log4j12 "1.7.12"]
                 [log4j/log4j "1.2.17"]
                 [org.lwjgl/lwjgl "3.0.0a"]
                 [org.lwjgl/lwjgl-platform "3.0.0a"
                  :classifier "natives-osx"
                  :native-prefix ""]]

  :jvm-opts ["-Xms1g" "-Xmx1g" "-XstartOnFirstThread"]
  :javac-options ["-target" "1.8" "-source" "1.8" "-Xlint:-options"]
  :omit-source true

  :source-paths ["src"]
  :java-source-paths ["base" "lib" "db" "app"]

  :test-paths ["test"]
  :junit ["junit"]

  :jar-name "morphy.jar"
  :uberjar-name "morphy-standalone.jar"

  :aot :all
  :main morphy.app.Plant3DMain)
