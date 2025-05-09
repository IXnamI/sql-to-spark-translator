# SQL to Spark Translator

A tool designed to converts standard SQL queries into equivalent Apache Spark code

---

## Features

- Support for SELECT, WHERE, JOIN, GROUP BY, ORDER BY
- Nested queries and subqueries
- Basic Window functions
- Translation into Spark Scala

---

## Prerequisites

- Java 8 or higher
- Maven as the build tool

---

## Build and Run

### Using Maven

```mvn clean package``` \
```java -cp target/sql-to-spark-1.0-SNAPSHOT.jar com.github.xnam.Main```

### Running Tests

```mvn test ```

---

## License

This project is licensed under the MIT License.  
See the [LICENSE](./LICENSE) file for details.