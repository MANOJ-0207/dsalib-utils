# dsalib-utils

📚 **Data Structures and Algorithms Utility Library** — A well-organized and extensible Java library containing common data structures and utilities for graph theory, caching, number theory, trees, heaps, segment trees, and string pattern matching.

## 📁 Project Structure

```
dsalib-utils/
├── src/
│   ├── main/java/io.github.manoj0207.dsalibutils/
│   │   ├── cache/               → FIFO, LRU, LFU, Segmented LRU cache implementations
│   │   ├── graph/               → Graph implementations (unweighted/weighted, list/matrix)
│   │   ├── heap/                → Efficient heaps
│   │   ├── math/                → Number theory, combinatorics
│   │   ├── rangequery/          → Segment trees, sparse tables, order statistic trees
│   │   ├── stringutils/         → Pattern matchers (KMP, Z, RK)
│   │   └── tree/                → Generic tree structure
│   └── test/java/io.github.manoj0207.dsalibutils/
│       ├── (unit tests for all modules)
```

## ✅ Features

- ✅ Efficient segment trees with lazy propagation
- ✅ LRU, LFU, FIFO, Segmented LRU cache
- ✅ Weighted/unweighted graph structures
- ✅ Union-Find (Disjoint Set)
- ✅ Number theory (prime, modmath)
- ✅ Pattern matching: KMP, Z-algo, Rabin-Karp
- ✅ Order Statistic Tree and Set
- ✅ Sparce table for array and matrix

## 🧪 Unit Testing

- Built using JUnit 5
- Thorough coverage for:
  - Valid scenarios
  - Edge cases
  - Exception handling

## 🚀 Getting Started

```bash
git clone https://github.com/MANOJ-0207/dsalib-utils.git
cd dsalib-utils
mvn clean install
```


## 📄 License

This project is open source and available under the MIT License.

## 👨‍💻 Author

**Manoj Kumar G**
