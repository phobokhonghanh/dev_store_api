#!/bin/bash

abs_filepath=$(readlink -f $0)
abs_dirpath=$(dirname $abs_filepath)
build_dirpath=$(dirname $abs_dirpath)

DOCKER_BUILDKIT=1 docker build -t my-mysql -f $build_dirpath/Dockerfile --target mysql $build_dirpath
DOCKER_BUILDKIT=1 docker build -t my-adminer -f $build_dirpath/Dockerfile --target adminer $build_dirpath