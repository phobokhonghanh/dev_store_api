#!/bin/bash
set -e

echo "⏳ Đang kiểm tra database dev_store..."

# Chờ MySQL sẵn sàng
until mysqladmin ping -h "localhost" --silent; do
    echo "⏳ Chờ MySQL khởi động..."
    sleep 2
done

# Kiểm tra xem database đã tồn tại chưa
DB_EXISTS=$(mysql -u root -p"$MYSQL_ROOT_PASSWORD" -e "SHOW DATABASES LIKE 'dev_store';" | grep "dev_store" | wc -l)

if [ "$DB_EXISTS" -eq 0 ]; then
    echo "✅ Database chưa tồn tại, đang import dữ liệu..."
    mysql -u root -p"$MYSQL_ROOT_PASSWORD" < /docker-entrypoint-initdb.d/init.sql
else
    echo "✅ Database đã tồn tại, bỏ qua import dữ liệu."
fi

# Khởi động MySQL
exec mysqld
