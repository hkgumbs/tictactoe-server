if mkdir checkouts jcheckouts; then
    cd checkouts
    git clone git@github.com:hkgumbs/webserver-clojure.git
    cd ../jcheckouts
    git clone git@github.com:hkgumbs/tictactoe-java.git
else
    cd checkouts/webserver-clojure
    git pull origin master
    cd ../../jcheckouts/tictactoe-java
    git pull origin master
fi
