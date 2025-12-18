# Contributing to Spring Boot Enterprise Boilerplate

First off, thanks for taking the time to contribute!

## Code of Conduct
This project is for educational and enterprise-starter purposes. Please treat everyone with respect.

## How Can I Contribute?

### Reporting Bugs
This section guides you through submitting a bug report.
- **Use a clear and descriptive title** for the issue to identify the problem.
- **Describe the exact steps which reproduce the problem** in as much detail as possible.
- **Provide specific examples to demonstrate the steps**.

### Suggesting Enhancements
- **Use a clear and descriptive title** for the issue to identify the suggestion.
- **Provide a step-by-step description of the suggested enhancement** in as much detail as possible.
- **Explain why this enhancement would be useful** to most users.

### Pull Requests
1. Fork the repo and create your branch from `main`.
2. If you've added code that should be tested, add tests.
3. Ensure the test suite passes.
4. Make sure your code follows the existing style (Google Java Style/Standard Spring Boot conventions).
5. Open that PR!

## Styleguides

### Git Commit Messages
- Use the present tense ("Add feature" not "Added feature")
- Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
- Limit the first line to 72 characters or less

### Java Styleguide
- Use strict **Hexagonal Architecture**.
    - **Domain**: POJOs only. No framework dependencies.
    - **Application**: Orchestration logic. Can depend on Domain and Ports.
    - **Infrastructure**: All framework implementations (Spring, Hibernate, etc.).
- Use Lombok for boilerplate.
- Use `final` variables where consistent.
