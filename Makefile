APP_NAME = expensetracker
JAR_NAME = target/$(APP_NAME).jar
CONTAINER_NAME = $(APP_NAME)-app
CONTAINER_TOOL = podman

build:
	./mvnw clean package

container-build: build
	$(CONTAINER_TOOL) build -t $(CONTAINER_NAME) .

container-run:
	$(CONTAINER_TOOL)-compose up --build

clean:
	./mvnw clean && rm -rf ./target

help:
	@echo 'Makefile commands:'
	@echo '    build             - Build the application with production configurations'
	@echo '    container-build   - Build the application, then create the container image'
	@echo '    container-run     - Run the containerized application'
	@echo '    clean             - Clean the project (Maven clean and remove target directory)'
	@echo '    help              - Display this help message'