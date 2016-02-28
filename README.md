# SENG302 Project Template

Basic maven project with required maven reporting setup and basic gitlab ci.

It is a requirement that your product should be completely built to a deliverable form using the maven package goal.

# Basic Structure
 - `src/` Your application source
 - `doc/` User and design documentation
 - `doc/examples/` Demo example files for use with your application

# CI Server Setup

This setup will happen on your ci server which has been refered to so far as your jenkins server. You will need to install maven and the gitlab ci runner. To do this you will have to SSH to the server with your root credentails. The following commands are executed on the server.

## Maven Install

Download maven tarball.

```sh
wget http://www.eu.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
```

Extract the tarball into the `/opt/` directory.

```sh
tar -C /opt/ -xvzf apache-maven-3.3.9-bin.tar.gz
```

Add maven to the system path.

```sh
echo "export PATH=/opt/apache-maven-3.3.9/bin:\$PATH" > /etc/profile.d/maven.sh
```

To check that installation was successful start a new shell and run `mvn --version` You should see output similar to the following


	Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-11T05:41:47+13:00)
	Maven home: /opt/apache-maven-3.3.9
	Java version: 1.8.0_66, vendor: Oracle Corporation
	Java home: /usr/java/jdk1.8.0_66/jre
	Default locale: en_US, platform encoding: UTF-8
	OS name: "linux", version: "2.6.32-573.12.1.el6.x86_64", arch: "amd64", family: "unix"

## CI Runner Install

Follow the instructions for CentOS in the [ci install guide] to install the gitlab-ci-multi-runner.

The details (url and token) for registering your runner can be found on the [runner settings] page. (From the gitlab project in the side bar `Settings` then `Runners`)

You should use the shell executor when prompted.

[runner settings]:https://eng-git.canterbury.ac.nz/SENG302-2016/team-0/runners
[ci install guide]:https://gitlab.com/gitlab-org/gitlab-ci-multi-runner/blob/master/docs/install/linux-repository.md
