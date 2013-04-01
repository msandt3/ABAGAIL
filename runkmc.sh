#!/bin/bash
# My first script

ant run-kmc -Dargs="-w 2 -s 2"
ant run-kmc -Dargs="-w 5 -s 5"
ant run-kmc -Dargs="-w 10 -s 10"
ant run-kmc -Dargs="-w 20 -s 20"
ant run-kmc -Dargs="-w 50 -s 50"
ant run-kmc -Dargs="-w 100 -s 100"