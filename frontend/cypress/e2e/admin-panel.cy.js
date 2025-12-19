describe('Admin Panel', () => {
    beforeEach(() => {
        // Mock Authentication with a valid JWT structure (Header.Payload.Signature)
        // Payload: { "sub": "admin@test.com", "role": "ADMIN", "exp": 1999999999 }
        // Base64Url Payload: eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInJvbGUiOiJBRE1JTiIsImV4cCI6MTk5OTk5OTk5OX0
        const mockJwt = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInJvbGUiOiJBRE1JTiIsImV4cCI6MTk5OTk5OTk5OX0.signature';
        localStorage.setItem('access_token', mockJwt);

        // Mock Users
        cy.intercept('GET', '/api/v1/users', {
            statusCode: 200,
            body: [
                { id: 1, email: 'admin@test.com', firstname: 'Admin', lastname: 'User', role: { name: 'ADMIN' } },
                { id: 2, email: 'user@test.com', firstname: 'Test', lastname: 'User', role: { name: 'USER' } }
            ]
        }).as('getUsers');

        // Mock Logs
        cy.intercept('GET', '/api/v1/activity-logs', {
            statusCode: 200,
            body: [
                { id: 1, action: 'LOGIN', userEmail: 'admin@test.com', timestamp: '2023-01-01', description: 'User login' }
            ]
        }).as('getLogs');
    });

    it('should display user list', () => {
        cy.visit('/admin'); // Assuming /admin, verified in next step if different
        cy.wait('@getUsers');

        cy.contains('Admin Panel').should('be.visible');
        cy.contains('admin@test.com').should('be.visible');
        cy.contains('user@test.com').should('be.visible');
    });

    it('should edit user', () => {
        cy.intercept('PUT', '/api/v1/users/2', {
            statusCode: 200,
            body: { id: 2, email: 'user@test.com', firstname: 'Updated', lastname: 'User', role: { name: 'USER' } }
        }).as('updateUser');

        cy.visit('/admin');
        cy.wait('@getUsers');

        // Click Edit on second user (User 2)
        cy.contains('tr', 'user@test.com').find('button').contains('Edit').click();

        // Change Name
        cy.get('input[type="text"]').first().clear().type('Updated');

        // Save
        cy.contains('Save Changes').click();

        // Verify Update
        cy.wait('@updateUser');
        cy.on('window:alert', (str) => {
            expect(str).to.equal('User updated successfully');
        });
    });

    it('should delete user', () => {
        cy.intercept('DELETE', '/api/v1/users/2', {
            statusCode: 204
        }).as('deleteUser');

        cy.visit('/admin');
        cy.wait('@getUsers');

        // Mock confirm dialog
        cy.on('window:confirm', () => true);

        // Click Delete on User 2
        cy.contains('tr', 'user@test.com').find('button').contains('Delete').click();

        cy.wait('@deleteUser');
        cy.wait('@getUsers'); // Should reload list
    });

    it('should switch to mock logs', () => {
        cy.visit('/admin');
        cy.wait('@getUsers');

        cy.contains('Activity Logs').click();
        cy.wait('@getLogs');

        cy.contains('User login').should('be.visible');
    });
});
