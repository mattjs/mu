#!/bin/bash
PROJECT_HOME="/users/mattjs/projects/mu/src/" #com/mu/"

function find_files {
  echo "fn called :" $1 
  cd $1
  TEST=0
  for entry in ./*
  do
    echo "entry : " $entry
    if [ -d "$entry" ]
    then
      find_files $entry
    fi
    if [ -f "$entry" ]
    then
      ((TEST++))
      echo $TEST
      echo $entry
    fi
    echo "loop end"
  done
  echo "fn end"
}

find_files $PROJECT_HOME
