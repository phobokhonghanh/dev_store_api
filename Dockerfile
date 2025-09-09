# Build MySQL
FROM mysql:latest AS mysql
COPY mysql/my.cnf /etc/mysql/my.cnf
RUN chown mysql:mysql /etc/mysql/my.cnf && chmod 444 /etc/mysql/my.cnf
COPY scripts/entrypoint_sql.sh /etc/scripts/entrypoint_sql.sh
RUN chmod +x /etc/scripts/entrypoint_sql.sh
ENTRYPOINT ["/etc/scripts/entrypoint_sql.sh"]
CMD ["mysqld"]
FROM adminer:latest AS adminer
