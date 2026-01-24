# Contributing to Enterprise Boilerplate

Thank you for considering contributing to this project!

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on what is best for the community

## How to Contribute

### Reporting Bugs

1. Check if the bug has already been reported
2. Open a new issue with:
   - Clear title and description
   - Steps to reproduce
   - Expected vs actual behavior
   - Environment details (OS, Java version, Node version)

### Suggesting Features

1. Open an issue with the "feature request" label
2. Describe the feature and its use case
3. Explain why it would be valuable

### Pull Requests

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Follow coding standards**
   - Backend: Follow Checkstyle rules
   - Frontend: Run `pnpm lint:fix`
   - Write tests for new features

4. **Commit messages**
   - Use Conventional Commits format
   - Examples:
     - `feat: add user profile endpoint`
     - `fix: resolve JWT token expiration issue`
     - `docs: update README with deployment instructions`

5. **Run tests**
   ```bash
   # Backend
   mvn verify

   # Frontend
   pnpm test
   ```

6. **Submit PR**
   - Reference related issues
   - Describe changes made
   - Include screenshots for UI changes

## Development Setup

See [README.md](README.md) for detailed setup instructions.

## Coding Standards

### Backend (Java)
- Follow hexagonal architecture patterns
- Use MapStruct for all DTO mappings
- Write Flyway migrations for schema changes
- Add JavaDoc for public APIs
- Minimum 70% test coverage

### Frontend (TypeScript/Angular)
- Use standalone components
- Signals for state management
- TanStack Query for server state
- Tailwind CSS for styling
- Follow Angular style guide

## Questions?

Open an issue or discussion for clarification.

Thank you for contributing!
