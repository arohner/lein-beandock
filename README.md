# lein-beandock

[![Clojars Project](http://clojars.org/lein-beandock/latest-version.svg)](http://clojars.org/lein-beandock)

A Leiningen plugin to use Docker containers with AWS ElasticBeanstalk. Built on top of lein-beanstalk and lein-docker.

Beandock is designed for the situation where you're using AWS ElasticBeanstalk with Docker containers.

## How it Works

Beandock uploads a (potentially) updated Dockerrun.aws.json, and creates & sets a new EB version. 

## Assumptions

- you are building "fully-baked" Docker containers (i.e. the container
  contains your application code, and needs nothing more than a
  `docker run` to execute.

- you are deploying via Dockerrun.aws.json, not Dockerfiles or .zip files

## Usage

- add `:docker {:repo "foo/bar"}` to your project.clj. (Same config used with [lein-docker] (https://github.com/arohner/lein-docker)
- Add :aws :beanstalk config. Beandock is based on lein-beandock is based on [lein-beanstalk] (https://github.com/weavejester/lein-beanstalk), so all of the credentials, configuration, etc carry over. At a minimum, you'll need an environment, and access keys specified. 
- Add a Dockerrun.aws.json file to the root of your project directory. If the Image Name string contains $VERSION, it will be replaced when uploading.

    $ lein beandock deploy env [version]

Env is the name of an environment specified in :aws :beanstalk in your project. Version is a tag on the docker repo. If not specified, defaults to the latest tag of the project's docker repo, on the local machine.

## License

Copyright © 2015 Allen Rohner

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
