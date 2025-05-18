# SQL to Spark Translator (WIP)

A tool designed to converts standard SQL queries into equivalent Apache Spark code

---

## Features

- Support for most basic statements
- Nested queries and subqueries
- Basic Window functions
- Translation into Spark Scala

---

## TODO: 

- Support for all tokens available in the SQL syntax, ie: IN, VALUES, BETWEEN-AND, ...
- Auto formatter of outputted code. 
- Compression of unneeded generated code.
- Maybe translation into Spark Python.

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