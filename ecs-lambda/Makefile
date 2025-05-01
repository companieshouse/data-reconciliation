artifact_name     := data-reconciliation-ecs-lambda
version           := "unversioned"
src_dir           := ecs-lambda



.PHONY: all
all: build

.PHONY: clean
clean:
	mvn clean
	rm -f ./$(artifact_name).jar
	rm -f ./$(artifact_name)-*.zip
	rm -rf ./build-*
	rm -f ./build.log

.PHONY: security-check
security-check:
	mvn org.owasp:dependency-check-maven:update-only
	mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=4 -DassemblyAnalyzerEnabled=false

.PHONY: build
build:
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true -DsourceDirectory=$(src_dir)
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name).jar

.PHONY: test
test: clean
	mvn verify

.PHONY: test-unit
test-unit: clean
	mvn verify -Dskip.unit.tests=false -Dskip.integration.tests=true

.PHONY: package
package:
ifndef version
	$(error No version given. Aborting)
endif
	$(info Packaging version: $(version))
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true -DsourceDirectory=$(src_dir)
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp ./target/$(artifact_name)-$(version).jar $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir);
	cp ./$(artifact_name).jar ./$(artifact_name).zip
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name)-$(version).zip
	rm -rf $(tmpdir)

.PHONY: dist
dist: clean build package

.PHONY: sonar
sonar:
	mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar -P sonar-pr-analysis