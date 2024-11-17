# Versaflake
![GitHub Stars](https://img.shields.io/github/stars/facundoraviolo/versaflake)
![Java](https://img.shields.io/badge/Java-8%2B-brightgreen)
![Coverage](https://img.shields.io/codecov/c/github/facundoraviolo/versaflake)
![Open Issues](https://img.shields.io/github/issues/facundoraviolo/versaflake)
![Pull Requests](https://img.shields.io/github/issues-pr/facundoraviolo/versaflake)
![License](https://img.shields.io/github/license/facundoraviolo/versaflake?label=License&logo=open-source-initiative)

## Overview

**Versaflake** is a highly customizable library for generating unique, distributed, and time-ordered IDs inspired by [Twitter's Snowflake ID](https://en.wikipedia.org/wiki/Snowflake_ID) algorithm. Unlike the original Snowflake, Versaflake allows developers to customize the bit allocation between the node identifier and the sequence counter. This flexibility enables you to tailor the generator to your specific use case:

- **More nodes with fewer IDs per millisecond per node**, or
- **Fewer nodes with more IDs per millisecond per node**.

This adaptability makes Versaflake suitable for a wide variety of distributed systems.

## Key Features

- **Customizable configuration**: Adjust the number of bits allocated for the node ID and sequence to match your system's needs.
- **Thread-safe ID generation**: Generate IDs concurrently in a multithreaded environment without collisions.
- **Epoch-based time-ordering**: The IDs are based on the current time, ensuring they are generated in a predictable, time-ordered sequence.
- **Builder design pattern**: Simplifies configuration with a fluent API for creating generators.
- **Default configuration**:
  - Start Epoch: 2024-01-01 00:00:00 UTC.
  - Node ID Bits: 10 (1024 possible nodes).
  - Sequence Bits: 12 (4096 IDs per millisecond per node).

## Getting Started

### Installation

Add Versaflake to your project via Maven or Gradle (coming soon to Maven Central).

### Example Usage

Using the default configuration:

```java
import ar.com.facundoraviolo.versaflake.VersaflakeIDGenerator;  

public class Main {
    
    public static void main(String[] args) {

        VersaflakeIDGenerator versaflakeIDGenerator = new VersaflakeIDGenerator.Builder(1).build();

        long id = versaflakeIDGenerator.nextId();

    }

}
```

Using a custom configuration:

```java
import ar.com.facundoraviolo.versaflake.VersaflakeConfiguration;
import ar.com.facundoraviolo.versaflake.VersaflakeIDGenerator;  

public class Main {
    
    public static void main(String[] args) {

        VersaflakeConfiguration configuration = new VersaflakeConfiguration.Builder()
                .startEpoch(1731802819000L)
                .nodeIdBits(14)
                .sequenceBits(8)
                .build();

        VersaflakeIDGenerator versaflakeIDGenerator = new VersaflakeIDGenerator.Builder(15)
                .configuration(configuration)
                .build();
        
        long id = versaflakeIDGenerator.nextId();

    }

}
```

## Customization

### Configuration Options

You can customize the following parameters using the `VersaflakeConfiguration.Builder`:

- **Start Epoch**: Set a custom start date for the ID generator.
- **Node ID Bits**: Define the number of bits for identifying nodes (default: 10).
- **Sequence Bits**: Define the number of bits for the sequence counter (default: 12).

**Important**: The sum of `nodeIdBits` and `sequenceBits` must always equal 22. This ensures compatibility with the 64-bit ID format.

### Trade-offs

- Increasing **Node ID Bits** allows more nodes in the system but reduces the number of IDs a node can generate per millisecond.
- Increasing **Sequence Bits** allows more IDs per node per millisecond but reduces the total number of nodes.

## Exceptions

- **InvalidNodeIdException**: thrown if the node ID is outside the valid range for the configured number of node ID bits.
- **InvalidBitConfigurationException**: thrown if the sum of `nodeIdBits` and `sequenceBits` does not equal 22.
- **ClockMovedBackwardException**: thrown if the system clock moves backward, which can result in duplicate IDs.

## License

This project is licensed under the [MIT License](LICENSE).