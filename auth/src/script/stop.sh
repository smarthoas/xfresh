#!/bin/sh

APPLICATION=xfresh-auth

kill -9 `cat $APPLICATION.pid`
echo $APPLICATION was stopped