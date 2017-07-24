default: py

py:
	./tools/build.py

sh:
	./tools/build.sh

clean:
	rm -rf bin/ gen/ libs/ obj/ build.xml local.properties proguard-project.txt  project.properties 2> /dev/null
