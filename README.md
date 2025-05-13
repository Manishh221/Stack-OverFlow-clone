# StackOverflow

## Core Features

1. **Authentication & Authorization**  
   User login, signup, and logout functionality.

2. **Question Management**  
   Full CRUD (Create, Read, Update, Delete) operations for questions.

3. **Answer Management**  
   Full CRUD operations for answers.

4. **Comment Management**  
   Full CRUD operations for comments.

5. **Tag Association**  
   Attach one or multiple tags to questions for better organization and searchability.

6. **Voting System**  
   Upvote and downvote functionality for both questions and answers.

7. **User Profiles**  
   View personal and other users' profiles.  
   Display user information, posted questions, submitted answers, and total reputation (based on votes).

8. **Reputation-Based Privileges**  
   Restrict answering and commenting features based on user reputation thresholds.

9. **Advanced Search**  
   Search by username, question title, question description, or tags.

10. **Pagination**  
    Implement pagination for lists of questions, users, and tags to optimize performance and user experience.
    
11. **Security**  
    A logged-in user isn't able to delete or edit other users' posts the user can only answer and comment on posts if he has a valid reputation.
    Only the users who can edit or delete the post are the post belong to him.
    and many other securities....

Postgres database 

PGPASSWORD=FlBjFBDRNtmlsEw0qRcB4jR9XcR1Wwti psql -h dpg-d083lp49c44c73bg6a80-a.singapore-postgres.render.com -U root stackdb_d9vc
