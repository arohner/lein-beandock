(defproject lein-beandock "0.1.2"
  :description "Tools for deploying docker containers to AWS ElasticBeandock"
  :url "https://github.com/arohner/lein-beandock"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.amazonaws/aws-java-sdk "1.3.31"]
                 [cheshire "5.4.0"]
                 [lein-beanstalk "0.2.7"]
                 [arohner/lein-docker "0.1.0"]
                 [amazonica "0.3.19"]]
  :eval-in-leiningen true)
