# Concurrent Computation Engine

A Java 21 command-line engine for evaluating matrix expressions using a custom multithreaded executor.

The engine reads a computation tree from JSON, resolves its operations, and writes the resulting matrix to a JSON file. Matrix operations are divided into row-level tasks and dispatched to worker threads.

## Features

- Matrix addition, multiplication, negation, and transposition.
- Nested matrix expressions represented as computation trees.
- Custom worker-thread executor with fatigue-based scheduling.
- Priority-based selection of available workers.
- Shared matrix and vector structures using read/write locks.
- Worker statistics reporting execution time, idle time, and fatigue.
- JSON input and output using Jackson.
- JUnit 5 tests for matrix operations, scheduling, and engine behavior.

## Architecture

| Component | Responsibility |
| --- | --- |
| `LinearAlgebraEngine` | Resolves computation trees and creates matrix-operation tasks |
| `TiredExecutor` | Dispatches tasks to available workers and waits for completion |
| `TiredThread` | Executes tasks and tracks worker activity |
| `SharedMatrix`, `SharedVector` | Store matrix data and provide synchronized vector operations |
| `ComputationNode` | Represents matrix operands and operations |
| `InputParser`, `OutputWriter` | Read expressions and write results as JSON |

The engine resolves the expression tree step by step. Within each operation, row-level tasks can execute concurrently.

Available workers are prioritized using a fatigue score based on accumulated execution time and a per-worker factor.

## Requirements

- JDK 21
- Maven

## Build and Test

```bash
mvn clean package
```

To run the tests separately:

```bash
mvn test
```

## Run

From the repository root:

```bash
java -jar target/lga-1.0.jar 4 example.json result.json
```

Arguments:

1. Number of worker threads.
2. Input JSON file.
3. Output JSON file.

The program writes the result to the output file and prints worker statistics.

## Input Example

The following expression adds two matrices:

```json
{
  "operator": "+",
  "operands": [
    [[1, 2], [3, 4]],
    [[5, 6], [7, 8]]
  ]
}
```

The resulting matrix is:

```text
6   8
10  12
```

Supported operators:

| Operator | Operation |
| --- | --- |
| `+` | Matrix addition |
| `*` | Matrix multiplication |
| `-` | Unary negation |
| `T` | Transposition |

Operands can contain matrices or nested expressions. See `example.json` for a larger example.

## Testing

The repository includes JUnit tests for:

- Shared vector operations.
- Shared matrix storage and access.
- Worker-thread behavior.
- Executor task dispatch.
- Matrix-expression evaluation.

## Background

Developed by Alon Moneli and Tomer Yaron as part of the Systems Programming course at Ben-Gurion University.
