#!/bin/sh

APPLICATION=xfresh-basket-example

kill -9 `cat $APPLICATION.pid`
echo $APPLICATION was stopped