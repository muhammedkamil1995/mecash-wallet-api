# meCash - Multicurrency Wallet API
meCash is a multicurrency wallet application that allows users to send, receive, deposit, withdraw, and manage money securely across different currencies.


# intialize spring
https://start.spring.io/
# maven installation guide
https://phoenixnap.com/kb/install-maven-windows
# select dependencies

H2
Java (Spring Boot)
MySQL = Create a MySQL database: mecash
JPA/Hibernate
Java 21
JWT Authentication
JUnit & Mockito (Testing)


# Server Configuration
server.port=8005
spring.sql.init.mode=never
spring.sql.init.data-locations=classpath:data/mecash.sql

# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/mecash?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.type=com.zaxxer.hikari.HikariDataSource

# Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=your key
jwt.expiration=3600000


# Authentication
Method	Endpoint	Description
POST	/auth/register	Register a new user
POST	/auth/login	Authenticate user & get JWT

# Wallet Operations
Method	Endpoint	Description
POST	/transactions/deposit?userId={id}&amount={amount}&currency={currency}	Deposit money
POST	/transactions/withdraw?userId={id}&amount={amount}&currency={currency}	Withdraw money
POST	/transactions/transfer?senderId={id}&recipientId={id}&amount={amount}&currency={currency}	Transfer money

