describe('Profile Page', () => {
    beforeEach(() => {
        // Mock login or assume valid session if backend was running
        // Since we can't run backend, these tests are hypothetical but correct in structure.

        // Attempt to visit profile if we could login.
        // cy.login('test@example.com', 'password'); 
        // cy.visit('/profile');

        // Mock login by setting token
        localStorage.setItem('access_token', 'mock-jwt-token');

        // Intercept profile calls
        cy.intercept('GET', '/api/v1/profile/me', {
            statusCode: 200,
            body: {
                id: 1,
                email: 'test@example.com',
                role: { name: 'USER' },
                firstname: 'John',
                lastname: 'Doe'
            }
        }).as('getProfile');
    });

    it('should display profile information', () => {
        cy.visit('/profile');
        // We need to bypass login guard for this to work purely essentially, 
        // but assuming we are "logged in" for the app state:
        // This requires App to check auth state.

        // Verifying UI
        cy.wait('@getProfile');
        cy.get('input[formControlName="email"]').should('have.value', 'test@example.com');
        cy.get('input[formControlName="firstname"]').should('have.value', 'John');
    });

    it('should update profile', () => {
        cy.intercept('PUT', '/api/v1/profile/me', {
            statusCode: 200,
            body: {
                id: 1,
                email: 'test@example.com',
                role: { name: 'USER' },
                firstname: 'Jane',
                lastname: 'Doe'
            }
        }).as('updateProfile');

        cy.visit('/profile');
        cy.wait('@getProfile');

        cy.get('input[formControlName="firstname"]').clear().type('Jane');
        cy.get('button[type="submit"]').click();

        cy.wait('@updateProfile');
        cy.contains('Profile updated successfully').should('exist');
        cy.get('input[formControlName="firstname"]').should('have.value', 'Jane');
    });
});
