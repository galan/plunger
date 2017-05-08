# Building plunger
Building plunger by yourself should be easy, here are the steps.

# Requirements
* JDK >= 8
* Maven >= 3.1.1
* A shell

# Process
1. Clone `git clone git@github.com:galan/plunger.git`
1. Call `plunger-support/install.sh`

Calling the script will build the different maven modules and copy the artifacts to the directory `$HOME/app/plunger`, including the generated scripts. The location can be overwritten using the environment-variable `PLUNGER_HOME`.

