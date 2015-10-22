#!/bin/bash

cd ~/clojure/cdm
lein clean
lein cljsbuild once min
rsync -av resources/public/ gmp26@nrich.maths.org:/www/nrich/html/cdm
