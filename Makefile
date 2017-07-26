default: py

PY := ./tools/build.py
SH := ./tools/legacy_build.sh

py:
	${PY}

sh:
	${SH}

legacy: sh

clean:
	rm -rf bin/ gen/ libs/ obj/ build.xml local.properties proguard-project.txt  project.properties 2> /dev/null

help:
	${PY} -h

