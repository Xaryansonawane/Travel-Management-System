#!/bin/sh
if [ -f "/c/Users/aryan/.maven/maven-3.10.0-rc-1/bin/mvn" ]; then
    /c/Users/aryan/.maven/maven-3.10.0-rc-1/bin/mvn "$@"
else
    mvn "$@"
fi
