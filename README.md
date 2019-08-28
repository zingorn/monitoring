# UrlMonitoring

This project is job scheduling application where you can implement any job to verify your service state.
At this moment implemented only Http Job to check site availability but you can add a Database checker or something else.

[Application design details](DESIGN.md)

## Requirements
- Maven 3.x
- Make GNU
- Java 8
- Docker

## Dependencies

| Name | Description |
|---   |---          |
| Dropwizard 1.3.11  | Application skeleton [framework](https://www.dropwizard.io/)
| Dager 2.24  | Dependency injection [framework](https://dagger.dev/)
| Quartz 2.3.1 | [](http://www.quartz-scheduler.org/)

## Build and run

### Build project:
```bash
make test
```
  
  This command builds project and Docker image with jar file.

### Local project run

Note: local port 8080 should be free for local run. 

* Run in Docker container
```bash
make local-run
```
* If you don't have Docker locally then you can build the project (with error,
 but application standalone jar file will be created) and run following command
 to run application server locally (then push config in separate console). 
```bash
make local-run-jar
```

* To push a new config:
  * Edit [dummy_config.json](dummy_config.json)
  * Run `make local-push-config`

Run Docker container with the application inside. Now application API 
available by [http://localhost:8080/api/](http://localhost:8080/api/).
Logs available in `docker_logs` folder.

```
  GET     /api/health (com.zingorn.monitoring.resources.HealthResource)
  GET     /api/v1.0/scheduler/last-runs (com.zingorn.monitoring.resources.SchedulerResource)
  PUT     /api/v1.0/scheduler/update-jobs (com.zingorn.monitoring.resources.SchedulerResource)
```  

Admin UI available by [http://localhost:8080/](http://localhost:8080/).

## Application API

| | URL | Description |
|--- |--- |--- |
| GET | /api/v1.0/scheduler/last-runs | Returns the last job's result reports. Each job report has at least job verdict, job elapsed time and the last run time. 
| PUT | /api/v1.0/scheduler/update-jobs | You can update jobs list without application restarting. Just send JSON config in [following format](dummy_config.json).