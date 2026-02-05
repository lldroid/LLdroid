# LLdroid: LLM-based Framework for Detecting Data Races in Android Applications

## Overview

LLdroid is an innovative framework that leverages large language models (LLMs) to detect, repair, and verify data races in Android applications. Built to address the limitations of traditional static and dynamic analysis techniques, LLdroid provides a more comprehensive approach to identifying concurrency issues in Android's complex programming model.

## Problem Statement

Data races are a primary source of reliability issues in Android applications, stemming from the platform's complex concurrency model involving lifecycle management and asynchronous callbacks. Even under single-threaded programming assumptions, data races can occur due to Android's underlying architecture.

Traditional analysis techniques have significant limitations:
- **Static analysis**: High overhead and elevated false positives due to limited semantic understanding
- **Dynamic analysis**: Insufficient runtime coverage leading to high false negatives

## System Architecture

As shown in Figure 4, LLdroid comprises three main stages:

### 1. Data Race Detection
To prevent context loss caused by large project files, we first abstract the source code into a tree structure and send both the structure and code to the LLM. Leveraging prompt engineering techniques, the LLM is guided to detect, locate, and classify data races in Android applications. We evaluate three different prompt strategies to determine the optimal performance.

### 2. Repair
The system reads relevant files and generates fixes based on identified bug types and their locations. The patched files are then repackaged with the original source into a new APK.

### 3. Verification
The LLM generates test scripts based on bug descriptions. These scripts are executed on real devices using the DroidBot framework to determine whether data races have been successfully resolved. If bugs persist, the process repeats; otherwise, the verified patch is finalized and output.

## Installation

### Prerequisites
- Python 3.8+
- Java Development Kit (JDK) 8+
- Android SDK
- Git
- Required Python packages (see `requirements.txt`)

### Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/lldroid/LLdroid.git
   cd LLdroid
   ```

2. **Install dependencies**:
   ```bash
   pip install -r tree-sitter-codeviews-main/tree-sitter-codeviews-main/requirements.txt
   ```

3. **Set up Android environment**:
   Ensure you have the Android SDK installed and properly configured with `ANDROID_HOME` environment variable set.

4. **Configure LLM access**:
   Set up API keys for the LLM providers you intend to use (e.g., OpenAI, Google AI, etc.) in the configuration file.

## Usage

### Basic Usage

```bash
# Detect data races in an Android application
python -m comex.detect --apk path/to/app.apk

# Detect and repair data races
python -m comex.repair --apk path/to/app.apk

# Full workflow: detect, repair, and verify
python -m comex.full_workflow --apk path/to/app.apk
```

### Command Line Options

| Option | Description |
|--------|-------------|
| `--apk` | Path to the Android APK file |
| `--source` | Path to the source code directory (if APK is not provided) |
| `--model` | LLM model to use (e.g., gpt-4, gemini-pro) |
| `--prompt-strategy` | Prompt strategy to use (1, 2, or 3) |
| `--output` | Directory to store output results |
| `--verify` | Enable verification step |
| `--device` | Device ID for running tests |

## Technical Details

### Code Abstraction
LLdroid uses tree-sitter to parse source code into abstract syntax trees (ASTs), control flow graphs (CFGs), and data flow graphs (DFGs). These structures are then sent to the LLM along with relevant code snippets to provide context.

### Prompt Engineering
We evaluate three distinct prompt strategies:
1. **Direct instruction**: Clear, concise instructions for race detection
2. **Few-shot learning**: Providing examples of data races and their fixes
3. **Chain-of-thought**: Guiding the LLM through a step-by-step reasoning process

### Repair Generation
Based on detected races, the LLM generates修复方案 that follow Android best practices, such as:
- Proper synchronization using `synchronized` blocks
- Appropriate use of `volatile` variables
- Correct implementation of thread-safe data structures
- Proper lifecycle management

### Verification Process
The verification stage uses DroidBot to simulate user interactions and execute generated test scripts on real devices, ensuring that fixes effectively resolve detected races without introducing new issues.

## Evaluation

### Benchmark Results
LLdroid was evaluated against state-of-the-art traditional tools using the BenchERoid benchmark dataset, which contains 30 synthetic Android applications with known data races.

### Real-world Applications
We further assessed LLdroid's scalability and efficiency on 300 real-world Android applications, demonstrating its ability to handle complex codebases.

### LLM Comparison
We systematically investigated the capabilities of five mainstream LLMs in repairing data races:
- GPT-4
- GPT-3.5 Turbo
- Gemini Pro
- Claude 2
- Llama 2

## Directory Structure

```
LLdroid/
├── BenchERoid/          # Benchmark dataset for evaluation
│   ├── AsyncTask*/      # AsyncTask-related test cases
│   ├── Executor*/       # Executor-related test cases
│   ├── LifeCycle*/      # Lifecycle-related test cases
│   ├── Looper*/         # Looper-related test cases
│   ├── Service*/        # Service-related test cases
│   ├── Thread*/         # Thread-related test cases
│   └── TimerTask*/      # TimerTask-related test cases
├── tree-sitter-codeviews-main/  # Code parsing and abstraction tools
│   └── tree-sitter-codeviews-main/
│       ├── src/comex/   # Main source code
│       ├── sample/      # Sample code and images
│       └── tests/       # Test files
├── README.md            # This file
└── requirements.txt     # Python dependencies
```

## Dependencies

- **Python Packages**: See `tree-sitter-codeviews-main/tree-sitter-codeviews-main/requirements.txt`
- **LLM APIs**: Access to at least one of the supported LLM providers
- **Android Tools**: SDK, ADB, and build tools
- **DroidBot**: For running tests on real devices

## Contributing

We welcome contributions to LLdroid! To get started:

1. Fork the repository
2. Create a new branch for your feature
3. Make your changes
4. Submit a pull request

Please refer to `CONTRIBUTING.md` for more details.

## License

This project is licensed under the MIT License - see the `LICENSE` file for details.

## Acknowledgments

- This work was inspired by recent advances in large language models for code understanding
- We thank the contributors to the tree-sitter and DroidBot projects
- Special thanks to our test subjects and evaluators


