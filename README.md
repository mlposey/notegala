# NoteGala
[![Build Status](http://jenkins.marcusposey.com:8081/buildStatus/icon?job=mlposey/notegala/master)](http://jenkins.marcusposey.com:8081/job/mlposey/job/notegala/job/master/)

NoteGala is an Android application that allows people to create, edit, and
categorize notes. The client is backed by a public GraphQL API that can
readily accommodate new clients or integrations.

## Demo Media
The res/demo [readme](res/demo) contains GIFs of the latest Android build
for those without the necessary development environment. In total, the demo
media is roughly 25 MB.

## Tech Stack
The system is comprised of the following core components:  
* CI/CD Pipeline
* GraphQL API 
* Android Application
* PostgreSQL Data Store

### Pipeline
Jenkins performs automated API testing on each push to any branch. Tests
are run in an isolated Docker Compose environment made of the Node.js
tests and an updated version of the PostgreSQL database. Unless the branch
is master, only the test stage is run. Changes to master result in execution
of all stages, i.e.,  
* isolated testing
* staging of the API on an internal network
* replacing the latest Docker Hub images
* deploying the API to production Google Compute Engine servers

### API
The API is a Node.js service that uses GraphQL to enable retrieval and mutation
of the system state. It requires a Google ID token to authenticate requests.
These tokens can be generated in the [Google OAuth 2.0 Playground](https://developers.google.com/oauthplayground/)
if querying the API manually.

TDD principles are followed somewhat religiously, which is made possible by
the Mocha and Chai frameworks. In test and production environments, the service
is run within a Docker container.

### Android
Android development is performed with Java. The current feature set is simple
enough that reactive logic can easily be coded using appropriate attention
to design patterns, but RxJava may eventually become necessary. Network interaction with the API uses the [apollo-android](https://github.com/apollographql/apollo-android)
client extensively.

### Database
All persistent state is kept in a PostgreSQL database managed by Google Cloud
SQL. That state includes (but is not limited to) account metadata, notes, and
notebooks. Additionally, most note content is structured to enable full text
search.
