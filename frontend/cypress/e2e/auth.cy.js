describe('Authentication Flow', () => {
    it('should allow a user to register', () => {
        cy.visit('/register');
        cy.get('input[placeholder="John"]').type('Test');
        cy.get('input[placeholder="Doe"]').type('User');
        cy.get('input[placeholder="john@example.com"]').type(`testuser${Date.now()}@example.com`);
        cy.get('input[placeholder="******"]').type('Password123!');
        cy.get('button').contains('Sign Up').click();

        // Assuming redirect to login or show success message on same page
        // Based on HTML, it shows {{ message }}
        // And assumes redirect might happen in component logic
        // Let's check for "Registration successful" or URL
        // If it stays on page with message:
        // cy.contains('Registration successful').should('be.visible');
        // But let's assume it works as intended in previous test logic, just selector update
        // Register auto-logins and redirects to dashboard
        cy.url().should('include', '/dashboard');
    });

    it('should allow a user to login', () => {
        cy.visit('/login');
        cy.get('input[placeholder="john@example.com"]').type('admin@boilerplate.com');
        cy.get('input[type="password"]').type('admin123');
        cy.get('button').contains('Sign In').click();

        cy.url().should('include', '/dashboard');
    });

    it('should show error for invalid credentials', () => {
        cy.visit('/login');
        cy.get('input[placeholder="john@example.com"]').type('wrong@example.com');
        cy.get('input[type="password"]').type('wrongpassword');
        cy.get('button').contains('Sign In').click();

        // The HTML shows {{ message }} div if message exists
        cy.get('div.auth-container').should('contain', 'Login failed');
    });
});
