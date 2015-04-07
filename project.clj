(defproject lein-beandock "0.1.2"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.amazonaws/aws-java-sdk "1.3.31"]
                 [cheshire "5.4.0"]
                 [lein-beanstalk "0.2.7"]
                 [arohner/lein-docker "0.1.0"]
                 [amazonica "0.3.19"]
                 [org.clojure/tools.reader "0.8.16"]]
  :eval-in-leiningen true)
