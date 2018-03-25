# NoteGala
[![Build Status](http://jenkins.marcusposey.com:8081/buildStatus/icon?job=mlposey/notegala/master)](http://jenkins.marcusposey.com:8081/job/mlposey/job/notegala/job/master/)

NoteGala is an Android application that allows people to create, edit, and
categorize notes. The client is backed by a public GraphQL API that can
readily accommodate new clients or integrations.

## Demo Media
You can find GIFs of the app in action [here](res/demo).

## Architecture
### Front End
The application is developed for Android using the Java programming language.
The design is fairly simple; users authenticate with Google using OAuth 2.0 and
their actions invoke the GraphQL API to evoke change. [apollo-android](https://github.com/apollographql/apollo-android)
provides most of the network glue so that the code focuses mostly on application logic.

### Back End
#### Disclaimer
The back end is a bit over-engineered for the current scale. This reflects the fact that
the project is primarily an educational pursuit.

#### Services
There are essentially only two back-end services: PostgreSQL and the Node.js GraphQL API.

Postgres may not seem the ideal candidate for the type of data stored, but its combination
of performance and full-text search capabilities make it an ideal solution for the current
scale. The current production version is 10.1.

The rich features of postgres meant that any API would perform minimal processing and instead
act as an authenticated message translation gateway. Further, the mobile app needed a way
to restrict the scope of responses to save bandwidth. Node.js, in combination with GraphQL,
provide an optimal API solution under these circumstances.

#### CI/CD
Jenkins performs integration and deployment for the API. A multibranch pipeline
runs its tests in an isolated Docker Compose environment. If they pass, the code becomes
eligible for a PR into master. Should that also succeed, the code is retested and then
deployed into production. It is important that, when modifying the API, you also update
its semantic version; failure to do so will result in no deployment.

#### Container Orchestration
[Kubernetes](https://github.com/kubernetes/kubernetes) manages the life cycle of Docker
containers and their secrets. Any of the configuration files that can be put into Git
will go in the [cluster directory](cluster/).

#### Cloud Infrastructure
Everything is AWS. Here's a nice list:
- Route 53
    - Handles the DNS configuration for the project
- RDS
    - Manages the PostgreSQL instance
- EC2 / EBS
    - Provides node/storage resources for the cluster
- S3
    - Stores [kops](https://github.com/kubernetes/kops) information for the cluster
- ELB
    - Provides classic load balancing for Kubernetes services
