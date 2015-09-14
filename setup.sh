function get-latest {
    curl -s -L https://github.com/hkgumbs/$1/releases/latest \
        | egrep -m 1 -o "/hkgumbs/$1/releases/download/v.*\.jar" \
        | wget --base=http://github.com/ -i -
}

mkdir lib
rm lib/*
cd lib
get-latest webserver-clojure
get-latest tictactoe-java
