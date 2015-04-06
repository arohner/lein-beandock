(ns leiningen.beandock
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [cheshire.core :as json]
            [leiningen.beanstalk.aws :as aws]
            [leiningen.docker :as docker])
  (:import com.amazonaws.services.elasticbeanstalk.model.CreateApplicationVersionRequest
           com.amazonaws.services.elasticbeanstalk.model.UpdateEnvironmentRequest
           com.amazonaws.services.elasticbeanstalk.model.S3Location
           com.amazonaws.services.s3.model.ObjectMetadata
           com.amazonaws.services.s3.AmazonS3Client
           java.io.ByteArrayInputStream
           java.text.SimpleDateFormat
           java.util.Date))

(defn ->json [s]
  (json/generate-string s))

(defn maybe-replace-version [dockerrun version]
  (-> dockerrun
      (update-in [:Image :Name] (fn [v]
                                  (str/replace v ":$VERSION" (str ":" version))))))

(defn transform-dockerrun [dockerrun version] 
  (-> dockerrun
      (maybe-replace-version version)
      (->json)))

(defn load-dockerrun [project]
  (edn/read-string (slurp (io/file (-> project :root) "Dockerrun.aws.edn"))))

(defn dockerrun-key-name [version]
  (format "Dockerrun-%s.aws.json" version))

(defn s3-upload-file [project dockerrun version]
  (inspect dockerrun)
  (let [bucket  (aws/s3-bucket-name project)]
    (doto (AmazonS3Client. (aws/credentials project))
      (.setEndpoint (aws/project-endpoint project aws/s3-endpoints))
      (aws/create-bucket bucket)
      (.putObject bucket (dockerrun-key-name version) (ByteArrayInputStream. (.getBytes dockerrun)) (ObjectMetadata.)))
    (println "Uploaded" (dockerrun-key-name version) "to S3 Bucket" bucket)))

(def time-pattern "YYYYMMdd-kkmmss")
(defn time-string []
  (->
   (SimpleDateFormat. time-pattern)
   (.format (Date.))))

(defn replace-snapshot
  "Replace a SNAPSHOT in version string with current date-time"
  [vs]
  (str/replace vs "SNAPSHOT" (time-string)))

(defn app-version [project]
  (-> project :version (replace-snapshot)))

(defn create-app-version
  [project version dockerrun-key]
  (.createApplicationVersion
   (#'aws/beanstalk-client project)
    (doto (CreateApplicationVersionRequest.)
      (.setAutoCreateApplication true)
      (.setApplicationName (aws/app-name project))
      (.setVersionLabel version)
      (.setDescription (:description project))
      (.setSourceBundle (S3Location. (aws/s3-bucket-name project) dockerrun-key))))
  (println "Created new app version" version))

(defn set-app-version [project env version]
  (println "set-app-version:" version)
  (assert env)
  (.updateEnvironment
   (#'aws/beanstalk-client project)
   (doto (UpdateEnvironmentRequest.)
     (.setEnvironmentId (inspect (.getEnvironmentId env)))
     (.setEnvironmentName (.getEnvironmentName env))
     (.setVersionLabel version))))

(defn deploy
  "Deploy the container for the current version to EB"
  ([project]
   (println "Usage: lein beandock deploy <environment> [<version>]"))
  ([project env]
   (let [version (-> project docker/project-repo docker/latest-version)]
     (deploy project env version)))
  ([project env-name version]
   (s3-upload-file project (-> (load-dockerrun project)
                               (transform-dockerrun version)) version)
   (create-app-version project version (dockerrun-key-name version))
   (set-app-version project (aws/get-env project env-name) version)))

(defn beandock
  "Manage docker containers on AWS Elastic Beanstalk"
  {:help-arglists '([deploy])
   :subtasks [#'deploy]}
  [project subtask & args]
  
  
  
  
  )
