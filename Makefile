# Tên image và container
MYSQL_IMAGE=my-mysql
MYSQL_CONTAINER=mysql_container

# Build MySQL Image
build-docker:
	@bash scripts/build.sh

# Chạy container MySQL
deploy-docker:
	@docker compose --env-file ./dev.env --profile database up -d

# Dừng container
stop-docker:
	@docker-compose --profile database down

# Xóa container và volume
clean-docker:
	@docker compose --env-file ./dev.env --profile database down -v

# Xem logs của MySQL
logs-mysql:
	@docker logs -f $(MYSQL_CONTAINER)

# Mở terminal MySQL trong container
exec-mysql:
	@docker exec -it $(MYSQL_CONTAINER) mysql -u root -p
