#!/bin/bash -e
JAVA_HOME=${JAVA_HOME-${HOME}/bin/jdk7}
PLUNGER_NAME=plunger
PLUNGER_HOME=${PLUNGER_HOME-${HOME}/bin/${PLUNGER_NAME}}
PLUNGER_LIBS=${PLUNGER_HOME}/libs
PLUNGER_PREFIX=${PLUNGER_PREFIX-p}

function header() {
	echo -e "\n---------------- $1 ----------------"
}

read -p "Installing ${PLUNGER_NAME} into ${PLUNGER_HOME} (Press enter to continue)"



header "assemblying jar"
mkdir -p ${PLUNGER_LIBS}
mvn -f ../plunger/pom.xml clean install dependency:copy-dependencies -DincludeScope=compile -DoutputDirectory=${PLUNGER_LIBS}
cp ../plunger/target/plunger-*.jar ${PLUNGER_LIBS}/plunger.jar

mvn -f ../plunger-hornetq-2.2.x/pom.xml clean install dependency:copy-dependencies -DincludeScope=compile -DoutputDirectory=${PLUNGER_LIBS}
cp ../plunger-hornetq-2.2.x/target/plunger-hornetq-*.jar ${PLUNGER_LIBS}/plunger-hornetq-2.2.x.jar

# Workaround, will check assembly-plugin instead.
rm ${PLUNGER_LIBS}/plunger-*-SNAPSHOT.jar
 
#rm -f ${PLUNGER_HOME}/assembly-plunger.jar
#cp target/plunger-*-jar-with-dependencies.jar $PLUNGER_HOME/assembly-plunger.jar



header "creating launch-scripts"

echo "#!/bin/bash
PATH_SCRIPT=\$(dirname \$(readlink -f \$0))
JAVA_HOME=\${JAVA_HOME-\${HOME}/bin/jdk7}
if [ -f \"\${PATH_SCRIPT}/plunger-environment.sh\" ]; then
	. \${PATH_SCRIPT}/plunger-environment.sh
fi
\${JAVA_HOME}/bin/java -cp \${PATH_SCRIPT}/libs/\* de.galan.plunger.application.Plunger \$*
" > ${PLUNGER_HOME}/${PLUNGER_NAME}

#echo "\${JAVA_HOME}/bin/java -cp .:jars/* de.galan.plunger.application.Plunger \$*" >> ${PLUNGER_HOME}/${PLUNGER_NAME}

#echo "#!/bin/bash" > ${PLUNGER_HOME}/${PLUNGER_NAME}
#echo "PATH_SCRIPT=\$(dirname \$(readlink -f \$0))" >> ${PLUNGER_HOME}/${PLUNGER_NAME}
#echo "JAVA_HOME=\${JAVA_HOME-\${HOME}/bin/jdk7}" >> ${PLUNGER_HOME}/${PLUNGER_NAME}
#echo "if []; then fi . \${PATH_SCRIPT}/plunger-environment.sh" >> ${PLUNGER_HOME}/${PLUNGER_NAME}
#echo "\${JAVA_HOME}/bin/java -cp \${PATH_SCRIPT}/libs/\* de.galan.plunger.application.Plunger \$*" >> ${PLUNGER_HOME}/${PLUNGER_NAME}
##echo "\${JAVA_HOME}/bin/java -cp .:jars/* de.galan.plunger.application.Plunger \$*" >> ${PLUNGER_HOME}/${PLUNGER_NAME}

chmod +x ${PLUNGER_HOME}/${PLUNGER_NAME}

createScript() {
	SCRIPT_NAME=${PLUNGER_HOME}/${PLUNGER_PREFIX}$1
	echo "#!/bin/bash" > ${SCRIPT_NAME}
	echo "target=\$1; shift 1" >> ${SCRIPT_NAME}
	echo "plunger \$target -c $1 \$*" >> ${SCRIPT_NAME}
	chmod +x ${SCRIPT_NAME}
}

createScript cat
createScript ls
createScript put
createScript count



header "finished"

if ! [[ -f `which ${PLUNGER_NAME}` ]]; then
	echo "You should expand your \$PATH to include \"$PLUNGER_HOME\""
fi
