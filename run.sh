#!/bin/bash

if [[ $# -lt 1 ]]; then
    echo "usage: ./run.sh CLASSNAME [args]"
    exit 0
fi

echo "Looking for .jar files..."

LIBS="$(find -name "*.jar")"

echo "Found the following libs:"
for jar in $LIBS; do
    echo " -| $jar"
done
echo "Also using default classpath: ./bin"
echo

LIBS=".:./bin/:$(echo $LIBS | sed "s@ @:@g"):"

# echo "$LIBS"

java -cp "$LIBS" -ea $@
