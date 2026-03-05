FRONT_IMAGE = okamitah/aeger-hub-deploy-frontend
BACK_IMAGE  = okamitah/aeger-hub-deploy-backend

DB_CONTAINER   = postgres-db
BACK_CONTAINER = backend
FRONT_CONTAINER = frontend

DB_VOLUME = /home/$(USER)/aeger_db_data


build-frontend:
	cd front && rm -rf dist && npm ci
	cd front && NODE_OPTIONS="--max_old_space_size=1024" npm run build
	docker build --no-cache -t $(FRONT_IMAGE):latest ./front

build-backend:
	cd back && mvn clean package -U
	docker build --no-cache -t $(BACK_IMAGE):latest ./back

build: build-backend build-frontend


run-db:
	docker stop $(DB_CONTAINER) 2>/dev/null || true
	docker rm   $(DB_CONTAINER) 2>/dev/null || true
	docker run -d --name $(DB_CONTAINER) -p 5432:5432 \
		-v $(DB_VOLUME):/var/lib/postgresql/data \
		-e POSTGRES_DB=aeger_hub_db \
		-e POSTGRES_USER=aeger \
		-e POSTGRES_PASSWORD=aeger \
		postgres:15

run-backend:
	docker stop $(BACK_CONTAINER) 2>/dev/null || true
	docker rm   $(BACK_CONTAINER) 2>/dev/null || true
	docker run -d --name $(BACK_CONTAINER) -p 8080:8080 \
		--network host \
		-e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/aeger_hub_db \
		-e SPRING_DATASOURCE_USERNAME=aeger \
		-e SPRING_DATASOURCE_PASSWORD=aeger \
		$(BACK_IMAGE):latest

run-frontend:
	docker stop $(FRONT_CONTAINER) 2>/dev/null || true
	docker rm   $(FRONT_CONTAINER) 2>/dev/null || true
	docker run -d --name $(FRONT_CONTAINER) -p 80:80 \
		$(FRONT_IMAGE):latest

run: run-db run-backend run-frontend


all: build run

stop:
	docker stop $(FRONT_CONTAINER) $(BACK_CONTAINER) $(DB_CONTAINER) 2>/dev/null || true
	docker rm   $(FRONT_CONTAINER) $(BACK_CONTAINER) $(DB_CONTAINER) 2>/dev/null || true

logs-backend:
	docker logs -f $(BACK_CONTAINER)

logs-frontend:
	docker logs -f $(FRONT_CONTAINER)

logs-db:
	docker logs -f $(DB_CONTAINER)
