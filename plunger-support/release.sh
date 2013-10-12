#!/bin/bash -e
PATH_SCRIPT=$(dirname $(readlink -f $0))
PLUNGER_NAME=plunger
PLUNGER_HOME=${PLUNGER_HOME-${HOME}/bin/${PLUNGER_NAME}}
PATH_TARGET=${PATH_SCRIPT}/${PLUNGER_NAME}
PATH_RELEASES=${PATH_SCRIPT}/releases

read -p "Version: " VERSION
if [ -z "${VERSION}" ]; then
	VERSION='dev'
fi

cp -r ${PLUNGER_HOME} ${PATH_SCRIPT}
cp ${PATH_SCRIPT}/../README.md ${PATH_TARGET}
cp ${PATH_SCRIPT}/../LICENCE ${PATH_TARGET}

mkdir ${PATH_RELEASES}
tar czf ${PATH_SCRIPT}/releases/plunger-${VERSION}.tgz -C ${PATH_SCRIPT} plunger
rm -rf ${PATH_TARGET}

echo "Created ${PATH_RELEASES}/plunger-${VERSION}.tgz"

