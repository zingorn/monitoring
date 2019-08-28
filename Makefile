PROJECT_NAME=url-monitoring
CUR_DIR=$(shell pwd)

test:
	mvn clean verify

local-run: local-stop
	@docker run -d --name url-monitoring-local -p8080:8080 \
		-v $(CUR_DIR)/docker_logs:/var/log/url_monitoring $(PROJECT_NAME) server env:local
	@sleep 10
	$(MAKE) local-push-config
	@echo "Application available by http://localhost:8080"

local-stop:
	@docker rm -f url-monitoring-local || true

local-push-config:
	curl -X PUT -H "Content-Type: application/json" http://localhost:8080/api/v1.0/scheduler/update-jobs --data "@dummy_config.json"

local-run-jar:
	@echo "Application available by http://localhost:8080"
	@java -server -jar standalone/target/standalone-1.0-SNAPSHOT-shaded.jar server env:local