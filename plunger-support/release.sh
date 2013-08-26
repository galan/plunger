#!/bin/bash -e
PATH_SCRIPT=$(dirname $(readlink -f $0))
PLUNGER_NAME=plunger
PLUNGER_HOME=${PLUNGER_HOME-${HOME}/bin/${PLUNGER_NAME}}

PATH_RELEASE=${PATH_SCRIPT}/${PLUNGER_NAME}
cp -r ${PLUNGER_HOME} ${PATH_SCRIPT}
cp ${PATH_SCRIPT}/../README.md ${PATH_RELEASE}

tar czf ${PATH_SCRIPT}/plunger.tgz -C ${PATH_SCRIPT} plunger
rm -rf ${PATH_RELEASE}

