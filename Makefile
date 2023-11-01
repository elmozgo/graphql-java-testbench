PLATFORM_PREFIX=

.PHONY: clean_downstream
clean_downstream:
	$(PLATFORM_PREFIX) rm -rf ./downstream/node_modules

.PHONY: install_downstream
install_downstream: clean_downstream
	cd downstream && npm install

.PHONY: run_downstream
run_downstream: install_downstream
	node ./downstream/index.js

.PHONY: clean
clean:
	gradlew clean

.PHONY: run_blocking_seq
run_blocking_seq: clean
	gradlew :blockingservlet:bootRun --args='--spring.profiles.active=sequential'

.PHONY: run_blocking_par
run_blocking_par: clean
	gradlew :blockingservlet:bootRun --args='--spring.profiles.active=parallel'

.PHONY: run_async_java11
run_async_java11: clean
	gradlew :asyncservlet:bootRun --args='--spring.profiles.active=java11-httpclient'

.PHONY: run_async_apache_hc_client4
run_async_apache_hc_client4: clean
	gradlew :asyncservlet:bootRun --args='--spring.profiles.active=apache-hc-client4'

.PHONY: run_async_webclient
run_async_webclient: clean
	gradlew :asyncservlet:bootRun --args='--spring.profiles.active=webclient'

.PHONY: run_webflux
run_webflux: clean
	gradlew :webflux:bootRun

.PHONY: run_pors
run_pors: clean
	gradlew :plainoldrestservice:bootRun

