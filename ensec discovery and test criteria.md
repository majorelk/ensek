# Ensek testing application

- What areas of the site exist:
    - [Home](https://ensekautomationcandidatetest.azurewebsites.net/)
        - find out more button
            - goes to actual site
    - [About](https://ensekautomationcandidatetest.azurewebsites.net/Home/About)
        - about the company
        - text out of date
        - additional info about the company
            - goes to actual site
    - [Contact](https://ensekautomationcandidatetest.azurewebsites.net/Home/Contact)
        - error page
    - [Register](https://ensekautomationcandidatetest.azurewebsites.net/Account/Register)
        - Email input field
        - Password input field
        - Confirm Password input field
        - Submit button
        - remember me button
        - alternative login options not enabled, warning displayed
    - [Log in](https://ensekautomationcandidatetest.azurewebsites.net/Account/Login)
        - email input field
        - password input field
        - submit button
        - registration link
    - Buy Energy
        - visual bug on discount box
            - 30% in writing
            - 20% offered on badge
            - box out of line
       - Reset button
       - form to buy energy with input values
       - buy button for energy type
       - nuclear not available
    - Sell Energy
        - maintainence page

Test scenarios:
Check links
    links already checked through while finding sources above out
    
Register user
    register user with email and password
    register with password and confirm password not matching
    register with password of single char
        rule is 6 chars
    register with password of 5 chars
    register with password of 7 chars
    register with password of 100 chars
    register with invalid email and valid password
    register with valid email and valid password
    register over api

Login user
    Login with valid email and password
    login with invalid email and password
    login with valid email and invalid password
    inject sql
    inject jql
    Login with remember me selected
    
