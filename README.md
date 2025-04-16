# Versaflake
![GitHub Stars](https://img.shields.io/github/stars/facundoraviolo/versaflake)
![Java](https://img.shields.io/badge/Java-8%2B-brightgreen)
![Coverage](https://img.shields.io/codecov/c/github/facundoraviolo/versaflake)
![Open Issues](https://img.shields.io/github/issues/facundoraviolo/versaflake)
![Pull Requests](https://img.shields.io/github/issues-pr/facundoraviolo/versaflake)
![License](https://img.shields.io/github/license/facundoraviolo/versaflake?label=License&logo=open-source-initiative)

## Overview

**Versaflake** is a highly customizable library for generating unique, distributed, and time-ordered IDs inspired by [Twitter's Snowflake ID](https://en.wikipedia.org/wiki/Snowflake_ID) algorithm. Unlike the original Snowflake, Versaflake allows developers to fully customize the bit allocation for all components (timestamp, node identifier, and sequence counter). This flexibility enables you to tailor the generator to your specific use case by adjusting:

- **Timestamp bits**: Control how far into the future IDs can be generated
- **Node identifier bits**: Control how many different nodes can generate IDs
- **Sequence bits**: Control how many IDs can be generated per millisecond per node

The only restriction is that the total number of bits must not exceed 63 (due to Java's signed long limitation).
This adaptability makes Versaflake suitable for a wide variety of distributed systems.

## Key Features

- **Fully customizable bit allocation**: Adjust the number of bits for each component (timestamp, node ID, and sequence) to match your system's requirements.
- **Thread-safe ID generation**: Generate IDs concurrently in a multithreaded environment without collisions.
- **Strict mode support**: Choose between waiting for clock synchronization or throwing exceptions on backwards clock movement.
- **Epoch-based time-ordering**: The IDs are based on the current time, ensuring they are generated in a predictable, time-ordered sequence.
- **Builder design pattern**: Simplifies configuration with a fluent API for creating generators.
- **Default configuration**:
  - Start Epoch: 2025-01-01 00:00:00 UTC
  - Timestamp Bits: 41 (supports dates until 2094)
  - Node ID Bits: 10 (1024 possible nodes)
  - Sequence Bits: 12 (4096 IDs per millisecond per node)
  - Strict Mode: false (waits for clock to catch up)

## Getting Started

### Installation

You can add Versaflake to your project directly from Maven Central.

#### Using Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>ar.com.facundoraviolo</groupId>
    <artifactId>versaflake</artifactId>
    <version>1.0.1</version>
</dependency>
```

#### Using Gradle

Add the following to your `build.gradle` file:

```groovy
implementation 'ar.com.facundoraviolo:versaflake:1.0.1'
```

### Example Usage

Using the default configuration:

```java
import ar.com.facundoraviolo.versaflake.VersaflakeGenerator;  

public class Application {
    
    public static void main(String[] args) {

        VersaflakeGenerator versaflakeGenerator = VersaflakeGenerator.builder(1).build();

        long id = versaflakeGenerator.nextId();

    }

}
```

Using a custom configuration:

```java
import ar.com.facundoraviolo.versaflake.VersaflakeConfiguration;
import ar.com.facundoraviolo.versaflake.VersaflakeGenerator;  

public class Application {
    
    public static void main(String[] args) {

        VersaflakeConfiguration configuration = VersaflakeConfiguration.builder()
                .startEpoch(1731802819000L)
                .timestampBits(41)
                .nodeIdBits(14)
                .sequenceBits(8)
                .strictMode()
                .build();

        VersaflakeGenerator versaflakeGenerator = VersaflakeGenerator.builder(15)
                .configuration(configuration)
                .build();
        
        long id = versaflakeGenerator.nextId();

    }

}
```

## Customization

### Configuration Options

You can customize the following parameters using the `VersaflakeConfiguration.Builder`:

- **Start Epoch**: Set a custom start date for the ID generator.
- **Timestamp Bits**: Control how far into the future IDs can be generated (default: 41).
- **Node ID Bits**: Define the number of bits for identifying nodes (default: 10).
- **Sequence Bits**: Define the number of bits for the sequence counter (default: 12).
- **Strict Mode**: Choose whether to throw an exception on backwards clock movement (default: false).

**Important**: The sum of `timestampBits`, `nodeIdBits`, and `sequenceBits` must not exceed 63. This limitation comes from Java's signed long type, where one bit is reserved for the sign.

### Trade-offs

When customizing bit allocation, consider these trade-offs:

- **Timestamp Bits**: More bits allow for a longer time range before overflow, but reduce bits available for other components
- **Node ID Bits**: More bits allow more nodes in the system, but reduce bits available for other components
- **Sequence Bits**: More bits allow more IDs per millisecond per node, but reduce bits available for other components

For example:
- Default configuration (63 bits total):
  - 41 bits timestamp = ~69 years from epoch
  - 10 bits node ID = 1024 nodes
  - 12 bits sequence = 4096 IDs per millisecond per node

- Reduced timestamp configuration (53 bits total):
  - 39 bits timestamp = ~17 years from epoch
  - 6 bits node ID = 64 nodes
  - 8 bits sequence = 256 IDs per millisecond per node

### Strict Mode

The generator supports two modes for handling backwards clock movement:

- **Default Mode (false)**: When the system clock moves backward, the generator will wait until the clock catches up to avoid generating duplicate IDs.
- **Strict Mode (true)**: When the system clock moves backward, the generator will immediately throw a `ClockMovedBackwardException`.

Example using strict mode:

```java
VersaflakeGenerator generator = VersaflakeGenerator.builder(1)
    .configuration(VersaflakeConfiguration.builder()
        .strictMode()
        .build())
    .build();
```

## Exceptions

- **InvalidNodeIdException**: Thrown if the node ID is outside the valid range for the configured number of node ID bits.
- **InvalidBitConfigurationException**: Thrown if the total bits exceeds 63 or if any component has zero or negative bits.
- **ClockMovedBackwardException**: Thrown if strict mode is enabled and the system clock moves backward.

## License

This project is licensed under the [MIT License](LICENSE).