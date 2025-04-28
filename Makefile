artifact_name       := data-reconciliation
version             := unversioned
exposed_port        := ${DATA_RECONCILIATION_PORT}

##### Start of variable block for top of Makfile
dependency_check_base_suppressions:=common_suppressions_spring_6.xml

# dependency_check_suppressions_repo_branch
# The branch of the dependency-check-suppressions repository to use
# as the source of the suppressions file.
# This should point to "main" branch when being used for release,
# but can point to a different branch for experimentation/development.
dependency_check_suppressions_repo_branch:=feature/suppressions-for-data-reconciliation

dependency_check_minimum_cvss := 4
dependency_check_assembly_analyzer_enabled := false
dependency_check_suppressions_repo_url:=git@github.com:companieshouse/dependency-check-suppressions.git
suppressions_file := target/suppressions.xml
##### End of variable block

# Lambda function related variables
lambda_name         := $(artifact_name)ecs-task-stopper
lambda_src_dir      := ecs-lambda
lambda_build_dir    := build

.PHONY: all
all: build

.PHONY: clean
clean:
	mvn clean
	rm -f ./$(artifact_name).jar
	rm -f ./$(artifact_name)-*.zip
	rm -rf ./build-*
	rm -f ./build.log
	rm -rf ./$(lambda_build_dir)
	rm -f ./$(lambda_name).zip
	rm -f ./$(lambda_name)-*.zip

.PHONY: build
build:
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name).jar

.PHONY: test
test: test-unit

.PHONY: test-unit
test-unit: clean
	mvn test

.PHONY: test-integration
test-integration: clean
	mvn test -Dskip.unit.tests=true

.PHONY: package
package:
ifndef version
	$(error No version given. Aborting)
endif
	$(info Packaging version: $(version))
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp ./start.sh $(tmpdir)
	# cp ./routes.yaml $(tmpdir)
	# cp ./src/main/resources/* $(tmpdir)
	cp ./target/$(artifact_name)-$(version).jar $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir); zip -r ../$(artifact_name)-$(version).zip *
	rm -rf $(tmpdir)

.PHONY: dist
dist: clean build docker-build

.PHONY: sonar
sonar:
	mvn sonar:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn sonar:sonar -P sonar-pr-analysis

.PHONY: docker-build
docker-build:
	docker build -t $(artifact_name):$(version) .

#####################################################
# Lambda Function Targets
#####################################################

.PHONY: lambda-clean
lambda-clean:
	rm -rf ./$(lambda_build_dir)
	rm -f ./$(lambda_name).zip
	rm -f ./$(lambda_name)-*.zip

.PHONY: lambda-build
lambda-build: lambda-clean
	mkdir -p $(lambda_build_dir)
	cp $(lambda_src_dir)/handler.py $(lambda_build_dir)/lambda_function.py
	# Use a virtual environment for clean dependency installation
	python -m venv $(lambda_build_dir)/.venv
	$(lambda_build_dir)/.venv/bin/pip install --upgrade pip
	$(lambda_build_dir)/.venv/bin/pip install -r $(lambda_src_dir)/requirements.txt --target $(lambda_build_dir)
	# Set correct permissions for Lambda execution
	chmod 755 $(lambda_build_dir)/lambda_function.py
	find $(lambda_build_dir) -type d -exec chmod 755 {} \; 2>/dev/null || true
	find $(lambda_build_dir) -type f -exec chmod 644 {} \; 2>/dev/null || true
	# Clean up unnecessary files to reduce package size
	find $(lambda_build_dir) -name "__pycache__" -type d -exec rm -rf {} + 2>/dev/null || true
	find $(lambda_build_dir) -name "*.pyc" -delete
	find $(lambda_build_dir) -name "*.pyo" -delete
	find $(lambda_build_dir) -name "*.dist-info" -type d -exec rm -rf {} + 2>/dev/null || true
	find $(lambda_build_dir) -name "*.egg-info" -type d -exec rm -rf {} + 2>/dev/null || true
	# Remove the virtual environment - it was only used for installation
	rm -rf $(lambda_build_dir)/.venv
	# Create the zip with proper permissions
	cd $(lambda_build_dir); zip -r ../$(lambda_name).zip *
	@echo "Created $(lambda_name).zip"
	@echo "Size: $$(du -h $(lambda_name).zip | cut -f1)"

.PHONY: lambda-package
lambda-package:
ifndef version
	$(error No version given. Aborting)
endif
	$(info Packaging Lambda version: $(version))
	mkdir -p $(lambda_build_dir)
	cp $(lambda_src_dir)/handler.py $(lambda_build_dir)/lambda_function.py
	# Use a virtual environment for clean dependency installation
	python -m venv $(lambda_build_dir)/.venv
	$(lambda_build_dir)/.venv/bin/pip install --upgrade pip
	$(lambda_build_dir)/.venv/bin/pip install -r $(lambda_src_dir)/requirements.txt --target $(lambda_build_dir)
	# Set correct permissions for Lambda execution
	chmod 755 $(lambda_build_dir)/lambda_function.py
	find $(lambda_build_dir) -type d -exec chmod 755 {} \; 2>/dev/null || true
	find $(lambda_build_dir) -type f -exec chmod 644 {} \; 2>/dev/null || true
	# Clean up unnecessary files to reduce package size
	find $(lambda_build_dir) -name "__pycache__" -type d -exec rm -rf {} + 2>/dev/null || true
	find $(lambda_build_dir) -name "*.pyc" -delete
	find $(lambda_build_dir) -name "*.pyo" -delete
	find $(lambda_build_dir) -name "*.dist-info" -type d -exec rm -rf {} + 2>/dev/null || true
	find $(lambda_build_dir) -name "*.egg-info" -type d -exec rm -rf {} + 2>/dev/null || true
	# Remove the virtual environment - it was only used for installation
	rm -rf $(lambda_build_dir)/.venv
	# Create the zip with proper permissions
	cd $(lambda_build_dir); zip -r ../$(lambda_name)-$(version).zip *
	@echo "Created $(lambda_name)-$(version).zip"
	@echo "Size: $$(du -h $(lambda_name)-$(version).zip | cut -f1)"

.PHONY: lambda-dist
lambda-dist: lambda-clean lambda-build lambda-package

##### Start of dependency-check block to be put at bottom of Makefile
.PHONY: dependency-check
dependency-check:
	@ if [ -d "$(DEPENDENCY_CHECK_SUPPRESSIONS_HOME)" ]; then \
		suppressions_home="$${DEPENDENCY_CHECK_SUPPRESSIONS_HOME}"; \
	fi; \
	if [ ! -d "$${suppressions_home}" ]; then \
	    suppressions_home_target_dir="./target/dependency-check-suppressions"; \
		if [ -d "$${suppressions_home_target_dir}" ]; then \
			suppressions_home="$${suppressions_home_target_dir}"; \
		else \
			mkdir -p "./target"; \
			git clone $(dependency_check_suppressions_repo_url) "$${suppressions_home_target_dir}" && \
				suppressions_home="$${suppressions_home_target_dir}"; \
			if [ -d "$${suppressions_home_target_dir}" ] && [ -n "$(dependency_check_suppressions_repo_branch)" ]; then \
				cd "$${suppressions_home}"; \
				git checkout $(dependency_check_suppressions_repo_branch); \
				cd -; \
			fi; \
		fi; \
	fi; \
	suppressions_path="$${suppressions_home}/suppressions/$(dependency_check_base_suppressions)"; \
	if [  -f "$${suppressions_path}" ]; then \
		cp -av "$${suppressions_path}" $(suppressions_file); \
		mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=$(dependency_check_minimum_cvss) -DassemblyAnalyzerEnabled=$(dependency_check_assembly_analyzer_enabled) -DsuppressionFiles=$(suppressions_file); \
	else \
		printf -- "\n ERROR Cannot find suppressions file at '%s'\n" "$${suppressions_path}" >&2; \
		exit 1; \
	fi

.PHONY: security-check
security-check: dependency-check

##### End of dependency-check block