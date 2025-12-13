describe('Dashboard Access', () => {
    it('should redirect to login if not authenticated', () => {
        cy.visit('/dashboard');
        cy.url().should('include', '/login');
    });

    it('should access dashboard if authenticated', () => {
        // Login first
        cy.visit('/login');
        cy.get('input[placeholder="john@example.com"]').type('admin@boilerplate.com');
        cy.get('input[type="password"]').type('admin123');
        cy.get('button').contains('Sign In').click();

        // Wait for redirect
        cy.url().should('include', '/dashboard');
        cy.contains('Dashboard').should('be.visible');
    });
});
